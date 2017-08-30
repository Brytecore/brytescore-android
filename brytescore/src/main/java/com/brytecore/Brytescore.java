package com.brytecore;

public class Brytescore {
    private String _apiKey;

    /**
     * Sets the API key.
     *
     * @param {string} apiKey The API key.
     */
    public Brytescore(String apiKey) { this._apiKey = apiKey; }

    /**
     * Returns the current API key
     */
    public String getAPIKey() { return _apiKey;}
}

