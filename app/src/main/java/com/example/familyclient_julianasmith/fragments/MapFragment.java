package com.example.familyclient_julianasmith.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.familyclient_julianasmith.DataCache;
import com.example.familyclient_julianasmith.R;
import com.example.familyclient_julianasmith.activities.EventActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import Models.*;
import java.util.*;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private MapFragment.Listener listener;
    private List<Marker> markers;
    private GoogleMap gMap;
    private List<Polyline> spousePolylines;
    private List<Polyline> lifeEventPolylines;
    private List<Polyline> familyTreePolylines;

    public interface Listener{
        void notifySwitch(String activity);
    }

    public void registerListener(MapFragment.Listener listener) {
        this.listener = listener;
    }

    private String eventID;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args= new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Bundle data = getArguments();
        if(data != null){
            eventID = data.getString("eventID");
        } else {
            eventID = null;
        }
        gMap = null;
        markers = null;

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                DataCache cache = DataCache.getInstance();
                Map<String, Event> events = cache.getEvents();
                markers = new ArrayList<>();

                generateMarkers(googleMap, events); // Generates all the markers for all specified events

                if(eventID != null){
                    Event thisEvent = cache.getEventByID(eventID);
                    Person eventPerson = cache.getPersonByID(thisEvent.getPersonID());
                    Map<Integer, Event> tempEvents = cache.getPersonEvents(eventPerson.getPersonID());
                    events = new HashMap<>();

                    int count = 0;
                    for(Map.Entry<Integer, Event> event: tempEvents.entrySet()){
                        events.put("Event"+count, event.getValue());
                        count++;
                    }

                    if(!cache.isSpouseLinesFilter()){
                        drawSpouseLines(events);
                    }
                    if(!cache.isLifeEventLinesFilter()){
                        drawLifeStoryLines(events);
                    }
                    if(!cache.isFamilyTreeLivesFilter()){
                        drawFamilyTreeLines(events);
                    }

                    LatLng latlng = new LatLng(thisEvent.getLatitude(), thisEvent.getLongitude());
                    CameraUpdate eventLocation = CameraUpdateFactory.newLatLngZoom(latlng, 8.0f);
                    googleMap.moveCamera(eventLocation);

                    editInfoText(getEventDescription(thisEvent));
                    ImageView imageView = (ImageView) getView().findViewById(R.id.genderIcon);
                    if(eventPerson.getGender().equalsIgnoreCase("f")){
                        imageView.setBackgroundResource(android.R.color.transparent);
                        Drawable genderImage = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                                colorRes(android.R.color.holo_red_light).sizeDp(40);
                        imageView.setImageDrawable(genderImage);
                    } else {
                        imageView.setBackgroundResource(android.R.color.transparent);
                        Drawable genderImage = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                                colorRes(android.R.color.holo_blue_dark).sizeDp(40);
                        imageView.setImageDrawable(genderImage);
                    }
                    eventID = null;
                }

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        clearAllPolylines();
                        String eventID = (String) marker.getTag();
                        Event event = cache.getEventByID(eventID);

                        if(event == null){
                            return false;
                        }

                        Person person = cache.getPersonByID(event.getPersonID());
                        String gender = person.getGender();
                        editInfoText(getEventDescription(event));

                        Map<Integer, Event> tempEvents = cache.getPersonEvents(person.getPersonID());
                        Map<String, Event> personEvents = new HashMap<>();

                        int count = 0;
                        for(Map.Entry<Integer, Event> eventEntry: tempEvents.entrySet()){
                            personEvents.put("Event"+count, eventEntry.getValue());
                            count++;
                        }

                        if(!cache.isSpouseLinesFilter()){
                            drawSpouseLines(personEvents);
                        }
                        if(!cache.isLifeEventLinesFilter()){
                            drawLifeStoryLines(personEvents);
                        }
                        if(!cache.isFamilyTreeLivesFilter()){
                            drawFamilyTreeLines(personEvents);
                        }

                        ImageView genderIcon = (ImageView) getView().findViewById(R.id.genderIcon);
                        if(gender.equalsIgnoreCase("f")){
                            genderIcon.setBackgroundResource(android.R.color.transparent);
                            Drawable genderImage = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                                    colorRes(android.R.color.holo_red_light).sizeDp(40);
                            genderIcon.setImageDrawable(genderImage);
                        } else {
                            genderIcon.setBackgroundResource(android.R.color.transparent);
                            Drawable genderImage = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                                    colorRes(android.R.color.holo_blue_dark).sizeDp(40);
                            genderIcon.setImageDrawable(genderImage);
                        }

                        LinearLayout eventDetails = (LinearLayout) getView().findViewById(R.id.eventDetails);
                        eventDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                listener.notifySwitch(person.getPersonID());
                            }
                        });
                        return true;
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DataCache cache = DataCache.getInstance();
        Map<String, Event> events = null;
        spousePolylines = new ArrayList<>();
        lifeEventPolylines = new ArrayList<>();
        familyTreePolylines = new ArrayList<>();

        // If all genders and maternal are selected=
        if ((cache.isMaleFilter() && cache.isFemaleFilter()) || (cache.isMaternalFilter() && cache.isPaternalFilter())) { // If both genders are selected but no side of the family
            events = addOnlyUserAndSpouse();
        } else if(cache.isMaleFilter()){ // If male is selected
                if(cache.isMaternalFilter() && !cache.isPaternalFilter()){ // If only maternal males
                    events = getEventsFromPeople(cache.getFemalePaternalAncestors());
                } else if (cache.isPaternalFilter() && !cache.isMaternalFilter()){ // If only paternal males
                    events = getEventsFromPeople(cache.getFemaleMaternalAncestors());
                } else { // If no side
                    events = getEventsFromPeople(cache.getFemaleAncestors());
                }
        } else if (cache.isFemaleFilter()){ // If females are selected
            if(cache.isMaternalFilter() && !cache.isPaternalFilter()){ // If only maternal males
                events = getEventsFromPeople(cache.getMalePaternalAncestors());
            } else if (cache.isPaternalFilter() && !cache.isMaternalFilter()){ // If only paternal males
                events = getEventsFromPeople(cache.getMaleMaternalAncestors());
            } else { // If neither side or both sides selected
                events = getEventsFromPeople(cache.getMaleAncestors());
            }
        } else if(cache.isPaternalFilter()) { // If only paternal
            events = getEventsFromPeople(cache.getMaternalAncestors());
        } else if(cache.isMaternalFilter()) { // If only maternal
            events = getEventsFromPeople(cache.getPaternalAncestors());
        } else{ // If nothing is selected
            events = cache.getEvents();
        }

        if(gMap != null){
            gMap.clear();
            if(cache.isSpouseLinesFilter()){
                clearSpouseLines();
            }
            if(cache.isLifeEventLinesFilter()){
                clearLifeEventLInes();
            }
            if(cache.isFamilyTreeLivesFilter()){
                clearFamilyTreeLines();
            }
            if(events != null) {
                generateMarkers(gMap, events);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity currActivity = getActivity();
        assert currActivity != null;
        if(currActivity.getClass().equals(EventActivity.class)){
            menu.clear();
        } else {
            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.options_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                listener.notifySwitch("search");
                break;
            case R.id.settings:
                listener.notifySwitch("settings");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<Marker> getMarkers(){return markers;}

    public void editInfoText(String newOutput){
        TextView textView = (TextView) Objects.requireNonNull(getView()).findViewById(R.id.MarkerInfo);
        textView.setText(newOutput);
    }

    /**
     * Gets the String version of an event
     * @param event
     * @return String
     */
    public String getEventDescription(Event event){
        DataCache cache = DataCache.getInstance();
        Person person = cache.getPersonByID(event.getPersonID());
        String firstName = person.getFirstName();
        String lastName = person.getLastName();
        String gender = person.getGender();
        String eventType = event.getEventType().toUpperCase(Locale.ROOT);
        String city = event.getCity();
        String country = event.getCountry();
        int year = event.getYear();

        return firstName + " " + lastName + "\n" + eventType + ": " + city + ", " + country + " (" + year + ")";
    }

    /**
     * Generates all the markers given a filtered map of events and a map
     * @param googleMap
     * @param events
     */
    public void generateMarkers(GoogleMap googleMap, Map<String, Event> events){
        DataCache cache = DataCache.getInstance();
        for(Map.Entry<String,Event> event: events.entrySet()) {
            Marker marker = null;
            Event currEvent = event.getValue();
            String eventType = currEvent.getEventType();
            Person person = cache.getPersonByID(currEvent.getPersonID());
            LatLng currEventLocation = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            switch (eventType.toLowerCase(Locale.ROOT)) {
                case "birth":
                    marker = googleMap.addMarker(new MarkerOptions().position(currEventLocation).title(currEvent.getEventID()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    marker.setTag(currEvent.getEventID());
                    break;
                case "marriage":
                    marker = googleMap.addMarker(new MarkerOptions().position(currEventLocation).title(currEvent.getEventID()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    marker.setTag(currEvent.getEventID());
                    break;
                case "death":
                    marker = googleMap.addMarker(new MarkerOptions().position(currEventLocation).title(currEvent.getEventID()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                    marker.setTag(currEvent.getEventID());
                    break;
                default:
                    marker = googleMap.addMarker(new MarkerOptions().position(currEventLocation).title(currEvent.getEventID()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    marker.setTag(currEvent.getEventID());
                    break;
            }
            markers.add(marker);
        }
    }

    public Map<String, Event> getEventsFromPeople(Map<String, Person> specificAncestors){
        DataCache cache = DataCache.getInstance();
        Map<String, Event> events = new HashMap<>();
        for(Map.Entry<String,Person> person: specificAncestors.entrySet()){
            Person thisPerson = person.getValue();
            Map<Integer, Event> personEvents = cache.getPersonEvents(thisPerson.getPersonID());
            for(Map.Entry<Integer,Event> event: personEvents.entrySet()){
                events.put(event.getValue().getEventID(), event.getValue());
            }
        }
        return events;
    }

    public void drawSpouseLines(Map<String, Event> events){
        DataCache cache = DataCache.getInstance();
        for(Map.Entry<String,Event> event: events.entrySet()){
            Event thisEvent = event.getValue();
            LatLng thisEventLatLng = new LatLng(thisEvent.getLatitude(), thisEvent.getLongitude());
            Person thisPerson = cache.getPersonByID(thisEvent.getPersonID());
            if(thisPerson.getSpouseID() != null) {
                Person thisPersonSpouse = cache.getPersonByID(thisPerson.getSpouseID());
                if(!cache.isMaleFilter() && thisPersonSpouse.getGender().equals("m") || !cache.isFemaleFilter() && thisPersonSpouse.getGender().equals("f")) {
                    List<Event> spouseEvents = new ArrayList<>(cache.getPersonEvents(thisPersonSpouse.getPersonID()).values());
                    LatLng spouseBirthLatLng = new LatLng(spouseEvents.get(0).getLatitude(), spouseEvents.get(0).getLongitude());
                    spousePolylines.add(this.gMap.addPolyline((new PolylineOptions()).add(thisEventLatLng, spouseBirthLatLng, thisEventLatLng, thisEventLatLng).width(8.0f).color(Color.RED)));
                }
            }
        }
    }

    public void drawLifeStoryLines(Map<String, Event> events){
        DataCache cache = DataCache.getInstance();
        for(Map.Entry<String,Event> event: events.entrySet()){
            List<LatLng> eventLocations = new ArrayList<>();
            Event thisEvent = event.getValue();
            Person thisPerson = cache.getPersonByID(thisEvent.getPersonID());
            List<Event> lifeEvents = new ArrayList<>(cache.getPersonEvents(thisPerson.getPersonID()).values());
            for(Event lifeEvent: lifeEvents){
                eventLocations.add(new LatLng(lifeEvent.getLatitude(), lifeEvent.getLongitude()));
            }
            for(int i = 0; i < eventLocations.size()-1; i++) {
                LatLng currLocation = eventLocations.get(i);
                LatLng nextLocation = eventLocations.get(i+1);
                lifeEventPolylines.add(this.gMap.addPolyline((new PolylineOptions()).add(currLocation, nextLocation).width(8.0f).color(Color.BLUE)));
            }
        }
    }

    public void drawFamilyTreeLines(Map<String, Event> events){
        DataCache cache = DataCache.getInstance();
        for(Map.Entry<String,Event> event: events.entrySet()) { // for each event in events
            Event thisEvent = event.getValue();
            LatLng thisEventLocation = new LatLng(thisEvent.getLatitude(), thisEvent.getLongitude()); // get latlng for event
            Person thisPerson = cache.getPersonByID(thisEvent.getPersonID()); // get the person associated with the event
            if(thisPerson.getFatherID() != null && !cache.isMaleFilter()){ // get the father of the person associated with the event
                Person currPersonFather = cache.getPersonByID(thisPerson.getFatherID());
                List<Event> fatherEvents = new ArrayList<>(cache.getPersonEvents(currPersonFather.getPersonID()).values());
                // Draw a line between the event given and recursively call draw tree on the father
                familyTreePolylines.add(this.gMap.addPolyline((new PolylineOptions()).add(thisEventLocation, drawTree(fatherEvents.get(0), 8.0f/2)).width(8.0f).color(Color.GREEN)));
            }
            if(thisPerson.getMotherID() != null && !cache.isFemaleFilter()){ // If these a mother
                Person currPersonMother = cache.getPersonByID(thisPerson.getMotherID()); // get the mother
                List<Event> motherEvents = new ArrayList<>(cache.getPersonEvents(currPersonMother.getPersonID()).values()); // Get the mother's events
                // Draw a line between the event given and recursively call draw tree on the father
                familyTreePolylines.add(this.gMap.addPolyline((new PolylineOptions()).add(thisEventLocation, drawTree(motherEvents.get(0), 8.0f/2)).width(8.0f).color(Color.GREEN)));
            }
        }

    }

    /**
     * recursively draw the family tree lines. Takes in an event and a width.
     */
    private LatLng drawTree(Event birthEvent, float lineSize){
        //If the given person has a father,
        DataCache cache = DataCache.getInstance();
        Person currPerson = cache.getPersonByID(birthEvent.getPersonID());
        LatLng birthLocation = new LatLng(birthEvent.getLatitude(), birthEvent.getLongitude());
        if(currPerson.getFatherID() != null && !cache.isMaleFilter()) {
            // Get the person's father's birth
            Person currPersonFather = cache.getPersonByID(currPerson.getFatherID());
            List<Event> fatherEvents = new ArrayList<>(cache.getPersonEvents(currPersonFather.getPersonID()).values());

            // Draw a line between the event given and recursively call draw tree on the father
            familyTreePolylines.add(this.gMap.addPolyline((new PolylineOptions()).add(birthLocation, drawTree(fatherEvents.get(0), lineSize/2)).width(lineSize).color(Color.GREEN)));
        }
        if(currPerson.getMotherID() != null && !cache.isFemaleFilter()) {
            // Get the person's father's birth
            Person currPersonMother = cache.getPersonByID(currPerson.getMotherID());
            List<Event> motherEvents = new ArrayList<>(cache.getPersonEvents(currPersonMother.getPersonID()).values());

            // Draw a line between the event given and recursively call draw tree on the father
            familyTreePolylines.add(this.gMap.addPolyline((new PolylineOptions()).add(birthLocation, drawTree(motherEvents.get(0), lineSize/2)).width(lineSize).color(Color.GREEN)));
        }
        return birthLocation;
    }

    /**
     * Clears all the polylines drawn
     */
    public void clearAllPolylines(){
        for(Polyline polyline : spousePolylines){
            polyline.remove();
        }
        spousePolylines.clear();

        for(Polyline line : lifeEventPolylines){
            line.remove();
        }
        lifeEventPolylines.clear();

        for(Polyline line : familyTreePolylines){
            line.remove();
        }
        familyTreePolylines.clear();
    }

    public void clearSpouseLines(){
        for(Polyline polyline : spousePolylines){
            polyline.remove();
        }
        spousePolylines.clear();
    }

    public void clearLifeEventLInes(){
        for(Polyline line : lifeEventPolylines){
            line.remove();
        }
        lifeEventPolylines.clear();
    }

    public void clearFamilyTreeLines(){
        for(Polyline line : familyTreePolylines){
            line.remove();
        }
        familyTreePolylines.clear();
    }

    public Map<String, Event> addOnlyUserAndSpouse(){
        DataCache cache = DataCache.getInstance();
        Map<String, Event> output = new HashMap<>();
        Map<Integer, Event> userEvents = cache.getPersonEvents(cache.getUserPersonID());
        for(Map.Entry<Integer, Event> event: userEvents.entrySet()){
            output.put(event.getValue().getEventID(), event.getValue());
        }
        Person user = cache.getPersonByID(cache.getUserPersonID());
        Map<Integer, Event> userSpouseEvents = cache.getPersonEvents(user.getPersonID());
        for(Map.Entry<Integer, Event> event: userSpouseEvents.entrySet()){
            output.put(event.getValue().getEventID(), event.getValue());
        }

        return output;
    }

}