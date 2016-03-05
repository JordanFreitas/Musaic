package edu.uw.eduong.musaic;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom array adapter for displaying song lists
 */
public class SongAdapter extends ArrayAdapter<Song> {
    private ArrayList<Song> songs;

    public SongAdapter(Context context, int textViewResource, ArrayList<Song> songs) {
        super(context, textViewResource, songs);
        this.songs = songs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.song_item, null);
        }

        TextView textView = (TextView) view.findViewById(R.id.songItem);

        textView.setText(songs.get(position).getTitle());
        return view;
    }
}
