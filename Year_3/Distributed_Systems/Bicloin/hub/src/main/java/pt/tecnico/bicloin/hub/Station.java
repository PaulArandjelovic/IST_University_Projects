package pt.tecnico.bicloin.hub;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import pt.tecnico.rec.QuorumFrontend;
import pt.tecnico.rec.RecFrontend;
import pt.tecnico.rec.grpc.Rec;

public class Station {
    private final String name;
    private final String stationID;
    private final Double latitude;
    private final Double longitude;
    private final Integer numDocks;
    private final Integer prize;

    public Station(String name, String stationID, Double latitude, Double longitude, Integer numDocks, Integer prize) {
        this.name = name;
        this.stationID = stationID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numDocks = numDocks;
        this.prize = prize;
    }

    public Double getLatitude() {
        return latitude;
    }


    public Double getLongitude() {
        return longitude;
    }


    public Integer getNumDocks() {
        return numDocks;
    }


    public synchronized Integer getNumBikes(QuorumFrontend quorumFrontend) throws InvalidProtocolBufferException {
        Any response = quorumFrontend.read("numBikes" + stationID);
        return response.unpack(Int32Value.class).getValue();
    }

    public synchronized void setNumBikes(Integer numBikes, QuorumFrontend quorumFrontend) {
        Any value = Any.pack(Int32Value.newBuilder().setValue(numBikes).build());
        quorumFrontend.write("numBikes" + stationID, value);
    }

    public Integer getPrize() {
        return prize;
    }

    public synchronized Integer getNumWithdrawals(QuorumFrontend quorumFrontend) throws InvalidProtocolBufferException {
        Any response = quorumFrontend.read("withdrawals" + stationID);
        return response.unpack(Int32Value.class).getValue();
    }

    public synchronized void setNumWithdrawals(Integer numWithdrawals, QuorumFrontend quorumFrontend){
        Any value = Any.pack(Int32Value.newBuilder().setValue(numWithdrawals).build());
        quorumFrontend.write("withdrawals" + stationID, value);
    }

    public synchronized void incNumWithdrawals(QuorumFrontend quorumFrontend) throws  InvalidProtocolBufferException {
        setNumWithdrawals(getNumWithdrawals(quorumFrontend) + 1, quorumFrontend);
    }


    public synchronized Integer getNumDeposits(QuorumFrontend quorumFrontend) throws InvalidProtocolBufferException {
        Any response = quorumFrontend.read("deposits" + stationID);
        return response.unpack(Int32Value.class).getValue();
    }

    public synchronized void setNumDeposits(Integer numDeposits, QuorumFrontend quorumFrontend){
        Any value = Any.pack(Int32Value.newBuilder().setValue(numDeposits).build());
        quorumFrontend.write("deposits" + stationID, value);
    }

    public synchronized void incNumDeposits(QuorumFrontend quorumFrontend) throws  InvalidProtocolBufferException {
        setNumDeposits(getNumDeposits(quorumFrontend) + 1, quorumFrontend);
    }

    public String getName() {
        return name;
    }
}
