package edu.uw.eduong.musaic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom array adapter for displaying song lists
 */
public class SongAdapter extends ArrayAdapter<Song> {
    private static final String TAG = "Music";
    private SongAdapterClick click;
    private ArrayList<Song> songs;

    public SongAdapter(Context context, int textViewResource, ArrayList<Song> songs) {
        super(context, textViewResource, songs);
        this.songs = songs;
    }

    // display each song item in the list
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.song_item, null);
        }

        final Song song = songs.get(position);

        // set song title
        TextView textTitle = (TextView) view.findViewById(R.id.songTitle);
        textTitle.setText(song.getTitle());

        // set song artist
        TextView textArtist = (TextView) view.findViewById(R.id.songArtist);
        textArtist.setText(song.getArtist());

        // set song album art
        ImageView imageView = (ImageView) view.findViewById(R.id.songArt);
        imageView.setImageBitmap(song.getAlbumArt());

        // TODO: TO FIX ADDITIONAL OPTIONS FOR ITEMS
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.menu_song, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.action_lyrics:
                        return true;
                    case R.id.action_wiki:
                        return true;
                    default:
                        return onMenuItemClick(item);
                }
            }
        });

        // set the on click action of the item
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "hi");
                // swap the fragments
                click.songClick(position);
            }
        });

        return view;
    }

    public void setClick(SongAdapterClick click) {
        this.click = click;
    }

    public interface SongAdapterClick {
        void songClick(int position);
    }
}