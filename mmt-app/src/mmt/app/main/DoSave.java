package mmt.app.main;

import java.io.IOException;
import java.io.FileNotFoundException;

import mmt.TicketOffice;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Input;

//FIXME import other classes if necessary

/**
 * §3.1.1. Save to file under current name (if unnamed, query for name).
 */
public class DoSave extends Command<TicketOffice> {

  Input<String> _fileName;

  /**
   * @param receiver
   */
  public DoSave(TicketOffice receiver) {
    super(Label.SAVE, receiver);
    if(_receiver.getFileName() == null)
      _fileName = _form.addStringInput(Message.newSaveAs());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    try{
      if(_receiver.getFileName() == null) {
        _form.parse();
        _receiver.save(_fileName.value());
      } else {
        _receiver.save("");
      }
    } catch(FileNotFoundException fnfe) {
      _display.popup(Message.fileNotFound());
    } catch(ClassNotFoundException | IOException e) {
      e.printStackTrace();
    }
  }
}
