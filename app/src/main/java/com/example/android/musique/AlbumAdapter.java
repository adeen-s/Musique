package com.example.android.musique;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adeen-s on 21/3/17.
 * Custom BaseAdapter to display the list of songs
 */
public class AlbumAdapter extends BaseAdapter {

    private ArrayList<Album> albums;
    private LayoutInflater albumInf;

    public AlbumAdapter(Context c, ArrayList<Album> theAlbums) {
        albums = theAlbums;
        albumInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return albums.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout) albumInf.inflate(R.layout.album, parent, false);
        //get title and artist views
        TextView albumView = (TextView) songLay.findViewById(R.id.album_title);
        TextView artistView = (TextView) songLay.findViewById(R.id.album_artist);
        TextView albumnr = (TextView) songLay.findViewById(R.id.album_no);
        ImageView albumart = (ImageView) songLay.findViewById(R.id.album_art_album);
        //get song using position
        Album currAlbum = albums.get(position);
        //get title and artist strings
        albumView.setText(currAlbum.getAlbumName());
        artistView.setText(currAlbum.getArtistName());
        albumnr.setText((currAlbum.getNr_of_songs()) + " Songs");
        if (currAlbum.getAlbumImg() != null) {
            Bitmap bmp = currAlbum.getAlbumImg();
            albumart.setImageBitmap(bmp);
        } else {
            albumart.setImageResource(R.drawable.ic_music_video_black_48dp);
        }
        //Log.v("ALBUMART","Location-->"+currSong.getAlbumart());
        //set position as tag
        songLay.setTag(currAlbum.getID());
        return songLay;
    }
}