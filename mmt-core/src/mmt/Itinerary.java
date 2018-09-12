package mmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;
import java.time.LocalDate;
import java.time.LocalTime;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Collections;
import java.time.temporal.ChronoUnit;

public class Itinerary implements Serializable {
  private int _id;
  private double _price = 0;
  private double _discountWhenBought;
  private LocalDate _date;
  private TreeMap<Integer, Segment> _segments = new TreeMap<Integer, Segment>();
  private Passenger _passenger;

  public Itinerary(LocalDate date, Passenger p) {
    _date = date;
    _passenger = p;
    _discountWhenBought = p.getCategory().getDiscount();
  }

  public LocalDate getDate() {
    return _date;
  }

  public void setId(int id) {
    _id = id;
  }

  /*returns the real cost of the itinerary considering the discount*/
  public double getRealPrice() {
    return _price - _discountWhenBought * _price;
  }

  public double getPrice() {
    return _price;
  }

  public int getDepartureServiceId() {
    int s = 0;
    for(Segment seg: getSegments()) {
      s = seg.getServiceId();
    }
    return s;
  }

  public int getNumberOfServices() {
    int s = 0;
    for(Segment seg: getSegments())
      s++;
    return s;
  }

  public LocalTime getItineraryDeparture() {
    LocalTime departure = LocalTime.MAX;
    for(Segment s: getSegments())
      for(Station st: s.getStations())
        if(st.getDeparture().isBefore(departure))
          departure = st.getDeparture();
    return departure;
  }

  public LocalTime getItineraryArrival() {
    LocalTime departure = LocalTime.MIN;
    for(Segment s: getSegments())
      for(Station st: s.getStations())
        if(st.getDeparture().isAfter(departure))
          departure = st.getDeparture();
    return departure;
  }

  public Collection<Segment> getSegments() {
    return Collections.unmodifiableCollection(_segments.values());
  }

  public Segment getSegment(int serviceId) {
    return _segments.get(serviceId);
  }

  /* adds a new segment and calcs it's price*/
  public void addSegment(Segment segment) {
    _segments.put(segment.getServiceId(), segment);
    _price += segment.getPrice();
  }

  public long getItineraryMinutes() {
    return getItineraryDeparture().until(getItineraryArrival(), ChronoUnit.MINUTES);
  }

  @SuppressWarnings("nls")
  @Override
  public String toString() {
    DecimalFormatSymbols s = new DecimalFormatSymbols();
    s.setDecimalSeparator('.');
    DecimalFormat df = new DecimalFormat("0.00", s);
    String a = "";
    for(Segment seg: getSegments()) {
      a = a + seg.toString();
    }
    return "\nItinerário " + _id + " para " + _date + " @ " + df.format(_price) + "\n" + a;
  }

}
