package com.example.mobileproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toDeezerMain = findViewById(R.id.toSongAPI);

        toDeezerMain.setOnClickListener( click -> {
            Intent nextActivity = new Intent(this, DeezerSongSearchMain.class);
            startActivity(nextActivity); //make the transition


        });

//        Button toCityActivity = findViewById(R.id.toCityFinder);
//
//        toCityActivity.setOnClickListener( click -> {
//            Intent nextActivity = new Intent(this, CityMainActivity.class);
//            startActivity(nextActivity); //make the transition
//        });
//
//        Button toDeezerActivity = findViewById(R.id.toSongAPI);
//
//        toCityActivity.setOnClickListener( click -> {
//            Intent nextActivity = new Intent(this, DeezerMainActivity.class);
//            startActivity(nextActivity); //make the transition
//        });
//
//        Button toSoccerActivity = findViewById(R.id.toSoccer);
//
//        toCityActivity.setOnClickListener( click -> {
//            Intent nextActivity = new Intent(this, SoccerMainActivity.class);
//            startActivity(nextActivity); //make the transition
//        });


    }
}