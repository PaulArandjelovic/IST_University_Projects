package m19.core;

import java.io.Serializable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;

import m19.core.user.CompareUsers;
import m19.core.user.User;
import m19.core.user.UserState;
import m19.core.rules.Evaluator;
import m19.core.exception.MissingFileAssociationException;
import m19.core.exception.BadEntrySpecificationException;
import m19.core.exception.NameLengthException;
import m19.app.exception.UserIsActiveException;
import m19.app.exception.RuleFailedException;
import m19.app.exception.WorkNotBorrowedByUserException;


/**
 * Class that represents the library as a whole.
 */
public class Library implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201901101348L;
  /** WorkID attributed to next created work */
  private int _nextWorkId;
  /** UserID attributed to next created user */
  private int _nextUserId;
  /** date */
  private Date _date;
  /** HashMap data structure stores users */
  private Map<Integer,User> _userHash;
  /** HashMap data structure stores works */
  private Map<Integer,Work> _workHash;
  /** Evaluator used to evaluate all rules */
  private Evaluator _evaluator;

  public Library(){
    _userHash = new HashMap<>();
    _workHash = new HashMap<>();
    _evaluator= new Evaluator();
    _nextUserId = 0;
    _nextWorkId = 0;
    _date = new Date();
  }

  /**
   * @return user HashMap
   */
  public Map<Integer,User> getUserHash(){
    return _userHash;
  }

  /**
   * @return work HashMap
   */
  public Map<Integer,Work> getWorkHash(){
    return _workHash;
  }

  /**
   * @return next work ID to be attributed
   */
  public int getNextWorkId(){
    return _nextWorkId;
  }

  /**
   * @return next user ID to be attributed
   */
  public int getnextUserId(){
    return _nextUserId;
  }

  /**
   * @return current date
   */
  public Date getDate(){
    return _date;
  }

  /**
   * increment userID for next user
   */
  public void increaseUserId(){
    _nextUserId++;
  }

  /**
   * increment workID for next work
   */
  public void increaseWorkId(){
    _nextWorkId++;
  }

  /**
   * Register a new user with provided username and email
   *
   * @param userName
   * @param email
   *
   * @throws NameLengthException
   *
   * @return newly registered user ID
   */
  public int registerUser(String userName, String email) throws NameLengthException{
    User user = new User(userName,email,_nextUserId);
    _userHash.put(_nextUserId,user);
    this.increaseUserId();
    return _nextUserId - 1;
  }

  /**
   * Show full user description given a unique id
   *
   * @param id
   *
   * @throws IndexOutOfBoundsException
   *
   * @return full description of a user
   */
  public String showUser(int id) throws IndexOutOfBoundsException{
    if (!_userHash.containsKey(id))
      throw new IndexOutOfBoundsException();
    else
      return (_userHash.get(id)).getDescription();
  }

  /**
   * Show full user description of all users
   *
   * @return description of all existent users
   */
  public String showUsers(){
    String result = "";

    List<User> userList =  new ArrayList<>(_userHash.values());

    Collections.sort(userList, new CompareUsers());
    for(User user : userList)
      result += user.getDescription() + "\n";

    return result;
  }

  /**
   * Show description of work given unique id
   *
   * @param id
   *
   * @throws IndexOutOfBoundsException
   *
   * @return full description of work
   */
  public String showWork(int id) throws IndexOutOfBoundsException{
    if (!_workHash.containsKey(id))
      throw new IndexOutOfBoundsException();
    else
      return (_workHash.get(id)).getDescription();
  }

  /**
   * Show description of all works
   *
   * @return full description of all works
   */
  public String showWorks(){
    String result = "";
    for(int value : _workHash.keySet())
      result += this.showWork(value) + "\n";

    return result;
  }

  /**
   * Show all works with a given term
   *
   * @param term
   *
   * @return description of all works which contain term "term" -> String
   */
  public String searchWorksTerm(String term){
    String search = "";
    for(int value: _workHash.keySet()){
      String tempWork = this.showWork(value);
      if (tempWork.contains(term))
        search += tempWork + "\n";
    }
    return search;
  }

  /**
   * Generate a new user request
   *
   * @param userId
   * @param workId
   *
   * @throws RuleFailedException
   * @throws IndexOutOfBoundsException
   *
   * @return deadline of request -> int
   */
  public int requestWork(int userId, int workId) throws RuleFailedException, IndexOutOfBoundsException{
      if (!_userHash.containsKey(userId))
        throw new IndexOutOfBoundsException("user");
      if (!_workHash.containsKey(workId))
        throw new IndexOutOfBoundsException("work");


      Work work = _workHash.get(workId);
      User user = _userHash.get(userId);
      int deadline;

      _evaluator.eval(user, work);
      user.addRequest(work, _date);
      deadline = user.getRequestDeadline(workId);
      work.removeCopy();

      return deadline;
  }

  /**
   * Return a Work
   *
   * @param userId
   * @param workId
   *
   * @throws WorkNotBorrowedByUserException
   * @throws IndexOutOfBoundsException
   *
   * @return fine to pay -> int
   */
  public int returnWork(int userId, int workId) throws WorkNotBorrowedByUserException, IndexOutOfBoundsException{
      if (!_userHash.containsKey(userId))
        throw new IndexOutOfBoundsException("user");
      if (!_workHash.containsKey(workId))
        throw new IndexOutOfBoundsException("work");

      Work work = _workHash.get(workId);
      User user = _userHash.get(userId);

      int deadline = user.getRequestDeadline(workId);
      int currentDate = _date.getCurrentDate();
      int fine = 0;
      boolean late = false;

      if(deadline < currentDate){
          fine = (currentDate - deadline) * 5;
          late = true;
      }

      user.removeRequest(workId, late);
      work.addCopy();
      work.notifyUsers();

      return fine;
  }

  /**
   * Pay user fine
   *
   * @param userId
   *
   * @throws UserIsActiveException
   * @throws IndexOutOfBoundsException
   *
   * @return void
   */
  public void payFine(int userId) throws UserIsActiveException, IndexOutOfBoundsException{
      if (!_userHash.containsKey(userId))
        throw new IndexOutOfBoundsException();
      else
        _userHash.get(userId).payFine();
  }

  /**
   * Add a fine to user fine
   *
   * @param fine
   * @param userId
   *
   * @return void
   */
  public void addFine(int fine, int userId){
      User user = _userHash.get(userId);
      user.addFine(fine);
  }

  /**
   * get current user fine
   *
   * @param userId
   *
   * @return fine of user -> int
   */
  public int getTotalFine(int userId){
      return _userHash.get(userId).getFine();
  }

  /**
   * Add a request notfication to notication request list
   *
   * @param userId
   * @param workId
   *
   * @return void
   */
  public void addRequestNotification(int userId, int workId){
      Notification notification = new RequestN(_workHash.get(workId));
      _userHash.get(userId).addNotification(notification);
  }

  /**
   * Add a delivery notification to user notification list
   *
   * @param userId
   * @param workId
   *
   * @return void
   */
  public void addDeliveryNotification(int userId, int workId){
      Notification notification = new Delivery(_workHash.get(workId));
      _userHash.get(userId).addNotification(notification);
      User u = _userHash.get(userId);
      _workHash.get(workId).addUserToNotify(u);
  }

  /**
   * Add a user to notify when work is available
   *
   * @param userId
   * @param workId
   *
   * @return void
   */
  public void addUserToNotify(int userId, int workId){
      Work work = _workHash.get(workId);
      User user = _userHash.get(userId);
      work.addUserToNotify(user);
  }

  /**
   * Show all user notifications
   *
   * @param userId
   *
   * @return user notifications -> String
   */
  public String showUserNotifications(int userId) throws IndexOutOfBoundsException{
      if (!_userHash.containsKey(userId))
        throw new IndexOutOfBoundsException();
      else
        return (_userHash.get(userId)).showUserNotifications();
  }

  /**
   * Update user status to ATIVO/SUSPENSO after returning work
   *
   * @return void
   */
  public void updateUserStatus(){
      for (User user: _userHash.values()){
          if (!user.checkDeadlines(getDate().getCurrentDate()))
              user.setStatus(false);
          else
              user.setStatus(true);
      }
  }

  /**
   * Work returned, update user status
   *
   * @param userId
   *
   * @return void
   */
  public void updateUserStatusActive(int userId){
      User user = _userHash.get(userId);
      if (user.checkDeadlines(getDate().getCurrentDate()))
          user.setStatus(true);
  }

  void importFile(String filename) throws BadEntrySpecificationException, IOException, NameLengthException {
    Parser parser = new Parser(this);
    parser.parseFile(filename);
  }
}
