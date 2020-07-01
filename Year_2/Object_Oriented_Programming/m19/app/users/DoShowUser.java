package m19.app.users;

import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import m19.app.exception.NoSuchUserException;

/**
 * 4.2.2. Show specific user.
 */
public class DoShowUser extends Command<LibraryManager> {
    /** ID of user to show */
    private Input<Integer> _userID;

  /**
   * @param receiver
   */
  public DoShowUser(LibraryManager receiver) {
    super(Label.SHOW_USER, receiver);
    _userID = _form.addIntegerInput(Message.requestUserId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
      _form.parse();
    try{
      _display.popup(_receiver.showUser(_userID.value()));
    } catch (IndexOutOfBoundsException ioobe){
      throw new NoSuchUserException(_userID.value());
    }
  }
}
