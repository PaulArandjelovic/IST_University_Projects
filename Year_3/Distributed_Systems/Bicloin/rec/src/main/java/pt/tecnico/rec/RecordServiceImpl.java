package pt.tecnico.rec;

import static io.grpc.Status.INVALID_ARGUMENT;

import io.grpc.stub.StreamObserver;
import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import pt.tecnico.rec.grpc.Rec.PingRequest;
import pt.tecnico.rec.grpc.Rec.PingResponse;
import pt.tecnico.rec.grpc.Rec.ReadRequest;
import pt.tecnico.rec.grpc.Rec.ReadResponse;
import pt.tecnico.rec.grpc.Rec.WriteRequest;
import pt.tecnico.rec.grpc.Rec.WriteResponse;

import com.google.protobuf.Any;


public class RecordServiceImpl extends RecordServiceGrpc.RecordServiceImplBase {

    private Record recordManager = new Record();

    @Override
    public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
        PingResponse response = PingResponse.newBuilder().setOutputText("OK").setPath(request.getPath()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();

    }

    @Override
    public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {
        System.out.println("Received read request");
        ReadResponse response;
        String recordIdentifier = request.getName();
        VersionedRecord readResult = recordManager.read(recordIdentifier);

        response = ReadResponse.newBuilder().setValue(readResult.getValue()).setVersion(readResult.getVersion()).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void write(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {
        System.out.println("Received write request");
        String recordIdentifier = request.getName();
        VersionedRecord record = new VersionedRecord(request.getValue(), request.getVersion());

        recordManager.write(recordIdentifier, record);

        WriteResponse response = WriteResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}