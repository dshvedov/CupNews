package ru.d9d.cupnews;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = "QueryUtils";
    private static final int HTTP_CONNECTION_READ_TIMEOUT = 10000; /* milliseconds */
    private static final int HTTP_CONNECTION_CONNECT_TIMEOUT = 15000; /* milliseconds */


    private QueryUtils() {
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<News> fetchNewsData(String url) {

        // Create an empty List
        List<News> news = new ArrayList<>();

        String jsonString = null;
        try {
            jsonString = makeHttpRequest(createUrl(url));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with HTTP request", e);
        }

        // Try to parse the JSON_RESPONSE.
        try {
            JSONObject jsonResponse = new JSONObject(jsonString);
            JSONObject responseObj = jsonResponse.getJSONObject("response");

            JSONArray results = responseObj.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject newsObj = results.getJSONObject(i);

                // Get publication date
                String newsDateStr = newsObj.getString("webPublicationDate");
                SimpleDateFormat guardianDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                Date date = guardianDateFormat.parse(newsDateStr);
                String newsTitle = newsObj.getString("webTitle");
                String newsUrl = newsObj.getString("webUrl");
                String section = newsObj.getString("sectionName");
                // Extract authors from news object
                String authors = extractAuthors(newsObj);

                // Add news object
                news.add(new News(date.getTime(), newsTitle, authors, section, newsUrl));
            }
            // build up a list of News objects with the corresponding data.

        } catch (JSONException e) {
            // Print a log message with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing news JSON results", e);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing date", e);
        }

        // Return the list of news
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(HTTP_CONNECTION_READ_TIMEOUT);
            urlConnection.setConnectTimeout(HTTP_CONNECTION_CONNECT_TIMEOUT);
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
            Log.e(LOG_TAG, "Problem retrieving news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    private static String extractAuthors(JSONObject news) {
        String newsAuthors = ""; // Empty author string if could not extract
        try {
            JSONArray tags = news.getJSONArray("tags");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tags.length(); i++) {
                JSONObject tag = tags.getJSONObject(i);
                // Add author to StringBuilder sb
                sb.append(tag.getString("webTitle"));
                // Add comma and space if more authors present
                if (i != tags.length() - 1) {
                    sb.append(", ");
                }
            }
            newsAuthors = sb.toString();
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing author", e);
        }
        return newsAuthors;
    }

}
