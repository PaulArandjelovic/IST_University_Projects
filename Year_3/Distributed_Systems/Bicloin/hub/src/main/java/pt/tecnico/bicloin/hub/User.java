package pt.tecnico.bicloin.hub;

import com.google.protobuf.Any;
import com.google.protobuf.BoolValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import pt.tecnico.rec.QuorumFrontend;
import pt.tecnico.rec.grpc.Rec.ReadRequest;
import pt.tecnico.rec.grpc.Rec.ReadResponse;
import pt.tecnico.rec.grpc.Rec.WriteRequest;
import pt.tecnico.rec.RecFrontend;

public class User {
    private final String username;
    private final String name;
    private final String phone;

    public User(String username, String name, String phone) {
        this.username = username;
        this.name = name;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }


    public String getName() {
        return name;
    }


    public String getPhone() {
        return phone;
    }


    public synchronized Integer getBalance(QuorumFrontend quorumFrontend) throws InvalidProtocolBufferException{
        Any response = quorumFrontend.read("balance" + username);
        Int32Value message = response.unpack(Int32Value.class);
        return message.getValue();
    }

    public synchronized Integer topUp(Integer amount, QuorumFrontend quorumFrontend) throws InvalidProtocolBufferException {
        Integer newBalance = this.getBalance(quorumFrontend) + amount;
        this.setBalance(newBalance, quorumFrontend);
        return newBalance;
    }

    public synchronized void setBalance(Integer balance, QuorumFrontend quorumFrontend){
        Any value = Any.pack(Int32Value.newBuilder().setValue(balance).build());
        quorumFrontend.write("balance" + username, value);
    }

    public synchronized boolean hasBike(QuorumFrontend quorumFrontend){
        ReadRequest request = ReadRequest.newBuilder().setName("hasBike" + username).build();
        Any value = quorumFrontend.read("hasBike" + username);
        boolean hasBike;
        try {
            BoolValue message = value.unpack(BoolValue.class);
            hasBike = message.getValue();
        } catch (InvalidProtocolBufferException e) {
            hasBike = false;
        }
        return hasBike;
    }

    public synchronized void setHasBike(Boolean hasBike, QuorumFrontend quorumFrontend){
        Any value = Any.pack(BoolValue.newBuilder().setValue(hasBike).build());
        quorumFrontend.write("hasBike" + username, value);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone +
                '}';
    }
}
