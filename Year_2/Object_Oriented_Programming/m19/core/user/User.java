package m19.core.user;

import m19.core.exception.NameLengthException;
import m19.app.exception.UserIsActiveException;
import m19.app.exception.WorkNotBorrowedByUserException;
import m19.core.Notification;
import m19.core.Request;
import m19.core.Work;
import m19.core.Date;

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.io.Serializable;

/**
 * Users of library
 */
public class User implements Serializable{
    private static final long serialVersionUID = 201918111350L;

    private UserState _state;
    /** id of user */
    private int _id;
    /** active user? T/F */
    private boolean _isActive;
    /** name of user */
    private String _name;
    /** email of user */
    private String _email;
    /** user notifications */
    private Set<Notification> _notifications;
    /** user requests */
    private Set<Request> _requests;
    /** current accumulated user fine */
    private int _fine;

    /**
     * @param name
     * @param email
     * @param id
     *
     * @throws NameLengthException
     */
    public User(String name, String email, int id) throws NameLengthException{
        if (name.length()==0)
            throw new NameLengthException();
        if (email.length()==0)
            throw new NameLengthException();
        _name = name;
        _email = email;
        _id = id;
        _isActive = true;
        _notifications = new LinkedHashSet<>();
        _requests = new LinkedHashSet<>();
        _state = new Normal();
        _fine = 0;
    }

    /**
     * @param status
     *
     * @return void
     */
    public void setStatus(boolean status){
        _isActive = status;
    }

    /**
     * @return current user state/behaviour -> FALTOSO/NORMAL/CUMPRIDOR
     */
    public UserState getUserBehavior(){
        return _state;
    }

    /**
     * @return id of user
     */
    public int getId(){
        return _id;
    }

    /**
     * @return state of user: ACTIVO/SUSPENSO
     */
    public String isActive(){
        if (_isActive)
            return "ACTIVO";
        return "SUSPENSO";
    }

    /**
     * @return name of user
     */
    public String getName(){
        return _name;
    }

    /**
     * @return email of user
     */
    public String getEmail(){
        return _email;
    }

    /**
     * @return full description of user
     */
    public String getDescription(){
        String description = (_id+" - "+_name+" - "+_email+" - "+_state.getState()+" - "+isActive());
        if (!_isActive)
            description += " - EUR "+_fine;
        description += this.showUserNotifications();

        this.clearNotifications();
        return description;
    }

    /**
     * add request to list of user requests
     *
     * @param work
     * @param date
     *
     * @return void
     */
    public void addRequest(Work work, Date date){
        int days = _state.getDaysUntilReturn(work.getCopies());
        int deadLineDate = date.getCurrentDate() + days;

        Request request = new Request(work.getId(), deadLineDate);
        _requests.add(request);
    }

    /**
     * remove request from list of user requests
     *
     * @param workId
     * @param late
     *
     * @throws WorkNotBorrowedByUserException
     *
     * @return void
     */
    public void removeRequest(int workId, boolean late) throws WorkNotBorrowedByUserException{
        for(Request r: _requests){
            if (r.getWorkId() == workId){
                _requests.remove(r);
                if(!late)
                _state.addConsecutiveReturns();
                if(late)
                    _state.removeConsecutiveReturns();
                _state = _state.changeBehaviour(_state);
                return;
            }
        }
        throw new WorkNotBorrowedByUserException(workId, _id);
    }

    /**
     * check if user has requested work with work ID "workId"
     *
     * @param workId
     *
     * @return boolean
     */
    public boolean checkRequest(int workId){
        for (Request r : _requests)
            if (r.getWorkId() == workId)
                return true;
        return false;
    }

    /**
     * get deadline of request for work with work ID "workId"
     *
     * @param workId
     *
     * @return int
     */
    public int getRequestDeadline(int workId){
        for(Request r: _requests)
            if (r.getWorkId() == workId)
                return r.getDeadline();
        return 0;
    }

    /**
     * add notification to list of user notifications
     *
     * @param notification
     *
     * @return void
     */
    public void addNotification(Notification notification){
        _notifications.add(notification);
    }

    /**
     * show all user notifications
     *
     * @return String
     */
    public String showUserNotifications(){
        String result = "";

        for(Notification n : _notifications)
          result += n.getMessage() + "\n";

        return result;
    }

    /**
     * clear all user notifications
     *
     * @return void
     */
    public void clearNotifications(){
        _notifications.clear();
    }

    /**
     * pay user fine in entirety
     *
     * @throws UserIsActiveException
     *
     * @return void
     */
    public void payFine() throws UserIsActiveException{
        if (_isActive)
            throw new UserIsActiveException(_id);

        _fine = 0;
        _isActive = true;
    }

    /**
     * add fine from returned work to current user fine
     *
     * @param fine
     *
     * @return void
     */
    public void addFine(int fine){
        _fine += fine;
    }

    /**
     * @return fine
     */
    public int getFine(){
        return _fine;
    }

    /**
     * @return size of user request list -> int
     */
    public int sizeOfRequestList(){
        return _requests.size();
    }

    /**
     * check all works to see if user has works past deadline
     *
     * @param date
     *
     * @return boolean
     */
    public boolean checkDeadlines(int date){
        for (Request r: _requests)
            if (r.getDeadline() < date)
                return false;
        return true;
    }
}
