package pt.tecnico.rec;

import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;


public class RecordMain {
	private static String serverHost;
	private static String serverPort;
	private static String path;

	private static ZKNaming zkNaming = null;

	public static void main(String[] args) throws Exception {
		System.out.println(RecordMain.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}
		if (args.length != 5) {
			System.out.printf("Received incorrect number of arguments, required: 5, given: %d", args.length);
			return;
		}

		final String zooHost = args[0];
		final String zooPort = args[1];
		serverHost = args[2];
		serverPort = args[3];
		final String instanceNumber = args[4];
		path = "/grpc/bicloin/rec/" + instanceNumber;


		final BindableService impl = new RecordServiceImpl();
		// start gRPC server
		Server server = ServerBuilder.forPort(Integer.parseInt(serverPort)).addService(impl).build();
		server.start();

		System.out.println("Contacting ZooKeeper at " + zooHost + ":" + zooPort + "...");
		zkNaming = new ZKNaming(zooHost, zooPort);
		zkNaming.rebind(path, serverHost, serverPort);
		// Use hook to register a thread to be called on shutdown.
		Runtime.getRuntime().addShutdownHook(new Unbind());

		System.out.println("Server started and awaiting requests on port " + serverPort);
		// await termination
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
