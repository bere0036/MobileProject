package com.example.mobileproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class DeezerFavouriteList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ListView listFavouriteSongs;
    ArrayList<DeezerSongs> songs;
    FavouriteSongsAdapter favouriteSongsAdapter;
    private DeezerOpener deezerSongDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_track_list);
        deezerSongDb = new DeezerOpener(this);


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

        listFavouriteSongs = findViewById(R.id.songtrackList);

        //in favourite page, can do delete
        listFavouriteSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeezerFavouriteList.this, DeezerSongDetailDelete.class);
                DeezerSongs selectedSong  = (DeezerSongs) parent.getItemAtPosition(position);
                intent.putExtra("songDetails", selectedSong);
                startActivity(intent);
            }
        });
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

            case R.id.back:
                Intent goHome = new Intent(this, DeezerSongSearchMain.class);
                startActivity(goHome);
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //get all favourite songs from database
        songs = deezerSongDb.getFavouriteSongs();
        favouriteSongsAdapter = new FavouriteSongsAdapter(this, songs);
        listFavouriteSongs.setAdapter(favouriteSongsAdapter);
    }

    //connect arraylist to listView in the layout
    public class FavouriteSongsAdapter extends BaseAdapter {

        LayoutInflater inflater;
        ArrayList<DeezerSongs> songs;

        FavouriteSongsAdapter(Context context, ArrayList<DeezerSongs> songs) {
            this.songs = songs;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Object getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_deezer_songs, parent, false);
            }
            //set the data to listview
            TextView textSongTitle = convertView.findViewById(R.id.textSongName);
            String songTitle = songs.get(position).getTitle();
            textSongTitle.setText(songTitle);
            return convertView;
        }
    }
}


