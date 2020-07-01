package m19.app.works;

import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.DialogException;
import m19.app.exception.NoSuchWorkException;

/**
 * 4.3.1. Display work.
 */
public class DoDisplayWork extends Command<LibraryManager> {
    /** ID of work to be shown */
    private Input<Integer> _workID;

  /**
   * @param receiver
   */
  public DoDisplayWork(LibraryManager receiver) {
    super(Label.SHOW_WORK, receiver);
    _workID = _form.addIntegerInput(Message.requestWorkId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
      _form.parse();
    try{
      _display.popup(_receiver.showWork(_workID.value()));
    } catch (IndexOutOfBoundsException ioobe){
      throw new NoSuchWorkException(_workID.value());
    }
  }
}
