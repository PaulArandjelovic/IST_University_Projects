package pt.tecnico.rec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.Any;
import com.google.protobuf.StringValue;
import pt.tecnico.rec.grpc.Rec;

public class Record {
    Map<String, VersionedRecord> dataStore;

    public Record() {
        dataStore = new ConcurrentHashMap<String, VersionedRecord>();
    }

    public synchronized String toString() {
        return "Data Store: " + dataStore.toString() + "%n";
    }

    public synchronized VersionedRecord read(String recordIdentifier) {
        VersionedRecord versionedRecord;

        if((versionedRecord = dataStore.get(recordIdentifier)) == null) {
            StringValue message = StringValue.newBuilder().setValue("-").build();
            com.google.protobuf.Any value = Any.pack(message);
            versionedRecord = new VersionedRecord(value, 0);
            dataStore.put(recordIdentifier, versionedRecord);
        }
        return versionedRecord;
    }

    public synchronized void write(String recordIdentifier, VersionedRecord value) {
        VersionedRecord currentValue = read(recordIdentifier);
        if (currentValue.getVersion() < value.getVersion()){
            dataStore.put(recordIdentifier, value);
        }
    }
}