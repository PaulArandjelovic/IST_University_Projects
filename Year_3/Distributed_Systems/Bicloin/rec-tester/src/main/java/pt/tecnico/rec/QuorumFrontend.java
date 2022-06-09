package pt.tecnico.rec;

import com.google.protobuf.Any;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.rec.grpc.Rec;
import pt.tecnico.rec.grpc.RecordServiceGrpc;
import pt.tecnico.rec.grpc.RecordServiceGrpc.RecordServiceStub;
import pt.ulisboa.tecnico.sdis.zk.ZKNaming;
import pt.ulisboa.tecnico.sdis.zk.ZKNamingException;
import pt.ulisboa.tecnico.sdis.zk.ZKRecord;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class QuorumFrontend implements AutoCloseable {
    final Map<String, RecordServiceStub> stubs = new ConcurrentHashMap<>();
    ArrayList<ManagedChannel> channels = new ArrayList<>();
    String zooHost;
    String zooPort;
    final int numReplicas;
    final int quorumReads;
    final int quorumWrites;

    final static long TIMEOUT = 10000;

    public QuorumFrontend(String zooHost, String zooPort){
        this.zooHost = zooHost;
        this.zooPort = zooPort;
        numReplicas = updateStubs();
        this.quorumReads = Math.floorDiv(numReplicas, 2) + 1;
        this.quorumWrites = Math.floorDiv(numReplicas, 2) + 1;
    }

    public QuorumFrontend(String zooHost, String zooPort, int quorumReads, int quorumWrites){
        this.zooHost = zooHost;
        this.zooPort = zooPort;
        numReplicas = updateStubs();
        this.quorumReads = quorumReads;
        this.quorumWrites = quorumWrites;
        if (quorumReads + quorumWrites < numReplicas || quorumWrites <= Math.floorDiv(numReplicas, 2)
                || quorumReads > numReplicas || quorumWrites > numReplicas) {
            System.out.println("Pesos inválidos.");
            exit(1);
        }
    }

    public int updateStubs(){
        try {
            this.close();
            ZKNaming zkNaming = new ZKNaming(zooHost,zooPort);
            List<ZKRecord> recs = (ArrayList<ZKRecord>) zkNaming.listRecords("/grpc/bicloin/rec");
            System.out.println("Updating stubs");
            synchronized (stubs) {
                for (ZKRecord record : recs) {
                    String target = record.getURI();
                    ManagedChannel channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
                    RecordServiceStub stub = RecordServiceGrpc.newStub(channel);
                    channels.add(channel);
                    System.out.println("Ligando a: " + record.getPath() + " em " + target);
                    stubs.put(record.getPath(), stub);
                }
            }
            return recs.size();
        } catch (ZKNamingException e) {
            System.out.println(e.getMessage());
            exit(1);
        }
        return 0;
    }

    public VersionedRecord recRead(String identifier) throws Exception {
        int maxSequenceNum = -1;
        VersionedRecord returnValue = new VersionedRecord();
        List<Rec.ReadResponse> results;
        ResponseCollector<Rec.ReadResponse> readObserver = new ResponseCollector<>(quorumReads);
        Rec.ReadRequest readRequest = Rec.ReadRequest.newBuilder().setName(identifier).build();
        synchronized(readObserver) {
            synchronized (stubs) {
                for (var stub: stubs.entrySet()) {
                    stub.getValue().read(readRequest, readObserver);
                }
            }
            while (readObserver.getResponseCounter() < quorumReads) {
                readObserver.wait(TIMEOUT);
                
                var exception = readObserver.getException();
                if (exception != null
                        && exception.toString().contains("io.grpc.StatusRuntimeException")
                        && readObserver.getResponseCounter() < quorumReads) {
                    updateStubs();
                    return returnValue;
                }
            }
            results = readObserver.getResults();
            for (var status : results) {
                if (status.getVersion() > maxSequenceNum) {
                    maxSequenceNum = status.getVersion();
                    returnValue.setValue(status.getValue());
                    returnValue.setVersion(status.getVersion());
                }
            }
        }
        return returnValue;
    }

    public boolean recWrite(String identifier, VersionedRecord record) throws Exception {
        ResponseCollector<Rec.WriteResponse> writeObserver = new ResponseCollector<>(quorumWrites);

        Rec.WriteRequest writeRequest = Rec.WriteRequest.newBuilder().setName(identifier).
                setValue(record.getValue()).setVersion(record.getVersion()).build();
        synchronized (writeObserver) {
            synchronized (stubs) {
                for (var stub: stubs.entrySet()) {
                    stub.getValue().write(writeRequest, writeObserver);
                }
            }
            while (writeObserver.getResponseCounter() < quorumWrites) {
                writeObserver.wait(TIMEOUT);
                var exception = writeObserver.getException();
                if (exception != null
                        && exception.toString().contains("io.grpc.StatusRuntimeException")
                        && writeObserver.getResponseCounter() < quorumWrites ) {
                    updateStubs();
                    return false;
                }
            }
        }
        return true;
    }


    public Any read(String identifier) {
        VersionedRecord record  = new VersionedRecord();
        try {
            while (record.getVersion() == -1) {
                record = recRead(identifier);
                if (record.getVersion() == -1) {
                    System.out.println("Trying to read again");
                }
            }
        } catch (Exception e) {
            // Thread was interrupted.
            System.out.println(e.getMessage());
            exit(1);
        }
        System.out.println("Read successfully");
        return record.getValue();
    }

    public Any write(String identifier, Any value) {
        VersionedRecord record  = new VersionedRecord();
        try {
            while (record.getVersion() == -1) {
                record = recRead(identifier);
                if (record.getVersion() == -1) {
                    System.out.println("Trying to read again");
                }
            }
            record.setValue(value);
            record.incVersion();
            boolean written = false;
            while (!written) {
                written = recWrite(identifier, record);
                if (!written) {
                    System.out.println("Trying to write again");
                }
            }

        } catch (Exception e) {
            // Thread was interrupted.
            System.out.println(e.getMessage());
            exit(1);
        }
        System.out.println("Write successfully");
        return record.getValue();
    }


    public Map<String, Boolean> ping(){
        Map<String, Boolean> pings = new HashMap<>();
        int numStubs = updateStubs();
        ResponseCollector<Rec.PingResponse> observer = new ResponseCollector<>(numStubs);

        /*wait pelas respostas*/
        synchronized(observer) {
            synchronized (stubs) {
                for (var stub : stubs.entrySet()) {
                    stub.getValue().ping(Rec.PingRequest.newBuilder().setPath(stub.getKey()).build(), observer);
                }
                try {
                    List<Rec.PingResponse> results;
                    do {
                        observer.clearExceptionCounter();
                        observer.wait(TIMEOUT);
                    } while(observer.getExceptionCounter() > 0);

                    results = observer.getResults();

                    Set<String> paths = new HashSet<>();
                    for (var status : results) {
                        paths.add(status.getPath());
                    }
                    for (var path : stubs.keySet()) {
                        if (paths.contains(path)) {
                            pings.put(path, true);
                        } else {
                            pings.put(path, false);
                        }
                    }
                } catch (InterruptedException e) {
                    // Thread was interrupted.
                    System.out.println(e.getMessage());
                    exit(1);
                }
            }
        }

        return pings;
    }

    @Override
    public void close() {
        for (ManagedChannel channel: channels) {
            channel.shutdown();
        }
        channels = new ArrayList<>();
    }
}
