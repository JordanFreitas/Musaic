package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
    private static final String POSITION = "position";
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
                        .authority("api.musixmatch.com/ws/1.1/track.search")
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
                Log.v("WTF", "WHT4");

                urlConnection.connect();

                Log.v("WTF", "I q.q");

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null) {
                    Log.v("WTF", "I q.q0");

                    return null;
                }
                Log.v("WTF", "I q.2");

                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    Log.v("WTF", "I q.q 4");

                    builder.append(line);
                }

                if (builder.length() == 0) {
                    Log.v("WTF", "I q.q4");

                    return null;
                }

                results = builder.toString();
                Log.v("WTF", results);

            } catch (IOException e) {
                Log.v("fuck", "WHYY " + e.toString());
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }


            // song track id and get lyrics
            String track_id = null;
            try {
                JSONObject jsonObject = new JSONObject(results);
                JSONArray message = jsonObject.getJSONArray("message");
                JSONArray body = message.getJSONArray(1);
                JSONArray track_list = body.getJSONArray(0);
                track_id = track_list.getString(0); // location of lyrics in JSONArray

            } catch (JSONException e) {
                Log.v(TAG, "JSON exception", e);
            }
            if (track_id != null) {
                // gets the lyrics
                String urlString2 = "";
                try {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http") //Forms the url one segment at a time
                            .authority("api.musixmatch.com/ws/1.1/track.lyrics.get")
                            .appendQueryParameter("track_id", track_id)
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
                    Log.v("WTF2", results);
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

                        }
                    }
                }
            } else {
                return results;
            }

            return results;
        }

        // Occurs after API calls, sends the track id
        protected void onPostExecute(String results){
            if (results != null) {
                adapter.clear();
                String songLyricsBody = null;

                // get string as json
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    JSONArray message = jsonObject.getJSONArray("message");
                    JSONArray songBody = message.getJSONArray(1);
                    JSONArray songLyrics = songBody.getJSONArray(0);
                    songLyricsBody = songLyrics.getString(6); // location of lyrics in JSONArray

                } catch (JSONException e) {
                    Log.v(TAG, "JSON exception", e);
                }

                if (songLyricsBody != null) {
                    TextView t = (TextView) getActivity().findViewById(R.id.textView);
                    t.setText(songLyricsBody);
                } else {
                    TextView t = (TextView) getActivity().findViewById(R.id.textView); //if song not in database
                    t.setText("Sorry, this song's lyrics are unavailable at this time.");
                }
            }
        }
    }
}
