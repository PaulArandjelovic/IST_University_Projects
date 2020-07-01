package m19.core;

import java.io.IOException;

import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;

import m19.core.exception.MissingFileAssociationException;
import m19.core.exception.BadEntrySpecificationException;
import m19.core.exception.ImportFileException;
import m19.core.exception.NameLengthException;
import m19.app.exception.RuleFailedException;
import m19.app.exception.UserIsActiveException;
import m19.app.exception.WorkNotBorrowedByUserException;

/**
 * The façade class.
 */
public class LibraryManager implements Serializable{
  private static final long serialVersionUID = 201918111350L;
  /** library to manage */
  private Library _library;
  /** current file we are working from */
  private String _filename;

  public LibraryManager(){
      _library = new Library();
      _filename = new String();
  }

  public void saveAs(String filename) throws MissingFileAssociationException, IOException {

  /**
   * Serialize the persistent state of this application into the specified file.
   *
   * @param filename the name of the target file
   *
   * @throws MissingFileAssociationException if the name of the file to store the persistent
   *         is not a valid one.
   * @throws IOException if some error happen during the serialization of the persistent state
   */

    try(FileOutputStream fileOut = new FileOutputStream(filename)){
        try(ObjectOutputStream out = new ObjectOutputStream(fileOut)){
            out.writeObject(_library);
            setFilename(filename);
        }
    }
  }

  public void save() throws MissingFileAssociationException, IOException {

    this.saveAs(this.getFilename());

    /**
     * Serialize the persistent state of this application.
     *
     * @throws MissingFileAssociationException if the name of the file to store the persistent
     *         state has not been set yet.
     * @throws IOException if some error happen during the serialization of the persistent state

      */
  }

  public void load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {

      try(FileInputStream fileIn = new FileInputStream(filename)){
          try(ObjectInputStream in = new ObjectInputStream(fileIn)){
              _library = (Library) in.readObject();
              setFilename(filename);
          }
      }

    /**
     * Recover the previously serialized persitent state of this application.
     *
     * @param filename the name of the file containing the perssitente state to recover
     *
     * @throws IOException if there is a reading error while processing the file
     * @throws FileNotFoundException if the file does not exist
     * @throws ClassNotFoundException
     */
  }

  public void importFile(String datafile) throws ImportFileException, NameLengthException {
    /**
     * Set the state of this application from a textual representation stored into a file.
     *
     * @param datafile the filename of the file with the textual represntation of the state of this application.
     * @throws ImportFileException if it happens some error during the parsing of the textual representation.
     * @throws NameLengthException if length of name to input == 0
     */
    try {
      _library.importFile(datafile);
    } catch (IOException | BadEntrySpecificationException e) {
      throw new ImportFileException(e);
    }
  }

  /**
   * Set filename to save/load data
   *
   * @param filename
   *            name of file to associate library with
   */
  public void setFilename(String filename){
      _filename = filename;
  }

  /**
   * @return name of file
   */
  public String getFilename(){
      return _filename;
  }

  /**
   * @return current date
   */
  public int getCurrentDate(){
      return (_library.getDate()).getCurrentDate();
  }

  /**
   * Advance the date by a given number of days (ndays)
   *
   * @param ndays
   */
  public void advanceCurrentDate(int ndays){
    if (ndays>0)
      (_library.getDate()).advanceDay(ndays);
    updateUserStatus();
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
    return _library.registerUser(userName,email);
  }

  /**
   * Show full description of user, given a unique userID
   *
   * @param int
   *
   * @throws IndexOutOfBoundsException
   *
   * @return full user description
   */
  public String showUser(int id) throws IndexOutOfBoundsException{
    return _library.showUser(id);
  }

  /**
   * Show full description of all users
   *
   * @return full description of all registered users
   */
  public String showUsers(){
    return _library.showUsers();
  }

  /**
   * Show full description of work, given a unique workID
   *
   * @param id
   *
   * @throws IndexOutOfBoundsException
   *
   * @return full description of work
   */
  public String showWork(int id) throws IndexOutOfBoundsException{
    return _library.showWork(id);
  }

  /**
   * Show full description of all works
   *
   * @return full description of all existent works
   */
  public String showWorks(){
    return _library.showWorks();
  }


  /**
   * Show all works with a given term
   *
   * @param term
   *
   * @return description of all works which contain term "term" -> String
   */
  public String searchWorksTerm(String term){
    return _library.searchWorksTerm(term);
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
      return _library.requestWork(userId, workId);
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
      return _library.returnWork(userId, workId);
  }

  /**
   * Show all user notifications
   *
   * @param userId
   *
   * @return user notifications -> String
   */
  public String showUserNotifications(int userId) throws IndexOutOfBoundsException{
      return _library.showUserNotifications(userId);
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
      _library.payFine(userId);
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
      _library.addFine(fine, userId);
  }

  /**
   * get current user fine
   *
   * @param userId
   *
   * @return fine of user -> int
   */
  public int getTotalFine(int userId){
      return _library.getTotalFine(userId);
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
      _library.addRequestNotification(userId, workId);
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
      _library.addDeliveryNotification(userId, workId);
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
      _library.addUserToNotify(userId,workId);
  }

  /**
   * Work returned, update user status
   *
   * @param userId
   *
   * @return void
   */
  public void updateUserStatusActive(int userId){
      _library.updateUserStatusActive(userId);
  }

  /**
   * Update user status to ATIVO/SUSPENSO after returning work
   *
   * @return void
   */
  public void updateUserStatus(){
      _library.updateUserStatus();
  }
}
