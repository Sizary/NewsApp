package com.example.android.newsapp;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils extends AppCompatActivity {

    private static final String LOG_TAG = Utils.class.getSimpleName();

    private Utils() {
    }

    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractNews(jsonResponse);
        return news;
    }

    // Returns new URL object from the given string URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    // HTTP request to the given URL and return a String
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // Return a list of {@link New} objects that has been built up from
    //parsing the given JSON response.
    public static List<News> extractNews(String newsJson) {

        // If the JSON string is empty or null, then return null.
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<News> newsItems = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJson);
// Extract the JSONObject associated with the key called "response",
            // which represents a response (only if response exists)

            if (!baseJsonResponse.has("response")) {
                return newsItems;
            }
            JSONObject responseObject = baseJsonResponse.getJSONObject("response");

            if (!responseObject.has("results")) {
                return newsItems;
            }

            JSONArray newsArray = responseObject.getJSONArray("results");

            for (int i = 0; i < newsArray.length(); i++) {

                JSONObject currentNews = newsArray.getJSONObject(i);

                String section;

                if (currentNews.has("sectionName"))
                    section = currentNews.getString("sectionName");
                else
                    section = "Section N/A";

                String headline;
                if (currentNews.has("webTitle"))
                    headline = currentNews.getString("webTitle");
                else
                    headline = "Title N/A";


                String date;
                if (currentNews.has("webPublicationDate"))
                    date = currentNews.getString("webPublicationDate");
                else
                    date = "Date N/A";

                String url;
                if (currentNews.has("webUrl"))
                    url = currentNews.getString("webUrl");
                else
                    url = null;

                newsItems.add(new News(section, headline, date, url));
            }

        } catch (JSONException e) {
            Log.e("Utils", "Problem parsing the news JSON results", e);
        }

        return newsItems;
    }

}