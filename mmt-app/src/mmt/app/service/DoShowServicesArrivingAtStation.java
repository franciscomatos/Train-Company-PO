package mmt.app.service;

import mmt.TicketOffice;
import mmt.Service;
import mmt.exceptions.NoSuchStationNameException;
import mmt.app.exceptions.NoSuchStationException;
import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

//FIXME import other classes if necessary

/**
 * 3.2.4 Show services arriving at station.
 */
public class DoShowServicesArrivingAtStation extends Command<TicketOffice> {

  Input<String> _stationName;

  /**
   * @param receiver
   */
  public DoShowServicesArrivingAtStation(TicketOffice receiver) {
    super(Label.SHOW_SERVICES_ARRIVING_AT_STATION, receiver);
    _stationName = _form.addStringInput(Message.requestStationName());
  }

  /** @see pt.tecnico.po.ui.Command#execute() */
  @Override
  public final void execute() throws DialogException, NoSuchStationException {
    try {
      _form.parse();
      for(Service s: _receiver.getServicesArrivingAtStation(_stationName.value()))
        _display.add(s.toString());
      _display.display();
    } catch(NoSuchStationNameException e) {
      throw new NoSuchStationException(_stationName.value());
    }

  }

}
