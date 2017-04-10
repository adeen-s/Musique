package com.example.android.musique;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ArtistActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ArrayList<Artist> artistList, artistSearch;
    private MusiqueService musicSrv = Connector.mMainActivity.musicSrv;
    private Intent nowPlayingIntent;
    private String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        setNavigationViewListener();


        Connector.mArtistActivity = this;
        final ListView albumView = (ListView) findViewById(R.id.artist_list_artist);
        artistList = new ArrayList<Artist>();
        artistSearch = new ArrayList<Artist>();
        getArtistsLists();
        Collections.sort(artistList, new Comparator<Artist>() {
            public int compare(Artist a, Artist b) {
                return a.getArtistName().compareTo(b.getArtistName());
            }
        });
        ArtistAdapter artistAdt = new ArtistAdapter(this, artistList);
        albumView.setAdapter(artistAdt);

        LinearLayout playingSong = (LinearLayout) findViewById(R.id.current_song_artist);
        playingSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowPlayingIntent = new Intent(ArtistActivity.this, NowPlaying.class);
                // Start the new activity
                startActivity(nowPlayingIntent);

            }
        });

        if (Connector.mMainActivity.musicSrv.isPlaying()) {
            setTitleInUi();
            setIconToPause();
        }

        final EditText searchBox = (EditText) findViewById(R.id.EditTextArtist);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                artistSearch.clear();
                int textLength = searchBox.getText().length();
                for (int i = 0; i < artistList.size(); i++) {
                    if (textLength <= artistList.get(i).getArtistName().length()) {
                        if (searchBox.getText().toString().equalsIgnoreCase((String) artistList.get(i).getArtistName().subSequence(0, textLength))) {
                            artistSearch.add(artistList.get(i));
                        }
                    }
                }
                albumView.setAdapter(new ArtistAdapter(getBaseContext(), artistSearch));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_end:
                stopService(Connector.mMainActivity.playIntent);
                musicSrv = null;
                System.exit(0);
                break;
            case R.id.action_shuffle:
                musicSrv.setShuffle();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void changeStateAlbum(View view) {
        if (musicSrv.isPlaying()) {
            pause();
        } else {
            play();
        }
    }

    public void play() {
        musicSrv.playPausedSong();
    }

    public void pause() {
        musicSrv.pausePlayingSong();
    }

    public void getArtistsLists() {
        String where = null;

        final Uri uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        final String _id = MediaStore.Audio.Artists._ID;
        final String artist_name = MediaStore.Audio.Artists.ARTIST;
        final String tracks = MediaStore.Audio.Artists.NUMBER_OF_TRACKS;

        final String[] columns = {_id, artist_name, tracks};
        Cursor cursor = this.getContentResolver().query(uri, columns, where, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            do {

                long id = cursor.getLong(cursor.getColumnIndex(_id));
                String name = cursor.getString(cursor.getColumnIndex(artist_name));
                int nr = Integer.parseInt(cursor.getString(cursor.getColumnIndex(tracks)));

                artistList.add(new Artist(id, name, nr));

            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    public void setTitleInUi() {
        songName = musicSrv.getSongTitle();
        TextView playingSong = (TextView) findViewById(R.id.song_name_artist);
        playingSong.setText(songName);
        Log.v("SongPicked", "Set song name to " + songName);
        TextView playingAlbum = (TextView) findViewById(R.id.album_name_artist);
        playingAlbum.setText(musicSrv.getAlbumName());
    }

    public void setIconToPlay() {
        ImageView playButton = (ImageView) findViewById(R.id.play_button_artist);
        playButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
    }

    public void setIconToPause() {
        ImageView playButton = (ImageView) findViewById(R.id.play_button_artist);
        playButton.setImageResource(R.drawable.ic_pause_white_36dp);
    }

    public void artistPicked(View view) {
        Connector.mMainActivity.artistSelected = true;
        Log.v("ArtistActivity", "ArtistSelected = " + Connector.mMainActivity.artistSelected);
        Connector.mMainActivity.artistName = (view.getTag().toString());
        Log.v("ArtistActivity", "Artist Tag from view = " + (view.getTag().toString()));
        Log.v("ArtistActivity", "---Going to main activity now---");
        Intent t = new Intent(ArtistActivity.this, MainActivity.class);
        startActivity(t);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_album: {
                Intent albumIntent = new Intent(ArtistActivity.this, AlbumActivity.class);
                startActivity(albumIntent);
                break;
            }

            case R.id.nav_songs: {
                Connector.mMainActivity.artistName = "";
                Connector.mMainActivity.artistSelected = false;
                Intent t = new Intent(ArtistActivity.this, MainActivity.class);
                startActivity(t);
            }

            case R.id.nav_artist: {
                break;
            }
        }
        //close navigation drawer
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNavigationViewListener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

}
