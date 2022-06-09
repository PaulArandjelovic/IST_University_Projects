package pt.tecnico.rec;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import pt.tecnico.rec.grpc.Rec.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

public class RecFrontend implements AutoCloseable{
    private final ManagedChannel channel;
    private final RecordServiceGrpc.RecordServiceBlockingStub stub;

    public RecFrontend(String zooHost, String zooPort, String recPath) throws ZKNamingException {
        ZKNaming zkNaming = new ZKNaming(zooHost,zooPort);
        // lookup
        ZKRecord record = zkNaming.lookup(recPath);
        String target = record.getURI();

        // Channel is the abstraction to connect to a service endpoint.
        // Let us use plaintext communication because we do not have certificates.
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        stub = RecordServiceGrpc.newBlockingStub(channel);
    }

    public RecFrontend(String host, int port) {
        // Channel is the abstraction to connect to a service endpoint.
        // Let us use plaintext communication because we do not have certificates.
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        // Create a blocking stub.
        stub = RecordServiceGrpc.newBlockingStub(channel);
    }

    public PingResponse ping(PingRequest request){
        return stub.ping(request);
    }

    public ReadResponse read(ReadRequest request) {
        return stub.read(request);
    }

    public WriteResponse write(WriteRequest request){
        return stub.write(request);
    }

    @Override
    public final void close() {
        channel.shutdown();
    }
}




