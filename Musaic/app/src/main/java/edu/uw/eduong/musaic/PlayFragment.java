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
    private boolean first;
    int position;
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

        seekBar = (SeekBar) rootView.findViewById(R.id.seek);
        play = (ImageButton) rootView.findViewById(R.id.pause);
        next = (ImageButton) rootView.findViewById(R.id.next);
        back = (ImageButton) rootView.findViewById(R.id.back);
        shuffle = (ImageButton) rootView.findViewById(R.id.shuffle);
        repeat = (ImageButton) rootView.findViewById(R.id.repeat);

        //for keeping track of whether the song is the defaulted first song
        first = false;
        position = 0;
        shuffleVal = false;
        repeatVal = false;
        seekHelper = new SeekHelper();

        // Holds the songs
        songs = new ArrayList<>();
        Bundle bundle = getArguments();

        // get reference to the media player in mainactivity
        mediaPlayer = ((MainActivity)getActivity()).getMediaPlayer();

        // get the songs
        if (bundle != null) {
            songs = bundle.getParcelableArrayList(SONGS_LIST);
            if (songs == null) {
                songs = new ArrayList<>();
            }

            position = bundle.getInt(POSITION);

            // should be null when first opened landscape mode
            if (mediaPlayer == null) {
                first = true;
                setText();
                play.setImageResource(R.drawable.play);
            } else {
                checkMediaPlayer();
            }

            mediaPlayer = new MediaPlayer();
            seekBar.setOnSeekBarChangeListener(this);
            mediaPlayer.setOnCompletionListener(this);
            ((MainActivity) getActivity()).setMediaPlayer(mediaPlayer);

            if (!first) {
                playSong(position);
            }
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mediaPlayer.start();
                if (mediaPlayer != null) {
                    try {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                            ((MainActivity) getActivity()).setMediaPlayer(mediaPlayer);
                            // Changing button image to play button
                            play.setImageResource(R.drawable.play);
                        } else if (first) {
                            playSong(position);
                        } else {
                            // Resume song
                            mediaPlayer.start();
                            ((MainActivity) getActivity()).setMediaPlayer(mediaPlayer);
                            // Changing button image to pause button
                            play.setImageResource(R.drawable.pause);
                        }
                    } catch (IllegalStateException e) {
                        Log.v("Play", "exception" + e);
                    }
                } else {
                    playSong(position);

                    // Changing button image to pause button
                    play.setImageResource(R.drawable.pause);
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
                    position = r.nextInt((songs.size() - 1) + 1);
                    playSong(position);
                }
                //plays same song
                else if(repeatVal){
                    playSong(position);
                }
                else if(position < (songs.size() - 1)){
                    position = position + 1;
                    playSong(position);
                }else{
                    // play first song
                    position = 0;
                    playSong(position);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0){
                    position = position - 1 ;
                    playSong(position);
                }else{
                    // play last song
                    position = songs.size() - 1;
                    playSong(position);

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

    // set album text and info
    public void setText() {
        if (!songs.isEmpty()) {
            Song song = songs.get(position);
            // Displaying Song title
            String songString = song.getTitle();
            songTitle.setText(songString);

            // make sure song has album art
            if (song.getAlbumArt() != null) {
                albumArt.setImageBitmap(song.getAlbumArt());
            } else {
                albumArt.setImageDrawable(null);
            }
//                } else {
//                    Bitmap albumArtwork = BitmapFactory.decodeResource(getResources(), R.drawable.album);
//                    albumArt.setImageBitmap(albumArtwork);
//                }

            artist.setText(song.getArtist());
            album.setText(" / " + song.getAlbum());
            // Changing Button Image to pause image
            //////play.setBackgroundResource(R.drawable.pause);

            // set Progress bar values
            seekBar.setProgress(0);
            seekBar.setMax(100);
        }
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
    public void playSong(int songIndex){
        // Play song
        if (!songs.isEmpty()) {
            try {
                setText();
                Song song = songs.get(songIndex);
                Log.v("wtf", song.getTitle() + songIndex);

                checkMediaPlayer();
                mediaPlayer = new MediaPlayer();

                mediaPlayer.setDataSource(song.getPath());
                //ioException e
                play.setImageResource(R.drawable.pause);

                mediaPlayer.prepare();
                mediaPlayer.start();
                ((MainActivity)getActivity()).setMediaPlayer(mediaPlayer);

                // Updating progress bar
                updateProgressBar();
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    //resets media player and creates a new one
    public void checkMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
            } catch (IllegalStateException e){
                Log.v("Play", "exception" + e);
            }
            mediaPlayer.release();
            mediaPlayer = null;
            ((MainActivity)getActivity()).setMediaPlayer(mediaPlayer);
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
            if (mediaPlayer != null) {
                try {
                    int totalDuration = mediaPlayer.getDuration();

                    int currentDuration = mediaPlayer.getCurrentPosition();

                    // Displaying Total Duration time
                    rightTime.setText("" + seekHelper.timeConvert(totalDuration));
                    // Displaying time completed playing
                    leftTime.setText("" + seekHelper.timeConvert(currentDuration));

                    // Updating progress bar
                    int progress = seekHelper.percentage(currentDuration, totalDuration);
                    //Log.d("Progress", ""+progress);
                    seekBar.setProgress(progress);

                    // Running this thread after 100 milliseconds
                    handler.postDelayed(this, 100);
                } catch (IllegalStateException e) {
                    Log.v("Play", "exception" + e);
                }
            }
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
        ((MainActivity)getActivity()).setMediaPlayer(mediaPlayer);

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
                    position = position + 1;
                    playSong(position);
                } else {
                    // play first song
                    position = 0;
                    playSong(position);
                }
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        handler.removeCallbacks(mUpdateTimeTask);
        checkMediaPlayer();
    }
}
