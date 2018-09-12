package mmt;

import java.io.Serializable;
import java.time.Duration;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FrequentCategory extends Category implements Serializable {

  public FrequentCategory(double spent, long time, double discount, Passenger passenger) {
    super(spent, time, discount, passenger);
  }
  /*FIXME este metodo vai atualizar os atributos e verificar se o passageiro
  muda de categoria*/
  @SuppressWarnings("nls")
  @Override
  public void updateCategory(double spent) {
    setTotalSpent(spent);
    if(getSpent() <= 250)
      getPassenger().updateCategory(new NormalCategory(getSpent(), getTime(), 0, getPassenger()));
    else if(getSpent() > 2500)
      getPassenger().updateCategory(new SpecialCategory(getSpent(), getTime(), 0.5, getPassenger()));
  }

  @SuppressWarnings("nls")
  @Override
  public String toString() {
    DecimalFormatSymbols s = new DecimalFormatSymbols();
    s.setDecimalSeparator('.');
    DecimalFormat df = new DecimalFormat("0.00", s);
    DecimalFormat dfi = new DecimalFormat("00");
    int hours = (int)Math.floor(getTime()/60);
    int totalMinutes = (int)getTime();
    long minutes = totalMinutes - hours*60;
    return "FREQUENTE|" + getPassenger().getTotalIt() + "|" + df.format(getSpent()) + "|" + dfi.format(hours) + ":" + dfi.format(minutes);
  }


}
