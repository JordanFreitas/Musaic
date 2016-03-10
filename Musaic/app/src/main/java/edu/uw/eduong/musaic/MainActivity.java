package edu.uw.eduong.musaic;

import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

// Displays a list of music on your phone
public class MainActivity extends AppCompatActivity implements GetSongsFragment.displayer,
        MainFragment.songSelector,
        PlayFragment.songInfo,
        PlayFragment.artistInfo{
    private ArrayList<Song> songs; //holds the list of songs
    private static final String GET_SONGS_FRAGMENT = "get_songs";
    private static final String SONGS_LIST = "songs_list"; //Songs list tag
    private static final String POSITION = "position";
    private static final String SONG = "SONG";
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction(MediaPlayerService.ACTION_PLAY);
        startService(intent);

        // run the fragment to retrieve the songs
        GetSongsFragment getSongs = (GetSongsFragment) getFragmentManager().findFragmentByTag(GET_SONGS_FRAGMENT);
        if (getSongs == null || songs == null) {
            getSongs = new GetSongsFragment();
            getFragmentManager().beginTransaction()
                    .add(getSongs, GET_SONGS_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void displaySongs(ArrayList<Song> list) {
        songs = list;
        MainFragment main = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SONGS_LIST, songs);
        main.setArguments(bundle);
        
        if (findViewById(R.id.container) == null) {
            //default to first song if non selected
//            PlayFragment play = new PlayFragment();
//            Bundle bundlePlay = new Bundle();
//            bundlePlay.putParcelableArrayList(SONGS_LIST, songs);
//            bundlePlay.putInt(POSITION, 0);
//            play.setArguments(bundlePlay);

            getFragmentManager().beginTransaction()
                    .add(R.id.pane_left, main)
                    .add(R.id.pane_right, new PlayFragment())
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, main)
                    .commit();
        }
    }

    // for song selector on song click, play the song
    @Override
    public void songSelected(int position) {
        PlayFragment play = new PlayFragment();
        Bundle bundlePlay = new Bundle();
        bundlePlay.putParcelableArrayList(SONGS_LIST, songs);
        bundlePlay.putInt(POSITION, position);
        play.setArguments(bundlePlay);

        MainFragment main = new MainFragment();
        Bundle bundleMain = new Bundle();
        bundleMain.putParcelableArrayList(SONGS_LIST, songs);
        main.setArguments(bundleMain);

        // Show Main List and Play if dual view, else just the play
        if (findViewById(R.id.container) == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.pane_left, play)
                    .replace(R.id.pane_right, main)
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, play)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // display fragment with song lyrics
    @Override
    public void getSongInfo(int position) {
        InfoFragment info = new InfoFragment();
        Bundle bundle = new Bundle();
        Song song = songs.get(position);
        bundle.putParcelable(SONG, song);
        info.setArguments(bundle);

        // Show play and song lyrics if dual view, else just the lyrics
        if (findViewById(R.id.container) == null) {
            PlayFragment play = new PlayFragment();
            Bundle bundlePlay = new Bundle();
            bundlePlay.putParcelableArrayList(SONGS_LIST, songs);
            bundlePlay.putInt(POSITION, position);
            play.setArguments(bundlePlay);

            getFragmentManager().beginTransaction()
                    .replace(R.id.pane_left, play)
                    .replace(R.id.pane_right, info)
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, info)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // display fragment with artist wiki page
    public void getArtistInfo(int position) {
        WikiFragment info = new WikiFragment();
        Bundle bundle = new Bundle();
        Song song = songs.get(position);
        bundle.putParcelable(SONG, song);
        info.setArguments(bundle);

        // Show play and song info if dual view, else just the info
        if (findViewById(R.id.container) == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.pane_right, info)
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, info)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // for back button
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // run the fragment to retrieve the songs
        GetSongsFragment getSongs = (GetSongsFragment) getFragmentManager().findFragmentByTag(GET_SONGS_FRAGMENT);
        if (getSongs == null || songs == null) {
            getSongs = new GetSongsFragment();
            getFragmentManager().beginTransaction()
                    .add(getSongs, GET_SONGS_FRAGMENT)
                    .commit();
        } else {
            displaySongs(songs);
        }
    }

//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//        savedInstanceState.putParcelableArrayList("songsList", songs);
//        // etc.
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
//        songs = savedInstanceState.getParcelableArrayList("songsList");
//    }
}
