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
    public static final String ARTIST = "ARTIST";
    public static final String TITLE = "TITLE";
    public static final String LYRICS = "LYRICS";

    SharedPreferences prefs = null;
    protected ArrayList<LyricsSavedFavourite> elements = new ArrayList<>();
    private LyricsFavouritesActivity.MyListAdapter myAdapter;
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
        setContentView(R.layout.lyrics_activity_favourites);

        setTitle(getResources().getString(R.string.lyricsActivityName));

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

        myAdapter = new LyricsFavouritesActivity.MyListAdapter();
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
            dataToPass.putString(ARTIST, elements.get(position).getArtist());
            dataToPass.putString(TITLE, elements.get(position).getTitle());
            dataToPass.putString(LYRICS, elements.get(position).getLyrics());

            if(isTablet) {
                LyricsDetailsFragment dFragment = new LyricsDetailsFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass );
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
            } else {
                //isPhone
                Intent nextActivity = new Intent(this, LyricsEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });


        myList.setOnItemLongClickListener( (parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.lyricsFavouritesDeleteQuestion))
                    .setMessage(getResources().getString(R.string.lyricsFavouritesDeleteArtist) + elements.get(position).getArtist()
                            + "\n" + getResources().getString(R.string.lyricsFavouritesDeleteSong) + elements.get(position).getTitle())
                    .setPositiveButton(getResources().getString(R.string.lyricsFavouritesDeleteYes), (click, arg) -> {
                        db.delete(LyricsMyOpener.TABLE_NAME, LyricsMyOpener.COL_ID + "= ?", new String[] {Long.toString(elements.get(position).getId())});
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
        inflater.inflate(R.menu.lyrics_menu, menu);
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
                message = getResources().getString(R.string.lyricsAboutProject);
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
                .setMessage(getResources().getString(R.string.lyricsFavouritesInstructionsBody))
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
        LyricsMyOpener dbOpener = new LyricsMyOpener(this);
        db = dbOpener.getWritableDatabase(); // Calls onCreate() if you've never built the table before, onUpgrade if the version here is newer

        String[] columns = {LyricsMyOpener.COL_ID, LyricsMyOpener.COL_ARTIST, LyricsMyOpener.COL_TITLE, LyricsMyOpener.COL_LYRICS};

        Cursor results;

        if (searchTerm!=null) {
            searchTerm = "%" + searchTerm + "%";
            elements.clear();
            String[] args = {searchTerm, searchTerm};
            results = db.query(false, LyricsMyOpener.TABLE_NAME, columns,
                    LyricsMyOpener.COL_ARTIST + " LIKE ? OR " + LyricsMyOpener.COL_TITLE + " LIKE ?" , args, null, null, null, null);
        } else {
            elements.clear();
            results = db.query(false, LyricsMyOpener.TABLE_NAME, columns, null, null, null, null, null, null);
        }

        int artistColIndex = results.getColumnIndex(LyricsMyOpener.COL_ARTIST);
        int titleColIndex = results.getColumnIndex(LyricsMyOpener.COL_TITLE);
        int lyricsColIndex = results.getColumnIndex(LyricsMyOpener.COL_LYRICS);
        int idColIndex = results.getColumnIndex(LyricsMyOpener.COL_ID);


        while (results.moveToNext()) {
            String artist = results.getString(artistColIndex);
            String title = results.getString(titleColIndex);
            String lyrics = results.getString(lyricsColIndex);
            int id = results.getInt( idColIndex);

            elements.add(new LyricsSavedFavourite(artist, title, lyrics, id));
        }

        if (elements.size() == 1)
            Toast.makeText(this, getResources().getString(R.string.lyricsFavouritesFoundOneSong), Toast.LENGTH_SHORT).show();
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
            View newView = inflater.inflate(R.layout.lyrics_favourite_individual, parent, false);
            //set what the text should be for this row:
            TextView artistTextView = newView.findViewById(R.id.artistName);
            TextView titleTextView = newView.findViewById(R.id.songTitle);

            artistTextView.setText(elements.get(position).getArtist());
            titleTextView.setText(elements.get(position).getTitle());

            //return it to be put in the table
            return newView;
        }
    }

}
