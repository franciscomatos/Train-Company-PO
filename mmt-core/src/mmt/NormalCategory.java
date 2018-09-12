package mmt;

import java.io.Serializable;
import java.time.Duration;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NormalCategory extends Category implements Serializable {

  public NormalCategory(Passenger passenger) {
    super(0.0, 0, 0, passenger);
  }

  public NormalCategory(double spent, long time, double discount, Passenger passenger) {
    super(spent, time, discount, passenger);
  }

  /*FIXME este metodo vai atualizar os atributos e verificar se o passageiro
  muda de categoria*/
  @SuppressWarnings("nls")
  @Override
  public void updateCategory(double spent) {
    setTotalSpent(spent);
    if(getSpent() > 250 && getSpent() <= 2500) {
      getPassenger().updateCategory(new FrequentCategory(getSpent(), getTime(), 0.15, getPassenger()));
    }
    else if(getSpent() > 2500)
      getPassenger().updateCategory(new SpecialCategory(getSpent(), getTime(), 0.5, getPassenger()));
  }

  /*FIXME _acumulatedTime*/
  @SuppressWarnings("nls")
  @Override
  public String toString() {
    DecimalFormatSymbols s = new DecimalFormatSymbols();
    s.setDecimalSeparator('.');
    DecimalFormat dfd = new DecimalFormat("0.00", s);
    DecimalFormat dfi = new DecimalFormat("00");
    int hours = (int)Math.floor(getTime()/60);
    int totalMinutes = (int)getTime();
    long minutes = totalMinutes - hours*60;
    return "NORMAL|" + getPassenger().getTotalIt() + "|" + dfd.format(getSpent()) + "|" + dfi.format(hours) + ":" + dfi.format(minutes);
  }

}
