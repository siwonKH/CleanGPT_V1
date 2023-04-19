package com.siwonkh.cleangpt_v1.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SearchCreatorProfile {
    private final String BASE_URL = "https://www.googleapis.com/youtube/v3/search";
    private final String APIKey;
    private String url;

    public SearchCreatorProfile(String APIKey) {
        this.APIKey = APIKey;
    }

    public void setChannel(String channel) {
        String urlStr = BASE_URL + "?part=snippet&type=channel";
        urlStr += "&key=" + APIKey;
        urlStr += "&q=" + channel;
        this.url = urlStr;
    }

    public JSONObject getApiResponse() throws Exception {
        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        if (connection.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((connection.getInputStream()), StandardCharsets.UTF_8));
        String output;
        StringBuilder apiResponse = new StringBuilder();

        while ((output = bufferedReader.readLine()) != null) {
            apiResponse.append(output);
        }

        connection.disconnect();

        return new JSONObject(apiResponse.toString());
    }
}
