package mmt.app.itineraries;

import mmt.TicketOffice;
import mmt.Passenger;
import mmt.Itinerary;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.app.exceptions.NoSuchPassengerException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

/**
 * §3.4.2. Show all itineraries (for a specific passenger).
 */
public class DoShowPassengerItineraries extends Command<TicketOffice> {
  /* Input field*/
  Input<Integer> _id;

  /**
   * @param receiver
   */
  public DoShowPassengerItineraries(TicketOffice receiver) {
    super(Label.SHOW_PASSENGER_ITINERARIES, receiver);
    _id = _form.addIntegerInput(Message.requestPassengerId());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException, NoSuchPassengerException {
    try {
      _form.parse();
      if(_receiver.getPassenger(_id.value()).getItineraries().size() > 0) {
        _display.add(_receiver.getPassenger(_id.value()).itinerariesToString());
      }
      else {
        _display.add(Message.noItineraries(_id.value()));
      }
      _display.display();
    } catch(NoSuchPassengerIdException e) {
      throw new NoSuchPassengerException(_id.value());
    }
  }

}
