package mmt;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public abstract class Category implements Serializable {
  private double _totalSpent;
  private long _acumulatedMinutes;
  private double _discount;
  private Passenger _passenger;

  public Category(double spent, long time, double discount, Passenger passenger) {
    _totalSpent = spent;
    _acumulatedMinutes = time;
    _discount = discount;
    _passenger = passenger;
  }

  public double getSpent() {
    return _totalSpent;
  }

  public void setTotalSpent(double spent) {
    _totalSpent = spent;
  }

  public long getTime() {
    return _acumulatedMinutes;
  }

  public double getDiscount() {
    return _discount;
  }

  public Passenger getPassenger() {
    return _passenger;
  }

  public void setAcumulatedMinutes (Itinerary itinerary) {
    _acumulatedMinutes += itinerary.getItineraryMinutes();
  }

  public void addMinutes(long minutes) {
    _acumulatedMinutes += minutes;
  }

  public abstract void updateCategory(double spent);

}
