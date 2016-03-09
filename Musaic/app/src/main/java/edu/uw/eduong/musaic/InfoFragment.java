package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    //Empty constructor
    public InfoFragment() {}

    private ArrayAdapter<String> adapter;
    private static final String TAG = "InputControl";

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //controller
        adapter = new ArrayAdapter<>(getActivity(), R.layout.fragment_info, R.id.textView);

        //supports either ListView or GridView
        /*AdapterView textView = (AdapterView) getView().findViewById(R.id.textView);
        textView.setAdapter(adapter);*/
    }




    // gathers song data
    public class songTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String zipCode = params[0];
            //constructs the url for the openweathermap API request
            String urlString = "";
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http") //Forms the url one segment at a time
                        .authority("api.musixmatch.com/ws/1.1/track.search")
                        .appendQueryParameter("q_track", BuildConfig.MUSIXMATCH_API_KEY)
                        .appendQueryParameter("q_artist", BuildConfig.MUSIXMATCH_API_KEY)
                        .appendQueryParameter("q_has_lyrics", "1")
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
                    }
                }
            }

            if(results.contains("\"available\":0}")) {
                    //TextView t = (TextView) getView().findViewById(R.id.textView); //if song not in database
                    //t.setText("Sorry, this song's lyrics are unavailable at this time.");

                return null;
            } else {
                adapter.clear();
                JSONArray songInfoList = null; // list that will contain all song info

                // get string as json
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    songInfoList = jsonObject.getJSONArray("list");
                } catch (JSONException e) {
                    Log.v(TAG, "JSON exception", e);
                }

                String trackID = ""; //tracks the song id number
                // creates a list of song info
                for (int i = 0; i < songInfoList.length(); i++) {
                    try {
                        //Accesses the date and time information
                        JSONObject track = songInfoList.getJSONObject(i);
                        JSONArray trackNumber = track.getJSONArray("track_id");
                        trackID = trackNumber.get(0).toString();
                    } catch (JSONException e) {
                        Log.v(TAG, "JSON exception", e);
                    }
                }

                // gets the lyrics
                String urlString2 = "";
                try {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("http") //Forms the url one segment at a time
                            .authority("api.musixmatch.com/ws/1.1/track.lyrics.get")
                            .appendQueryParameter("track_id", trackID)
                            .appendQueryParameter("apikey", BuildConfig.MUSIXMATCH_API_KEY);
                    urlString2 = builder.build().toString();
                } catch (Exception e) {
                    return null;
                }

                //HttpURLConnection urlConnection = null;
               reader = null;

                //String results = null;

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
                        }
                    }
                }
            }

            return results;
        }

        // Occurs after API calls, sends the track id
        protected void onPostExecute(String results){
            if(results.contains("\"body\":[]}}")) {
                TextView t = (TextView) getView().findViewById(R.id.textView); //if song not in database
                t.setText("Sorry, this song's lyrics are unavailable at this time.");
            } else {
                adapter.clear();
                JSONArray songBody = null; // list that will contain all song info
                JSONArray songLyrics = null;
                JSONArray songLyricsBody = null;

                // get string as json
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    songBody = jsonObject.getJSONArray("body");
                    songLyrics = songBody.getJSONArray(0);
                    songLyricsBody = songLyrics.getJSONArray(6); // location of lyrics in JSONArray
                    TextView t = (TextView) getView().findViewById(R.id.textView);
                    t.setText(songLyricsBody.toString());
                } catch (JSONException e) {
                    Log.v(TAG, "JSON exception", e);
                }
            }
        }
    }
}
