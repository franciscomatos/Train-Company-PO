package mmt.app.passenger;

import mmt.TicketOffice;
import mmt.TrainCompany;
import mmt.Passenger;
import mmt.app.exceptions.BadPassengerNameException;
import mmt.app.exceptions.DuplicatePassengerNameException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.NonUniquePassengerNameException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import java.util.Collection;
import java.util.Collections;

/**
 * §3.3.3. Register passenger.
 */
public class DoRegisterPassenger extends Command<TicketOffice> {
  /* Input field  */
  Input<String> _name;

  /**
   * @param receiver
   */
  public DoRegisterPassenger(TicketOffice receiver) {
    super(Label.REGISTER_PASSENGER, receiver);
    _name = _form.addStringInput(Message.requestPassengerName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException {
    try {
    _form.parse();
    _receiver.registerPassenger(_name.value());
    } catch(NonUniquePassengerNameException e) {
      throw new DuplicatePassengerNameException(_name.value());
    }
  }

}
