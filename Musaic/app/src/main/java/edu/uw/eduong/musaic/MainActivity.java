package edu.uw.eduong.musaic;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

// Displays a list of music on your phone which can be clicked on to play
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Music";
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
        if (isExternalStorageReadable()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                // from http://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764
                ContentResolver musicResolver = getContentResolver();
                Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

                if (musicCursor != null && musicCursor.moveToFirst()) {
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
                        Log.v(TAG, title);
                        adapter.add(new Song(id, title, artist));
                    }
                    while (musicCursor.moveToNext());
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        "Cannot access external storage", Toast.LENGTH_LONG).show();
                Log.v(TAG, "Cannot access external storage");
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Cannot read external storage", Toast.LENGTH_LONG).show();
            Log.v(TAG, "Cannot read external storage");
        }
    }

    /* Checks if external storage is available to at least read */
    // from https://developer.android.com/guide/topics/data/data-storage.html
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
