package com.example.android.musique;

import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

public class NowPlaying extends AppCompatActivity {

    private MusiqueService musicSrv = Connector.mMainActivity.musicSrv;
    private SeekBar seekBar;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        Connector.mNowPlaying = this;
        updateUi();
    }

    public void updateUi() {
        TextView playingSong = (TextView) findViewById(R.id.song_name_playing);
        playingSong.setText(musicSrv.getSongTitle());
        TextView playingAlbum = (TextView) findViewById(R.id.album_name_playing);
        playingAlbum.setText(musicSrv.getAlbumName());
        TextView playingArtist = (TextView) findViewById(R.id.artist_name_playing);
        playingArtist.setText(musicSrv.getArtist());
        ImageView playPause = (ImageView) findViewById(R.id.play_pause_toggle_playing);
        if (musicSrv.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_pause_white_36dp);
        } else {
            playPause.setImageResource(R.drawable.ic_play_arrow_white_36dp);
        }
        ImageView albumartView = (ImageView) findViewById(R.id.album_art_playing);
        if (musicSrv.getAlbumart() != null) {
            File imageFile = new File(musicSrv.getAlbumart());
            if (imageFile.exists()) {
                albumartView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            }
        } else {
            albumartView.setImageResource(R.drawable.image1);
        }

        musicSrv.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                seekBar.setMax(musicSrv.getDuration() / 1000);
            }
        });

        NowPlaying.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (musicSrv.player != null) {
                    int mCurrentPosition = musicSrv.player.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicSrv.player != null && fromUser) {
                    musicSrv.player.seekTo(progress * 1000);
                }
            }
        });

    }

    public void changeStatePlaying(View view) {
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

    public void setIconToPlay() {
        ImageView playButton = (ImageView) findViewById(R.id.play_pause_toggle_playing);
        playButton.setImageResource(R.drawable.ic_play_arrow_white_36dp);
    }

    public void setIconToPause() {
        ImageView playButton = (ImageView) findViewById(R.id.play_pause_toggle_playing);
        playButton.setImageResource(R.drawable.ic_pause_white_36dp);
    }

    public void playNext(View v) {
        musicSrv.playNext();
    }

    public void playPrev(View v) {
        musicSrv.playPrev();
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

}
