package com.siwonkh.cleangpt_v1.util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SearchCommentThread {
    private final String APIKey;
    private String url;

    public SearchCommentThread(String APIKey) {
        this.APIKey = APIKey;
    }

    public void setVideo(String video) {
        String BASE_URL = "https://www.googleapis.com/youtube/v3/commentThreads";
        String urlStr = BASE_URL + "?part=snippet";
        urlStr += "&key=" + APIKey;
        urlStr += "&videoId=" + video;
        urlStr += "&maxResults=" + "10";
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
