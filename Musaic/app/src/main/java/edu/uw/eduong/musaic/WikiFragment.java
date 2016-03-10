package edu.uw.eduong.musaic;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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
import java.util.Iterator;


/**
 * Displays lyric sheet and/or wiki page (when possible)
 */
public class WikiFragment extends Fragment {
    private static final String SONG = "SONG";

    //Empty constructor
    public WikiFragment() {}

    private ArrayAdapter<String> adapter;
    private static final String TAG = "InputControl";

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        //controller
        adapter = new ArrayAdapter<>(getActivity(), R.layout.fragment_info, R.id.textView);

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

            //constructs the url for the wikipedia API request
            String urlString = "";
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https") //Forms the url one segment at a time
                        .authority("en.wikipedia.org")
                        .appendPath("w")
                        .appendPath("api.php")
                        .appendQueryParameter("action", "query")
                        .appendQueryParameter("prop", "extracts")
                        .appendQueryParameter("format", "json")
                        .appendQueryParameter("exintro", "")
                        .appendQueryParameter("titles", song.getArtist());
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
            return results;
        }

        // Occurs after API calls, sends the track id
        protected void onPostExecute(String results){
            String wikiBody = "Sorry, this artist's wiki page is unavailable at this time.";
            if (results != null) {
                adapter.clear();

                // get string as json
                try {
                    JSONObject jsonObject = new JSONObject(results);
                    JSONObject wBody = jsonObject.getJSONObject("query");
                    JSONObject pages = wBody.getJSONObject("pages");
                    Iterator<String> keys = pages.keys();
                    String key = keys.next();
                    JSONObject bandWiki = pages.getJSONObject(key);
                    wikiBody = bandWiki.getString("extract"); // location of wiki text in JSONArray

                } catch (JSONException e) {
                    Log.v(TAG, "JSON exception", e);
                }
            }

            TextView t = (TextView) getActivity().findViewById(R.id.textView);
            t.setText(Html.fromHtml(wikiBody));
        }
    }
}