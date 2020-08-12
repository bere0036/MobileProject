package com.example.mobileproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class CitiesSearchActivity extends AppCompatActivity {

    private EditText etLat, etLong;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cities_activity_search);

        SharedPreferences geoDataSourcePrefs = getSharedPreferences("CitiesLatitudeLongitude", Context.MODE_PRIVATE);

        etLat = findViewById(R.id.etLat);
        etLong = findViewById(R.id.etLong);
        searchButton = findViewById(R.id.city_search);

        etLat.setText(geoDataSourcePrefs.getString(CitiesActivity.LATITUDE, ""));
        etLong.setText(geoDataSourcePrefs.getString(CitiesActivity.LONGITUDE, ""));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent citiesActivity = new Intent(CitiesSearchActivity.this, CitiesActivity.class);
                citiesActivity.putExtra(CitiesActivity.LATITUDE, etLat.getText().toString());
                citiesActivity.putExtra(CitiesActivity.LONGITUDE, etLong.getText().toString());
                startActivity(citiesActivity);

                SharedPreferences.Editor edit = geoDataSourcePrefs.edit();
                edit.putString(CitiesActivity.LATITUDE, etLat.getText().toString());
                edit.putString(CitiesActivity.LONGITUDE, etLong.getText().toString());
                edit.commit();
            }
        });
    }
}

