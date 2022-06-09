package pt.tecnico.bicloin.hub;


import pt.tecnico.bicloin.hub.grpc.Hub;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

public class HubTester {
	
	public static void main(String[] args) {
		System.out.println(HubTester.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		String zooHost = args[0];
		String zooPort = args[1];
		String hubPath = args[2];

		try (HubFrontend recFrontend = new HubFrontend(zooHost, zooPort, hubPath)) {
			System.out.println(recFrontend.ping(Hub.PingRequest.newBuilder().build()).getOutputText());
		} catch (ZKNamingException e){
			System.out.println("ZK error");
		}
	}
	
}
