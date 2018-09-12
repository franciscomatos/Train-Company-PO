package mmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;

public class Segment implements Serializable {
  private double _price;
  private Service _service;
  private ArrayList<Station> _stations = new ArrayList<Station>();

  public Segment(Service service, double price) {
    _service = service;
    _price = price;
  }

  public int getServiceId() {
    return _service.getId();
  }

  public double getPrice() {
    return _price;
  }

  public Collection<Station> getStations() {
    return Collections.unmodifiableCollection(_stations);
  }

  public Station getFirstStation() {
    return _stations.get(0);
  }

  public Station getLastStation() {
    return _stations.get(_stations.size()-1);
  }

  public void addStations(String firstStation, String lastStation) {
    int firstIndex = _service.getStationNames().indexOf(firstStation);
    int lastIndex = _service.getStationNames().indexOf(lastStation);
    for(int i = firstIndex; i <= lastIndex; i++)
      _stations.add(_service.getStationByIndex(i));
    calcPrice();
  }

  public void calcPrice() {
    double servicePrice = _service.getPrice();
    int serviceDuration = (int)_service.getDuration();
    int segmentDuration = (int)getFirstStation().getDeparture().until(getLastStation().getDeparture(), ChronoUnit.MINUTES);
    _price = servicePrice * segmentDuration / serviceDuration;
  }


  @SuppressWarnings("nls")
  @Override
  public String toString() {
    String a = "";
    for(Station s: _stations) {
      a = a + s.toString() + "\n";
    }
    DecimalFormatSymbols s = new DecimalFormatSymbols();
    s.setDecimalSeparator('.');
    DecimalFormat df = new DecimalFormat("0.00", s);
    return "Serviço #" + getServiceId() + " @ " + df.format(_price) + "\n" + a;
  }
}
