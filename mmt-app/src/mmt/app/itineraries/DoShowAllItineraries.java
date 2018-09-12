package mmt.app.itineraries;

//import mmt.DefaultSelector;
//import mmt.DefaultVisitor;
import mmt.TicketOffice;
import mmt.Passenger;
import mmt.Itinerary;
import pt.tecnico.po.ui.Command;


//FIXME import other classes if necessary

/**
 * §3.4.1. Show all itineraries (for all passengers).
 */
public class DoShowAllItineraries extends Command<TicketOffice> {

  /**
   * @param receiver
   */
  public DoShowAllItineraries(TicketOffice receiver) {
    super(Label.SHOW_ALL_ITINERARIES, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() {
    for(Passenger p: _receiver.getPassengers())
      if(p.getItineraries().size() > 0)
        _display.add(p.itinerariesToString());
    _display.display();
  }

}
