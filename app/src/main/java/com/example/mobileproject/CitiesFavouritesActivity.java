package com.example.mobileproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class CitiesFavouritesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    public static final String ACTIVITY_NAME = "FAVOURITES_ACTIVITY";

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
//    public static final String ARTIST = "ARTIST";
//    public static final String TITLE = "TITLE";
//    public static final String LYRICS = "LYRICS";


    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE= "LONGITUDE";

    public static final String COUNTRY= "COUNTRY";
    public static final String REGION= "REGION";
    public static final String CITY= "CITY";
    public static final String CURRENCY= "CURRENCY";

    SharedPreferences prefs = null;
    protected ArrayList<CitiesFavouritesActivity> elements = new ArrayList<>();
    private CitiesFavouritesActivity.MyListAdapter myAdapter;
    SQLiteDatabase db;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    String savedString;
    EditText favouritesSearchInput;
    ListView myList;
    Button favouritesSearchButton;
    String searchTerm;
    InputMethodManager imm;
    NavigationView navigationView;
    Intent searchGoogle;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cities_activity_favourites);

        setTitle(getResources().getString(R.string.citiesActivityName));

        //Getting the last performed search back as a hint
        prefs = getSharedPreferences("Last Lookup", Context.MODE_PRIVATE);
        savedString = prefs.getString("Last Lookup", "");
        favouritesSearchInput = findViewById(R.id.favouritesSearch);
        favouritesSearchInput.setHint(savedString);

        loadToolbar();


        //Show list of songs
        myList = findViewById(R.id.myListView);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        loadDataFromDatabase(false, null);

        myAdapter = new FavouritesActivity.MyListAdapter();
        myList.setAdapter( myAdapter);

        favouritesSearchButton = findViewById(R.id.favouritesSearchButton);
        favouritesSearchButton.setOnClickListener(click -> {
            //Saving user's most recent search to display again next time

            saveSharedPrefs(favouritesSearchInput.getText().toString());

            searchTerm = favouritesSearchInput.getText().toString();

            //If something was typed into the search bar
            if (searchTerm.length() > 0) {
                loadDataFromDatabase(true, searchTerm);
                myAdapter.notifyDataSetChanged();
            } else {
                //Nothing was typed in
                Snackbar.make(favouritesSearchButton, getResources().getString(R.string.lyricsFavouritesSnackbarString), Snackbar.LENGTH_SHORT).show();
                loadDataFromDatabase(false, null);
                myAdapter.notifyDataSetChanged();
            }

            //Hide Keyboard
            imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(favouritesSearchInput.getWindowToken(), 0);
        });

        myList.setOnItemClickListener( (list, view, position, id) -> {
            //Code for switching to fragment with selected item's lyrics
            //Creating and passing a bundle with that item's info
            Bundle dataToPass = new Bundle();
            dataToPass.putLong(ITEM_ID, id);

            dataToPass.putString(LATITUDE, elements.get(position).getLatitude());
            dataToPass.putString(LONGITUDE, elements.get(position).getLongitude());
            dataToPass.putString(CITY, elements.get(position).getCity());
            dataToPass.putString(COUNTRY, elements.get(position).getCountry());
            dataToPass.putString(REGION, elements.get(position).getRegion());
            dataToPass.putString(CURRENCY, elements.get(position).getCurrency());


            if(isTablet) {
                CitiesDetailsFragment dFragment = new CitiesDetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass );
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            } else {
                //isPhone
                Intent nextActivity = new Intent(this, CitiesEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });


        myList.setOnItemLongClickListener( (parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.lyricsFavouritesDeleteQuestion))
                    .setMessage(getResources().getString(R.string.favouritesDeleteLatitude) + elements.get(position).getLatitude()
                            + "\n" + getResources().getString(R.string.favouritesDeleteLongitude) + elements.get(position).getTitle())
                    .setPositiveButton(getResources().getString(R.string.lyricsFavouritesDeleteYes), (click, arg) -> {
                        db.delete(CitiesMyOpener.TABLE_NAME, CitiesMyOpener.COL_ID + "= ?", new String[] {Long.toString(elements.get(position).getId())});
                        elements.remove(position);
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton(getResources().getString(R.string.lyricsFavouritesDeleteNo), (click, arg) -> {  })
                    .create().show();

            return true;
        });
    }

    private void saveSharedPrefs(String stringToSave) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("Last Lookup", stringToSave);
        edit.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.city_menu, menu);
        return true;
    }

    public void loadToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.lyricsOpen, R.string.lyricsClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:


//            case R.id.toCityFinderButton:
//                Intent goToCity = new Intent(this, CityMainActivity.class);
//                startActivity(goToCity); //make the transition
//                break;
//
//            case R.id.toSoccerButton:
//                Intent goToSoccer = new Intent(this, SoccerMainActivity.class);
//                startActivity(goToSoccer); //make the transition
//                break;
//
//            case R.id.toDeezerButton:
//                Intent goToDeezer = new Intent(this, DeezerMainActivity.class);
//                startActivity(goToDeezer); //make the transition
//                break;

            case R.id.aboutProject:
                message = getResources().getString(R.string.citiesAboutProject);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        String message = null;

        switch(item.getItemId())
        {
            case R.id.donateItem:
                donateDialog();
                break;

            case R.id.helpItem:
                helpDialog();
                break;

            case R.id.aboutAPI:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lyricsovh.docs.apiary.io/"));
                startActivity(browserIntent);
                break;
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }


    public void helpDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.lyricsFavouritesInstructionsTitle))
                .setMessage(getResources().getString(R.string.citiesFavouritesInstructionsTitle))
                .setPositiveButton("OK", (click, arg) -> {}).create().show();
    }


    public void donateDialog() {
        EditText alertDialogEditText = new EditText(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.lyricsDonateTitle))
                .setMessage(getResources().getString(R.string.lyricsDonateBody))
                .setView(alertDialogEditText)
                .setPositiveButton(getResources().getString(R.string.lyricsThankYou), (click, arg) -> {})
                .setNegativeButton(getResources().getString(R.string.lyricsCancel), (click, arg) -> {})
                .create().show();
    }

    private void loadDataFromDatabase(boolean searching, String searchTerm) {
        //get a database connection"
        CitiesMyOpener dbOpener = new CitiesMyOpener(this);
        db = dbOpener.getWritableDatabase(); // Calls onCreate() if you've never built the table before, onUpgrade if the version here is newer

        String[] columns = {CitiesMyOpener.COL_ID, CitiesMyOpener.COL_LATITUDE, CitiesMyOpener.COL_LONGITUDE, CitiesMyOpener.COL_CITY , CitiesMyOpener.COL_COUNTRY, CitiesMyOpener.COL_CURRENCY};

        Cursor results;

        if (searchTerm!=null) {
            searchTerm = "%" + searchTerm + "%";
            elements.clear();
            String[] args = {searchTerm, searchTerm};
            results = db.query(false, CitiesMyOpener.TABLE_NAME, columns,
                    CitiesMyOpener.COL_LATITUDE + " LIKE ? OR " + CitiesMyOpener.COL_LONGITUDE + " LIKE ?" , args, null, null, null, null);
        } else {
            elements.clear();
            results = db.query(false, CitiesMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        }

        int latitudeColIndex = results.getColumnIndex(CitiesMyOpener.COL_LATITUDE);
        int longitudeColIndex = results.getColumnIndex(CitiesMyOpener.COL_LONGITUDE);
        int cityColIndex = results.getColumnIndex(CitiesMyOpener.COL_CITY);
        int regionColIndex = results.getColumnIndex(CitiesMyOpener.COL_REGION);
        int countryColIndex = results.getColumnIndex(CitiesMyOpener.COL_COUNTRY);
        int currencyColIndex = results.getColumnIndex(CitiesMyOpener.COL_CURRENCY);
        int idColIndex = results.getColumnIndex(CitiesMyOpener.COL_ID);

        while (results.moveToNext()) {
            String latitude = results.getString(latitudeColIndex);
            String longitude = results.getString(longitudeColIndex);
            String city = results.getString(cityColIndex);
            String region = results.getString(regionColIndex);
            String country = results.getString(countryColIndex);
            String currency = results.getString(currencyColIndex);
            int id = results.getInt( idColIndex);

            elements.add(new LyricsSavedFavourite(latitude, longitude, city, region, country, currency, id));
        }

        if (elements.size() == 1)
            Toast.makeText(this, getResources().getString(R.string.citiesFavouritesFoundOneItem), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, getResources().getString(R.string.lyricsFavouritesFoundManySongs1) + elements.size() +
                    getResources().getString(R.string.lyricsFavouritesFoundManySongs2), Toast.LENGTH_SHORT).show();
    }

    class MyListAdapter extends BaseAdapter implements ListAdapter {

        public int getCount() { return elements.size(); }

        public Object getItem(int position) { return elements.get(position); }

        public long getItemId(int position) { return position; }

        public View getView(int position, View old, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            //make a new row:
            View newView = inflater.inflate(R.layout.citis_favourite_individual, parent, false);
            //set what the text should be for this row:
            TextView latitudeTextView = newView.findViewById(R.id.latitude);
            TextView longitudeTextView = newView.findViewById(R.id.longitudenumber);

            latitudeTextView.setText(elements.get(position).getLatitude());
            longitudeTextView.setText(elements.get(position).getLongitude());

            //return it to be put in the table
            return newView;
        }
    }

}
