package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button toCitiesActivity = findViewById(R.id.toCityFinder);

        toCitiesActivity.setOnClickListener( click -> {
            Intent nextActivity = new Intent(this, CitiesMainActivity.class);
            startActivity(nextActivity); //make the transition
        });

    }
}

