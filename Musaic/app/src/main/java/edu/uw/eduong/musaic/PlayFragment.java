package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Currently playing screen. Gives access to additional info
 */
public class PlayFragment extends Fragment implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {
    SeekHelper seekHelper;
    MediaPlayer mediaPlayer;
    SeekBar seekBar;
    boolean shuffleVal;
    boolean repeatVal;
    ImageButton play, next, back, shuffle, repeat;
    TextView album, artist, songTitle, rightTime, leftTime;
    ImageView albumArt;
    int position, randPosition;
    private Handler handler = new Handler();
    private static final String SONGS_LIST = "songs_list";  //Songs list tag
    private static final String POSITION = "position";
    private ArrayList<Song> songs;

    //Empty constructor
    public PlayFragment() {}

    // show the song lyrics
    public interface songInfo {
        void getSongInfo(int position);
    }

    // show the artist wiki page info
    public interface artistInfo {
        void getArtistInfo(int position);
    }

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
        //songTitle.setText(Display song Name);

        seekBar = (SeekBar) rootView.findViewById(R.id.seek);
        //playlist = (Button) rootView.findViewById(R.id.playlist);
        play = (ImageButton) rootView.findViewById(R.id.pause);
        next = (ImageButton) rootView.findViewById(R.id.next);
        back = (ImageButton) rootView.findViewById(R.id.back);
        shuffle = (ImageButton) rootView.findViewById(R.id.shuffle);
        repeat = (ImageButton) rootView.findViewById(R.id.repeat);

        position = 0;
        shuffleVal = false;
        repeatVal = false;
        seekHelper = new SeekHelper();

        //resets player on create
        mediaPlayer = new MediaPlayer();

        seekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        // Holds the songs
        songs = new ArrayList<>();
        Bundle bundle = getArguments();

        // get the songs
        if (bundle != null) {
            songs = bundle.getParcelableArrayList(SONGS_LIST);
            if (songs == null) {
                songs = new ArrayList<>();
            }

            position = bundle.getInt(POSITION);
            playSong(position);
        }


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mediaPlayer.start();
                if(mediaPlayer.isPlaying()){
                    if(mediaPlayer!=null){
                        mediaPlayer.pause();
                        // Changing button image to play button
                        play.setImageResource(R.drawable.play);
                    }
                }else{
                    // Resume song
                    if(mediaPlayer!=null){
                        mediaPlayer.start();
                        Log.wtf("Started", "Plz");
                        // Changing button image to pause button
                        play.setImageResource(R.drawable.pause);
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
                    shuffle.setImageResource(R.drawable.shuffle);
                }else{
                    // make repeat to true
                    shuffleVal= true;
                    Toast.makeText(getActivity(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    repeatVal = false;
                    shuffle.setImageResource(R.drawable.son);
                    repeat.setImageResource(R.drawable.repeat);
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatVal){
                    repeatVal = false;
                    Toast.makeText(getActivity(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    repeat.setImageResource(R.drawable.repeat);
                }else{
                    // make repeat to true
                    repeatVal = true;
                    Toast.makeText(getActivity(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    shuffleVal = false;
                    repeat.setImageResource(R.drawable.ron);
                    shuffle.setImageResource(R.drawable.shuffle);
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
        ImageButton lyrics = (ImageButton) rootView.findViewById(R.id.lyrics);
        lyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((songInfo) getActivity()).getSongInfo(position);
            }
        });

        ImageButton wiki = (ImageButton) rootView.findViewById(R.id.wiki);
        wiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((artistInfo) getActivity()).getArtistInfo(position);
            }
        });

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
        if (!songs.isEmpty()) {
            try {
                Song song = songs.get(songIndex);

                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getPath());
                //ioException e

                mediaPlayer.prepare();
                mediaPlayer.start();
                // Displaying Song title
                String songString = song.getTitle();
                songTitle.setText(songString);

                // make sure song has album art
                if (song.getAlbumArt() != null) {
                    albumArt.setImageBitmap(song.getAlbumArt());
                } else {
                    albumArt.setImageResource(R.drawable.album);
                }
                artist.setText(song.getArtist());
                album.setText(R.string.slash + song.getAlbum());
                // Changing Button Image to pause image
                //////play.setBackgroundResource(R.drawable.pause);

                // set Progress bar values
                seekBar.setProgress(0);
                seekBar.setMax(100);

                // Updating progress bar
                updateProgressBar();
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }
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
     * When user stops moving the progress handler
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
        if (!songs.isEmpty()) {
            // check for repeat is ON or OFF
            if (repeatVal) {
                // repeat is on play same song again
                playSong(position);
            } else if (shuffleVal) {
                // shuffle is on - play a random song
                Random r = new Random();
                position = r.nextInt((songs.size() - 1) + 1);
                playSong(position);
            } else {
                // no repeat or shuffle ON - play next song
                if (position < (songs.size() - 1)) {
                    playSong(position + 1);
                    position++;
                } else {
                    // play first song
                    playSong(0);
                    position = 0;
                }
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(mUpdateTimeTask);
    }
}





