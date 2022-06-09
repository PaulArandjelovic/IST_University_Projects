package pt.tecnico.rec;

import com.google.protobuf.Any;

public class VersionedRecord {
    Any value;
    int version;

    VersionedRecord(Any value, int version){
        this.value = value;
        this.version = version;
    }

    public Any getValue() {
        return value;
    }

    public int getVersion() {
        return version;
    }
}
