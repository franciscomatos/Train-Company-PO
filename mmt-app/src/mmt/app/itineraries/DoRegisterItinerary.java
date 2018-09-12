package mmt.app.itineraries;

import mmt.TicketOffice;
import mmt.Itinerary;
import mmt.app.exceptions.BadDateException;
import mmt.app.exceptions.BadTimeException;
import mmt.app.exceptions.NoSuchItineraryException;
import mmt.app.exceptions.NoSuchPassengerException;
import mmt.app.exceptions.NoSuchStationException;
import mmt.app.exceptions.NoSuchServiceException;
import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NoSuchServiceIdException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;

/**
 * §3.4.3. Add new itinerary.
 */
public class DoRegisterItinerary extends Command<TicketOffice> {

  Input<Integer> _passengerId;
  Input<String> _departureStationName;
  Input<String> _arrivalStationName;
  Input<String> _departureDate;
  Input<String> _departureTime;
  Input<Integer> _itineraryChoice;
  /**
   * @param receiver
   */
  public DoRegisterItinerary(TicketOffice receiver) {
    super(Label.REGISTER_ITINERARY, receiver);
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  /*retirar comentarios*/
  @Override
  public final void execute() throws DialogException {
    try {
      _passengerId = _form.addIntegerInput(Message.requestPassengerId());
      _departureStationName = _form.addStringInput(Message.requestDepartureStationName());
      _arrivalStationName = _form.addStringInput(Message.requestArrivalStationName());
      _departureDate = _form.addStringInput(Message.requestDepartureDate());
      _departureTime = _form.addStringInput(Message.requestDepartureTime());
      _form.parse();
      _form.clear();
      Collection<Itinerary> a = _receiver.search(_passengerId.value(), _departureStationName.value(),
                                         _arrivalStationName.value(), _departureDate.value(),
                                         _departureTime.value());
      for(Itinerary it: a) {
          _display.add(it.toString());
      }
      _display.display();


      if(a.size() > 0) {
        _itineraryChoice = _form.addIntegerInput(Message.requestItineraryChoice());
        _form.parse();
        _form.clear();
        _receiver.commitItinerary(_passengerId.value(), _itineraryChoice.value());
      }

    } catch (NoSuchPassengerIdException e) {
      throw new NoSuchPassengerException(e.getId());
    } catch (NoSuchServiceIdException e) {
      throw new NoSuchServiceException(e.getId());
    } catch (NoSuchStationNameException e) {
      throw new NoSuchStationException(e.getName());
    } catch (NoSuchItineraryChoiceException e) {
      throw new NoSuchItineraryException(e.getPassengerId(), e.getItineraryId());
    } catch (BadDateSpecificationException e) {
      throw new BadDateException(e.getDate());
    } catch (BadTimeSpecificationException e) {
      throw new BadTimeException(e.getTime());
    }
  }
}
