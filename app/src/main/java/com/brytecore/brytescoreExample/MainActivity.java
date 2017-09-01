package com.brytecore.brytescoreExample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.brytecore.Brytescore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    // ------------------------------------ static variables ------------------------------------ //
    private static final String TAG = MainActivity.class.getSimpleName();

    // ------------------------------------ dynamic variables ----------------------------------- //
    // Bools for local status of dev and debug mode, used to toggle state with buttons
    private Boolean devMode = true;


    // Initialize the API Manager with your API key.
    Brytescore brytescore = new Brytescore("abc123");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable dev mode - logs API calls instead of making HTTP request
        brytescore.devMode(devMode);
        ToggleButton toggle = ((ToggleButton)findViewById(R.id.devModeButton));
        toggle.setChecked(devMode);

        // Update the API Key label to show our API key for debugging
        final TextView textViewToChange = (TextView) findViewById(R.id.apikey);
        textViewToChange.setText("Your API Key:" + brytescore.getAPIKey());
    }

    /** Toggle devMode bool, pass to _apiManager, update button title and color */
    public void toggleDevMode(View view) {
        devMode = !devMode;
        brytescore.devMode(devMode);

        ToggleButton toggle = ((ToggleButton)findViewById(R.id.devModeButton));
        toggle.setChecked(devMode);
    }

    /** Called when the user taps the Track Page View button */
    public void trackPageView(View view) {
        Log.d(TAG, "Calling trackPageView");

        HashMap<String, String> pageViewData = new HashMap<String, String>();
        brytescore.pageView(pageViewData);
    }
}
