package com.example.familyclient_julianasmith.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.familyclient_julianasmith.R;
import com.example.familyclient_julianasmith.fragments.LoginFragment;
import com.example.familyclient_julianasmith.fragments.MapFragment;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


public class MainActivity extends AppCompatActivity implements LoginFragment.Listener, MapFragment.Listener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Iconify.with(new FontAwesomeModule());

        Bundle bundle = getIntent().getExtras();
        Bundle filters = new Bundle();
        String startFragment = "login";
        if(bundle != null) {
            if(bundle.keySet().size() > 1){
                filters.putString("maleFilter", bundle.getString("maleFilter"));
                filters.putString("femaleFilter", bundle.getString("femaleFilter"));
                filters.putString("maternalFilter", bundle.getString("maternalFilter"));
                filters.putString("paternalFilter", bundle.getString("paternalFilter"));
            }
            startFragment = bundle.getString("startFragment");
        }

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.loginMapLayout);

        if(fragment == null) {
            fragment = createFirstFragment(startFragment);
            fragmentManager.beginTransaction()
                    .add(R.id.loginMapLayout, fragment)
                    .commit();
            if(fragment.getClass().equals(MapFragment.class)){
                fragment.setArguments(filters);
            }
        } else {
            // If the fragment is not null, the MainActivity was destroyed and recreated
            // so we need to reset the listener to the new instance of the fragment
            if(fragment instanceof LoginFragment) {
                ((LoginFragment) fragment).registerListener(this);

            }
        }
    }

    private Fragment createFirstFragment(String startFragment) {
        Fragment fragment = null;
        if(startFragment.equalsIgnoreCase("login")) {
            fragment = new LoginFragment();
            ((LoginFragment) fragment).registerListener(this);
        } else {
            fragment = new MapFragment();
            ((MapFragment) fragment).registerListener(this);
        }
        return fragment;
    }

    @Override
    public void notifyDone() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment mapFragment = new MapFragment();

        fragmentManager.beginTransaction().replace(R.id.loginMapLayout, mapFragment).commit();
        ((MapFragment) mapFragment).registerListener(this);
    }

    @Override
    public void notifySwitch(String activity) {
        Intent switchActivityIntent = null;
        if(activity.equalsIgnoreCase("settings")){
            switchActivityIntent = new Intent(this, SettingsActivity.class);
        } else if (activity.equalsIgnoreCase("search")){
            switchActivityIntent = new Intent(this, SearchActivity.class);
        } else {
            switchActivityIntent = new Intent(this, PersonActivity.class);
            switchActivityIntent.putExtra("PersonID", activity);
        }
        startActivity(switchActivityIntent);
    }
}
