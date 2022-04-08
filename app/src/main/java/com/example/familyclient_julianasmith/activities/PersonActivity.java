package com.example.familyclient_julianasmith.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.familyclient_julianasmith.DataCache;
import com.example.familyclient_julianasmith.R;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Models.*;


public class PersonActivity extends AppCompatActivity {
    private Person currPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataCache cache = DataCache.getInstance();
        Bundle extras = getIntent().getExtras();
        if(extras != null && !extras.isEmpty()){
            String personID = extras.getString("PersonID");
            currPerson = cache.getPersonByID(personID);
        }

        TextView firstName = (TextView) findViewById(R.id.personFirstName);
        TextView lastName = (TextView)  findViewById(R.id.personLastName);
        TextView gender = (TextView) findViewById(R.id.personGender);

        firstName.setText(currPerson.getFirstName());
        lastName.setText(currPerson.getLastName());
        String personGender = currPerson.getGender();
        if(personGender.equalsIgnoreCase("f")){
            gender.setText("Female");
        } else {
            gender.setText("Male");
        }
        ExpandableListView expandableListView = findViewById(R.id.expandableList);

        List<Person> family = new ArrayList<>(cache.getFamily(currPerson.getPersonID()).values());
        List<Event> lifeEvents = new ArrayList<>();

        if (currPerson.getPersonID().equals(cache.getUserPersonID())) {
            lifeEvents = new ArrayList<>(cache.getPersonEvents(currPerson.getPersonID()).values());
        } else if (cache.isFemaleFilter() && currPerson.getGender().equals("f") || cache.isMaleFilter() && currPerson.getGender().equals("m")){
            lifeEvents = new ArrayList<>();
        } else{ // If nothing is selected
            lifeEvents = new ArrayList<>(cache.getPersonEvents(currPerson.getPersonID()).values());
        }

        expandableListView.setAdapter(new ExpandableListAdapter(family, lifeEvents));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("startFragment", "map");
                intent.putExtras(bundle);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter{
        private static final int PERSON_POSITION = 1;
        private static final int EVENT_POSITION = 0;

        private final List<Person> family;
        private final List<Event> lifeEvents;

        ExpandableListAdapter(List<Person> family, List<Event> lifeEvents){
            this.family = family;
            this.lifeEvents = lifeEvents;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition){
                case PERSON_POSITION:
                    return family.size();
                case EVENT_POSITION:
                    return lifeEvents.size();
                default:
                    try {
                        throw new IllegalAccessException("Unrecognized group position");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
            }
            return groupPosition;
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch(groupPosition){
                case PERSON_POSITION:
                    return "Family";
                case EVENT_POSITION:
                    return "Life Events";
                default:
                    return null;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch(groupPosition){
                case PERSON_POSITION:
                    return family.get(childPosition);
                case EVENT_POSITION:
                    return lifeEvents.get(childPosition);
                default:
                    return null;
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
            if(view == null){
                view = getLayoutInflater().inflate(R.layout.expandable_headers, viewGroup, false);
            }

            TextView headerTitle = view.findViewById(R.id.expandableHeader);

            switch(groupPosition){
                case PERSON_POSITION:
                    headerTitle.setText("Family");
                    break;
                case EVENT_POSITION:
                    headerTitle.setText("Life Events");
                    break;
                default:
                    return view;
            }
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {
            View itemView;

            switch(groupPosition){
                case PERSON_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_item, viewGroup, false);
                    initializePersonView(itemView, childPosition);
                    break;
                case EVENT_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_item, viewGroup, false);
                    initializeEventView(itemView, childPosition);
                    break;
                default:
                    itemView = null;
            }
            return itemView;
        }

        private void initializePersonView(View personItemView, final int childPosition){
            TextView personName = personItemView.findViewById(R.id.personName);
            personName.setText(family.get(childPosition).getFirstName() + " " + family.get(childPosition).getLastName());

            ImageView personGender = personItemView.findViewById(R.id.searchGender);
            if(family.get(childPosition).getGender().equalsIgnoreCase("f")){
                personGender.setBackgroundResource(android.R.color.transparent);
                Drawable genderImage = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(android.R.color.holo_red_light).sizeDp(40);
                personGender.setImageDrawable(genderImage);
            } else {
                personGender.setBackgroundResource(android.R.color.transparent);
                Drawable genderImage = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(android.R.color.holo_blue_dark).sizeDp(40);
                personGender.setImageDrawable(genderImage);
            }

            DataCache cache = DataCache.getInstance();

            TextView relation = personItemView.findViewById(R.id.relation);
            if(family.size() >= 4){
                switch(childPosition){
                    case 0:
                        relation.setText("Mother");
                        break;
                    case 1:
                        relation.setText("Father");
                        break;
                    case 2:
                        relation.setText("Spouse");
                        break;
                    default:
                        relation.setText("Child");
                }
            } else if (family.size() == 2){
                switch (childPosition){
                    case 0:
                        if(currPerson.getMotherID() == null || !currPerson.getMotherID().equals(family.get(childPosition).getPersonID())){
                            relation.setText("Spouse");
                        } else {
                            relation.setText("Mother");
                        }
                        break;
                    default:
                        if(currPerson.getFatherID() == null || !currPerson.getFatherID().equals(family.get(childPosition).getPersonID())){
                            relation.setText("Child");
                        } else {
                            relation.setText("Father");
                        }
                }
            }

            personItemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Toast.makeText(PersonActivity.this, "You clicked on a person!", Toast.LENGTH_LONG).show();
                    Intent switchActivityIntent = new Intent(PersonActivity.this, PersonActivity.class);
                    switchActivityIntent.putExtra("PersonID", family.get(childPosition).getPersonID());
                    startActivity(switchActivityIntent);
                }
            });
        }

        private void initializeEventView(View eventItemView, final int childPosition){
            DataCache cache = DataCache.getInstance();
            Event currEvent = lifeEvents.get(childPosition);
            Person associatedPerson = cache.getPersonByID(currEvent.getPersonID());

            TextView eventType = eventItemView.findViewById(R.id.eventType);
            eventType.setText(currEvent.getEventType().toUpperCase(Locale.ROOT));

            TextView eventLocation = eventItemView.findViewById(R.id.eventLocation);
            eventLocation.setText(currEvent.getCity() + ", " + currEvent.getCountry() + " (" + currEvent.getYear() + ")");

            TextView eventPerson = eventItemView.findViewById(R.id.eventPerson);
            eventPerson.setText(associatedPerson.getFirstName() + " " + associatedPerson.getLastName());

            ImageView eventLocationImage = eventItemView.findViewById(R.id.searchEventLocation);
            eventLocationImage.setBackgroundResource(R.drawable.ic_default_location);

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent switchActivityIntent = new Intent(PersonActivity.this, EventActivity.class);
                    switchActivityIntent.putExtra("EventID", lifeEvents.get(childPosition).getEventID());
                    startActivity(switchActivityIntent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }
}