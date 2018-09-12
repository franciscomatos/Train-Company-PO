package mmt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.time.LocalDate;
import java.util.Comparator;
import java.time.Duration;

public class Passenger implements Serializable {
  private int _id;
  private String _name;
  private Category _category;
  private ArrayList<Itinerary> _itineraries = new ArrayList<Itinerary>();
  private Comparator<Itinerary> _itineraryComparator = new DateComparator();
  private Itinerary _lastAdded;

  private class DateComparator implements Comparator<Itinerary>, Serializable {

  		@Override
  		public int compare(Itinerary itinerary1, Itinerary itinerary2) {
  			return itinerary1.getDate().compareTo(itinerary2.getDate());
  		}

  }

  public Passenger(int id, String name){
    _id = id;
    _name = name;
    _category = new NormalCategory(this);
  }

  public int getId() {
    return _id;
  }

  /* verificar se e realmente necessario*/
  public void setId(int id) {
    _id = id;
  }

  public String getName() {
    return _name;
  }

  public void setName(String name) {
    _name = name;
  }

  public Category getCategory() {
    return _category;
  }

  public int getTotalIt() {
    return _itineraries.size();
  }

  public Collection<Itinerary> getItineraries() {
    ArrayList<Itinerary> itineraries = _itineraries;
    itineraries.sort(_itineraryComparator);
    for(Itinerary i: itineraries)
      i.setId(itineraries.indexOf(i) + 1);
    //Collections.sort(itineraries, _itineraryComparator);
    return Collections.unmodifiableCollection(itineraries);
  }

  /* este método vai ser chamado sempre que o passageiro efetuar mais um
    itinerario */
  public void addItinerary(Itinerary i) {
    _itineraries.add(i);
    i.setId(_itineraries.size());
  }

  public Itinerary getLastAddedIt() {
    return _itineraries.get(_itineraries.size()-1);
  }

  public void checkCategoryChanges() {
    List<Itinerary> lastTenItineraries = _itineraries.subList(
      Math.max(_itineraries.size() - 10, 0), _itineraries.size());
    double spent = 0;
    for(Itinerary i: lastTenItineraries)
      spent += i.getRealPrice();
    _category.updateCategory(spent);
  }

  /*este metodo vai ser usado por uma classe externa (Category) por isso
  usa-se a visibilidade de package*/
  void updateCategory(Category category) {
    _category = category;
  }

  @SuppressWarnings("nls")
  @Override
  public String toString() {
    return _id + "|" + _name + "|" + _category.toString();
  }

  public String itinerariesToString() {
    String a = "";
    for(int i = 0; i < getItineraries().size() - 1; i++) {
      a += _itineraries.get(i).toString();
    }
    a += _itineraries.get(getItineraries().size()-1).toString();
    return "== Passageiro " + getId() + ": " + getName() + " ==\n" + a;
  }

}
