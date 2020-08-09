package com.example.mobileproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import java.io.IOException;
import java.net.URL;

public class DeezerSongDetails extends AppCompatActivity {

    ImageView imageAlbum;
    TextView textSongTitle, textSongDuration, textAlbumTitle;
    Button buttonSave;
    CoordinatorLayout layout;
    //bitmap to store in database
    Bitmap albumBitmap = null;
    String songTitle, albumName, imagePath;
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
        layout = findViewById(R.id.layout);

        //save button OnClickListener
        buttonSave.setOnClickListener(click -> {
            DeezerSongs song = new DeezerSongs();
            song.setTitle(songTitle);
            song.setCoverURL(imagePath);
            song.setDuration(duration);
            song.setAlbum(albumName);
            song.setId(songId);
            deezerSongDatabse.insertSong(song);

            Intent intent = new Intent(DeezerSongDetails.this, DeezerSongSearchMain.class);
            startActivity(intent);
        });
        //load song data
        DeezerSongs song = (DeezerSongs) getIntent().getSerializableExtra("songDetails");
        songId = song.getId();
        if (song != null) {
            songTitle = song.getTitle();
            duration = song.getDuration();
            albumName = song.getAlbum();
            imagePath = song.getCoverURL();
            new LoadImage(imagePath).execute();
            textSongTitle.setText(songTitle);
            textSongDuration.setText(String.valueOf(duration));
            textAlbumTitle.setText(albumName);
        }
    }

    //load image from URL
    class LoadImage extends AsyncTask<Void, Void, Bitmap> {

        String imageUrl;

        LoadImage(String imageUrl) {
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
