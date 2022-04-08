package com.example.familyclient_julianasmith;


import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import Result.*;
import Models.*;

public class DataCache {

    private Map<String, Person> people;
    private Map<String, Event> events;
    private Map<String, Person> paternalAncestors;
    private Map<String, Person> maternalAncestors;
    private Map<String, Person> maleAncestors;
    private Map<String, Person> femaleAncestors;
    private Map<String, Person> malePaternalAncestors;
    private Map<String, Person> femalePaternalAncestors;
    private Map<String, Person> maleMaternalAncestors;
    private Map<String, Person> femaleMaternalAncestors;
    private String authToken;
    private String username;
    private String userPersonID;
    private ServerProxy proxy;

    private boolean maleFilter;
    private boolean femaleFilter;
    private boolean maternalFilter;
    private boolean paternalFilter;
    private boolean spouseLinesFilter;
    private boolean lifeEventLinesFilter;
    private boolean familyTreeLivesFilter;

    private static DataCache instance = new DataCache();


    private DataCache(){
        maternalAncestors = new HashMap<>();
        paternalAncestors = new HashMap<>();
        femaleAncestors = new HashMap<>();
        maleAncestors = new HashMap<>();
        maleMaternalAncestors = new HashMap<>();
        malePaternalAncestors = new HashMap<>();
        femaleMaternalAncestors = new HashMap<>();
        femalePaternalAncestors = new HashMap<>();
        proxy = new ServerProxy();

        maleFilter = false;
        femaleFilter = false;
        maternalFilter = false;
        paternalFilter = false;
    }

    public static DataCache getInstance(){  return instance;    }

    /**
     * Gets all people associated with that user
     * @param result
     * @return
     */
    private Map<String, Person> populatePeople(PersonResult result){
        Map<String, Person> people = new HashMap<>();

        Person[] data = result.getData();
        for (Person person : data) {
            String personID = person.getPersonID();
            people.put(personID, person);
            if(person.getGender().equals("f")){
                femaleAncestors.put(personID, person);
            } else {
                maleAncestors.put(personID, person);
            }
        }
        return people;
    }

    /**
     * Gets all people associated with the user's mother's side
     * @param motherID
     */
    public void populateMaternalAncestors(String motherID){
        Person person = getPersonByID(motherID);
        maternalAncestors.put(person.getPersonID(), person);
        if(person.getMotherID() != null){
            String personMother = person.getMotherID();
            populateMaternalAncestors(personMother);
        }
        if (person.getFatherID() != null){
            String personFather = person.getFatherID();
            populateMaternalAncestors(personFather);
        }

        if(person.getGender().equals("f")){
            femaleMaternalAncestors.put(motherID, getPersonByID(motherID));
        } else {
            maleMaternalAncestors.put(motherID, getPersonByID(motherID));
        }
    }

    /**
     * Gets all ancestors associated with the user's father's side
     * @param fatherID
     */
    public void populatePaternalAncestors(String fatherID){
        Person person = getPersonByID(fatherID);
        paternalAncestors.put(fatherID, person);
        if(person.getMotherID() != null){
            String personMother = person.getMotherID();
            populatePaternalAncestors(personMother);
        }
        if (person.getFatherID() != null){
            String personFather = person.getFatherID();
            populatePaternalAncestors(personFather);
        }
        if(person.getGender().equals("f")){
            femalePaternalAncestors.put(fatherID, getPersonByID(fatherID));
        } else {
            malePaternalAncestors.put(fatherID, getPersonByID(fatherID));
        }
    }

    /**
     * Gets all events associated with a user after all events are gathered from Server
     * @param result
     */
    public void populateEvents(EventResult result){
        events = new HashMap<>();
        Event[] data = result.getData();
        for (Event event : data) {
            String personID = event.getPersonID();
            events.put(event.getEventID(), event);
        }
        System.out.println("STOP HERE!");
    }

    /**
     * Method to find all the family members associated with a given person's ID (father, mother, and children)
     * @param personID
     * @return Map<String, Person>
     */
    public Map<String, Person> getFamily(String personID){
        Map<String, Person> family = new HashMap<>();
        Person currPerson = getPersonByID(personID); // Get the current Person Object
        if(currPerson.getMotherID() != null && currPerson.getFatherID() != null) {
            family.put("mother", getPersonByID(currPerson.getMotherID())); // Put in the mother and father with appropriate keys
            family.put("father", getPersonByID(currPerson.getFatherID()));
        }

        if(currPerson.getSpouseID() != null){ // If the current peron has a spouse, include them in the family
            family.put("spouse", getPersonByID(currPerson.getSpouseID()));
        }

        int count = 0; // counter to make children keys unique
        for(Map.Entry<String,Person> person: people.entrySet()){ // Cycle through every person in the map of people
            Person thisPerson = person.getValue(); // Get the currently looked at person
            if(thisPerson.getMotherID() != null || thisPerson.getFatherID() != null) {
                if (thisPerson.getMotherID().equals(personID) || thisPerson.getFatherID().equals(personID)) { // If the given person is the mother or father of this person being looked at
                    family.put("child" + count, getPersonByID(thisPerson.getPersonID())); // Add the kid to the list
                    count++; // Increment the count to ensure unique keys
                }
            }
        }
        return family; // return the family map. All members can be accessed via the appropriate keys
    }

    public Map<Integer, Event> getPersonEvents(String personID){
        Map<Integer, Event> personEvents = new TreeMap<>();
        Person currPerson = getPersonByID(personID);
        for(Map.Entry<String, Event> event : events.entrySet()){
            Event currEvent = event.getValue();
            if(currEvent.getPersonID().equals(personID)){
                personEvents.put(currEvent.getYear(), currEvent);
            }
        }
        return personEvents;
    }

    public void setEvents(Map<String, Event> events) {
        this.events = events;
    }

    public Map<String, Event> getEvents(){
        return events;
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

    public String getUserPersonID() { return userPersonID; }

    public Person getPersonByID(String personID){
        return people.get(personID);
    }

    public void putUserInPaternal(String personID){
        paternalAncestors.put(personID, getPersonByID(personID));
    }

    public void putUserInMaternal(String personID){
        maternalAncestors.put(personID, getPersonByID(personID));
    }

    public Event getEventByID(String eventID){
        return events.get(eventID);
    }

    public void setPeople(PersonResult result) {
        this.people = populatePeople(result);
    }

    public Map<String, Person> getPeople() {
        return people;
    }

    public Map<String, Person> getPaternalAncestors() {
        return paternalAncestors;
    }

    public Map<String, Person> getMaternalAncestors() {
        return maternalAncestors;
    }

    public Map<String, Person> getMaleAncestors() {
        return maleAncestors;
    }

    public Map<String, Person> getFemaleAncestors() {
        return femaleAncestors;
    }

    public Map<String, Person> getMalePaternalAncestors() {
        return malePaternalAncestors;
    }

    public Map<String, Person> getFemalePaternalAncestors() {
        return femalePaternalAncestors;
    }

    public Map<String, Person> getMaleMaternalAncestors() {
        return maleMaternalAncestors;
    }

    public Map<String, Person> getFemaleMaternalAncestors() {
        return femaleMaternalAncestors;
    }

    public boolean isMaleFilter() {
        return maleFilter;
    }

    public void setMaleFilter(boolean maleFilter) {
        this.maleFilter = maleFilter;
    }

    public boolean isFemaleFilter() {
        return femaleFilter;
    }

    public void setFemaleFilter(boolean femaleFilter) {
        this.femaleFilter = femaleFilter;
    }

    public boolean isMaternalFilter() {
        return maternalFilter;
    }

    public void setMaternalFilter(boolean maternalFilter) {
        this.maternalFilter = maternalFilter;
    }

    public boolean isPaternalFilter() {
        return paternalFilter;
    }

    public void setPaternalFilter(boolean paternalFilter) {
        this.paternalFilter = paternalFilter;
    }

    public boolean isSpouseLinesFilter() {
        return spouseLinesFilter;
    }

    public void setSpouseLinesFilter(boolean spouseLinesFilter) {
        this.spouseLinesFilter = spouseLinesFilter;
    }

    public boolean isLifeEventLinesFilter() {
        return lifeEventLinesFilter;
    }

    public void setLifeEventLinesFilter(boolean lifeEventLinesFilter) {
        this.lifeEventLinesFilter = lifeEventLinesFilter;
    }

    public boolean isFamilyTreeLivesFilter() {
        return familyTreeLivesFilter;
    }

    public void setFamilyTreeLivesFilter(boolean familyTreeLivesFilter) {
        this.familyTreeLivesFilter = familyTreeLivesFilter;
    }

    public void addPersonToGender(String personID, String gender){
        if(gender.equalsIgnoreCase("f")){
            femaleAncestors.put(personID, getPersonByID(personID));
            femaleMaternalAncestors.put(personID, getPersonByID(personID));
            femalePaternalAncestors.put(personID, getPersonByID(personID));
        } else {
            maleAncestors.put(personID,getPersonByID(personID));
            maleMaternalAncestors.put(personID, getPersonByID(personID));
            malePaternalAncestors.put(personID, getPersonByID(personID));
        }
    }

    public void resetFilters(){
        maleFilter = false;
        femaleFilter = false;
        maternalFilter = false;
        paternalFilter = false;
        lifeEventLinesFilter = false;
        spouseLinesFilter = false;
        familyTreeLivesFilter = false;
    }
}