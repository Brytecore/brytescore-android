package com.brytecore;

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
import retrofit2.http.POST;

public class Brytescore {

    // ------------------------------------ static variables ------------------------------------ //
    // Variables used to fill event data for tracking
    private static String _url = "https://api.brytecore.com/";
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

    // HTTP Connection service for generic track endpoint
    public interface ApiService {
        @POST("track")
        Call<ResponseBody> track(@Body Map<String, Object> params );
    }
    // HTTP Connection instance for generic track endpoint
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(_url)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    ApiService service = retrofit.create(ApiService.class);

    // ------------------------------------ public functions: ----------------------------------- //
    /**
     * Sets the API key.
     * Generates a new unique session ID.
     * Retrieves the saved user ID, if any.
     *
     * @param apiKey The API key.
     */
    public Brytescore(String apiKey) {
        _apiKey = apiKey;

        // TODO Generate and save unique session ID

        // TODO Retrieve user ID from brytescore_uu_uid
    }

    /**
     * Returns the current API key
     */
    public String getAPIKey() { return _apiKey;}

    /**
     * Function to load json packages.
     *
     * @param package The name of the package.
     */
    public void load(String packageName) {
        // STUB
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
     *
     */
    public void brytescore(String property, HashMap<String, Object> data) {
        System.out.printf("Calling brytescore %s \n", property);
        // STUB
    }

    /**
     * Start a pageView.
     *
     * @param data The pageView data.
     * @param data.isImpersonating
     * @param data.pageUrl
     * @param data.pageTitle
     * @param data.referrer
     */
    public void pageView(HashMap<String, Object> data) {
        System.out.println("Calling pageView");

        track(eventNames.get("pageView"), "Viewed a Page", data);
    }

    /**
     * Sends a new account registration event.
     * - parameter data: The registration data.
     * - data.isImpersonating
     * - data.userAccount.id
     */
    public void registeredAccount(HashMap<String, Object> data) {
        System.out.println("Calling registeredAccount");

        // TODO handle impersonating
        // If the user is being impersonated, do not track.

        // TODO

        // Finally, as long as the data was valid, track the account registration
        track(eventNames.get("registeredAccount"), "Created a new account", data);
    }

    /**
     * Sends a submittedForm event.
     *
     * @param data: The chat data.
     */
    public void submittedForm(HashMap<String, Object> data) {
        System.out.println("Calling submittedForm");

        // TODO If the user is being impersonated, do not track.

        track(eventNames.get("submittedForm"), "Submitted a form", data);
    }

    /**
     * Sends a startedChat event.
     *
     * @param data: The chat data.
     */
    public void startedChat(HashMap<String, Object> data) {
        System.out.println("Calling startedChat");

        // TODO If the user is being impersonated, do not track.

        track(eventNames.get("startedChat"), "User Started a Live Chat", data);
    }

    /**
     * Updates a user's account information.
     *
     * @param data: The account data.
     */
    public void updatedUserInfo(HashMap<String, Object> data) {
        System.out.println("Calling updatedUserInfo");
        // TODO If the user is being impersonated, do not track.

        // TODO validate user info

        track(eventNames.get("updatedUserInfo"), "User Started a Live Chat", data);
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
        System.out.println("Calling authenticated");
        // TODO If the user is being impersonated, do not track.

        // TODO validate user info

        track(eventNames.get("authenticated"), "Logged in", data);
    }

    /**
     * Kills the session.
     */
    public void killSession() {
        System.out.println("Calling killSession");

        // TODO Stop the timer

        // TODO Reset the heartbeat start time

        // Delete and TODO save session id
        sessionId = null;

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
     * @param data.isImpersonating Bool whether user is being impersonated
     */
    private void track(String eventName, String eventDisplayName, HashMap<String, Object> data) {
        System.out.println("Calling track");

        // TODO: check impersonation mode
        sendRequest("track", eventName, eventDisplayName, data);
    }

    /**
     * Helper Function for making CORS calls to the API.
     *
     * @param path path for the API URL.
     * @param eventName name of the event being tracked.
     * @param eventDisplayName display name of the event being tracked.
     * @param data metadate of the event being tracked.
     */
    private void sendRequest(String path, final String eventName, final String eventDisplayName, final HashMap<String, Object> data) {
        System.out.printf("Calling sendRequest %s %s %s\n", path, eventName, eventDisplayName);

        if (_apiKey.length() == 0) {
            System.out.printf("Abandon ship! You must provide an API key.");
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
            put("pageViewId", generateUUID());
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
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println("HTTP: Response Caught");
                    try {
                        if (response.isSuccessful()) {
                            System.out.println("HTTP: Response Success");
                        } else {
                            System.out.println("HTTP: Response Failed");
                        }
                    } catch (Exception e) {
                        System.out.println("HTTP : Response Exception");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println("HTTP: Request failed");
                }
            });
        } else {
            System.out.println("Dev mode is enabled");
        }
    }

    /**
     * Generate RFC4112 version 4 compliant UUID using Java's built-in generator
     *
     * @return a new UUID string.
     */
    private String generateUUID() {
        UUID uuid = UUID.randomUUID();
        String UUIDString = uuid.toString();
        return UUIDString;
    }

    // TODO override println and printf to suppress when debug mode is off.
}
