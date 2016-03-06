//package edu.uw.eduong.musaic;
//
//import android.app.Fragment;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.Button;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Random;
//
//public class Play extends Fragment {
//    SeekHelper seekHelper;
//    MediaPlayer mediaPlayer;
//    SeekBar seekBar;
//    boolean shuffleVal;
//    boolean repeatVal;
//    Button play, next, back, shuffle, repeat, playlist;
//    TextView album, artist, songTitle, rightTime, leftTime;
//    int position;
////    private int seekForwardTime = 5000;
////    private int seekBackwardTime = 5000;
//    private Handler handler = new Handler();
//    private ArrayList<HashMap<String, String>> songsList;
//
//    //Thread mSeek;
//    /**
//     * ATTENTION: This was auto-generated to implement the App Indexing API.
//     * See https://g.co/AppIndexing/AndroidStudio for more information.
//     */
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_play);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        position = 0;
//        shuffleVal = false;
//        repeatVal = false;
//        seekHelper = new SeekHelper();
//        songsList = new ArrayList<HashMap<String, String>>();
//        //resets player on create
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
//        ///need to get the list of songs on the SD card
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), (uri from the string position of the songs))
//        mediaPlayer.start();
//
//        rightTime = (TextView) findViewById(R.id.rightTimeDisplay);
//        leftTime = (TextView) findViewById(R.id.leftTimeDisplay);
//        album = (TextView) findViewById(R.id.album);
//        artist = (TextView) findViewById(R.id.artist);
//        songTitle = (TextView) findViewById(R.id.songTitle);
//        album.setText(Display Album Titele);
//        artist.setText(Display Artist Name);
//        //songTitle.setText(Displlay song Name);
//
//
//        seekBar = (SeekBar) findViewById(R.id.seek);
//        playlist = (Button) findViewById(R.id.playlist);
//        play = (Button) findViewById(R.id.play);
//        next = (Button) findViewById(R.id.next);
//        back = (Button) findViewById(R.id.back);
//        shuffle = (Button) findViewById(R.id.shuffle);
//        repeat = (Button) findViewById(R.id.repeat);
//
//
//
////        MediaMetadataRetriever metaRetriver;
////        byte[] art;
////
////        metaRetriver = new MediaMetadataRetriever();
////        metaRetriver.setDataSource(songsList.get(songIndex).get("songPath"));
////        try {
////            art = metaRetriver.getEmbeddedPicture();
////            Bitmap songImage = BitmapFactory
////                    .decodeByteArray(art, 0, art.length);
//
//
//
//
//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mediaPlayer.isPlaying()){
//                    if(mediaPlayer!=null){
//                        mediaPlayer.pause();
//                        // Changing button image to play button
//                        ---change to right pic----play.setImageResource(R.drawable.btn_play);
//                    }
//                }else{
//                    // Resume song
//                    if(mediaPlayer!=null){
//                        mediaPlayer.start();
//                        // Changing button image to pause button
//                        ----change to right pic---play.setImageResource(R.drawable.btn_pause);
//                    }
//                }
//
//            }
//        });
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // check if next song is there or not
//                if(position < (songsList.size() - 1)){
//                    playSong(position + 1);
//                    position ++;
//                }else{
//                    // play first song
//                    playSong(0);
//                    position = 0;
//                }
//            }
//        });
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(position > 0){
//                    playSong(position - 1);
//                    position --;
//                }else{
//                    // play last song
//                    playSong(songsList.size() - 1);
//                    position = songsList.size() - 1;
//                }
//            }
//        });
//        shuffle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(shuffleVal){
//                    shuffleVal = false;
//                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
//                    ----get image for shuffle-----shuffle.setImageResource(R.drawable.btn_shuffle);
//                }else{
//                    // make repeat to true
//                    shuffleVal= true;
//                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
//                    // make shuffle to false
//                    repeatVal = false;
//                    ---get image for shuffle on---shuffle.setImageResource(R.drawable.btn_shuffle_focused);
//                    ----get image for repeat-----repeat.setImageResource(R.drawable.btn_repeat);
//                }
//            }
//        });
//        repeat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(repeatVal){
//                    repeatVal = false;
//                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
//                    ---get image---repeat.setImageResource(R.drawable.btn_repeat);
//                }else{
//                    // make repeat to true
//                    repeatVal = true;
//                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
//                    // make shuffle to false
//                    shuffleVal = false;
//                    ---get image---repeat.setImageResource(R.drawable.btn_repeat_focused);
//                    ----get image---shuffle.setImageResource(R.drawable.btn_shuffle);
//                }
//            }
//        });
//    playlist.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Intent i = new Intent(getApplicationContext(), ------whatever the playlist class is going to be--------.class);
//            startActivityForResult(i, 100);
//        }
//    });
//    }
//    /**
//     * Receiving song index from playlist view
//     * and play the song
//     * */
//    @Override
//    protected void onActivityResult(int requestCode,
//                                    int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == 100){
//            position = data.getExtras().getInt("songIndex");
//            // play selected song
//            playSong(position);
//        }
//
//    }
//
//    /**
//     * Function to play a song
//     * @param songIndex - index of song
//     * */
//    public void  playSong(int songIndex){
//        // Play song
//        try {
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(songsList.get(songIndex).get("songPath"));
//            //ioException e
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//            // Displaying Song title
//            String song = songsList.get(songIndex).get("songTitle");
//            songTitle.setText(song);
//
//            // Changing Button Image to pause image
//            ---set image----play.setImageResource(R.drawable.btn_pause);
//
//            // set Progress bar values
//            seekBar.setProgress(0);
//            seekBar.setMax(100);
//
//            // Updating progress bar
//            updateProgressBar();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Update timer on seekbar
//     * */
//    public void updateProgressBar() {
//        handler.postDelayed(mUpdateTimeTask, 100);
//    }
//
//    /**
//     * Background Runnable thread
//     * */
//    private Runnable mUpdateTimeTask = new Runnable() {
//        public void run() {
//            long totalDuration = mediaPlayer.getDuration();
//            long currentDuration = mediaPlayer.getCurrentPosition();
//
//            // Displaying Total Duration time
//            rightTime.setText("" + seekHelper.timerConvert(totalDuration));
//            // Displaying time completed playing
//            leftTime.setText(""+seekHelper.timerConvert(currentDuration));
//
//            // Updating progress bar
//            int progress = (int)(seekHelper.progress(currentDuration, totalDuration));
//            //Log.d("Progress", ""+progress);
//            seekBar.setProgress(progress);
//
//            // Running this thread after 100 milliseconds
//            handler.postDelayed(this, 100);
//        }
//    };
//
//    /**
//     *
//     * */
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
//
//    }
//
//    /**
//     * When user starts moving the progress handler
//     * */
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        // remove message Handler from updating progress bar
//        handler.removeCallbacks(mUpdateTimeTask);
//    }
//
//    /**
//     * When user stops moving the progress hanlder
//     * */
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        handler.removeCallbacks(mUpdateTimeTask);
//        int totalDuration = mediaPlayer.getDuration();
//        int currentPosition = seekHelper.progress(seekBar.getProgress(), totalDuration);
//
//        // forward or backward to certain seconds
//        mediaPlayer.seekTo(currentPosition);
//
//        // update timer progress again
//        updateProgressBar();
//    }
//
//    /**
//     * On Song Playing completed
//     * if repeat is ON play same song again
//     * if shuffle is ON play random song
//     * */
//    @Override
//    public void onCompletion(MediaPlayer arg0) {
//
//        // check for repeat is ON or OFF
//        if(repeatVal){
//            // repeat is on play same song again
//            playSong(position);
//        } else if(shuffleVal){
//            // shuffle is on - play a random song
//            Random r = new Random();
//            position = r.nextInt((songsList.size() - 1) - 0 + 1) + 0;
//            playSong(position);
//        } else{
//            // no repeat or shuffle ON - play next song
//            if(position < (songsList.size() - 1)){
//                playSong(position + 1);
//                position ++;
//            }else {
//                // play first song
//                playSong(0);
//                position = 0;
//            }
//        }
//    }
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        mediaPlayer.release();
//        super.onDestroy();
//        mediaPlayer.release();
//        handler.removeCallbacks(mUpdateTimeTask)
//    }
//}
