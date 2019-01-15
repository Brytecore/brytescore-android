package com.brytecore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public class Brytescore {

    // ------------------------------------ static variables ------------------------------------ //
    // Variables used to fill event data for tracking
    private static final String TAG = "Brytescore";
    private static String _url = "https://api.brytecore.com/";
    private static String _packageUrl = "https://cdn.brytecore.com/packages/";
    private static String _packageName = "/package.json";
    private static String hostname = "com.brytecore.mobile";
    private static String library = "Android";
    private static String libraryVersion = "1.3.1";

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
    private SharedPreferences preferences;

    // Variables to hold package-wide IDs
    private String userId;
    private String anonymousId;
    private String sessionId;
    private String pageViewId;

    // Variables used to fill event data for tracking
    // When additional packages are loaded, they are added to this dictionary
    private HashMap<String, String> schemaVersion = new HashMap<String, String>() {{
        put("analytics","0.3.1");
    }};

    // Dynamically loaded packages
    private HashMap<String, JsonObject> packageFunctions = new HashMap<>();

    // Variables for heartbeat timer
    private Runnable heartbeatTimer;
    private Handler heartbeatTimerHandler;
    private Integer heartbeatLength = 15000;
    private Date startHeartbeatTime = Calendar.getInstance().getTime();
    private Integer totalPageViewTime = 0;

    // Variables for mode statuses
    private Boolean devMode = false;
    private Boolean debugMode = false;
    private Boolean impersonationMode = false;
    private Boolean validationMode = false;

    // HTTP Connection service for generic track endpoint
    interface ApiService {
        @POST("track")
        Call<ResponseBody> track(@Body Map<String, Object> params);
    }

    // HTTP Connection service for generic package endpoint
    interface PackageApiService {
        @GET
        Call<Object> getPackage(@Url String url);
    }

    // HTTP Connection instance for generic track endpoint
    private Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    private ApiService service = retrofit.create(ApiService.class);

    // HTTP Connection instance for generic package endpoint
    private Retrofit packageRetrofit = new Retrofit.Builder()
            .baseUrl(_packageUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private PackageApiService packageService = packageRetrofit.create(PackageApiService.class);

    // ------------------------------------ public functions: ----------------------------------- //
    /**
     * Sets the API key.
     * Generates a new unique session ID.
     * Retrieves the saved user ID, if any.
     *
     * @param apiKey The API key.
     */
    public Brytescore(Context context, String apiKey) {
        _apiKey = apiKey;

        // Get shared preferences
        preferences = context.getSharedPreferences("com.brytecore.brytescore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Generate and save unique session ID
        sessionId = generateUUID();
        editor.putString("brytescore_session_sid", sessionId);

        // Retrieve user ID from brytescore_uu_uid -
        // check if userID is an int or String for backwards compatibility, userID should be a String
        Map<String, ?> all = preferences.getAll();
        if (all.get("brytescore_uu_uid") instanceof String) {
            userId = preferences.getString("brytescore_uu_uid", "");
        } else if(all.get("brytescore_uu_uid") instanceof Integer) {
            int temp = preferences.getInt("brytescore_uu_uid", -1);
            userId = temp == -1 ? "" : String.valueOf(temp);
        } else {
            userId = "";
        }

        editor.putString("brytescore_uu_uid", userId);

        // Check if we have an existing anonymous ID from brytescore_uu_aid, otherwise generate
        if (!preferences.getString("brytescore_uu_aid", "").equals("")) {
            anonymousId = preferences.getString("brytescore_uu_aid", "");
            print("Retrieved anonymous user ID: " + anonymousId);
        } else {
            anonymousId = generateUUID();
            print("Generated new anonymous user ID: " + anonymousId);
            editor.putString("brytescore_uu_aid", anonymousId);
        }

        editor.apply();
    }

    /**
     * Returns the current API key
     */
    public String getAPIKey() {
        return _apiKey;
    }

    /**
     * Function to load json packages.
     *
     * @param packageName The name of the package.
     */
    public void load(final String packageName) {
        print("Calling load: " + packageName);
        print("Loading " + _packageUrl + packageName + _packageName);

        // Generate the request endpoint
        String requestEndpoint = packageName + _packageName;

        // Set up the URL request
        Call<Object> call = packageService.getPackage(requestEndpoint);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                print("HTTP: Response Caught");
                try {
                    if (response.isSuccessful()) {
                        print("HTTP: Response Success");

                        // Parse the API response data
                        Gson gson = new Gson();
                        String responseStr = gson.toJson(response.body());

                        // Get JsonObject from String
                        JsonParser jsonParser = new JsonParser();
                        JsonObject responseJSON = jsonParser.parse(responseStr).getAsJsonObject();

                        print("Call Successful, response: " + responseJSON);

                        // Get just the events object of the package
                        packageFunctions.put(packageName, responseJSON.get("events").getAsJsonObject());

                        // Get the namespace of the package
                        String namespace = responseJSON.get("namespace").getAsString();
                        schemaVersion.put(namespace, responseJSON.get("version").getAsString());
                    } else {
                        print("HTTP: Response Failed");
                    }
                } catch (Exception e) {
                    print("HTTP : Response Exception");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                print("HTTP: Request failed");
            }
        });
    }

    /**
     * Sets dev mode.
     * Logs events to the console instead of sending to the API.
     * Turning on dev mode automatically triggers debug mode.
     *
     * @param enabled If true, then dev mode is enabled.
     */
    public void devMode(Boolean enabled) {
        devMode = enabled;

        // If devMode is turned on, debugMode should be too.
        if (devMode) {
            debugMode(true);
        }
    }

    /**
     * Sets debug mode.
     * Log events are suppressed when debug mode is off.
     *
     * @param enabled If true, then debug mode is enabled.
     */
    public void debugMode(Boolean enabled) {
        debugMode = enabled;
    }

    /**
     * Sets impersonation mode.
     * Bypasses sending information to the API when impersonating another user.
     *
     * @param enabled If true, then impersonation mode is enabled.
     */
    public void impersonationMode(Boolean enabled) {
        impersonationMode = enabled;
    }

    /**
     * Sets validation mode.
     * Adds a validation attribute to the data for all API calls.
     *
     * @param enabled If true, then validation mode is enabled.
     */
    public void validationMode(Boolean enabled) {
        validationMode = enabled;
    }

    /**
     * Check if property is valid, and if so, track it.
     */
    public void brytescore(String property, HashMap<String, Object> data) {
        print("Calling brytescore " + property);

        //Ensure that a property is provided
        if (property == null || property.length() == 0) {
            print("Abandon ship! You must provide a tracking property.");
            return;
        }

        // Retrieve the namespace and function name, from property of format 'namespace.functionName'
        String[] splitPackage = property.split("\\.");

        if (splitPackage.length != 2) {
            print("Invalid tracking property name received. Should be of the form: 'namespace.functionName");
            return;
        }

        String namespace = splitPackage[0];
        String functionName = splitPackage[1];

        JsonObject functions = packageFunctions.get(namespace);
        if (functions == null || functions.get(functionName) == null) {
            print("The " + namespace + " package is not loaded, or " + functionName + " is not a valid function name.");
            return;
        }

        // Retrieve the function details from the loaded package, ensuring that it exists
        JsonObject functionDetails = functions.get(functionName).getAsJsonObject();

        if (functionDetails == null || functionDetails.get("displayName") == null) {
            print("The function display name could not be loaded.");
            return;
        }

        String eventDisplayName = functionDetails.get("displayName").getAsString();

        // Tack the validated listing
        track(property, eventDisplayName, data);
    }

    /**
     * Start a pageView.
     *
     * @param data The pageView data.
     * data.isImpersonating
     * data.pageUrl
     * data.pageTitle
     * data.referrer
     */
    public void pageView(HashMap<String, Object> data) {
        print("Calling pageView: " + data);

        // If the user is being impersonated, do not track.
        if (!checkImpersonation(data)) {
            return;
        }

        totalPageViewTime = 0;
        pageViewId = generateUUID();

        track(eventNames.get("pageView"), "Viewed a Page", data);

        // Save session information
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("brytescore_session_sid", sessionId);
        editor.putString("brytescore_session_aid", anonymousId);
        editor.apply();

        // Send the first heartbeat and start the timer
        print("Sending 'first' heartbeat");
        heartbeat();
        heartbeatTimerHandler = new Handler();
        heartbeatTimer = new Runnable() {
            @Override
            public void run() {
                checkHeartbeat();
                if (heartbeatTimerHandler != null) {
                    heartbeatTimerHandler.postDelayed(this, heartbeatLength);
                }
            }
        };
        heartbeatTimerHandler.postDelayed(heartbeatTimer, heartbeatLength);
    }

    /**
     * Sends a new account registration event.
     * - parameter data: The registration data.
     * - data.isImpersonating
     * - data.userAccount.id
     */
    public void registeredAccount(HashMap<String, Object> data) {
        print("Calling registeredAccount: " + data);
        Boolean userStatus = updateUser(data);

        // If the user is being impersonated, do not track.
        if (!checkImpersonation(data)) {
            return;
        }

        // Finally, as long as the data was valid, track the account registration
        if (userStatus) {
            track(eventNames.get("registeredAccount"), "Created a new account", data);
        }
    }

    /**
     * Sends a submittedForm event.
     *
     * @param data: The chat data.
     */
    public void submittedForm(HashMap<String, Object> data) {
        print("Calling submittedForm");

        // If the user is being impersonated, do not track.
        if (!checkImpersonation(data)) {
            return;
        }

        track(eventNames.get("submittedForm"), "Submitted a form", data);
    }

    /**
     * Sends a startedChat event.
     *
     * @param data: The chat data.
     */
    public void startedChat(HashMap<String, Object> data) {
        print("Calling startedChat");

        // If the user is being impersonated, do not track.
        if (!checkImpersonation(data)) {
            return;
        }

        track(eventNames.get("startedChat"), "User Started a Live Chat", data);
    }

    /**
     * Updates a user's account information.
     *
     * @param data: The account data.
     */
    public void updatedUserInfo(HashMap<String, Object> data) {
        print("Calling updatedUserInfo: " + data);

        // If user is being impersonated, do not track.
        if (!checkImpersonation(data)) {
            return;
        }

        // Finally, as long as the data was valid, track the user info update
        boolean userStatus = updateUser(data);
        if (userStatus) {
            track(eventNames.get("updatedUserInfo"), "User Started a Live Chat", data);
        }
    }

    /**
     * Sends a user authentication event.
     *
     * @param data: The authentication data.
     * data.isImpersonating
     * data.userAccount
     * data.userAccount.id
     */
    public void authenticated(HashMap<String, Object> data) {
        print("Calling authenticated");

        // If the user is being impersonated, do not track.
        if (!checkImpersonation(data)) {
            return;
        }

        // Ensure that we have a user ID from data.userAcount.id
        HashMap<String, Object> userAccount;
        try {
            userAccount = (HashMap<String, Object>) data.get("userAccount");
        } catch (ClassCastException ex) {
            print("data.userAccount is not defined");
            print(ex.toString());
            return;
        }

        if (userAccount == null) {
            print("data.userAccount is not defined");
            return;
        }

        String newUserId = String.valueOf(userAccount.get("id"));
        if(newUserId == null) {
            print("data.userAccount.id is not defined");
            return;
        }

        // Check if we have an existing aid, otherwise generate
        if (!preferences.getString("brytescore_uu_aid", "").equals("")) {
            anonymousId = preferences.getString("brytescore_uu_aid", "");
            print("Retrieved anonymous user ID: " + anonymousId);
        } else {
            anonymousId = generateUUID();
        }

        // Retrieve user ID from brytescore_uu_uid
        String storedUserID = preferences.getString("brytescore_uu_uid", "");

        // If there is a UID stored locally and the localUID does not match our new UID
        if (storedUserID != null && !storedUserID.equals(newUserId)) {
            print("Retrieved user ID: " + storedUserID);
            changeLoggedInUser(newUserId); // Saves our new user ID to our global userID
        }

        // Save our anonymous id and user id to local storage
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("brytescore_uu_aid", anonymousId);
        editor.putString("brytescore_uu_uid", userId);
        editor.apply();

        // Finally, in any case, track the authentication
        track(eventNames.get("authenticated"), "Logged in", data);
    }

    /**
     * Kills the session.
     */
    public void killSession() {
        print("Calling killSession");

        // Stop the timer
        if (heartbeatTimer != null) {
            heartbeatTimerHandler.removeCallbacks(heartbeatTimer);
            heartbeatTimerHandler = null;
            heartbeatTimer = null;
        }

        // Reset the heartbeat start time
        startHeartbeatTime = Calendar.getInstance().getTime();

        // Delete and save session id
        sessionId = null;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("brytescore_session_sid", sessionId);
        editor.apply();

        // Reset pageViewIDs
        pageViewId = null;
    }

    // ------------------------------------ private functions: ---------------------------------- //
    /**
     * Main track function
     *
     * @param eventName The event name.
     * @param eventDisplayName The event display name.
     * @param data The event data.
     */
    private void track(String eventName, String eventDisplayName, HashMap<String, Object> data) {
        print("Calling track: " + eventName + " " + eventDisplayName + " " + data);

        // If the user is being impersonated, do not track.
        if (!checkImpersonation(data)) {
            return;
        }

        sendRequest("track", eventName, eventDisplayName, data);
    }

    /**
     * Helper Function for making CORS calls to the API.
     *
     * @param path path for the API URL.
     * @param eventName name of the event being tracked.
     * @param eventDisplayName display name of the event being tracked.
     * @param data metadata of the event being tracked.
     */
    private void sendRequest(String path, final String eventName, final String eventDisplayName, final HashMap<String, Object> data) {
        print("Calling sendRequest " + path + " " + eventName + " " + eventDisplayName);

        if (_apiKey.length() == 0) {
            print("Abandon ship! You must provide an API key.");
            return;
        }

        // Deduce the schema version (namespace)
        // Check if the property is of the format 'namespace.functionName'
        // If so, replace the namespace
        final String namespace[] = new String[1];
        namespace[0] = "analytics";
        String[] splitPackage = path.split(".");
        if (splitPackage.length == 2) {
            namespace[0] = splitPackage[0];
        }

        // Check if sessionId is set, if null, generate a new one
        if (sessionId == null) {
            // Get shared preferences editor
            SharedPreferences.Editor editor = preferences.edit();

            // Generate and save unique session ID
            sessionId = generateUUID();
            editor.putString("brytescore_session_sid", sessionId);
            editor.apply();
        }

        /**
         * Generate the object to send to the API
         *
         * - "event"              - param     - eventName
         * - "eventDisplayName"   - param     - eventDisplayName
         * - "hostName" - static  - static    - custom Android hostname
         * - "apiKey"             - static    - user's API key
         * - "anonymousId"        - generated - Brytescore UID
         * - "userId"             - retrieved - Client user id, may be null if unauthenticated
         * - "pageViewId"         - generated - Brytescore UID
         * - "sessionId"          - generated - Brytescore session id
         * - "library"            - static    - library type
         * - "libraryVersion"     - static    - library version
         * - "schemaVersion"      - generated - if eventName contains '.', use a custom schemaVersion based on the eventName. otherwise, use schemaVersion.analytics
         * - "data"               - param     - data
         * */
        HashMap<String, Object> eventData = new HashMap<String, Object>() {{
            put("event", eventName);
            put("eventDisplayName", eventDisplayName);
            put("hostName", hostname);
            put("apiKey", _apiKey);
            put("anonymousId", anonymousId);
            put("userId", userId);
            put("pageViewId", pageViewId);
            put("sessionId", sessionId);
            put("library", library);
            put("libraryVersion", libraryVersion);
            put("schemaVersion", schemaVersion.get(namespace[0]));
            put("data", data);
        }};

        // Handle validation mode, if activated
        if (validationMode) {
            eventData.put("validationOnly", validationMode);
        }

        if (!devMode) {
            Call<ResponseBody> call = service.track(eventData);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    print("HTTP: Response Caught");
                    try {
                        if (response.isSuccessful()) {
                            print("HTTP: Response Success");

                            // Parse the API response data
                            Gson gson = new Gson();
                            String responseStr = gson.toJson(response.body().string());

                            print("Call Successful, response: " + responseStr);
                        } else {
                            print("HTTP: Response Failed");
                        }
                    } catch (Exception e) {
                        print("HTTP : Response Exception");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    print("HTTP: Request failed");
                }
            });
        } else {
            print("Dev mode is enabled");
        }
    }

    /**
     * Generate RFC4112 version 4 compliant UUID using Java's built-in generator
     *
     * @return a new UUID string.
     */
    private String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Process a change in the logged in user:
     * - Kill current session for old user
     * - Update and save the global user ID variable
     * - Generate and save new anonymousId
     * - Generate new sessionId
     *
     * @param userID the user ID.
     */
    private void changeLoggedInUser(String userID) {
        // Kill current session for old user
        killSession();

        // Update and save the global user ID variable
        userId = userID;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("brytescore_uu_uid", userId);


        // Generate and save new anonymousId
        anonymousId = generateUUID();
        editor.putString("brytescore_uu_aid", anonymousId);
        editor.apply();

        HashMap<String, Object> data = new HashMap<>();
        data.put("anonymousId", anonymousId);
        track(eventNames.get("brytescoreUUIDCreated"), "New user id Created", data);

        // Generate new sessionId
        sessionId = generateUUID();

        data = new HashMap<>();
        data.put("sessionId", sessionId);
        data.put("anonymousId", anonymousId);
        track(eventNames.get("sessionStarted"), "started new session", data);

        // Page view will update session cookie no need to write one.
        pageView(new HashMap<String, Object>());
    }

    /**
     * Update heartbeat and track.
     */
    private void heartbeat() {
        print("Calling heartbeat");

        totalPageViewTime = totalPageViewTime + heartbeatLength;

        HashMap<String, Object> data = new HashMap<>();
        data.put("elapsedTime", totalPageViewTime);
        track(eventNames.get("heartBeat"), "Heartbeat", data);
    }

    /**
     * - Ensure that the user is not being impersonated
     * - Ensure that we have a user ID in the data parameter
     * - Update the global `userId` if it is not accurate
     */
    private Boolean updateUser(HashMap<String, Object> data) {
        // If the user is being impersonated, do not track.
        if (!checkImpersonation(data)) {
            return Boolean.FALSE;
        }

        // Ensure that we have a user ID from data.userAccount.id
        if (!data.containsKey("userAccount")) {
            print("data.userAccount is not defined");
            return Boolean.FALSE;
        }

        HashMap<String, Object> userAccount;
        try {
            userAccount = (HashMap<String, Object>) data.get("userAccount");
        } catch (ClassCastException ex){
            print("data.userAccount is not defined");
            print(ex.toString());
            return Boolean.FALSE;
        }

        if (!userAccount.containsKey("id")) {
            print("data.userAccount.id is not defined");
            return Boolean.FALSE;
        }

        String localUserID = String.valueOf(userAccount.get("id"));

        // If we haven't saved the user ID globally, or the user IDs do not match
        if (userId == null || !localUserID.equals(userId)) {
            // Retrieve anonymous user ID from brytescore_uu_aid, or generate a new anonymous uer ID
            if (!preferences.getString("brytescore_uu_aid", "").equals("")) {
                anonymousId = preferences.getString("brytescore_uu_aid", "");
                print("Retrieved anonymous user ID: " + anonymousId);
            } else {
                print("No anonymous ID has been saved. Generating...");
                String anonymousId = generateUUID();
                print("Generated new anonymous user ID: " + anonymousId);
                HashMap<String, Object> createdData = new HashMap<>();
                createdData.put("anonymousId", anonymousId);
                track(eventNames.get("brytescoreUUIDCreated"), "New user id Created", createdData);
            }

            // Save our new user ID to our global userId
            userId = localUserID;

            // Save our anonymous id and user id to local storage
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("brytescore_uu_aid", anonymousId);
            editor.putString("brytescore_uu_uid", userId);
            editor.apply();
        }

        return Boolean.TRUE;
    }

    /**
     * Checks to make sure that impersonation mode is not on globally, or that the user's data
     * does not contain impersonation mode.
     * @return Boolean Whether impersonation mode is on or not
     */
    private Boolean checkImpersonation(HashMap<String, Object> data) {
        if (impersonationMode || data.get("impersonationMode") != null) {
            print("Impersonation mode is on - will not track event");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     *Checks the heartbeat timer. If not dead, restarts it. If dead, kills session.
     */
    private void checkHeartbeat() {
        print("Calling checkHeartbeat");

        Long elapsed = Calendar.getInstance().getTime().getTime() - startHeartbeatTime.getTime();

        if (elapsed < 1800000) {
            // Heartbeat is not dead yet.
            print("Heartbeat is not dead yet.");
            startHeartbeatTime = Calendar.getInstance().getTime();
            heartbeat();
        } else {
            // Heartbeat is dead
            print("Heartbeat is dead.");
            killSession();
        }
    }

    /**
     * Override log function to only print while in debug mode.
     *
     * @param details Details to print.
     */
    private void print(String details) {
        if (debugMode) {
            Log.d(TAG, details);
        }
    }
}
