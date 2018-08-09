package com.brytecore.brytescoreExample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private Boolean impersonationMode = false;
    private Boolean validationMode = false;


    // Initialize the API Manager with your API key.
    Brytescore brytescore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brytescore = new Brytescore(getApplicationContext(), "abc123");

        // Enable dev mode - logs API calls instead of making HTTP request
        brytescore.devMode(devMode);
        ToggleButton devModeToggle = ((ToggleButton)findViewById(R.id.devModeButton));
        devModeToggle.setChecked(devMode);

        // Enable debug mode - turns on console logs
        brytescore.debugMode(debugMode);
        ToggleButton debugModeToggle = ((ToggleButton)findViewById(R.id.debugModeButton));
        debugModeToggle.setChecked(debugMode);

        // Load real estate package
        brytescore.load("realestate");

        // Update the API Key label to show our API key for debugging
        final TextView textViewToChange = (TextView) findViewById(R.id.apikey);
        textViewToChange.setText("Your API Key:" + brytescore.getAPIKey());
    }

    @Override
    protected void onResume() {
        super.onResume();

        HashMap<String, Object> pageViewData = new HashMap<String, Object>();
        pageViewData.put("test_key", "test_data");

        brytescore.pageView(pageViewData);
    }

    @Override
    protected void onPause() {
        super.onPause();

        brytescore.killSession();
    }

    /**
     * Called when the user taps the Track Page View button
     * Example usage of tracking a page view
     */
    public void trackPageView(View view) {
        HashMap<String, Object> pageViewData = new HashMap<String, Object>();
        pageViewData.put("test_key", "test_data");

        brytescore.pageView(pageViewData);
    }

    /**
     * Called when the user taps the Track Registered Account button
     * Example usage of tracking an account registration
     */
    public void trackRegisteredAccount(View view) {
        final HashMap<String, Integer> userAccount = new HashMap<String, Integer>() {{
            put("id", 1);
        }};

        HashMap<String, Object> registeredAccountData = new HashMap<String, Object>() {{
            put("userAccount", userAccount);
            put("isLead", false);
        }};
        brytescore.registeredAccount(registeredAccountData);
    }

    /**
     * Called when the user taps the Track Authenticated button
     * Example usage of tracking authentication
     */
    public void trackAuthenticated(View view) {
        final HashMap<String, Integer> userAccount = new HashMap<String, Integer>() {{
            put("id", 1);
        }};

        HashMap<String, Object> authenticatedData = new HashMap<String, Object>() {{
            put("userAccount", userAccount);
        }};
        brytescore.authenticated(authenticatedData);
    }

    /**
     * Called when the user taps the Track Submitted Form button
     * Example usage of tracking a submitted form
     */
    public void trackSubmittedForm(View view) {
        final HashMap<String, Integer> userAccount = new HashMap<String, Integer>() {{
            put("id", 1);
        }};

        HashMap<String, Object> submittedFormData = new HashMap<String, Object>() {{
            put("userAccount", userAccount);
        }};
        brytescore.submittedForm(submittedFormData);
    }

    /**
     * Called when the user taps the Track Started Chat button
     * Example usage of tracking the start of a chat
     */
    public void trackStartedChat(View view) {
        final HashMap<String, Integer> userAccount = new HashMap<String, Integer>() {{
            put("id", 1);
        }};

        HashMap<String, Object> startedChatData = new HashMap<String, Object>() {{
            put("userAccount", userAccount);
        }};
        brytescore.startedChat(startedChatData);
    }

    /**
     * Called when the user taps the Track Updated User Info button
     * Example usage of tracking when a user updates their information
     */
    public void trackUpdatedUserInfo(View view) {
        final HashMap<String, Integer> userAccount = new HashMap<String, Integer>() {{
            put("id", 1);
        }};

        HashMap<String, Object> userInfoData = new HashMap<String, Object>() {{
            put("userAccount", userAccount);
        }};
        brytescore.updatedUserInfo(userInfoData);
    }

    /**
     * Called when the user taps the Track Reviewed Listing button
     * Example usage of tracking when a user reviews a listing.
     */
    public void trackReviewedListing(View view) {
        HashMap<String, Object> viewedListingData = new HashMap<String, Object>() {{
            put("price", 123456);
            put("mls_id", "string");
            put("street_address", "string");
            put("street_address2", "string");
            put("city", "string");
            put("state_province", "string");
            put("postal_code", "string");
            put("latitude", "string");
            put("longitude", "string");
        }};

        brytescore.brytescore("realestate.viewedListing", viewedListingData);
    }

    /** Toggle devMode bool, pass to brytescore, update button */
    public void toggleDevMode(View view) {
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

    /** Toggle debugMode bool, pass to brytescore, update button */
    public void toggleDebugMode(View view) {
        debugMode = !debugMode;
        brytescore.debugMode(debugMode);

        ToggleButton toggle = ((ToggleButton)findViewById(R.id.debugModeButton));
        toggle.setChecked(debugMode);
    }

    /** Toggle impersonation bool, pass to brytescore, update button */
    public void toggleImpersonationMode(View view) {
        impersonationMode = !impersonationMode;
        brytescore.impersonationMode(impersonationMode);

        ToggleButton toggle = ((ToggleButton)findViewById(R.id.impersonationModeButton));
        toggle.setChecked(impersonationMode);
    }

    /** Toggle validationMode bool, pass to brytescore, update button */
    public void toggleValidationMode(View view) {
        validationMode = !validationMode;
        brytescore.validationMode(validationMode);

        ToggleButton toggle = ((ToggleButton)findViewById(R.id.validationModeButton));
        toggle.setChecked(validationMode);
    }
}
