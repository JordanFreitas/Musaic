package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;

// Displays a list of music on your phone
public class MainFragment extends Fragment implements SongAdapter.SongAdapterClick {
    private ArrayList<Song> songs; //holds the list of songs to display
    private SongAdapter adapter;   //displays the songs
    private static final String SONGS_LIST = "songs_list";  //Songs list tag

    // Empty constructor
    public MainFragment() {}

    public interface songSelector {
        void songSelected(int position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // holds the songs
        songs = new ArrayList<>();
        Bundle bundle = getArguments();

        // get the songs
        if (bundle != null) {
            songs = bundle.getParcelableArrayList(SONGS_LIST);
            if (songs == null) {
                songs = new ArrayList<>();
            }
        }

        // controller
        adapter = new SongAdapter(
                getActivity(), R.layout.song_item, songs);
        adapter.setClick(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        songSelector callback;
        try {
            callback = (songSelector) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement songSelector");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //support ListView or GridView
        AdapterView listView = (AdapterView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        ImageView sort = (ImageView)rootView.findViewById(R.id.action_sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortSongs();
            }
        });


        return rootView;
    }

    // sort songs
    public void sortSongs() {
        Collections.reverse(songs);
        adapter.notifyDataSetChanged();
    }

    // when song item is clicked
    @Override
    public void songClick(int position) {
        ((songSelector) getActivity()).songSelected(position);
    }
}