package com.example.mobileproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeezerTrackList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ListView trackListView;
    String trackListUrl = "";
    ArrayList<DeezerSongs> songs;
    DeezerAdapter trackListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_track_list);

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

        //initial views
        trackListView = findViewById(R.id.songtrackList);
        songs = new ArrayList<>();
        trackListAdapter = new DeezerAdapter(this, songs);
        trackListView.setAdapter(trackListAdapter);
        trackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeezerTrackList.this, DeezerSongDetails.class);
                DeezerSongs song = songs.get(position);
                intent.putExtra("songDetails", song);
                startActivity(intent);
            }
        });
        //get trackList
        trackListUrl = getIntent().getStringExtra("tracklist_url");
        new GetTrackList(trackListUrl).execute();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

            case R.id.back:
                Intent goHome = new Intent(this, DeezerSongSearchMain.class);
                startActivity(goHome);
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    class GetTrackList extends AsyncTask<Void, Void, String> {

        String trackListUrl;

        public GetTrackList(String trackListUrl) {
            this.trackListUrl = trackListUrl;
        }

        @Override
        protected String doInBackground(Void... voids) {
            URL mUrl = null;
            String trackData = "";
            try {
                mUrl = new URL(trackListUrl);
                HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
                InputStream inputStream = connection.getInputStream();

                StringBuilder stringBuilder = new StringBuilder();
                String readLine;
                try {
                    //read input string
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    while ((readLine = br.readLine()) != null) {
                        stringBuilder.append(readLine);
                    }
                    trackData = stringBuilder.toString();
                } catch (IOException e) {
                    trackData = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return trackData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                int count = jsonArray.length();
                for (int i = 0; i < count; i++) {
                    JSONObject data = jsonArray.getJSONObject(i);
                    int id = data.getInt("id");
                    String title = data.getString("title");
                    int duration = data.getInt("duration");
                    JSONObject album = data.getJSONObject("album");
                    String albumTitle = album.getString("title");
                    String cover_big = album.getString("cover_big");

                    DeezerSongs song = new DeezerSongs();
                    song.setId(Long.valueOf(id));
                    song.setTitle(title);
                    song.setDuration(duration);
                    song.setAlbum(albumTitle);
                    song.setCoverURL(cover_big);
                    songs.add(song);
                }
                trackListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class DeezerAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<DeezerSongs> list;

        public DeezerAdapter(Context context, ArrayList<DeezerSongs> list){
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
            View newView = inflater.inflate(R.layout.activity_deezer_songs, parent, false);

            TextView songNameText = (TextView) newView.findViewById(R.id.textSongName);
            String songTitle = list.get(position).getTitle();
            songNameText.setText(songTitle);

            return newView;
        }
    }
}
