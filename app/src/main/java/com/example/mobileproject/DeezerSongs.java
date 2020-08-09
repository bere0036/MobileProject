package com.example.mobileproject;

import android.graphics.Bitmap;

import java.io.Serializable;

public class DeezerSongs implements Serializable{
    private String title;
    private String album;
    private String artistName;
    private String coverURL;
    private Bitmap cover;
    private int duration;
    private long id;

    //constructor
    public DeezerSongs(){
    }

    public String getTitle(){
        return this.title;
    }

    public String getAlbum(){
        return this.album;
    }

    public String getArtistName(){
        return this.artistName;
    }

    public String getCoverURL(){
        return this.coverURL;
    }

    public Bitmap getCover(){
        return this.cover;
    }

    public int getDuration(){
        return this.duration;
    }

    public long getId(){
        return this.id;
    }


    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }
}
