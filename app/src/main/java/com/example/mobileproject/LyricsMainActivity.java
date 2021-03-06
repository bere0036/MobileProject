package com.example.mobileproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class LyricsMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ACTIVITY_NAME = "MAIN_ACTIVITY";
    protected String artist;
    protected String title;
    protected String lyrics;

    ProgressBar progressBar;
    EditText artistNameEditText;
    EditText songTitleEditText;
    Button toSavedFavouritesButton;
    Button searchAPIButton;
    Button searchGoogleButton;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    Intent searchGoogle;
    Intent nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyrics_activity_main);

        setTitle(getResources().getString(R.string.lyricsActivityName));

        toSavedFavouritesButton = findViewById(R.id.savedFavouritesButton);
        artistNameEditText = findViewById(R.id.enterArtistName);
        songTitleEditText = findViewById(R.id.enterSongTitle);
        searchAPIButton = findViewById(R.id.searchAPIButton);
        searchGoogleButton = findViewById(R.id.searchButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        loadToolbar();

        //Google search using artist and title edittexts for search terms
        searchGoogleButton.setOnClickListener(click -> {
            artist = artistNameEditText.getText().toString();
            title = songTitleEditText.getText().toString();

            String url = "https://www.google.com/search?q=" + artist + "+" + title;
            searchGoogle = new Intent(Intent.ACTION_VIEW);
            searchGoogle.setData(Uri.parse(url));
            startActivity(searchGoogle);
        });

        //search API
        searchAPIButton.setOnClickListener(click -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(songTitleEditText.getWindowToken(), 0);

            progressBar.setVisibility(View.VISIBLE);
            artist = artistNameEditText.getText().toString();
            title = songTitleEditText.getText().toString();

            SongQuery req = new SongQuery();
            //replacing spaces " " with "%20" for the search
            String url = "https://api.lyrics.ovh/v1/" +
                    artist.replace(" ", "%20") + "/" +
                    title.replace(" ", "%20");
            req.execute(url);
        });

        //go to saved favourites
        toSavedFavouritesButton.setOnClickListener(click -> {
            nextPage = new Intent(this, LyricsFavouritesActivity.class);
            startActivity(nextPage);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lyrics_menu, menu);
        return true;
    }

    public void loadToolbar() {
        // load toolbar
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
        //cases for switching to other activities
        String message;

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

            case R.id.toDeezerIcon:
                Intent nextActivity = new Intent(this, DeezerSongSearchMain.class);
                startActivity(nextActivity); //make the transition
                break;

            case R.id.aboutProject:
                message = getResources().getString(R.string.lyricsAboutProject);
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

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
        alertDialogBuilder.setTitle(getResources().getString(R.string.lyricsFavouritesDonateTitle))
                .setMessage(getResources().getString(R.string.lyricsDonateBody))
                .setView(alertDialogEditText)
                .setPositiveButton(getResources().getString(R.string.lyricsThankYou), (click, arg) -> {})
                .setNegativeButton(getResources().getString(R.string.lyricsCancel), (click, arg) -> {})
                .create().show();
    }



    private class SongQuery extends AsyncTask<String, Integer, String> {

        boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

        @Override
        protected String doInBackground(String... args) {
            try {
                //lyrics from previous search are displayed in fragment, delete them.
                if(lyrics != null) {
                    lyrics = null;
                    if( isTablet) {
                        getSupportFragmentManager()
                                .beginTransaction().
                                remove(getSupportFragmentManager().findFragmentByTag("LyricsDetailsFragment")).commit();
                    }
                }

                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //Replace " " with "%20" so the API can search artists/titles with spaces
                artist = artist.replace("%20", " ");
                title = title.replace("%20", " ");

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                publishProgress(50);

                //JSON reading:   Look at slide 26
                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                    sb.append(line + "\n");

                String result = sb.toString(); //result is the whole string

                JSONObject returnedInfo = new JSONObject(result);
                lyrics = returnedInfo.getString("lyrics");

            } catch (Exception e) {
                e.printStackTrace();
            }

            return title;
        }


        protected void onProgressUpdate(Integer... value) {
            progressBar.setProgress(value[0]);
        }

        protected void onPostExecute(String fromDoInBackground) {
            if(lyrics!=null) {
                //Code for switching to fragment with selected item's lyrics
                //Creating and passing a bundle with that item's info
                Bundle dataToPass = new Bundle();
                dataToPass.putString("ARTIST", artist);
                dataToPass.putString("TITLE", title);
                dataToPass.putString("LYRICS", lyrics);
                boolean isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded

                if( isTablet) {
                    LyricsDetailsFragment dFragment = new LyricsDetailsFragment(); //add a DetailFragment

                    dFragment.setArguments(dataToPass);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, dFragment, "LyricsDetailsFragment") //Add the fragment in FrameLayout
                            .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
                } else {
                    //isPhone
                    Intent nextActivity = new Intent(LyricsMainActivity.this, LyricsEmptyActivity.class);
                    nextActivity.putExtras(dataToPass); //send data to next activity
                    startActivity(nextActivity); //make the transition
                }

            } else {
                String message = getResources().getString(R.string.lyricsNotFound);
                Toast.makeText(LyricsMainActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
