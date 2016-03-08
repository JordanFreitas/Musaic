package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Currently playing screen. Gives access to additional info
 */
public class PlayFragment extends Fragment {
    private ArrayList<Song> songs;

    //Empty constructor
    public PlayFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        // Holds the songs
        songs = new ArrayList<>();
        Bundle bundle = getArguments();

        // get the songs
        if (bundle != null) {
            songs = bundle.getParcelableArrayList("songs");
            if (songs != null) {
                Log.v("PLAYMUSIC", "YES");
            } else {
                Log.v("PLAYMUSIC", "???");
                songs.isEmpty();
            }
        }


        return rootView;
    }

}
