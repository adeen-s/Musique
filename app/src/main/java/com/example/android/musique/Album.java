package com.example.android.musique;

import android.graphics.Bitmap;

/**
 * Created by adeen-s on 4/4/17.
 */

public class Album {

    private long id;
    private String albumName;
    private String artistName;
    private int nr_of_songs;
    private Bitmap albumImg;

    public Album(long id, String albumName, String artistName, Bitmap albumImg, int nr_of_songs) {
        this.albumImg = albumImg;
        this.id = id;
        this.albumName = albumName;
        this.artistName = artistName;
        this.nr_of_songs = nr_of_songs;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getID() {
        return id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Bitmap getAlbumImg() {
        return albumImg;
    }

    public void setAlbumImg(Bitmap albumImg) {
        this.albumImg = albumImg;
    }

    public String getNr_of_songs() {
        return Integer.toString(nr_of_songs);
    }

    public void setNr_of_songs(int nr_of_songs) {
        this.nr_of_songs = nr_of_songs;
    }

}
