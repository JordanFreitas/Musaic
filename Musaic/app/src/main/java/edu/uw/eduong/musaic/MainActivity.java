package edu.uw.eduong.musaic;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

// Displays a list of music on your phone
public class MainActivity extends AppCompatActivity implements GetSongsFragment.displayer, MainFragment.songSelector {
    private ArrayList<Song> songs; //holds the list of songs
    private static final String GET_SONGS_FRAGMENT = "get_songs";
    private static final String SONGS_LIST = "songs_list"; //Songs list tag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // run the fragment to retrieve the songs
        GetSongsFragment getSongs = (GetSongsFragment) getFragmentManager().findFragmentByTag(GET_SONGS_FRAGMENT);
        if (getSongs == null && songs.isEmpty()) {
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
        if (getSongs == null && songs.isEmpty()) {
            getSongs = new GetSongsFragment();
            getFragmentManager().beginTransaction()
                    .add(getSongs, GET_SONGS_FRAGMENT)
                    .commit();
        } else {
            displaySongs(songs);
        }
    }
}
