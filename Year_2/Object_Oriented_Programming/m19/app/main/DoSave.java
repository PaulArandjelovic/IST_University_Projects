package m19.app.main;

import java.io.IOException;
import m19.core.exception.MissingFileAssociationException;
import m19.core.LibraryManager;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;
import pt.tecnico.po.ui.DialogException;

/**
 * 4.1.1. Save to file under current name (if unnamed, query for name).
 */
public class DoSave extends Command<LibraryManager> {
  /** filename for new file creation if not currently saved in file */
  private Input<String> _filename;

  /**
   * @param receiver
   */
  public DoSave(LibraryManager receiver) {
    super(Label.SAVE, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {

    try{
      if ("".equals(_receiver.getFilename())){
        _filename = _form.addStringInput(Message.newSaveAs());
        _form.parse();
        _receiver.saveAs(_filename.value());
      }
      else
        _receiver.save();
    } catch (MissingFileAssociationException | IOException e) {
        e.printStackTrace();
    }
  }
}
