package edu.uw.eduong.musaic;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
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
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "Music";
    private ArrayList<Song> songs; //holds the list of songs to display
    private SongAdapter adapter;   //displays the songs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSongs();
    }

    // Get list of songs on device
    public void getSongs() {
        if (isExternalStorageReadable()) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    displayList();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 255);
                }
            } else {
                displayList();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Cannot read external storage", Toast.LENGTH_LONG).show();
            Log.v(TAG, "Cannot read external storage");
        }
    }

    // Display list
    public void displayList() {
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
                songs.add(new Song(id, title, artist));
            }
            while (musicCursor.moveToNext());

            // set view of playlist
            // controller
            adapter = new SongAdapter(
                    this, R.layout.song_item, songs);

            //support ListView or GridView
            AdapterView listView = (AdapterView)findViewById(R.id.listView);
            listView.setAdapter(adapter);
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

    // http://stackoverflow.com/questions/33162152/storage-permission-error-in-marshmallow/33162201
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            displayList();
        } else {
            Toast.makeText(getApplicationContext(),
                    "Cannot display songs, please enable permission", Toast.LENGTH_LONG).show();
            Log.v(TAG, "Cannot read external storage, permission denied");
        }
    }
}
