package com.example.familyclient_julianasmith;

import android.media.metrics.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import Result.*;
import Models.*;

public class DataCache {
    private static DataCache instance = new DataCache();
    private DataCache(){}

    public static DataCache getInstance(){  return instance;    }

    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, List<Event>> personEvents;
    private Set<String> paternalAncestors;
    private Set<String> maternalAncestors;
    private String authToken;
    private String username;

    public void setPeople(PersonResult result) {
        this.people = populatePeople(result);
    }

    private Map<String, Person> populatePeople(PersonResult result){
        Map<String, Person> people = new HashMap<>();
        Person[] data = result.getData();
        for (Person person : data) {
            String personID = person.getPersonID();
            people.put(personID, person);
        }
        return people;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }

    public void setPersonEvents(Map<String, List<Event>> personEvents) {
        this.personEvents = personEvents;
    }

    public void setPaternalAncestors(Set<String> paternalAncestors) {
        this.paternalAncestors = paternalAncestors;
    }

    public void setMaternalAncestors(Set<String> maternalAncestors) {
        this.maternalAncestors = maternalAncestors;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserPersonID(String userPersonID) {
        this.userPersonID = userPersonID;
    }

    private String userPersonID;

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