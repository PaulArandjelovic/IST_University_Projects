package pt.tecnico.bicloin.hub;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import pt.tecnico.rec.QuorumFrontend;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

public class HubMain {

	private static String serverHost;
	private static String serverPort;

	private static String path;

	private static ZKNaming zkNaming = null;
	
	public static void main(String[] args) throws IOException, InterruptedException, ZKNamingException {
		System.out.println(HubMain.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		// Check arguments.
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s port%n", HubMain.class.getName());
			return;
		}
		if (args.length <= 7) {
			System.out.printf("Received incorrect number of arguments, required: more than 7, given: %d", args.length);
			return;
		}

		final String zooHost = args[0];
		final String zooPort = args[1];

		serverHost = args[2];
		serverPort = args[3];
		final String instanceNumber = args[4];

		final String userImportPath = args[5];
		final String stationImportPath = args[6];

		final boolean initRec = args[7].equals("initRec");

		path = "/grpc/bicloin/hub/" + instanceNumber;

		QuorumFrontend quorumFrontend;
		if (args.length > 8){ // pesos diferenciados pra read e write
			int quorumReads = Integer.parseInt(args[8]);
			int quorumWrites = Integer.parseInt(args[9]);
			quorumFrontend = new QuorumFrontend(zooHost, zooPort, quorumReads, quorumWrites);
		} else {
			quorumFrontend = new QuorumFrontend(zooHost, zooPort);
		}
		final Hub hub = new Hub(path, quorumFrontend, initRec);
		//parse user csv
		try (BufferedReader br = Files.newBufferedReader(Paths.get(userImportPath))) {
			// read the file line by line
			String line;
			while ((line = br.readLine()) != null) {

				// convert line into columns
				String[] columns = line.split(",");

				//add user
				hub.newUser(columns[0], columns[1], columns[2]);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		//parse station csv
		try (BufferedReader br = Files.newBufferedReader(Paths.get(stationImportPath))) {
			// read the file line by line
			String line;
			while ((line = br.readLine()) != null) {

				// convert line into columns
				String[] columns = line.split(",");

				//add user
				hub.newStation(columns);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		BindableService impl = new HubServiceImpl(hub, zkNaming);
		// Create a new server to listen on port.
		Server server = ServerBuilder.forPort(Integer.parseInt(serverPort)).addService(impl).build();
		// Start the server.
		server.start();

		// Register on ZooKeeper.
		System.out.println("Contacting ZooKeeper at " + zooHost + ":" + zooPort + "...");
		zkNaming = new ZKNaming(zooHost, zooPort);
		zkNaming.rebind(path, serverHost, serverPort);
		// Use hook to register a thread to be called on shutdown.
		Runtime.getRuntime().addShutdownHook(new Unbind());

		System.out.println("Server started and awaiting requests on port " + serverPort);
		// Do not exit the main thread. Wait until server is terminated.
		server.awaitTermination();
	}

	/**
	 * Unbind class unbinds replica from ZKNaming after interruption.
	 */
	static class Unbind extends Thread {
		@Override
		public void run() {
			if (zkNaming != null) {
				try {
					System.out.println("Unbinding " + path + " from ZooKeeper...");
					zkNaming.unbind(path, serverHost, String.valueOf(serverPort));
				}
				catch (ZKNamingException e) {
					System.err.println("Could not close connection with ZooKeeper: " + e);
				}
			}
		}
	}
	
}
