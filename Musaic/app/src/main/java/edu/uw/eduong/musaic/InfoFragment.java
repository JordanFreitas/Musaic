package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Displays lyric sheet and/or wiki page (when possible)
 */
public class InfoFragment extends Fragment {
    private static final String SONG = "SONG";

    //Empty constructor
    public InfoFragment() {}

    private ArrayAdapter<String> adapter;
    private static final String TAG = "InputControl";

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        //controller
        adapter = new ArrayAdapter<>(getActivity(), R.layout.fragment_info, R.id.textView);

        //supports either ListView or GridView
        /*AdapterView textView = (AdapterView) getView().findViewById(R.id.textView);
        textView.setAdapter(adapter);*/

        Bundle bundle = getArguments();
        Song song = bundle.getParcelable(SONG);

        songTask getInfo = new songTask();
        getInfo.execute(song);

        return rootView;
    }

    // gathers song data
    public class songTask extends AsyncTask<Song, Void, String> {
        protected String doInBackground(Song... params) {
            Song song = params[0];

            //constructs the url for the musixmatch API request
            String urlString = "";
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http") //Forms the url one segment at a time
                        .authority("api.musixmatch.com")
                        .appendPath("ws")
                        .appendPath("1.1")
                        .appendPath("track.search")
                        .appendQueryParameter("q_track", song.getTitle())
                        .appendQueryParameter("q_artist", song.getArtist())
                        .appendQueryParameter("f_has_lyrics", "1")
                        .appendQueryParameter("apikey", BuildConfig.MUSIXMATCH_API_KEY);
                urlString = builder.build().toString();
            } catch (Exception e) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String results = null;

            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                if (builder.length() == 0) {
                    return null;
                }

                results = builder.toString();
            } catch (IOException e) {
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.v(TAG, "Exception" + e);
                    }
                }
            }


            // song track id and get lyrics
            int track_id = -1;
            try {
                JSONObject jsonObject = new JSONObject(results);
                JSONObject body = jsonObject.getJSONObject("message").getJSONObject("body");
                JSONArray track_list = body.getJSONArray("track_list");
                JSONObject tracks = track_list.getJSONObject(0);
                JSONObject track = tracks.getJSONObject("track");
                track_id = track.getInt("track_id"); // location of track id in JSONArray

            } catch (JSONException e) {
                Log.v(TAG, "JSON exception", e);
            }
            if (track_id != -1) {
                // gets the lyrics
                String urlString2 = "";
                try {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http") //Forms the url one segment at a time
                            .authority("api.musixmatch.com")
                            .appendPath("ws")
                            .appendPath("1.1")
                            .appendPath("track.lyrics.get")
                            .appendQueryParameter("track_id", Integer.toString(track_id))
                            .appendQueryParameter("apikey", BuildConfig.MUSIXMATCH_API_KEY);
                    urlString2 = builder.build().toString();
                } catch (Exception e) {
                    return null;
                }

                urlConnection = null;
                reader = null;

                try {
                    URL url = new URL(urlString2);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    if (builder.length() == 0) {
                        return null;
                    }
                    results = builder.toString();
                } catch (IOException e) {
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.v(TAG, "Exception" + e);
                        }
                    }
                }
            }

            return results;
        }

        // Occurs after API calls, sends the track id
        protected void onPostExecute(String results){
            String songLyricsBody = "Sorry, this song's lyrics are unavailable at this time.";
            if (results != null) {
                adapter.clear();

                // get string as json
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    JSONObject songBody = jsonObject.getJSONObject("message").getJSONObject("body");
                    JSONObject songLyrics = songBody.getJSONObject("lyrics");
                    songLyricsBody = songLyrics.getString("lyrics_body"); // location of lyrics in JSONArray

                } catch (JSONException e) {
                    Log.v(TAG, "JSON exception", e);
                }
            }

            TextView t = (TextView) getActivity().findViewById(R.id.textView);
            t.setText(songLyricsBody);
        }
    }
}
