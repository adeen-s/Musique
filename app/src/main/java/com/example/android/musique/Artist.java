package com.example.android.musique;

/**
 * Created by adeen-s on 4/4/17.
 */

public class Artist {

    private long id;
    private String artistName;
    private int nr_of_songs;

    public Artist(long id, String artistName, int nr_of_songs) {
        this.id = id;
        this.artistName = artistName;
        this.nr_of_songs = nr_of_songs;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getID() {
        return id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getNr_of_songs() {
        return Integer.toString(nr_of_songs);
    }

    public void setNr_of_songs(int nr_of_songs) {
        this.nr_of_songs = nr_of_songs;
    }

}
