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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Displays lyric sheet and/or wiki page (when possible)
 */
public class InfoFragment extends Fragment {

    //Empty constructor
    public InfoFragment() {}

    // gathers song data
    public class songTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String zipCode = params[0];
            //constructs the url for the openweathermap API request
            String urlString = "";
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http") //Forms the url one segment at a time
                        .authority("http://api.musixmatch.com/ws/1.1/track.search")
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
            return results;
        }


    }
}
