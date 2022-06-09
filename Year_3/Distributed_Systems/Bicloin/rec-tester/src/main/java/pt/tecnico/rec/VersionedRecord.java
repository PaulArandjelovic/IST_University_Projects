package pt.tecnico.rec;

import com.google.protobuf.Any;

public class VersionedRecord {
    Any value;
    int version;

    VersionedRecord(Any value, int version){
        this.value = value;
        this.version = version;
    }

    VersionedRecord(){
        this.value = null;
        this.version = -1;
    }

    public void incVersion() {
        this.version++;
    }

    public Any getValue() {
        return value;
    }

    public int getVersion() {
        return version;
    }

    public void setValue(Any value) {
        this.value = value;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
