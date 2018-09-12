package mmt;

import java.io.Serializable;
import java.time.LocalTime;

public class Station implements Serializable {
  private String _name;
  private LocalTime _departure;

  public Station(String name, LocalTime time) {
    _name = name;
    _departure = time;
  }

  public String getName() {
    return _name;
  }

  public LocalTime getDeparture() {
    return _departure;
  }

  public void setName(String name) {
    _name = name;
  }

  public void setDeparture(LocalTime time) {
    _departure = time;
  }

  @SuppressWarnings("nls")
  @Override
  public String toString() {
    return _departure + " " + _name;
  }

  /* compares stations based on the station name */
  @SuppressWarnings("nls")
  @Override
  public boolean equals(Object o) {
    if(o instanceof String){
      String s = (String) o;
      return this.getName().equals(s);
    }
    return false;
  }

}
