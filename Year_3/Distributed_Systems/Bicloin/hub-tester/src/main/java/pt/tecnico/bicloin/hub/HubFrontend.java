package pt.tecnico.bicloin.hub;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc;
import pt.tecnico.bicloin.hub.grpc.Hub.*;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

public class HubFrontend implements AutoCloseable{

    private final ManagedChannel channel;
    private final HubServiceGrpc.HubServiceBlockingStub stub;

    public HubFrontend(String host, int port) {
        // Channel is the abstraction to connect to a service endpoint.
        // Let us use plaintext communication because we do not have certificates.
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        // Create a blocking stub.
        stub = HubServiceGrpc.newBlockingStub(channel);
    }

    public HubFrontend(String zooHost, String zooPort, String hubPath) throws ZKNamingException {
        ZKNaming zkNaming = new ZKNaming(zooHost,zooPort);
        // lookup
        ZKRecord record = zkNaming.lookup(hubPath);
        String target = record.getURI();

        // Channel is the abstraction to connect to a service endpoint.
        // Let us use plaintext communication because we do not have certificates.
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        stub = HubServiceGrpc.newBlockingStub(channel);
    }
    public HubFrontend(ZKRecord record) {
        String target = record.getURI();

        // Channel is the abstraction to connect to a service endpoint.
        // Let us use plaintext communication because we do not have certificates.
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        // Create a blocking stub.
        stub = HubServiceGrpc.newBlockingStub(channel);
    }

    public PingResponse ping(PingRequest request){
        return stub.ping(request);
    }

    public BalanceResponse balance(BalanceRequest request) {
        return stub.balance(request);
    }

    public BalanceResponse topUp(TopUpRequest request) {
        return stub.topUp(request);
    }

    public InfoStationResponse infoStation(InfoStationRequest request) {
        return stub.infoStation(request);
    }

    public LocateStationResponse locateStation(LocateStationRequest request) {
        return stub.locateStation(request);
    }

    public BikeUpResponse bikeUp(BikeUpRequest request) {
        return stub.bikeUp(request);
    }

    public BikeDownResponse bikeDown(BikeDownRequest request) {
        return stub.bikeDown(request);
    }

    public SysStatusResponse sysStatus(SysStatusRequest request) {
        return stub.sysStatus(request);
    }

    @Override
    public final void close() {
        channel.shutdown();
    }

}
