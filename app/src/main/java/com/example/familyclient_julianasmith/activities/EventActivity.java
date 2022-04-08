package com.example.familyclient_julianasmith.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.familyclient_julianasmith.DataCache;
import com.example.familyclient_julianasmith.R;
import com.example.familyclient_julianasmith.fragments.MapFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.List;
import java.util.Locale;

import Models.*;

public class EventActivity extends AppCompatActivity {
    private Event currEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventActivity.this.invalidateOptionsMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        MapFragment fragment = (MapFragment) fragmentManager.findFragmentById(R.id.loginMapLayout);

        DataCache cache = DataCache.getInstance();
        Bundle extras = getIntent().getExtras();
        if(!extras.isEmpty()){
            String eventID = extras.getString("EventID");
            currEvent = cache.getEventByID(eventID);
        }

        if(fragment == null) {
            fragment = createFirstFragment();

            Bundle data = new Bundle();
            data.putString("eventID", currEvent.getEventID());

            fragment.setArguments(data);
            fragmentManager.beginTransaction().add(R.id.loginMapLayout, fragment).commit();
        }

    }

    private String getEventDescription(){
        DataCache cache = DataCache.getInstance();

        Person person = cache.getPersonByID(currEvent.getPersonID());
        String firstName = person.getFirstName();
        String lastName = person.getLastName();
        String gender = person.getGender();
        String eventType = currEvent.getEventType().toUpperCase(Locale.ROOT);
        String city = currEvent.getCity();
        String country = currEvent.getCountry();
        int year = currEvent.getYear();

        String output = firstName + " " + lastName + "\n" + eventType + ": " + city + ", " + country + " (" + year + ")";
        return output;
    }

    private MapFragment createFirstFragment() {
        MapFragment fragment = new MapFragment();
        return fragment;
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
}