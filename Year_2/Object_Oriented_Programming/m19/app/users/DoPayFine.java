package m19.app.users;

import m19.core.LibraryManager;
import m19.app.exception.UserIsActiveException;
import m19.app.exception.NoSuchUserException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;


/**
 * 4.2.5. Settle a fine.
 */
public class DoPayFine extends Command<LibraryManager> {
    /** userID of user for fine to be paid */
    private Input<Integer> _userId;

  /**
   * @param receiver
   */
  public DoPayFine(LibraryManager receiver) {
    super(Label.PAY_FINE, receiver);
    _userId = _form.addIntegerInput(Message.requestUserId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try{
        _receiver.payFine(_userId.value());
    }
    catch(UserIsActiveException e){
        throw e;
    }
    catch(IndexOutOfBoundsException e){
        throw new NoSuchUserException(_userId.value());
    }
  }

}
