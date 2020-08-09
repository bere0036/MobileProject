package com.example.mobileproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeezerSongSearchMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public EditText artistName;
    public SharedPreferences pref;
    public String artist; //stores search artist name
    public final static String myPreference = "myPref";
    public final static String Artist = "artistKey";
    private ArrayList<DeezerArtist> deezerArtistList = new ArrayList<>();
    private DeezerArtistAdapter Artistadapter = new DeezerArtistAdapter(this, deezerArtistList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_search_main);

        //initialization
        artistName = findViewById(R.id.deezerSearchEditText);
        Button searchButton = findViewById(R.id.searchButton);
        Button favButton = findViewById(R.id.deezerFavouritesButton);
        ListView myList = findViewById(R.id.deezerListView);

        pref = getSharedPreferences(myPreference, Context.MODE_PRIVATE);

        if(pref.contains(Artist)){
            artistName.setText(pref.getString(Artist, ""));
        }

        //search button onClickListener
        searchButton.setOnClickListener( click -> {
            //set adaptor to listview
            myList.setAdapter(Artistadapter);
            //get user input artist
            String artist = artistName.getText().toString();
            //cleat the listview
            deezerArtistList.clear();
            Artistadapter.notifyDataSetChanged();
            String deezerURL = "https://api.deezer.com/search/artist/?q=" + artist + "&output=xml";
            DeezerQuery req = new DeezerQuery();
            req.execute(deezerURL);
        });

        //favourites button onClickListener
        favButton.setOnClickListener( click -> {
            Intent intent = new Intent(DeezerSongSearchMain.this, DeezerFavouriteList.class);
            startActivity(intent);
        });

        //toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        //NavigationDrawer
        DrawerLayout drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        //mylist onClickListener
        //get clicked artist from the list and pass in the corresponding DeezerArtist to TrackList page
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeezerSongSearchMain.this, DeezerTrackList.class);
                DeezerArtist artist = (DeezerArtist) parent.getItemAtPosition(position);
                intent.putExtra("tracklist_url", artist.getTracklist());
                startActivity(intent);
            }
        });

        //setting progressBar to invisible
        ProgressBar progBar = (ProgressBar) findViewById(R.id.deezerProgressBar);
        progBar.setVisibility(View.INVISIBLE);
    }

    //Stores the text in the search box
    @Override
    public void onPause(){
        super.onPause();

        if(artistName.getText().toString().equals("")){
            //puts the last entered artist's name into pref if nothing is currently written in the EditText
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(Artist, artist);
            edit.commit();
        }else {
            //puts whatever is currently written in the EditText into pref
            String artistEditText = artistName.getText().toString();
            SharedPreferences.Editor edit = pref.edit();
            edit.putString(Artist, artistEditText);
            edit.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.deezer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch (item.getItemId()) {

            //what to do when the menu item is selected:
            case R.id.item1:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("This page written by Ningxin Zhao");
                alertDialogBuilder.setNegativeButton("Exit", null);
                alertDialogBuilder.create().show();
                break;
            case R.id.item2:
                Intent goHome = new Intent(this, MainActivity.class);
                startActivity(goHome);

                break;
            case R.id.item3:
                Intent gotoLyrics = new Intent(this, LyricsMainActivity.class);
                startActivity(gotoLyrics);
                break;

            case R.id.item4:
                Intent gotoSoccer = new Intent(this, MainActivity.class);
                startActivity(gotoSoccer);
                break;

            case R.id.item5:
                Intent gotoGeo = new Intent(this, MainActivity.class);
                startActivity(gotoGeo);
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String message = null;
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.prompts, null);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.etUserInput);
        AlertDialog.Builder alertDialogBuilder;
        switch(item.getItemId())
        {
            case R.id.help:
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Enter artist name and search to get the songs. \n You can add to favourite list to be view after.");
                alertDialogBuilder.setNegativeButton("Exit", null);
                alertDialogBuilder.create().show();
                break;

            case R.id.donation:
                alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setMessage("Donation: Please give generously.\n How much money do you want to donate?");
                alertDialogBuilder.setView(promptsView);
                alertDialogBuilder.setPositiveButton("THANK YOU", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Thank you for donated "+userInput.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                });
                alertDialogBuilder.setNegativeButton("CANCEL", null);
                alertDialogBuilder.create().show();
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    //search artists from input artist
    private class DeezerQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(response, "UTF-8");

                int eventType = xpp.getEventType();
                DeezerArtist artist = null;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    //********************************
                    String tag = xpp.getName();
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            if (tag.equals("artist")) {
                                artist = new DeezerArtist();
                            } else if (tag.equals("name")) {
                                String artistName = xpp.nextText();
                                artist.setName(artistName);
                            } else if (tag.equals("tracklist")) {
                                String tracklist = xpp.nextText();
                                artist.setTracklist(tracklist);
                                deezerArtistList.add(artist);
                            }
                            break;
                    }
                    eventType = xpp.next();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            return "";
        }


        public void onPostExecute(String fromDoInBackground){
            //hiding progressBar
            ProgressBar progBar = findViewById(R.id.deezerProgressBar);
            progBar.setVisibility(View.INVISIBLE);
            //update the listView
            Artistadapter.notifyDataSetChanged();
        }
    }

    // Adapter class for populating the ListView
    public class DeezerArtistAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<DeezerArtist> list;

        //constructor
        public DeezerArtistAdapter(Context context, ArrayList<DeezerArtist> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.deezer_listview_search, parent, false);
            DeezerArtist currentSong = (DeezerArtist) getItem(position);

            //set artist name for each ListView row
            TextView artistNameText = (TextView) newView.findViewById(R.id.songArtist);
            artistNameText.setText(currentSong.getName());
            return newView;
        }
    }

}