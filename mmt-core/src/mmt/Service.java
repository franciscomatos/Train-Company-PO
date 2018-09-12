package mmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Service implements Serializable {
  private int _id;
  private double _price;
  private ArrayList<Station> _stations = new ArrayList<Station>();

  public Service(int id, double price) {
    _id = id;
    _price = price;
  }

  public void addStation(Station station) {
    _stations.add(station);
  }

  public int getId() {
    return _id;
  }

  public double getPrice() {
    return _price;
  }

  public long getDuration() {
    return getFirstStation().getDeparture().until(getLastStation().getDeparture(), ChronoUnit.MINUTES);
  }

  public Station getFirstStation() {
    return _stations.get(0);
  }

  public Station getLastStation() {
    return _stations.get(_stations.size()-1);
  }

  public ArrayList<String> getStationNames() {
    ArrayList<String> stationNames = new ArrayList<String>();
    for(Station s: _stations)
      stationNames.add(s.getName());
    return stationNames;
  }

  public Collection<Station> getStations() {
    return Collections.unmodifiableCollection(_stations);
  }

  public Station getStation(String stationName) {
    for(Station s: _stations)
      if(s.equals(stationName))
        return s;
    return null;
  }

  public Station getStationByIndex(int index) {
    return _stations.get(index);
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
    return "Serviço #" + _id + " @ " + df.format(_price) + "\n" + a;
  }

}
