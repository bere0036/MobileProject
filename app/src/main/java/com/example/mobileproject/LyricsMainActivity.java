package com.example.mobileproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
    Button toSavedFavourites;
    EditText artistName;
    EditText songTitle;
    Button searchAPIButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyrics_activity_main);

        toSavedFavourites = findViewById(R.id.savedFavouritesButton);
        artistName = findViewById(R.id.enterArtistName);
        songTitle = findViewById(R.id.enterSongTitle);
        searchAPIButton = findViewById(R.id.searchAPIButton);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        loadToolbar();

        searchAPIButton.setOnClickListener(lb -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(songTitle.getWindowToken(), 0);

            progressBar.setVisibility(View.VISIBLE);
            artist = artistName.getText().toString();
            title = songTitle.getText().toString();

            SongQuery req = new SongQuery();
            String url = "https://api.lyrics.ovh/v1/" +
                    artist.replace(" ", "%20") + "/" +
                    title.replace(" ", "%20");
            req.execute(url);
        });

        toSavedFavourites.setOnClickListener(click -> {
            Intent nextPage = new Intent(this, LyricsFavouritesActivity.class);
            nextPage.putExtra("Artist", artistName.getText().toString());
            nextPage.putExtra("Title", songTitle.getText().toString());
            startActivity(nextPage);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lyrics_favourites_activity_menu, menu);
        return true;
    }

    public void loadToolbar() {
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:

            case R.id.donateItem:
                donateDialog();
                break;

            case R.id.helpItem:
                helpDialog();
                break;
        }
        return true;
    }


    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        String message = null;

        switch(item.getItemId())
        {
            case R.id.goToMainPage:
                Intent nextActivity = new Intent(this, MainActivity.class);
                startActivity(nextActivity); //make the transition
                break;

            case R.id.donateItem:
                donateDialog();
                break;

            case R.id.helpItem:
                helpDialog();
                break;

            case R.id.aboutProject:
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://lyricsovh.docs.apiary.io/"));
//                startActivity(browserIntent);
                Toast.makeText(this, R.string.favouritesInstructionsTitle, Toast.LENGTH_LONG);
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    public void helpDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.favouritesInstructionsTitle)
                .setMessage(R.string.favouritesInstructionsBody)
                .setPositiveButton(R.string.OK, (click, arg) -> {}).create().show();
    }


    public void donateDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.favouritesDonateTitle)
                .setMessage(R.string.favouritesDonate)
                .setPositiveButton(R.string.yes, (click, arg) -> {
                    Toast.makeText(this, R.string.favouritesThankYou, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, (click, arg) -> {
                    Toast.makeText(this, R.string.favouritesThatsOK, Toast.LENGTH_SHORT).show();
                }).create().show();
    }



    private class SongQuery extends AsyncTask<String, Integer, String> {
        TextView lyricsTextView = findViewById(R.id.lyricsTextView);

        @Override
        protected String doInBackground(String... args) {
            try {
                //create a URL object of what server to contact:
                URL url = new URL(args[0]);

                //Replace " " with "%20" so the API can search artists/titles with spaces
                artist = artist.replace("%20", " ");
                title = title.replace("%20", " ");

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                //From part 3, slide 20
                String parameter = null;

                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                response = urlConnection.getInputStream();

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

                if(isTablet) {
                    LyricsDetailsFragment dFragment = new LyricsDetailsFragment(); //add a DetailFragment
                    dFragment.setArguments( dataToPass );
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                            .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
                } else {
                    //isPhone
                    Intent nextActivity = new Intent(LyricsMainActivity.this, LyricsEmptyActivity.class);
                    nextActivity.putExtras(dataToPass); //send data to next activity
                    startActivity(nextActivity); //make the transition
                }

            } else lyricsTextView.setText("Could not find lyrics.");

            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
