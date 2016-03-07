package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Currently playing screen. Gives access to additional info
 */
public class PlayFragment extends Fragment {

    //Empty constructor
    public PlayFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_play, container, false);

        // Holds the song
        if (savedInstanceState != null) {
            Bundle bundle = getArguments();

            // show the title
            if (bundle.getParcelable("song") != null) {
                //??
            } else {

            }
        }

        return rootView;
    }

}
