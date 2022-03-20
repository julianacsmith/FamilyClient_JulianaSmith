import android.app.Person;
import android.media.metrics.Event;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataCache {
    private static DataCache instance = new DataCache();
    private DataCache(){}

    public static DataCache getInstance(){  return instance;    }

    Map<String, Person> people;
    Map<String, Event> events;
    Map<String, List<Event>> personEvents;
    Set<String> paternalAncestors;
    Set<String> maternalAncestors;

    Person getPersonByID(String personID){
        return people.get(personID);
    }

    Event getEventByID(String eventID){
        return events.get(eventID);
    }

    List<Event> getPersonEvents(String personID){
        return personEvents.get(personID);
    }
}