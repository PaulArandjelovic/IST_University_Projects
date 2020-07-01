package m19.core.user;

import m19.core.exception.NameLengthException;
import m19.app.exception.UserIsActiveException;
import m19.core.Notification;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.io.Serializable;

/**
 * Type of UserState
 */
public class Cumpridor implements UserState, Serializable{
    private static final long serialVersionUID = 201918111350L;
    /** Consecutive times user has (or has not -> -) returned a work */
    private int _consecutiveReturns;

    /**
     * _consecutiveReturns initialized to 0
     */
    public Cumpridor(){
        _consecutiveReturns = 0;
    }

    /**
     * Increment _consecutiveReturns
     *
     * @return current consecutive returns
     */
    public int addConsecutiveReturns(){
        _consecutiveReturns++;
        return _consecutiveReturns;
    }

    /**
     * Decrement _consecutiveReturns
     *
     * @return current consecutive returns
     */
    public int removeConsecutiveReturns(){
        _consecutiveReturns = -1;
        return _consecutiveReturns;
    }

    /**
     * change user state (FALTOSO/NORMAL/CUMPRIDOR)
     *
     * @param state
     *
     * @return new user state
     */
    public UserState changeBehaviour(UserState state){
        if(_consecutiveReturns == -1)
            state = new Normal();
        return state;
    }

    /**
     * @param numCopies of work
     *
     * @return days until return
     */
    public int getDaysUntilReturn(int numCopies){
        if(numCopies == 1)
            return 8;
        if(numCopies <= 5)
            return 15;
        return 30;
    }

    /**
     * @return Max requests a user can make
     */
    public int getMaxRequests(){
        return 5;
    }

    /**
     * @return UserState in string format
     */
    public String getState(){
        return "CUMPRIDOR";
    }
}
