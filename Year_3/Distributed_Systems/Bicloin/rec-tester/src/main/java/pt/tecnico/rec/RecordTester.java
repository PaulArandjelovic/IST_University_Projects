package pt.tecnico.rec;


import pt.tecnico.rec.grpc.Rec;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;

public class RecordTester {
	
	public static void main(String[] args) {
		System.out.println(RecordTester.class.getSimpleName());
		
		// receive and print arguments
		System.out.printf("Received %d arguments%n", args.length);
		for (int i = 0; i < args.length; i++) {
			System.out.printf("arg[%d] = %s%n", i, args[i]);
		}

		String zooHost = args[0];
		String zooPort = args[1];
		String recPath = args[2];

		try (RecFrontend recFrontend = new RecFrontend(zooHost, zooPort, recPath)) {
			System.out.println(recFrontend.ping(Rec.PingRequest.newBuilder().build()).getOutputText());
		} catch (ZKNamingException e){
			System.out.println("ZK error");
		}
	}
	
}
