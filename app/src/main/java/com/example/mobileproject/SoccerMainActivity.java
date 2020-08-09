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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.google.android.material.navigation.NavigationView;

public class SoccerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    public static final String ACTIVITY_NAME = "MAIN_ACTIVITY";
    protected String title;
    protected String URL;
    protected String team1;
    protected String team2;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    myList list_of_soccer;
    ArrayList<String> elements = new ArrayList<>();
    ListView matchs;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.soccer_acivity_main);
        setTitle("scorebat soccer search");

        matchs = findViewById(R.id.list_of_matchs);
        Button saved_faves = findViewById(R.id.soccer_saved_faves);
        ProgressBar progress = findViewById(R.id.progressBar);
        progress.setVisibility(View.INVISIBLE);

        MyHTTPRequest request = new MyHTTPRequest();
        request.execute("https://www.scorebat.com/video-api/v1/");
        matchs.setAdapter(list_of_soccer);


        matchs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle dataToPass = new Bundle();
                dataToPass.putString(title ,elements.get(position) );

                Intent nextActivity = new Intent(SoccerMainActivity.this, SoccerDetailsActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition

            }
        });

        loadToolbar();


       saved_faves.setOnClickListener( click -> {
           Intent go_to_faves = new Intent(this, SoccerFavouritesActivity.class);
           startActivity(go_to_faves);
       } );


    }// end of onCreate()


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.soccer_menu, menu);
        return true;
    }// end of onCreateOptionsMenu()


    public void loadToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.lyricsOpen, R.string.lyricsClose);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }// end of loadToolbar()


    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;

        switch( item.getItemId() ) {
//            case R.id.toCityFinderIcon:
//                Intent goToCity = new Intent(this, CityMainActivity.class);
//                startActivity(goToCity);
//            break;

//            case R.id.toDeezerIcon:
//                Intent goToDeezer = new Intent(this, DeezerMainActivity.class);
//                startActivity(goToDeezer);
//            break;

            case R.id.toSongSearchIcon:
                Intent goToSongSearch = new Intent(this, LyricsMainActivity.class);
                startActivity(goToSongSearch);
            break;

            case R.id.aboutProject:
                message = "this is the soccer highlights made by Robert Nix";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        }// end of switch statement
        return true;
    }// end of onOptionsItemSelected()


    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        String message = null;

        switch(item.getItemId())
        {
            case R.id.donateItem:
                donateBox();
                break;

            case R.id.helpItem:
                helpBox();
                break;

            case R.id.aboutAPI:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.scorebat.com/video-api/v1/"));
                startActivity(browserIntent);
                break;
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }// end of onNavigationItemSelected()


    public void helpBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("this is the soccer match highlights")
                .setMessage("to find a soccer match type in the title and press the scorebat button")
                .setPositiveButton("OK", (click, arg) -> {}).create().show();
    }// end of helpBox()


    public void donateBox () {
        EditText alertDialogEditText = new EditText(this);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("donations")
                .setMessage("how much would you like to donate")
                .setView(alertDialogEditText)
                .setPositiveButton(getResources().getString(R.string.lyricsThankYou), (click, arg) -> {})
                .setNegativeButton(getResources().getString(R.string.lyricsCancel), (click, arg) -> {})
                .create().show();

    }// end of donateBox()


    private class MyHTTPRequest extends AsyncTask< String, Integer, String> {

        String result = null;

        @Override
        protected String doInBackground(String... args) {

            try {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while (true) {
                        if ( ( line = reader.readLine() ) != null ) break;
                    sb.append(line + "\n");
                }

                result = sb.toString();
                JSONArray soccer_report = new JSONArray(result);


                for (int i = 0; i < soccer_report.length(); i++) {
                            JSONObject one_m = soccer_report.getJSONObject(i);
                        for (int j = 0; j < one_m.length(); j++) {
                            JSONObject one_match = soccer_report.getJSONObject(i);
                            String soccer_title = "Title: " + one_match.get("title");
                            String date_of_match = "Date: " + one_match.get("date");
                            URL = one_match.getString("url");
                            String team1 = "team 1: " + one_match.get("side1");
                            String team2 = "team 2" + one_match.get("side2");
                            elements.add(soccer_title);
                            elements.add(date_of_match);
                            elements.add(URL);
                            elements.add(team1);
                            elements.add(team2);
                        }
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }// end of onPostExecute()

    }// end of MyHTTPRequest()




    private class myList extends BaseAdapter {


        @Override
        public int getCount() {
            return elements.size();
        }// end of getCount()

        @Override
        public Object getItem(int position) {
            return elements.get(position);
        }// end of getItem

        @Override
        public long getItemId(int position) {
            return (long) position;
        }// end of getItemId()

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View newView = inflater.inflate(R.layout.soccer_row_layout, parent, false);

            TextView display = findViewById(R.id.textGoesHere);
            display.setText( elements.get(position) );

            Button googlebutton = findViewById(R.id.soccer_search_google);
            Button delete = findViewById(R.id.delete);

            delete.setOnClickListener( click -> {
                elements.remove(position);
            } );

            googlebutton.setOnClickListener( click -> {
                Intent searchOnGoogle = new Intent(Intent.ACTION_VIEW);
                searchOnGoogle.setData(Uri.parse(URL));
                startActivity(searchOnGoogle);
            } );

            return newView;

        }// end of getView()

    }// end of class myList()


}// end of SoccerMainActivity()
