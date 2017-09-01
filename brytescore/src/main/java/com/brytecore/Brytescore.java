package com.brytecore;

import java.util.HashMap;

import sun.jvm.hotspot.types.basic.BasicOopField;

public class Brytescore {

    // ------------------------------------ static variables ------------------------------------ //
    // Variables used to fill event data for tracking
    private static String _url = "https://api.brytecore.com";
    private static String hostname = "com.brytecore.mobile";
    private static String library = "Android";
    private static String libraryVersion = "0.0.0";

    private HashMap<String, String> eventNames = new HashMap<String, String>() {{
        put("authenticated", "authenticated");
        put("brytescoreUUIDCreated", "brytescoreUUIDCreated");
        put("heartBeat", "heartBeat");
        put("pageView", "pageView");
        put("registeredAccount", "registeredAccount");
        put("sessionStarted", "sessionStarted");
        put("startedChat", "startedChat");
        put("submittedForm", "submittedForm");
        put("updatedUserInfo", "updatedUserInfo");
    }};

    // ------------------------------------ dynamic variables ----------------------------------- //
    private String _apiKey;

    // Variables to hold package-wide IDs
    private Integer userId;
    private String anonymousId;
    private String sessionId;
    private String pageViewId;

    // Variables used to fill event data for tracking
    // When additional packages are loaded, they are added to this dictionary
    private HashMap<String, String> schemaVersion = new HashMap<String, String>() {{
        put("analytics","0.3.1");
    }};

    // Variables for mode statuses
    private Boolean devMode = false;
    private Boolean debugMode = false;
    private Boolean impersonationMode = false;
    private Boolean validationMode = false;

    // ------------------------------------ public functions: ----------------------------------- //
    /**
     * Sets the API key.
     *
     * @param apiKey The API key.
     */
    public Brytescore(String apiKey) { this._apiKey = apiKey; }

    /**
     * Returns the current API key
     */
    public String getAPIKey() { return _apiKey;}

    /**
     * Start a pageView.
     *
     * @param data The pageView data.
     * @param data.isImpersonating
     * @param data.pageUrl
     * @param data.pageTitle
     * @param data.referrer
     */
    public void pageView(HashMap<String, String> data) {
        System.out.println("Calling pageView");
        track();
    }

    // ------------------------------------ private functions: ---------------------------------- //
    /**
     * Main track function
     *
     * @param eventName The event name.
     * @param eventDisplayName The event display name.
     * @param data The event data.
     * @param data.isImpersonating
     */

    private void track() {
        System.out.println("Calling track");
        sendRequest();
    }

    private void sendRequest() {
        System.out.println("Calling sendRequest");
    }
}

