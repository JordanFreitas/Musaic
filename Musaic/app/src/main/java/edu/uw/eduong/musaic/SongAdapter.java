package edu.uw.eduong.musaic;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Custom array adapter for displaying song lists
 */
public class SongAdapter extends ArrayAdapter<Song> {
    private static final String TAG = "Music";
    private ArrayList<Song> songs;

    public SongAdapter(Context context, int textViewResource, ArrayList<Song> songs) {
        super(context, textViewResource, songs);
        this.songs = songs;
    }

    // display each song item in the list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.song_item, null);
        }

        Song song = songs.get(position);

        // set song title
        TextView textView = (TextView) view.findViewById(R.id.songTitle);
        textView.setText(song.getTitle());

        // set song album art
        ImageView imageView = (ImageView) view.findViewById(R.id.songArt);
        imageView.setImageBitmap(song.getAlbumArt());

        // set the on click action of the item
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "hi");
            }
        });

        return view;
    }

}