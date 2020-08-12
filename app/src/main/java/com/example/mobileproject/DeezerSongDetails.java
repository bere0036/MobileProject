package com.example.mobileproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.net.URL;

public class DeezerSongDetails extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView imageAlbum;
    TextView textSongTitle, textSongDuration, textAlbumTitle;
    Button buttonSave;
    CoordinatorLayout layout;
    //bitmap to store in database
    Bitmap albumBitmap = null;
    String songTitle, albumName, imageURL;
    long songId;
    int duration;
    DeezerOpener deezerSongDatabse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_details);
        //initials
        deezerSongDatabse = new DeezerOpener(this);
        imageAlbum = findViewById(R.id.imageAlbum);
        textSongTitle = findViewById(R.id.textSongTitle);
        textSongDuration = findViewById(R.id.textSongDuration);
        textAlbumTitle = findViewById(R.id.textAlbumTitle);
        buttonSave = findViewById(R.id.buttonSave);

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

        //save button OnClickListener
        //insert song to fav database
        buttonSave.setOnClickListener(click -> {
            DeezerSongs song = new DeezerSongs();
            song.setTitle(songTitle);
            song.setCoverURL(imageURL);
            song.setDuration(duration);
            song.setAlbum(albumName);
            song.setId(songId);
            deezerSongDatabse.insertSong(song);

            Toast.makeText(getApplicationContext(), "Added to Favourites", Toast.LENGTH_LONG).show();
        });

        //load song data
        DeezerSongs song = (DeezerSongs) getIntent().getSerializableExtra("songDetails");
        songId = song.getId();
        if (song != null) {
            songTitle = song.getTitle();
            duration = song.getDuration();
            albumName = song.getAlbum();
            imageURL = song.getCoverURL();
            new AlbumImage(imageURL).execute();

            textSongTitle.setText(songTitle);
            textSongDuration.setText(String.valueOf(duration));
            textAlbumTitle.setText(albumName);
        }
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

    //load image from URL
    class AlbumImage extends AsyncTask<Void, Void, Bitmap> {

        String imageUrl;

        AlbumImage(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            try {
                URL url = new URL(imageUrl);
                return BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                albumBitmap = bitmap;
                imageAlbum.setImageBitmap(albumBitmap);
            }
        }
    }
}
