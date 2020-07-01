package m19.app.users;

import m19.core.LibraryManager;
import m19.core.user.User;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.DialogException;
import m19.core.exception.NameLengthException;
import m19.app.exception.UserRegistrationFailedException;

/**
 * 4.2.1. Register new user.
 */
public class DoRegisterUser extends Command<LibraryManager> {
    /** username of new user */
    private Input<String> _userName;
    /** email of new user */
    private Input<String> _userEmail;

  /**
   * @param receiver
   */
  public DoRegisterUser(LibraryManager receiver) {
    super(Label.REGISTER_USER, receiver);
    _userName = _form.addStringInput(Message.requestUserName());
    _userEmail = _form.addStringInput(Message.requestUserEMail());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException{
      /** id of new user */
      int id;

    _form.parse();
    try{
      id = _receiver.registerUser(_userName.value(), _userEmail.value());
    } catch (NameLengthException nle){
      throw new UserRegistrationFailedException(_userName.value(), _userEmail.value());
    }
    _display.popup(Message.userRegistrationSuccessful(id));
  }
}
