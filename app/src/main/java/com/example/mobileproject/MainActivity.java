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

        Button toLyricsActivity = findViewById(R.id.toLyricsMain);

        toLyricsActivity.setOnClickListener( click -> {
            Intent nextActivity = new Intent(this, LyricsMainActivity.class);
            startActivity(nextActivity); //make the transition
        });

    }
}