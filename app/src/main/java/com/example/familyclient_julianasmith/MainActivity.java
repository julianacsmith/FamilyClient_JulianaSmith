package com.example.familyclient_julianasmith;

import android.icu.util.Freezable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import android.view.*;
import android.os.*;


import Request.LoginRequest;
import Request.RegisterRequest;
import Result.*;
import Models.*;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment loginFragment = fragmentManager.findFragmentById(R.id.loginFragment);
        if(loginFragment == null) {
            loginFragment = createFirstFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.loginFragment, loginFragment)
                    .commit();
        } else {
            // If the fragment is not null, the MainActivity was destroyed and recreated
            // so we need to reset the listener to the new instance of the fragment
            if(loginFragment instanceof LoginFragment) {
                ((LoginFragment) loginFragment).registerListener(this);
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

    }
}
