package m19.app.requests;

import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.Form;
import pt.tecnico.po.ui.DialogException;

import java.util.Scanner;

import m19.app.exception.RuleFailedException;
import m19.app.exception.NoSuchUserException;
import m19.app.exception.NoSuchWorkException;

/**
 * 4.4.1. Request work.
 */
public class DoRequestWork extends Command<LibraryManager> {
  /** userID of user requesting work */
  private Input<Integer> _userId;
  /** workID of work being requested */
  private Input<Integer> _workId;
  /** form used to parse user response */
  private Form _form2;
  /** user response -> s/n */
  private Input<String> response;

  /**
   * @param receiver
   */
  public DoRequestWork(LibraryManager receiver) {
    super(Label.REQUEST_WORK, receiver);
    _userId = _form.addIntegerInput(Message.requestUserId());
    _workId = _form.addIntegerInput(Message.requestWorkId());
    _form2 = new Form();
    response = _form2.addStringInput(Message.requestReturnNotificationPreference());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
      int deadline = 0;
      _form.parse();

      try{
          deadline = _receiver.requestWork(_userId.value(), _workId.value());
          _display.popup(Message.workReturnDay(_workId.value(), deadline));
      }
      catch(IndexOutOfBoundsException e){
          if (e.getMessage().equals("user"))
              throw new NoSuchUserException(_userId.value());

          if (e.getMessage().equals("work"))
              throw new NoSuchWorkException(_workId.value());
      }
      catch(RuleFailedException e){
          int rule = e.getRuleIndex();

          if (rule == 3){
              _form2.parse();
              if(response.value().equals("s")){
                  _receiver.addUserToNotify(_userId.value(), _workId.value());
              }
          }
          else{
              throw new RuleFailedException(_userId.value(), _workId.value(), rule);
          }
      }
  }
}
