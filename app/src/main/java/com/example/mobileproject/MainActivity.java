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


        Button toSoccerActivity = findViewById(R.id.toSoccer);

        Button toLyricsMain = findViewById(R.id.toLyricsMain);

        toLyricsMain.setOnClickListener( click -> {
            Intent nextActivity = new Intent(this, LyricsMainActivity.class);
            startActivity(nextActivity); //make the transition
        });



        toSoccerActivity.setOnClickListener( click -> {
            Intent soccerActivity = new Intent (this, SoccerMainActivity.class);
            startActivity(soccerActivity); // transition to soccer high lights
        } );

        Button toDeezerMain = findViewById(R.id.toSongAPI);

        toDeezerMain.setOnClickListener( click -> {
            Intent nextActivity = new Intent(this, DeezerSongSearchMain.class);
            startActivity(nextActivity); //make the transition
        });

        Button toCityActivity = findViewById(R.id.toCityFinder);

         toCityActivity.setOnClickListener( click -> {
             Intent nextActivity = new Intent(this, CityMainActivity.class);
             startActivity(nextActivity); //make the transition
         });


    }
}