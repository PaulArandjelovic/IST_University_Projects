package m19.app.requests;

import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.Form;
import pt.tecnico.po.ui.DialogException;
import m19.app.exception.WorkNotBorrowedByUserException;
import m19.app.exception.NoSuchUserException;
import m19.app.exception.NoSuchWorkException;

/**
 * 4.4.2. Return a work.
 */
public class DoReturnWork extends Command<LibraryManager> {
    /** userID of user to return work */
    private Input<Integer> _userId;
    /** workID of work to be returned */
    private Input<Integer> _workId;
    /** form used to parse user response */
    private Form _form2;
    /** user response -> s/n */
    private Input<String> response;


  /**
   * @param receiver
   */
  public DoReturnWork(LibraryManager receiver) {
    super(Label.RETURN_WORK, receiver);
    _userId = _form.addIntegerInput(Message.requestUserId());
    _workId = _form.addIntegerInput(Message.requestWorkId());
    _form2 = new Form();
    response = _form2.addStringInput(Message.requestFinePaymentChoice());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    int fineToPay = 0;

    _form.parse();

    int userId = _userId.value();
    int workId = _workId.value();

    try{
        fineToPay = _receiver.returnWork(userId, workId);
    }
    catch(IndexOutOfBoundsException e){
        if (e.getMessage().equals("user"))
            throw new NoSuchUserException(userId);
        if (e.getMessage().equals("work"))
            throw new NoSuchWorkException(workId);
    }
    catch(WorkNotBorrowedByUserException e){
        throw e;
    }

    if(fineToPay != 0){
        _display.popup(Message.showFine(userId, _receiver.getTotalFine(userId) + fineToPay));
        _form2.parse();

        if(response.value().equals("n"))
            _receiver.addFine(fineToPay, userId);
        else
            _receiver.updateUserStatusActive(userId);
    }
  }
}
