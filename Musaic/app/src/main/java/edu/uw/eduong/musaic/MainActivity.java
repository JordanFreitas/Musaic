package edu.uw.eduong.musaic;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

// Displays a list of music on your phone which can be clicked on to play
public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<Song> adapter;   // holds the list of songs to display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //controller
        adapter = new ArrayAdapter<>(
                this, R.layout.song_item, R.id.songItem);

        //support ListView or GridView
        AdapterView listView = (AdapterView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        getSongs();
    }

    // Get list of songs on device
    public void getSongs() {
        // from http://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                Log.v("Music", title);
                adapter.add(new Song(id, title, artist));
            }
            while (musicCursor.moveToNext());
        }
    }
}
