package mmt;

import mmt.exceptions.BadDateSpecificationException;
import mmt.exceptions.BadEntryException;
import mmt.exceptions.BadTimeSpecificationException;
import mmt.exceptions.InvalidPassengerNameException;
import mmt.exceptions.NoSuchDepartureException;
import mmt.exceptions.NoSuchPassengerIdException;
import mmt.exceptions.NoSuchServiceIdException;
import mmt.exceptions.NoSuchStationNameException;
import mmt.exceptions.NoSuchItineraryChoiceException;
import mmt.exceptions.NonUniquePassengerNameException;
import mmt.exceptions.ImportFileException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.io.BufferedReader;
import java.io.FileReader;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Collection;
import java.util.Collections;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.util.Comparator;
import java.lang.*;

/**
 * A train company has schedules (services) for its trains and passengers that
 * acquire itineraries based on those schedules.
 */
public class TrainCompany implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 201708301010L;

  private TreeMap<Integer, Service> _servicesRegistry;
  private TreeMap<Integer, Passenger> _passengersRegistry;
  private List<Itinerary> _itineraryChoices;
  private int _passengerCounter;
  private boolean _hasChanged;
  private Comparator<Itinerary> _itineraryComparator;
  private Comparator<Service> _departureComparator;
  private Comparator<Service> _arrivalComparator;

  private class ItineraryComparator implements Comparator<Itinerary>, Serializable {

  		@Override
  		public int compare(Itinerary itinerary1, Itinerary itinerary2) {
        Double price1 = itinerary1.getPrice();
        Double price2 = itinerary2.getPrice();
          if(itinerary1.getItineraryDeparture().equals(itinerary2.getItineraryDeparture())) {
            if(itinerary1.getItineraryArrival().equals(itinerary2.getItineraryArrival()))
              return price1.compareTo(price2);
            return itinerary1.getItineraryArrival().compareTo(itinerary2.getItineraryArrival());
          }
          return itinerary1.getItineraryDeparture().compareTo(itinerary2.getItineraryDeparture());
  		}

  }

  private class DepartureComparator implements Comparator<Service>, Serializable {

  		@Override
  		public int compare(Service service1, Service service2) {
  			return service1.getFirstStation().getDeparture().compareTo(service2.getFirstStation().getDeparture());
  		}

  	}

  private class ArrivalComparator implements Comparator<Service>, Serializable {

    	@Override
    	public int compare(Service service1, Service service2) {
    		return service1.getLastStation().getDeparture().compareTo(service2.getLastStation().getDeparture());
    	}

  }

  /**
   * Construtor da classe.
   *
   **/
  public TrainCompany() {
      _servicesRegistry = new TreeMap<Integer,Service>();
      _passengersRegistry = new TreeMap<Integer,Passenger>();
      _itineraryChoices = new ArrayList<Itinerary>();
      _passengerCounter = 0;
      _hasChanged = false;
      _itineraryComparator = new ItineraryComparator();
      _departureComparator = new DepartureComparator();
      _arrivalComparator = new ArrivalComparator();
  }

  /**
   * Construtor da classe. Cria uma instancia atraves de um registo predefinido
   * de servicos.
   *
   * @param servicesRegistry
   *        registo base de servicos.
   **/
  public TrainCompany(TreeMap<Integer, Service> servicesRegistry) {
      _servicesRegistry = servicesRegistry;
      _passengersRegistry = new TreeMap<Integer,Passenger>();
      _itineraryChoices = new ArrayList<Itinerary>();
      _passengerCounter = 0;
      _hasChanged = false;
      _itineraryComparator = new ItineraryComparator();
      _departureComparator = new DepartureComparator();
      _arrivalComparator = new ArrivalComparator();
  }
  /**
   * Metodo simples utilizado para mostrar o estado da flag.
   *
   * @return o atributo _hasChanged.
   **/
  public boolean getFlag() {
    return _hasChanged;
  }

  /**
   * Cria um servico sem estacoes e adiciona ao registo.
   *
   * @param id
   *        id do servico a ser registado.
   * @param price
   *        preco total do servico.
   **/
  public void addService(int id, double price) {
    Service service = new Service(id,price);
    _servicesRegistry.put(id,service);
  }

  /**
   * Cria uma nova estacao e adiciona ao servico correspondente.
   *
   * @param id
   *        id do servico correspondente.
   * @param name
   *        nome da estacao.
   * @param time
   *        hora de partida da estacao.
   **/
  public void addStationToService(int id, String name, LocalTime time) {
    Station station = new Station(name, time);
    _servicesRegistry.get(id).addStation(station);
  }

  /**
   * Metodo utilizado para mostrar um servico atraves do id.
   *
   * @param id
   *        id do servico.
   * @return um objeto Servico com o id dado.
   * @throws NoSuchServiceIdException
   *         caso nao exista nenhum servico com o id dado como argumento.
   **/
  public final Service getService(int id) throws NoSuchServiceIdException {
    if(_servicesRegistry.get(id) == null)
      throw new NoSuchServiceIdException(id);
    return _servicesRegistry.get(id);
  }

  /**
   * Metodo utilizado para mostrar todos os servicos disponiveis.
   *
   * @return uma colecao com todos os servicos disponiveis.
   **/
  public Collection<Service> getServices() {
    return Collections.unmodifiableCollection(_servicesRegistry.values());
  }

  public TreeMap<Integer,Service> getAllServices() {
    return _servicesRegistry;
  }

  /**
   * Metodo utilizado para mostrar os servicos que saem de uma dada estacao.
   *
   * @param stationName
   *        nome da estacao de partida.
   * @return uma colecao de servicos que saem da estacao dada.
   * @throws NoSuchStationNameException
   *         caso nao exista nenhuma estacao com o nome dado como argumento.
   **/
  public Collection<Service> getServicesDepartingFromStation(String stationName) throws NoSuchStationNameException {
    ArrayList<Service> services = new ArrayList<Service>();
    boolean noSuchStationName = false;
    for(Service s: getServices()) {
      if(s.getFirstStation().getName().equals(stationName))
        services.add(s);
      for(Station st: s.getStations())
        if(st.getName().equals(stationName))
          noSuchStationName = true;
    }
    if(noSuchStationName == false)
      throw new NoSuchStationNameException(stationName);
    Collections.sort(services, _departureComparator);
    return Collections.unmodifiableCollection(services);
  }

  /**
   * Metodo utilizado para mostrar os servicos que chegam a uma dada estacao.
   *
   * @param stationName
   *        nome da estacao de chegada.
   * @return uma colecao de servicos que chegam a estacao dada.
   * @throws NoSuchStationNameException
   *         caso nao exista nenhuma estacao com o nome dado como argumento.
   **/
  public Collection<Service> getServicesArrivingAtStation(String stationName) throws NoSuchStationNameException {
    ArrayList<Service> services = new ArrayList<Service>();
    boolean noSuchStationName = false;
    for(Service s: getServices()) {
      if(s.getLastStation().getName().equals(stationName))
        services.add(s);
      for(Station st: s.getStations())
        if(st.getName().equals(stationName))
          noSuchStationName = true;
    }
    if(noSuchStationName == false)
        throw new NoSuchStationNameException(stationName);
    Collections.sort(services, _arrivalComparator);
    return Collections.unmodifiableCollection(services);
  }


  /**
   * Metodo utilizado para criar um novo passageiro e adicionar ao registo.
   *
   * @param name
   *        nome do passageiro.
   * @throws NonUniquePassengerNameException
   *        caso ja exista um passageiro no registo com o mesmo nome.
   **/
  public void registerPassenger(String name) throws NonUniquePassengerNameException {
      for(Passenger p: getPassengers())
        if(p.getName().equals(name))
          throw new NonUniquePassengerNameException(name);
      Passenger passenger = new Passenger(_passengerCounter++, name);
      _passengersRegistry.put(passenger.getId(), passenger);
  }

  /**
   * Metodo utilizado para alterar o nome de um passageiro.
   * @param id
   *        id do passageiro a alterar.
   * @param name
   *        novo nome do passageiro.
   * @throws NoSuchPassengerIdException
   *        caso nao exista nenhum passageiro com o id dado como argumento.
   * @throws NonUniquePassengerNameException
   *        caso ja exista um passageiro no registo com o nome para que se
   *        pretende alterar.
   **/
  public void changePassengerName(int id, String name) throws NoSuchPassengerIdException, NonUniquePassengerNameException {
      if(_passengersRegistry.get(id) == null)
        throw new NoSuchPassengerIdException(id);
      for(Passenger p: getPassengers())
        if(p.getName().equals(name))
          throw new NonUniquePassengerNameException(name);
      Passenger p = getPassenger(id);
      p.setName(name);
  }

  /**
   * Metodo simples utilizado para mostrar um passageiro atraves do id.
   *
   * @param id
   *        id do passageiro.
   * @return um objeto Passageiro com o id dado.
   * @throws NoSuchPassengerIdException
   *         caso nao exista nenhum passageiro com o id dado como argumento.
   **/
  public final Passenger getPassenger(int id) throws NoSuchPassengerIdException {
    if(_passengersRegistry.get(id) == null)
      throw new NoSuchPassengerIdException(id);
    return _passengersRegistry.get(id);
  }

  /**
   * Metodo simples utilizado para mostrar todos os passageiros registados.
   *
   * @return uma colecao com todos os passageiros em registo.
   **/
  public Collection<Passenger> getPassengers() {
    return Collections.unmodifiableCollection(_passengersRegistry.values());
  }

  /**
   * Cria um itinerario sem estacoes e adiciona ao passageiro correspondente.
   *
   * @param passengerId
   *        id do passageiro
   * @param date
   *        data do itinerario
   * @throws NoSuchPassengerIdException
   *         caso nao exista nenhum passageiro com o id dado como argumento.
   **/
  public void addItinerary(int passengerId, LocalDate date) throws NoSuchPassengerIdException {
    Itinerary itinerary = new Itinerary(date, getPassenger(passengerId));
    getPassenger(passengerId).addItinerary(itinerary);
  }

  /**
   * Cria um novo segmento com inicio e fim correspondente as estacoes dadas como
   * argumento e adiciona ao ultimo itinerario lido.
   *
   * @param passengerId
   *        id do passageiro correspondente.
   * @param serviceId
   *        id do servico correspondente.
   * @param departureStationName
   *        nome da estacao de partida.
   * @param arrivalStationName
   *        nome da estacao de chegada.
   * @throws NoSuchPassengerIdException
   *         caso nao exista nenhum passageiro com o id dado como argumento.
   * @throws NoSuchServiceIdException
   *         caso nao exista nenhum servico com o id dado como argumento.
   **/
  public void addSegmentToItinerary(int passengerId, int serviceId, String departureStationName, String arrivalStationName)
    throws NoSuchPassengerIdException, NoSuchServiceIdException {

    Segment segment = new Segment(getService(serviceId), 0.0);
    segment.addStations(departureStationName, arrivalStationName);
    getPassenger(passengerId).getLastAddedIt().addSegment(segment);
  }

  /**
   * Cria um novo segmento com inicio e fim correspondente as estacoes dadas como
   * argumento e adiciona a determinado itinerario.
   *
   * @param passengerId
   *        id do passageiro correspondente.
   * @param serviceId
   *        id do servico correspondente.
   * @param departureStationName
   *        nome da estacao de partida.
   * @param arrivalStationName
   *        nome da estacao de chegada.
   * @throws NoSuchPassengerIdException
   *         caso nao exista nenhum passageiro com o id dado como argumento.
   * @throws NoSuchServiceIdException
   *         caso nao exista nenhum servico com o id dado como argumento.
   **/
  public void addSegmentToItinerary(int passengerId, Itinerary itinerary, int serviceId, String departureStationName, String arrivingStationName)
    throws NoSuchPassengerIdException, NoSuchServiceIdException {

    Segment segment = new Segment(getService(serviceId), 0.0);

    segment.addStations(departureStationName, arrivingStationName);
    itinerary.addSegment(segment);
  }

  /**
   * Metodo simples para ler informacao de ficheiros sobre passageiros, servicos e itinerarios.
   * Invoca a funcao registerFromFields que analisa e trata a informacao recolhida.
   * As linhas sao da forma exemplificada a seguir. A notacao "..." significa
   * repeticao do formato.
   *
   * SERVICE|id|price|departure|stationName|...|departure|stationName
   * PASSENGER|name
   * ITINERARY|passengerId|date|serviceIDd/departureStationName/arrivingStationName...
   *
   * Exemplo:
   *
   * SERVICE|180|51.5|05:47|Porto - Campanhã|...|11:23|Faro
   * PASSENGER|Obi-Wan
   * ITINERARY|0|2017-10-18|690/Évora/Pinhal Novo|180/Pinhal Novo/Tunes|5904/Tunes/Silves
   *
   * @param filename
   *        ficheiro de input
   * @throws IOException
   * @throws NonUniquePassengerNameException
   *         caso ja exista um passageiro no registo com o nome para que se
   *         spretende alterar.
   * @throws NoSuchPassengerIdException
   *         caso nao exista nenhum passageiro com o id lido.
   * @throws NoSuchServiceIdException
   *         caso nao exista nenhum servico com o id lido.
   */
  void importFile(String filename) throws IOException, NonUniquePassengerNameException,
    NoSuchPassengerIdException, NoSuchServiceIdException {

      BufferedReader reader = new BufferedReader(new FileReader(filename));
      String line;
      while((line = reader.readLine()) != null) {
        String[] fields = line.split("\\|");
        registerFromFields(fields);
      }
      reader.close();

  }


  /**
   * Metodo simples que analisa e trata a informacao recolhida pela funcao importFile.
   *
   * @param fields
   *        array de strings atraves do qual a informacao vai ser tratada
   * @throws IOException
   * @throws NonUniquePassengerNameException
   *         caso ja exista um passageiro no registo com o nome para que se
   *         spretende alterar.
   * @throws NoSuchPassengerIdException
   *         caso nao exista nenhum passageiro com o id lido.
   * @throws NoSuchServiceIdException
   *         caso nao exista nenhum servico com o id lido.
   */
  void registerFromFields(String[] fields) throws IOException, NonUniquePassengerNameException,
    NoSuchPassengerIdException, NoSuchServiceIdException {

    if(fields[0].equals("SERVICE")) {
      addService(Integer.parseInt(fields[1]),Double.parseDouble(fields[2]));
      _hasChanged = true;
      for(int i = 3; i < fields.length-1; i = i+2)
        addStationToService(Integer.parseInt(fields[1]), fields[i+1], LocalTime.parse(fields[i]));
    }
    if(fields[0].equals("PASSENGER")) {
      registerPassenger(fields[1]);
      _hasChanged = true;
    }
    if(fields[0].equals("ITINERARY")) {
      addItinerary(Integer.parseInt(fields[1]), LocalDate.parse(fields[2]));
      _hasChanged = true;
      for(int i = 3; i < fields.length; i++) {
        String[] fields2 = fields[i].split("\\/");
        addSegmentToItinerary(Integer.parseInt(fields[1]), Integer.parseInt(fields2[0]), fields2[1], fields2[2]);
      }
      getPassenger(Integer.parseInt(fields[1])).checkCategoryChanges();
      getPassenger(Integer.parseInt(fields[1])).getCategory().setAcumulatedMinutes(getPassenger(Integer.parseInt(fields[1])).getLastAddedIt());
    }
  }

  /**
   * Metodo que verifica a existencia de itinerarios com igual servico de partida
   * e remove o mais tardio.
   *
   * @param itineraries
   *        lista de itinerarios que vai ser analisada.
   * @return uma lista tratada de itinerarios.
   */
  public List<Itinerary> removeDuplicateDepartureServiceItineraries(List<Itinerary> itineraries) {
    boolean flag = false;
    List<Itinerary> updated = new ArrayList<Itinerary>();

    for(Itinerary i1 : itineraries) {
      for(Itinerary i2 : updated) {
        if(i1.getDepartureServiceId() == i2.getDepartureServiceId()) {
          if(i1.getNumberOfServices() == 1)
            flag = false;
          if(i1.getItineraryArrival().isBefore(i2.getItineraryArrival()))
            flag = true;
        }
        else
          flag = false;
      }
      if(!flag)
        updated.add(i1);
    }
    return updated;
  }

  /**
   * Metodo recursivo utilizado para procurar todas as hipoteses de itinerarios
   * entre duas estacoes dadas como argumento.
   *
   * @param passengerId
   *        id do passageiro que procura o itinerario.
   * @param originalDepartureStation
   *        nome da estacao de origem.
   * @param departureStation
   *        nome da estacao de origem do estado atual da recursao.
   * @param arrivalStation
   *        nome da estacao de chegada.
   * @param departureDate
   *        data da viagem.
   * @param departureTime
   *        hora minima para a partida.
   * @param itineraries
   *        lista de itinerarios encontrados ao longo da recursao.
   * @param services
   *        lista de servicos que vao servir de base para os itinerarios.
   * @param serviceStations
   *        lista de estacoes do servico que esta a ser analisado no momento.
   * @param currentItinerary
   *        itinerario atual.
   * @param currentSegmentStations
   *        lista de estacoes do segmento atual do itinerario atual.
   * @throws NoSuchPassengerIdException
   *         caso nao exista nenhum passageiro com o id dado como argumento.
   * @throws NoSuchStationNameException
   *         caso nao exista nenhuma estacao com o nome dado como argumento.
   * @throws NoSuchServiceIdException
   *         caso nao exista nenhum servico com o id pretendido.
   * @return uma lista com todos os itinerarios possiveis.
   */
  public List<Itinerary> search(int passengerId, String originalDepartureStation, String departureStation, String arrivalStation,
                                LocalDate departureDate, LocalTime departureTime, List<Itinerary> itineraries,
                                List<Service> services, List<Station> serviceStations,
                                Itinerary currentItinerary, List<Station> currentSegmentStations) throws NoSuchPassengerIdException,
                                NoSuchStationNameException, NoSuchServiceIdException {

    Service s = services.get(0);
    if(s.getStationNames().contains(departureStation) && s.getStation(departureStation).getDeparture().isBefore(departureTime) == false
       && s.getStationNames().contains(arrivalStation) &&  s.getStation(originalDepartureStation).getDeparture().isBefore(s.getStation(arrivalStation).getDeparture())) {
        /*for direct services*/
        addSegmentToItinerary(passengerId, currentItinerary, s.getId(), departureStation, arrivalStation);
        itineraries.add(currentItinerary);
        currentItinerary.setId(itineraries.size());

        services.remove(0);

        /*if there are no more services to test, return*/
        if(services.size() == 0) {
          itineraries = removeDuplicateDepartureServiceItineraries(itineraries);
          itineraries.sort(_itineraryComparator);
          for(int i = 0; i < itineraries.size(); i++)
            itineraries.get(i).setId(i+1);
          _itineraryChoices = itineraries;
          return itineraries;
        }

        serviceStations.clear();
        currentSegmentStations.clear();
        serviceStations.addAll(services.get(0).getStations());


        return search(passengerId, originalDepartureStation, departureStation, arrivalStation, departureDate,
                      departureTime, itineraries, services, serviceStations,
                      new Itinerary(departureDate, getPassenger(passengerId)),
                      currentSegmentStations);
    }
    else if((s.getStationNames().contains(departureStation) && s.getStation(departureStation).getDeparture().isBefore(departureTime) == false &&
            !s.getStationNames().contains(arrivalStation) )|| s.getStationNames().contains(departureStation) &&
             s.getStation(departureStation).getDeparture().isBefore(departureTime) == false &&
             s.getStationNames().contains(arrivalStation) && !s.getStation(arrivalStation).getDeparture().isBefore(s.getStation(originalDepartureStation).getDeparture())) {
      /*non direct services*/

      /*if there's no itinerary, go back and check another service*/
      if(serviceStations.size() == 1) {
        services.remove(0);
        serviceStations.clear();
        serviceStations.addAll(services.get(0).getStations());

        return search(passengerId, originalDepartureStation, originalDepartureStation, arrivalStation, departureDate,
                      departureTime, itineraries, services, serviceStations,
                      currentItinerary, currentSegmentStations);
      }

      //adiciona a estacao ao segmento
      currentSegmentStations.add(s.getStation(departureStation));

      /*checks if there are other services containing the current departing
        station that are direct */
      List<Service> temp = services.subList(1,services.size());
      for(Service serv: temp) {
        if(serv.getStationNames().contains(departureStation) &&
           serv.getStationNames().contains(arrivalStation) &&
           currentSegmentStations.get(currentSegmentStations.size()-1).getDeparture().isBefore(serv.getStation(departureStation).getDeparture()) &&
           serv.getStation(departureStation).getDeparture().isBefore(serv.getStation(arrivalStation).getDeparture())) {

           if(currentSegmentStations.size() > 1) {
             addSegmentToItinerary(passengerId, currentItinerary, s.getId(), currentSegmentStations.get(0).getName(),
                                 currentSegmentStations.get(currentSegmentStations.size() - 1).getName());
             addSegmentToItinerary(passengerId, currentItinerary, serv.getId(), departureStation, arrivalStation);
             itineraries.add(currentItinerary);
             currentItinerary.setId(itineraries.size());
           }
        }
      }

      //passa para a proxima estacao
      serviceStations.remove(0);

      /*if there's no itinerary, go back and check another service*/
      if(serviceStations.size() == 0 && services.size() != 0) {
        services.remove(0);
        /*if there are no more services to test, return*/
        if(services.size() == 0) {
          itineraries = removeDuplicateDepartureServiceItineraries(itineraries);
          itineraries.sort(_itineraryComparator);
          for(int i = 0; i < itineraries.size(); i++)
            itineraries.get(i).setId(i+1);
          _itineraryChoices = itineraries;
          return itineraries;
        }
        serviceStations.clear();
        serviceStations.addAll(services.get(0).getStations());

        return search(passengerId, originalDepartureStation, originalDepartureStation, arrivalStation, departureDate,
                      departureTime, itineraries, services, serviceStations,
                      currentItinerary, currentSegmentStations);
      }

      return search(passengerId, originalDepartureStation, serviceStations.get(0).getName(), arrivalStation, departureDate,
                    departureTime, itineraries, services, serviceStations,
                    currentItinerary, currentSegmentStations);
    }
    else {
      services.remove(0);

      /*if there are no more services to test, return*/
      if(services.size() == 0) {
        itineraries = removeDuplicateDepartureServiceItineraries(itineraries);
        itineraries.sort(_itineraryComparator);
        for(int i = 0; i < itineraries.size(); i++)
          itineraries.get(i).setId(i+1);
        _itineraryChoices = itineraries;
        return itineraries;
      }

      serviceStations.clear();
      serviceStations.addAll(services.get(0).getStations());

      return search(passengerId, originalDepartureStation, departureStation, arrivalStation, departureDate,
                    departureTime, itineraries, services, serviceStations,
                    currentItinerary, currentSegmentStations);

    }
  }

  /**
   * Metodo utilizado para escolher e efetuar a compra do itinerario escolhido.
   *
   * @param passengerId
   *        id do passageiro que procura o itinerario.
   * @param itineraryNumber
   *        numero de ordem do itinerario escolhido.
   * @throws NoSuchPassengerIdException
   *         caso nao exista nenhum passageiro com o id dado como argumento.
   * @throws NoSuchServiceIdException
   *         caso nao exista nenhum itinerario com o numero de ordem pretendido.
   */
  public void commitItinerary(int passengerId, int itineraryNumber) throws NoSuchPassengerIdException,
                              NoSuchItineraryChoiceException {
    if(itineraryNumber != 0) {
      if(itineraryNumber < 1 || itineraryNumber > _itineraryChoices.size()) {
        _itineraryChoices.clear();
        throw new NoSuchItineraryChoiceException(passengerId, itineraryNumber);
      }
      else {
        getPassenger(passengerId).addItinerary(_itineraryChoices.get(itineraryNumber - 1));
        getPassenger(passengerId).checkCategoryChanges();
        getPassenger(passengerId).getCategory().setAcumulatedMinutes(_itineraryChoices.get(itineraryNumber - 1));
      }
    }
    else
      _itineraryChoices.clear();
  }

}
