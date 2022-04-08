package com.example.familyclient_julianasmith.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyclient_julianasmith.DataCache;
import com.example.familyclient_julianasmith.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.*;
import Models.*;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;
    private static List<Person> people;
    private static List<Event> events;
    private static searchAdapter adapter;
    private SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        DataCache cache = DataCache.getInstance();
        searchBar = (SearchView) findViewById(R.id.search_bar);

        people = new ArrayList<>(cache.getPeople().values());
        events = new ArrayList<>();
        if(cache.isFemaleFilter() && cache.isMaleFilter()){
            events = new ArrayList<>();
        } else if (cache.isFemaleFilter()){
            events = new ArrayList<>(getEventsFromPeople(cache.getMaleAncestors()).values());
        } else if (cache.isMaleFilter()) {
            events = new ArrayList<>(getEventsFromPeople(cache.getFemaleAncestors()).values());
        } else { // If nothing is selected
            events = new ArrayList<>(cache.getEvents().values());
        }

        List<Person> emptyPerson = new ArrayList<>();
        List<Event> emptyEvent = new ArrayList<>();
        adapter = new searchAdapter(emptyPerson, emptyEvent);
        recyclerView.setAdapter(adapter);

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(String text){
        ArrayList<Person> filteredPeople = new ArrayList<>();
        ArrayList<Event> filteredEvents = new ArrayList<>();

        for (Person person : people){
            if(person.getFirstName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) || person.getLastName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                filteredPeople.add(person);
            }
        }
        for (Event event : events){
            if(event.getCity().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                filteredEvents.add(event);
            }
        }
        if(filteredPeople.isEmpty() && filteredEvents.isEmpty()){
            Toast.makeText(SearchActivity.this, "No matches found!", Toast.LENGTH_LONG).show();
        } else {
            adapter.updateList(filteredPeople, filteredEvents);
        }
    }

    private class searchAdapter extends RecyclerView.Adapter<searchViewHolder>{
        private List<Person> people;
        private List<Event> events;

        searchAdapter(List<Person> people, List<Event> events){
            this.people = people;
            this.events = events;
        }

        public void updateList(List<Person> filteredPeople, List<Event> filteredEvents){
            people = filteredPeople;
            events = filteredEvents;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public searchViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view;

            if(i == PERSON_ITEM_VIEW_TYPE){
                view = getLayoutInflater().inflate(R.layout.person_item, viewGroup, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_item, viewGroup, false);
            }

            return new searchViewHolder(view, i);
        }

        @Override
        public void onBindViewHolder(@NonNull searchViewHolder searchViewHolder, int i) {
            if(i < people.size()){
                searchViewHolder.bind(people.get(i));
            } else {
                searchViewHolder.bind(events.get(i-people.size()));
            }
        }

        @Override
        public int getItemViewType(int position){
            return position < people.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @Override
        public int getItemCount() {
            return people.size() + events.size();
        }
    }

    private class searchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView eventType;
        private final TextView location;
        private final TextView personName;
        private final ImageView personGender;
        private final ImageView eventLocationImage;

        private final int viewType;
        private Person person;
        private Event event;

        public searchViewHolder(View view, int viewType){
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == PERSON_ITEM_VIEW_TYPE){
                eventType = null;
                location = null;
                personName = itemView.findViewById(R.id.personName);
                personGender = itemView.findViewById(R.id.searchGender);
                eventLocationImage = null;
            } else {
                eventType = itemView.findViewById(R.id.eventType);
                location = itemView.findViewById(R.id.eventLocation);
                personName = itemView.findViewById(R.id.eventPerson);
                personGender = null;
                eventLocationImage = itemView.findViewById(R.id.searchEventLocation);
            }
        }

        private void bind (Person person){
            this.person = person;
            String name = person.getFirstName() + " " + person.getLastName();
            personName.setText(name);
            if(person.getGender().equalsIgnoreCase("m")){
                personGender.setBackgroundResource(android.R.color.transparent);
                Drawable genderImage = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(android.R.color.holo_blue_dark).sizeDp(40);
                personGender.setImageDrawable(genderImage);
            } else {
                personGender.setBackgroundResource(android.R.color.transparent);
                Drawable genderImage = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(android.R.color.holo_red_light).sizeDp(40);
                personGender.setImageDrawable(genderImage);
            }
        }

        private void bind (Event event){
            this.event = event;
            eventType.setText(event.getEventType().toUpperCase(Locale.ROOT));
            location.setText(event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")");

            DataCache cache = DataCache.getInstance();
            Person associatedPerson = cache.getPersonByID(event.getPersonID());
            personName.setText(associatedPerson.getFirstName() + " " + associatedPerson.getLastName());

            if(event.getEventType().equalsIgnoreCase("birth")){
                eventLocationImage.setBackgroundResource(R.drawable.ic_birth_location);
            } else if (event.getEventType().equalsIgnoreCase("marriage")){
                eventLocationImage.setBackgroundResource(R.drawable.ic_marriage_location);
            } else if (event.getEventType().equalsIgnoreCase("death")){
                eventLocationImage.setBackgroundResource(R.drawable.ic_death_location);
            }
        }

        @Override
        public void onClick(View view) {
            Intent switchActivityIntent = null;
            if(viewType == PERSON_ITEM_VIEW_TYPE){
                switchActivityIntent = new Intent(SearchActivity.this, PersonActivity.class);
                switchActivityIntent.putExtra("PersonID", person.getPersonID());
            } else {
                switchActivityIntent = new Intent(SearchActivity.this, EventActivity.class);
                switchActivityIntent.putExtra("EventID", event.getEventID());
            }
            startActivity(switchActivityIntent);
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
}