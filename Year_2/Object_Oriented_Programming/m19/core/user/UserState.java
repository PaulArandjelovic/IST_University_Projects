package m19.core.user;

import m19.app.exception.UserIsActiveException;//create exception in core
import m19.core.Notification;

/** User interface to implement "State" design pattern for user states -> FALTOSO/NORMAL/CUMPRIDOR */
public interface UserState{

    public String getState();

    public int addConsecutiveReturns();

    public int removeConsecutiveReturns();

    public int getDaysUntilReturn(int numCopies);

    public int getMaxRequests();

    public UserState changeBehaviour(UserState state);
}
