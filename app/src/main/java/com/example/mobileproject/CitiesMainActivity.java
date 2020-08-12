package com.example.mobileproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class CitiesMainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    Button citySearch, cityFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cities_activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //For NavigationDrawer:
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.city_open, R.string.city_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        citySearch = findViewById(R.id.city_search);
        cityFavorite = findViewById(R.id.city_favorite);

        citySearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchCities = new Intent(CitiesMainActivity.this, CitiesSearchActivity.class);
                startActivity(searchCities);
            }
        });
        cityFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent favCities = new Intent(CitiesMainActivity.this, CitiesFavouritesActivity.class);
                startActivity(favCities);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.citieshelpItem:
                    showHelpDialog();
                    break;
                case R.id.citiesaboutAPI:
                    openLink();
                    break;
                case R.id.citiesdonateItem:
                    showDonateDialog();
                    break;
            }
            return true;
        });
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(CitiesMainActivity.this)
                .setTitle(R.string.city_help)
                .setMessage(R.string.city_help_description)
                .show();
    }

    private void showDonateDialog() {
        EditText editTextDonateAmount = new EditText(CitiesMainActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        editTextDonateAmount.setHint("$$$");
        editTextDonateAmount.setLayoutParams(layoutParams);
        new AlertDialog.Builder(this)
                .setMessage(R.string.city_donate_message)
                .setTitle(R.string.city_donate)
                .setView(editTextDonateAmount)
                .setNegativeButton(R.string.city_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.city_thank_you, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CitiesMainActivity.this, R.string.city_thank_you, Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void openLink() {
        Intent aboutGeoLocation = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.geodatasource.com/web-service"));
        startActivity(aboutGeoLocation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.city_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toSoccerIcon:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.toLyricsIcon:
                startActivity(new Intent(this, LyricsMainActivity.class));
                break;
            case R.id.toDeezerIcon:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.citiesaboutProject:
                Toast.makeText(this, R.string.citiesAboutProject, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}