package com.example.familyclient_julianasmith.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.familyclient_julianasmith.R;
import com.example.familyclient_julianasmith.fragments.LoginFragment;
import com.example.familyclient_julianasmith.fragments.MapFragment;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.loginMapLayout);

        if(fragment == null) {
            fragment = createFirstFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.loginMapLayout, fragment)
                    .commit();
        } else {
            // If the fragment is not null, the MainActivity was destroyed and recreated
            // so we need to reset the listener to the new instance of the fragment
            if(fragment instanceof LoginFragment) {
                ((LoginFragment) fragment).registerListener(this);
            }
        }
    }
    private Fragment createFirstFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    public void notifyDone() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment mapFragment = new MapFragment();

        fragmentManager.beginTransaction().replace(R.id.loginMapLayout, mapFragment).commit();
    }
}
