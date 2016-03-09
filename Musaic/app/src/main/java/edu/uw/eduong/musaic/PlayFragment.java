package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;



import java.util.ArrayList;

/**
 * Currently playing screen. Gives access to additional info
 */
public class PlayFragment extends Fragment implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    SeekHelper seekHelper;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    boolean shuffleVal;
    boolean repeatVal;
    Button play, next, back, shuffle, repeat, playlist;
    TextView album, artist, songTitle, rightTime, leftTime;
    ImageView albumArt;
    int position, randPosition;
    //    private int seekForwardTime = 5000;
//    private int seekBackwardTime = 5000;
    private Handler handler = new Handler();
    private static final String SONGS_LIST = "songs_list";  //Songs list tag
    private static final String POSITION = "position";
    private ArrayList<Song> songs;

    //Empty constructor
    public PlayFragment() {}

    //Thread mSeek;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        rightTime = (TextView) rootView.findViewById(R.id.rightTimeDisplay);
        leftTime = (TextView) rootView.findViewById(R.id.leftTimeDisplay);
        album = (TextView) rootView.findViewById(R.id.album);
        artist = (TextView) rootView.findViewById(R.id.artist);
        songTitle = (TextView) rootView.findViewById(R.id.songTitle);
        albumArt = (ImageView) rootView.findViewById(R.id.albumArt);
//        album.setText(Display Album Title);
//
        //songTitle.setText(Displlay song Name);


        seekBar = (SeekBar) rootView.findViewById(R.id.seek);
        playlist = (Button) rootView.findViewById(R.id.playlist);
        play = (Button) rootView.findViewById(R.id.pause);
        next = (Button) rootView.findViewById(R.id.next);
        back = (Button) rootView.findViewById(R.id.back);
        shuffle = (Button) rootView.findViewById(R.id.shuffle);
        repeat = (Button) rootView.findViewById(R.id.repeat);
        // Holds the songs
        songs = new ArrayList<>();
        Bundle bundle = getArguments();

        // get the songs
        if (bundle != null) {
            songs = bundle.getParcelableArrayList(SONGS_LIST);
            if (songs != null) {
                Log.v("PLAYMUSIC", "YES");
            } else {
                Log.v("PLAYMUSIC", "???");
                songs.clear();
            }
        }

        position = 0;
        shuffleVal = false;
        repeatVal = false;
        seekHelper = new SeekHelper();

        //resets player on create
        mediaPlayer = new MediaPlayer();
        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }

        position = bundle.getInt(POSITION);
        playSong(position);




        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mediaPlayer.start();
                if(mediaPlayer.isPlaying()){
                    if(mediaPlayer!=null){
                        mediaPlayer.pause();
                        // Changing button image to play button
                        //play.setBackgroundResource(R.drawable.play);
                    }
                }else{
                    // Resume song
                    if(mediaPlayer!=null){
                        mediaPlayer.start();
                        Log.wtf("Started", "Plz");
                        // Changing button image to pause button
                        //play.setBackgroundResource(R.drawable.pause);
                    }
                }

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if next song is there or not
                if(shuffleVal) {
                    // shuffle is on - play a random song
                    Random r = new Random();
                    randPosition = r.nextInt((songs.size() - 1) + 1);
                    playSong(randPosition);
                }
                //plays same song
                else if(repeatVal){
                    playSong(position);
                }
                else if(position < (songs.size() - 1)){
                    playSong(position + 1);
                    position ++;
                }else{
                    // play first song
                    playSong(0);
                    position = 0;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0){
                    playSong(position - 1);
                    position --;
                }else{
                    // play last song
                    playSong(songs.size() - 1);
                    position = songs.size() - 1;
                }
            }
        });
        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleVal){
                    shuffleVal = false;
                    Toast.makeText(getActivity(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    shuffle.setBackgroundResource(R.drawable.shuffle);
                }else{
                    // make repeat to true
                    shuffleVal= true;
                    Toast.makeText(getActivity(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    repeatVal = false;
                    shuffle.setBackgroundResource(R.drawable.son);
                    repeat.setBackgroundResource(R.drawable.repeat);
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatVal){
                    repeatVal = false;
                    Toast.makeText(getActivity(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    repeat.setBackgroundResource(R.drawable.repeat);
                }else{
                    // make repeat to true
                    repeatVal = true;
                    Toast.makeText(getActivity(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    shuffleVal = false;
                    repeat.setBackgroundResource(R.drawable.ron);
                    shuffle.setBackgroundResource(R.drawable.shuffle);
                }
            }
        });
//    playlist.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent i = new Intent(getApplicationContext(), ------whatever the playlist class is going to be--------.class);
//            startActivityForResult(i, 100);
//        }
//    });
        return rootView;
    }
    /**
     * Receiving song index from playlist view
     * and play the song
     * */
    public void onActivityResult(int requestCode,
                                 int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            position = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(position);
        }

    }

    //plays the song
    public void  playSong(int songIndex){
        // Play song
        try {
            Log.wtf("song", ""+songIndex);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(songs.get(songIndex).getPath());
            //ioException e
            //TODO: HANDLE EXCEPTION
            mediaPlayer.prepare();
            mediaPlayer.start();
            // Displaying Song title
            String song = songs.get(songIndex).getTitle();
            songTitle.setText(song);
            albumArt.setImageBitmap(songs.get(songIndex).getAlbumArt());
            artist.setText(songs.get(songIndex).getArtist());
            // Changing Button Image to pause image
            //////play.setBackgroundResource(R.drawable.pause);

            // set Progress bar values
            seekBar.setProgress(0);
            seekBar.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        handler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            int totalDuration = mediaPlayer.getDuration();
            int currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            rightTime.setText("" + seekHelper.timeConvert(totalDuration));
            // Displaying time completed playing
            leftTime.setText(""+seekHelper.timeConvert(currentDuration));

            // Updating progress bar
            int progress = (int)(seekHelper.percentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            seekBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            handler.postDelayed(this, 100);
        }
    };

    /**
     *
     * */
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        handler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * When user stops moving the progress hanlder
     * */
    public void onStopTrackingTouch(SeekBar seekBar) {
        handler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = seekHelper.progress(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     * */
    public void onCompletion(MediaPlayer arg0) {

        // check for repeat is ON or OFF
        if(repeatVal){
            // repeat is on play same song again
            playSong(position);
        } else if(shuffleVal){
            // shuffle is on - play a random song
            Random r = new Random();
            position = r.nextInt((songs.size() - 1) + 1);
            playSong(position);
        } else{
            // no repeat or shuffle ON - play next song
            if(position < (songs.size() - 1)){
                playSong(position + 1);
                position ++;
            }else {
                // play first song
                playSong(0);
                position = 0;
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(mUpdateTimeTask);
    }
}





