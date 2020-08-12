package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toLyricsActivity = findViewById(R.id.toLyricsMain);

        toLyricsActivity.setOnClickListener(click -> {
            Intent nextActivity = new Intent(this, LyricsMainActivity.class);
            startActivity(nextActivity); //make the transition
        });

        Button toCitiesActivity = findViewById(R.id.toCityFinder);

        toCitiesActivity.setOnClickListener(click -> {
            Intent nextActivity = new Intent(this, CitiesMainActivity.class);
            startActivity(nextActivity); //make the transition
        });

    }
}