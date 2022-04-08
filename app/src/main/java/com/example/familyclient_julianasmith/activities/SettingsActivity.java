package com.example.familyclient_julianasmith.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.View;
import android.widget.*;

import com.example.familyclient_julianasmith.DataCache;
import com.example.familyclient_julianasmith.R;

public class SettingsActivity extends AppCompatActivity {

    private Button logout;
    private Switch lifeStoryLinesToggle;
    private Switch spouseLinesToggle;
    private Switch familyTreeLinesToggle;
    private Switch malesToggle;
    private Switch femaleToggle;
    private Switch maternalToggle;
    private Switch paternalToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DataCache cache = DataCache.getInstance();
        logout = (Button) findViewById(R.id.logoutButton);
        lifeStoryLinesToggle = (Switch) findViewById(R.id.lifeStoryLinesToggle);
        spouseLinesToggle = (Switch) findViewById(R.id.spouseLinesToggle);
        familyTreeLinesToggle = (Switch) findViewById(R.id.familyTreeLinesToggle);
        malesToggle = (Switch) findViewById(R.id.maleEventsToggle);
        femaleToggle = (Switch) findViewById(R.id.femaleEventsToggle);
        maternalToggle = (Switch) findViewById(R.id.motherSideToggle);
        paternalToggle = (Switch) findViewById(R.id.fatherSideToggle);

        lifeStoryLinesToggle.setChecked(!cache.isLifeEventLinesFilter());
        spouseLinesToggle.setChecked(!cache.isSpouseLinesFilter());
        familyTreeLinesToggle.setChecked(!cache.isFamilyTreeLivesFilter());
        malesToggle.setChecked(!cache.isMaleFilter());
        femaleToggle.setChecked(!cache.isFemaleFilter());
        maternalToggle.setChecked(!cache.isMaternalFilter());
        paternalToggle.setChecked(!cache.isPaternalFilter());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("startFragment", "login");
                intent.putExtras(bundle);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        lifeStoryLinesToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cache.setLifeEventLinesFilter(!lifeStoryLinesToggle.isChecked());
            }
        });

        spouseLinesToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cache.setSpouseLinesFilter(!spouseLinesToggle.isChecked());
            }
        });

        familyTreeLinesToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cache.setFamilyTreeLivesFilter(!familyTreeLinesToggle.isChecked());
            }
        });

        malesToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cache.setMaleFilter(!malesToggle.isChecked());
            }
        });

        femaleToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cache.setFemaleFilter(!femaleToggle.isChecked());
            }
        });

        maternalToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cache.setMaternalFilter(!maternalToggle.isChecked());
            }
        });

        paternalToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cache.setPaternalFilter(!paternalToggle.isChecked());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("startFragment", "map");
                intent.putExtras(bundle);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}