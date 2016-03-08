package edu.uw.eduong.musaic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// Displays a list of music on your phone
public class MainActivity extends AppCompatActivity implements MainFragment.songSelector {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.container) == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.pane_left, new MainFragment(), "main")
                    .add(R.id.pane_right, new PlayFragment())
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainFragment(), "main")
                    .commit();
        }
    }

    // for song selector on song click
    @Override
    public void songSelected(Song song) {
        PlayFragment play = new PlayFragment();

        // data song to a bundle
        Bundle bundle = new Bundle();
        bundle.putParcelable("song", song);

        play.setArguments(bundle);

        // Show Main List and Play if dual view, else just the play
        if (findViewById(R.id.container) == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.pane_left, play)
                    .replace(R.id.pane_right, new MainFragment(), "main")
                    .addToBackStack(null)
                    .commit();
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, play)
                    .addToBackStack(null)
                    .commit();
        }
    }

    // for back button
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
