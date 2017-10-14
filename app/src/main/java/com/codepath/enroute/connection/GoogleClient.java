package com.codepath.enroute.connection;


import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vidhya on 10/13/17.
 */

public class GoogleClient extends AsyncHttpClient {


    private static final String GOOGLE_API_MAPS_DIRECTIONS_URL="https://maps.googleapis.com/maps/api/directions/json"; //outputFormat?parameters
    private static final String API_KEY = "AIzaSyAfa5N3xhcHHr60leENZSv-xP996hQiZf0";
    private OkHttpClient okHttpClient;

    private static GoogleClient googleClientInstance;

    public GoogleClient() {
        okHttpClient = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(GOOGLE_API_MAPS_DIRECTIONS_URL).newBuilder();
        urlBuilder.addQueryParameter("key", API_KEY);
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ERROR", "HTTP Call failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response.body().string());
                    Log.d("GoogleClient", obj.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getDirections(RequestParams params, AsyncHttpResponseHandler handler) {
        googleClientInstance.get(GOOGLE_API_MAPS_DIRECTIONS_URL, params, handler);
    }

    protected String getApiUrl(String path) {
        return this.GOOGLE_API_MAPS_DIRECTIONS_URL + "/" + path;
    }

    public static GoogleClient getInstance() {
        if (googleClientInstance == null) {
            googleClientInstance = new GoogleClient();
        }
        return googleClientInstance;
    }
}
