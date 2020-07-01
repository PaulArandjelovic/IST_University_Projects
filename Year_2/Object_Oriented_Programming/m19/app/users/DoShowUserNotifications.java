package m19.app.users;

import m19.core.LibraryManager;
import m19.app.exception.NoSuchUserException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.DialogException;

/**
 * 4.2.3. Show notifications of a specific user.
 */
public class DoShowUserNotifications extends Command<LibraryManager> {
    /** show notificatons of user with this userID*/
    private Input<Integer> _userId;

  /**
   * @param receiver
   */
  public DoShowUserNotifications(LibraryManager receiver) {
    super(Label.SHOW_USER_NOTIFICATIONS, receiver);
    _userId = _form.addIntegerInput(Message.requestUserId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    _form.parse();
    try{
        _display.popup(_receiver.showUserNotifications(_userId.value()));
    }
    catch (IndexOutOfBoundsException e){
        throw new NoSuchUserException(_userId.value());
    }
  }

}
