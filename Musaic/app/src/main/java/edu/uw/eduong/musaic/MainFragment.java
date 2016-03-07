package edu.uw.eduong.musaic;

import android.Manifest;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

// Displays a list of music on your phone
public class MainFragment extends Fragment implements SongAdapter.SongAdapterClick {
    private static final String TAG = "Musaic";
    private ArrayList<Song> songs; //holds the list of songs to display
    private SongAdapter adapter;   //displays the songs
    private Menu menu;             //menu

    // Empty constructor
    public MainFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        songs = new ArrayList<>();

        // controller
        adapter = new SongAdapter(
                getActivity(), R.layout.song_item, songs);
        adapter.setClick(this);

        //support ListView or GridView
        AdapterView listView = (AdapterView)rootView.findViewById(R.id.listView);
        listView.setAdapter(adapter);

        getSongs();

        return rootView;
    }

    // create menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();   // clears menu as it is recreated when rotating
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;
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

    public class songDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // from http://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764
            ContentResolver musicResolver = getActivity().getContentResolver();
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


                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                    Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
                    ContentResolver res = getActivity().getContentResolver();
                    artwork = decodeSampledBitmapFromResource(uri, res, 100, 100);

                    songs.add(new Song(id, title, artist, albumId, artwork, path));
                }
                while (musicCursor.moveToNext());

                // default sort songs in alphabetical order
                Collections.sort(songs);

                musicCursor.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.notifyDataSetChanged();
        }
    }

    public static Bitmap decodeSampledBitmapFromResource(Uri uri, ContentResolver res, int reqWidth, int reqHeight) {
        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = res.openInputStream(uri);

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            if (in != null) {
                try {
                    in.close();
                    in = res.openInputStream(uri);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                }
                catch(IOException ex) {
                    Log.v(TAG, "Couldn't close input stream");
                }
            }
        } catch (Exception e) {
            Log.v(TAG, "Album artwork not found");
        } finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch(IOException ex) {
                    Log.v(TAG, "Couldn't close input stream");
                }
            }
        }

        return bitmap;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // Get list of songs on device
    public void getSongs() {
        if (isExternalStorageReadable()) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    songDataTask getSongs = new songDataTask();
                    getSongs.execute();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 255);
                }
            } else {
                songDataTask getSongs = new songDataTask();
                getSongs.execute();
            }
        } else {
            Toast.makeText(getActivity(),
                    "Cannot read external storage", Toast.LENGTH_LONG).show();
            Log.v(TAG, "Cannot read external storage");
        }
    }

//    // Display list
//    public void displayList() {
//        // from http://code.tutsplus.com/tutorials/create-a-music-player-on-android-project-setup--mobile-22764
//        ContentResolver musicResolver = getActivity().getContentResolver();
//        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        String selection = android.provider.MediaStore.Audio.Media.IS_MUSIC + " != 0";  //make sure we only get music
//        Cursor musicCursor = musicResolver.query(musicUri, null, selection, null, null);
//
//        if (musicCursor != null && musicCursor.moveToFirst()) {
//            //get columns
//            int titleColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media.TITLE);
//            int idColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media._ID);
//            int artistColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media.ARTIST);
//            int albumIdColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media.ALBUM_ID);
//            int pathColumn = musicCursor.getColumnIndex
//                    (android.provider.MediaStore.Audio.Media.DATA);
//
//            //add songs to list
//            do {
//                long id = musicCursor.getLong(idColumn);
//                String title = musicCursor.getString(titleColumn);
//                String artist = musicCursor.getString(artistColumn);
//                long albumId = musicCursor.getLong(albumIdColumn);
//                String path = musicCursor.getString(pathColumn);
//                Bitmap artwork = null;
//                Log.v(TAG, title + id + artist + albumId);
//
//                try {
//                    Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
//                    Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
//                    ContentResolver res = getActivity().getContentResolver();
//                    InputStream in = res.openInputStream(uri);
//                    artwork = BitmapFactory.decodeStream(in);
//                } catch (Exception e) {
//                    Log.v(TAG, "Album artwork not found");
//                }
//
//                songs.add(new Song(id, title, artist, albumId, artwork, path));
//            }
//            while (musicCursor.moveToNext());
//
//            // default sort songs in alphabetical order
//            Collections.sort(songs);
//
//            musicCursor.close();
//        }
//    }

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

        MenuItem menuItem = menu.findItem(R.id.action_sort);
        String text = menuItem.getTitle().toString();
        if (text.equals("A-Z")) {
            menuItem.setTitle("Z-A");
        } else {
            menuItem.setTitle("A-Z");

        }
    }

    // requests permission to access external storage to get songs
    // http://stackoverflow.com/questions/33162152/storage-permission-error-in-marshmallow/33162201
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            songDataTask getSongs = new songDataTask();
            getSongs.execute();
        } else {
            Toast.makeText(getActivity(),
                    "Cannot display songs, please enable permission", Toast.LENGTH_LONG).show();
            Log.v(TAG, "Cannot read external storage, permission denied");
        }
    }

    // when song item is clicked
    @Override
    public void songClick(int position) {
        //dostuff;
        Song song = songs.get(position);
        ((MainActivity) getActivity()).songSelected(song);
    }
}
