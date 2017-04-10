package com.example.android.musique;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by adeen-s on 21/3/17.
 * Custom BaseAdapter to display the list of songs
 */
public class ArtistAdapter extends BaseAdapter implements SectionIndexer{

    private ArrayList<Artist> artists;
    private LayoutInflater artistInf;

    public ArtistAdapter(Context c, ArrayList<Artist> theArtists) {
        artists = theArtists;
        artistInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return artists.size();
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
        LinearLayout songLay = (LinearLayout) artistInf.inflate(R.layout.artist, parent, false);
        //get title and artist views
        TextView albumView = (TextView) songLay.findViewById(R.id.artist_title);
        TextView albumnr = (TextView) songLay.findViewById(R.id.no_of_songs_artist);
        //get song using position
        Artist currArtist = artists.get(position);
        //get title and artist strings
        albumView.setText(currArtist.getArtistName());
        albumnr.setText((currArtist.getNr_of_songs()) + " Songs");
        ImageView albumart = (ImageView) songLay.findViewById(R.id.album_art_artist);
        albumart.setImageResource(R.drawable.ic_music_video_black_48dp);
        //Log.v("ALBUMART","Location-->"+currSong.getAlbumart());
        //set name as tag
        songLay.setTag(currArtist.getArtistName());
        return songLay;
    }

    private void setSection(LinearLayout header, String label) {
        TextView text = new TextView(Connector.mMainActivity);
        header.setBackgroundColor(0xffaabbcc);
        text.setTextColor(Color.WHITE);
        text.setText(label.substring(0, 1).toUpperCase());
        text.setTextSize(20);
        text.setPadding(5, 0, 0, 0);
        text.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(text);
    }
    public int getPositionForSection(int section) {
        if (section == 35) {
            return 0;
        }
        for (int i = 0; i < artists.size(); i++) {
            String l = artists.get(i).getArtistName();
            char firstChar = l.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }
    public int getSectionForPosition(int arg0) {
        return 0;
    }
    public Object[] getSections() {
        return null;
    }
}