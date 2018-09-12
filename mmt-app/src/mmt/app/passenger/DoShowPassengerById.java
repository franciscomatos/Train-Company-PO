package mmt.app.passenger;

import mmt.TicketOffice;
import mmt.Passenger;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.app.exceptions.NoSuchPassengerException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

//FIXME import other classes if necessary

/**
 * §3.3.2. Show specific passenger.
 */
public class DoShowPassengerById extends Command<TicketOffice> {
  /* Input field*/
  Input<Integer> _id;

  /**
   * @param receiver
   */
  public DoShowPassengerById(TicketOffice receiver) {
    super(Label.SHOW_PASSENGER_BY_ID, receiver);
    _id = _form.addIntegerInput(Message.requestPassengerId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException, NoSuchPassengerException {
    try {
      _form.parse();
      _display.popup(_receiver.getPassenger(_id.value()).toString());
    } catch(NoSuchPassengerIdException e) {
      throw new NoSuchPassengerException(_id.value());
    }

  }

}
