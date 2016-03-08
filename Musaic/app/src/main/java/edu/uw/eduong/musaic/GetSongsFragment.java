package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.os.Bundle;

import java.util.ArrayList;

/**
* Gets the lists of songs. This list is retained across configuration changes
 * */
public class GetSongsFragment extends Fragment {
    ArrayList<Song> songs;

    // Empty constructor
    public GetSongsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songs = new ArrayList<>();

        setRetainInstance(true);
    }
}
