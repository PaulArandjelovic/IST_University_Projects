package pt.tecnico.bicloin.hub;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.UNKNOWN;

import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.stub.StreamObserver;
import pt.tecnico.bicloin.hub.grpc.Hub.*;
import pt.tecnico.bicloin.hub.grpc.HubServiceGrpc;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HubServiceImpl extends HubServiceGrpc.HubServiceImplBase {
    private final Hub hub;
    private final ZKNaming zkNaming;

    public HubServiceImpl(Hub hub, ZKNaming zkNaming) {
        this.hub = hub;
        this.zkNaming = zkNaming;
    }

    @Override
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        PingResponse response = PingResponse.newBuilder().
                setOutputText("OK").build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void sysStatus(SysStatusRequest request, StreamObserver<SysStatusResponse> responseObserver) {
        SysStatusResponse.Builder sysStatusBuilder = SysStatusResponse.newBuilder();
        SysStatusResponse.Result.Builder resultBuilder = SysStatusResponse.Result.newBuilder().setPath(hub.getPath());
        resultBuilder.setIsUp(true);
        sysStatusBuilder.addResults(resultBuilder.build());
        Map<String, Boolean> recStatus = hub.pingRecs();
        for (String path : recStatus.keySet()) {
            resultBuilder = SysStatusResponse.Result.newBuilder().setPath(path);
            resultBuilder.setIsUp(recStatus.get(path));
            sysStatusBuilder.addResults(resultBuilder.build());
        }

        SysStatusResponse response = sysStatusBuilder.build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private boolean isUp(ZKRecord zkRecord){
        try (HubFrontend hubFrontend = new HubFrontend(zkRecord)){
            return hubFrontend.ping(PingRequest.newBuilder().build()).getOutputText().equals("OK");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        try {
            int balance = hub.balance(request.getUsername());
            BalanceResponse response = BalanceResponse.newBuilder().setBalance(balance).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription(e.getMessage()).asRuntimeException());
        } catch (InvalidProtocolBufferException e) {
            responseObserver.onError(UNKNOWN
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }
    
    @Override
    public void topUp(TopUpRequest request, StreamObserver<BalanceResponse> responseObserver) {
        try {
            int balance = hub.topUp(request.getUsername(), request.getAmount(), request.getPhone());
            BalanceResponse response = BalanceResponse.newBuilder().setBalance(balance).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription(e.getMessage()).asRuntimeException());
        } catch (InvalidProtocolBufferException e) {
            responseObserver.onError(UNKNOWN
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void bikeUp(BikeUpRequest request, StreamObserver<BikeUpResponse> responseObserver) {
        try {
            hub.bikeUp(request.getUsername(), request.getStationID(), request.getLatitude(), request.getLongitude());
            BikeUpResponse response = BikeUpResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription(e.getMessage()).asRuntimeException());
        } catch (InvalidProtocolBufferException | AccessControlException e) {
            responseObserver.onError(UNKNOWN
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void bikeDown(BikeDownRequest request, StreamObserver<BikeDownResponse> responseObserver) {
        try {
            hub.bikeDown(request.getUsername(), request.getStationID(), request.getLatitude(), request.getLongitude());
            BikeDownResponse response = BikeDownResponse.newBuilder().build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription(e.getMessage()).asRuntimeException());
        } catch (InvalidProtocolBufferException | AccessControlException e) {
            responseObserver.onError(UNKNOWN
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void infoStation(InfoStationRequest request, StreamObserver<InfoStationResponse> responseObserver) {
        try {
            String stationID = request.getStationID();
            Station s = hub.getStation(stationID);
            InfoStationResponse response = InfoStationResponse.newBuilder().setName(s.getName())
                    .setLatitude(s.getLatitude()).setLongitude(s.getLongitude()).setCapacity(s.getNumDocks())
                    .setNumOfAvailableBikes(hub.getNumBikes(stationID)).setPrize(s.getPrize())
                    .setWithdrawals(hub.getNumWithdrawals(stationID)).setDeposits(hub.getNumDeposits(stationID)).build();


            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription(e.getMessage()).asRuntimeException());
        } catch (InvalidProtocolBufferException | AccessControlException e) {
            responseObserver.onError(UNKNOWN
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }




    @Override
    public void locateStation(LocateStationRequest request, StreamObserver<LocateStationResponse> responseObserver) {
        try {
            Double latitude = request.getLatitude();
            Double longitude = request.getLongitude();
            Integer numStations = request.getNumOfStations();
            List<String> stationIDs = hub.getKClosestStations(latitude, longitude, numStations);

            LocateStationResponse response = LocateStationResponse.newBuilder().addAllStationIDs(stationIDs)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IllegalArgumentException e) {
            responseObserver.onError(INVALID_ARGUMENT
                    .withDescription(e.getMessage()).asRuntimeException());
        } catch (AccessControlException e) {
            responseObserver.onError(UNKNOWN
                    .withDescription(e.getMessage()).asRuntimeException());
        }
    }




}
