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
    private Boolean debugMode = true;


    // Initialize the API Manager with your API key.
    Brytescore brytescore = new Brytescore("abc123");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable dev mode - logs API calls instead of making HTTP request
        brytescore.devMode(devMode);
        ToggleButton devModeToggle = ((ToggleButton)findViewById(R.id.devModeButton));
        devModeToggle.setChecked(devMode);

        // Enable debug mode - turns on console logs
        brytescore.debugMode(debugMode);
        ToggleButton debugModeToggle = ((ToggleButton)findViewById(R.id.debugModeButton));
        debugModeToggle.setChecked(debugMode);

        // Update the API Key label to show our API key for debugging
        final TextView textViewToChange = (TextView) findViewById(R.id.apikey);
        textViewToChange.setText("Your API Key:" + brytescore.getAPIKey());
    }

    /** Called when the user taps the Track Page View button */
    public void trackPageView(View view) {
        Log.d(TAG, "Calling trackPageView");

        HashMap<String, String> pageViewData = new HashMap<String, String>();
        brytescore.pageView(pageViewData);
    }

    /** Toggle devMode bool, pass to _apiManager, update button title and color */
    public void toggleDevMode() {
        devMode = !devMode;
        brytescore.devMode(devMode);

        ToggleButton devModeToggle = ((ToggleButton)findViewById(R.id.devModeButton));
        devModeToggle.setChecked(devMode);

        // If devMode is now on and debugMode was off, debugMode is now on.
        // Only update if debugMode wasn't already on.
        if (devMode && ! debugMode) {
            debugMode = true;
            ToggleButton debugModeToggle = ((ToggleButton)findViewById(R.id.debugModeButton));
            debugModeToggle.setChecked(debugMode);
        }
    }

    /** Toggle devMode bool, pass to _apiManager, update button title and color */
    public void toggleDebugMode() {
        debugMode = !debugMode;
        brytescore.debugMode(debugMode);

        ToggleButton toggle = ((ToggleButton)findViewById(R.id.debugModeButton));
        toggle.setChecked(debugMode);
    }
}
