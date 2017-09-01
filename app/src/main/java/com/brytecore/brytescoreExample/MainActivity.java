package com.brytecore.brytescoreExample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.brytecore.Brytescore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Initialize the API Manager with your API key.
    Brytescore brytescore = new Brytescore("abc123");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Update the API Key label to show our API key for debugging
        final TextView textViewToChange = (TextView) findViewById(R.id.apikey);
        textViewToChange.setText("Your API Key:" + brytescore.getAPIKey());
    }

    /** Called when the user taps the Track Page View button */
    public void trackPageView(View view) {
        Log.d(TAG, "Calling trackPageView");
        brytescore.pageView();
    }
}
