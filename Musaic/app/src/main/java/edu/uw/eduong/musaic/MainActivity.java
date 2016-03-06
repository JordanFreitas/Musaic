package edu.uw.eduong.musaic;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// Displays a list of music on your phone which can be clicked on to play
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "Music";
    private ArrayList<Song> songs; //holds the list of songs to display
    private SongAdapter adapter;   //displays the songs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songs = new ArrayList<Song>();
        getSongs();
    }

    // create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // handle menu click events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_help:
                return true;
            case R.id.action_sort:
                sortSongs();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        String selection = android.provider.MediaStore.Audio.Media.IS_MUSIC + " != 0";  //make sure we only get music
        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumIdColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ALBUM_ID);
            int pathColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.DATA);

            //add songs to list
            do {
                long id = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                long albumId = musicCursor.getLong(albumIdColumn);
                String path = musicCursor.getString(pathColumn);
                Bitmap artwork = null;
                Log.v(TAG, title + id + artist + albumId);

                try {
                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
                    ContentResolver res = getContentResolver();
                    InputStream in = res.openInputStream(uri);
                    artwork = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.v(TAG, "Album artwork not found");
                }

                songs.add(new Song(id, title, artist, albumId, artwork, path));
            }
            while (musicCursor.moveToNext());

            // default sort songs in alphabetical order
            Collections.sort(songs);

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

    // sort songs
    public void sortSongs() {
        Collections.reverse(songs);
        adapter.notifyDataSetChanged();

        TextView textView = (TextView) findViewById(R.id.action_sort);
        if (textView.equals("A-Z")) {
            textView.setText("Z-A");
        } else {
            textView.setText("A-Z");

        }


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
