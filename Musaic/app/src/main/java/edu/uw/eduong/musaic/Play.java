package edu.uw.eduong.musaic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class Play extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    Button play, next, back, shuffle, repeat;
    TextView album, artist, songTitle;
    int position;
    Thread mSeek;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent mIntent = getIntent();
        Bundle bundle = mIntent.getExtras();
        position = bundle.getInt("pos", 0);


        //resets player on create
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        ///need to get the list of songs on the SD card
        mediaPlayer = MediaPlayer.create(getApplicationContext(), (uri from the string position of the songs))
        mediaPlayer.start();

        album = (TextView) findViewById(R.id.album);
        artist = (TextView) findViewById(R.id.artist);
        songTitle = (TextView) findViewById(R.id.songTitle);
        album.setText(Display Album Titele);
        artist.setText(Display Artist Name);
        songTitle.setText(Displlay song Name);


        seekBar = (SeekBar) findViewById(R.id.seek);
        play = (Button) findViewById(R.id.play);
        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.back);
        shuffle = (Button) findViewById(R.id.shuffle);
        repeat = (Button) findViewById(R.id.repeat);

        play.setOnClickListener(this);
        next.setOnClickListener(this);
        back.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);


        mSeek = new Thread() {
            @Override
        public void run(){

            }
        }
    }

    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.play:
                if (mediaPlayer.isPlaying()) {
                    play.setText(">");
                    mediaPlayer.pause();
                } else {
                    play.setText("||");
                    mediaPlayer.start();
                }
                break;
            case R.id.next:
                mediaPlayer.stop();
                mediaPlayer.release();
                Uri u = Uri.parse($$$$$$$(basically do position plus 1));
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                mediaPlayer.start();
                break;
            case R.id.back:
                mediaPlayer.stop();
                mediaPlayer.release();
                Uri u = Uri.parse($$$$$$$(basically do position plus 1));
                mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
                mediaPlayer.start();
                break;
        }
    }

}
