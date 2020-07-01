package m19.core;

import java.util.HashMap;
import java.io.Serializable;
import m19.core.user.User;

/** Stores a user request */
public class Request implements Serializable{
    /** deadline for request */
    private int _deadline;
    /** workID of work requested */
    private int _workId;

    /**
     * Create new request
     *
     * @param workId
     * @param deadline
     */
    public Request(int workId, int deadline){
        _workId = workId;
        _deadline = deadline;
    }

    /**
     * @return workID -> int
     */
    public int getWorkId(){
        return _workId;
    }

    /**
     * @return deadline -> int
     */
    public int getDeadline(){
        return _deadline;
    }
}
