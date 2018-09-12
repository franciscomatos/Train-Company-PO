package mmt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.ImportFileException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.MissingFileAssociationException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NonUniquePassengerNameException;
import java.time.format.DateTimeParseException;

import java.util.TreeMap;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.LocalDate;


/**
 * Façade for handling persistence and other functions.
 */
public class TicketOffice {

  /** The object doing most of the actual work. */
  private TrainCompany _trains = new TrainCompany();

  private String _fileName;

  public String getFileName() {
    return _fileName;
  }

  public void addService(int id, double price) {
    _trains.addService(id, price);
  }

  public void addStationToService(int id, String name, LocalTime time) {
    _trains.addStationToService(id, name, time);
  }

  public final Service getService(int id) throws NoSuchServiceIdException {
    return _trains.getService(id);
  }

  public Collection<Service> getServices() {
    return _trains.getServices();
  }

  public Collection<Service> getServicesDepartingFromStation(String stationName) throws NoSuchStationNameException {
    return _trains.getServicesDepartingFromStation(stationName);
  }

  public Collection<Service> getServicesArrivingAtStation(String stationName) throws NoSuchStationNameException {
    return _trains.getServicesArrivingAtStation(stationName);
  }

  public void registerPassenger(String name) throws NonUniquePassengerNameException {
     _trains.registerPassenger(name);
  }

  public void changePassengerName(int id, String name) throws NoSuchPassengerIdException, NonUniquePassengerNameException {
    _trains.changePassengerName(id,name);
  }

  public final Passenger getPassenger(int id) throws NoSuchPassengerIdException {
    return _trains.getPassenger(id);
  }

  public Collection<Passenger> getPassengers() {
    return _trains.getPassengers();
  }

  public void reset() {
    _fileName = null;
    _trains = new TrainCompany(_trains.getAllServices());
  }

  public void save(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
    if( _trains.getFlag()) {
      if(filename == "") {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(_fileName));
        out.writeObject(_trains);
        out.close();
      }
      else {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename));
        out.writeObject(_trains);
        out.close();
        _fileName = filename;
      }
    }
  }

  public void load(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
    _trains = (TrainCompany) in.readObject();
    in.close();
    _fileName = filename;
  }

  public void importFile(String datafile) throws ImportFileException {
    try {
      _trains.importFile(datafile);
    } catch(IOException e) {
      throw new ImportFileException();
    } catch(NonUniquePassengerNameException e) {
      throw new ImportFileException();
    } catch(NoSuchPassengerIdException e) {
      throw new ImportFileException();
    } catch(NoSuchServiceIdException e) {
      throw new ImportFileException();
    }
  }

  public Collection<Itinerary> search(int passengerId, String departureStation, String arrivalStation,
                                      String departureDate, String departureTime) throws NoSuchPassengerIdException,
                                      NoSuchStationNameException, NoSuchServiceIdException,
                                      BadDateSpecificationException, BadTimeSpecificationException {

    try {
      LocalDate date = LocalDate.parse(departureDate);
      LocalTime time = LocalTime.parse(departureTime);
      List<Itinerary> itineraries = new ArrayList<Itinerary>();
      List<Service> allServices = new ArrayList<Service>();
      allServices.addAll(_trains.getServices());
      List<Station> serviceStations = new ArrayList<Station>();
      serviceStations.addAll(allServices.get(0).getStations());
      Itinerary currentItinerary = new Itinerary(date, getPassenger(passengerId));
      List<Station> currentSegmentStations = new ArrayList<Station>();

      List<Itinerary> sortedItineraries = _trains.search(passengerId, departureStation, departureStation,
                                                       arrivalStation, date, time, itineraries,
                                                       allServices, serviceStations,
                                                       currentItinerary, currentSegmentStations);

      return Collections.unmodifiableCollection(sortedItineraries);

    } catch(DateTimeParseException e) {
        if(e.getParsedString().equals(departureDate)) {
          throw new BadDateSpecificationException(departureDate);
        }
        else {
          throw new BadTimeSpecificationException(departureTime);
        }
    }
  }

  public void commitItinerary(int passengerId, int itineraryNumber) throws NoSuchPassengerIdException,
                              NoSuchItineraryChoiceException {

    _trains.commitItinerary(passengerId, itineraryNumber);
  }

}
