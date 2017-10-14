package com.codepath.enroute.connection;

import com.codepath.enroute.models.PointEnRoute;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by qunli on 10/11/17.
 */

public class YelpClient extends AsyncHttpClient {


    //The following is for Yelp Fusion Api 3.0
    //https://www.yelp.com/developers/documentation/v3/authentication
    //
    private static final String YELP_API_ACCESS_TOKEN_URL="https://api.yelp.com/oauth2/token";
    private static final String YELP_API_V3_BASE_URL ="https://api.yelp.com/v3";

    private static final String GRANT_TYPE ="client_credentials";
    private static final String CLIENT_ID="IOMtMNQUV18bf83T4-WQ8A";
    private static final String CLIENT_SECRET="az0Oc7jGfqz9zwJt0B3hyrCd4rcctW8giN1FoJXLpZKCcaoX1Z08cdcYZ3jQyGa3";

    private OkHttpClient httpClient;


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");


    private static YelpClient instance;

    //private String token="o7z1vHo64KNMCPXUq53wQfR6WRyK5PB5SiD-JZ5sv5mN9XO52jipwBeBTJyZnaNbleMIvDvp33wXJXrroHI2xkQsHUoY3MKl7LD4TM7KnPsAr8h6OIbAE3ITA7rdWXYx";
    private String token=null;

    private static String BuildJsonBody (String grant_type, String client_id, String clicent_secret){

        return "{\"grant_type\":\""+grant_type+"\","
                +"\"client_id\":\""+client_id+"\","
                +"\"client_secret\":\""+clicent_secret+"\"}";
    }

    private YelpClient(){
        httpClient = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(YELP_API_ACCESS_TOKEN_URL).newBuilder();
        urlBuilder.addQueryParameter("grant_type",GRANT_TYPE);
        urlBuilder.addQueryParameter("client_id",CLIENT_ID);
        urlBuilder.addQueryParameter("client_secret",CLIENT_SECRET);
        String url = urlBuilder.build().toString();

        String jsonBody = BuildJsonBody(GRANT_TYPE,CLIENT_ID,CLIENT_SECRET);
        RequestBody body = RequestBody.create(JSON,jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                JSONObject obj=null;
                try {
                    obj = new JSONObject(response.body().string());
                    token = obj.getString("access_token");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void getSearchResult(RequestParams params, AsyncHttpResponseHandler handler){
        String apiUrl = getApiUrl("businesses/search");
//        RequestParams params = new RequestParams();
//        params.put("term","food");
//        params.put("location","880 West Maude Avenue, Sunnyvale, CA");
//        params.put("radius",1000);
        instance.addHeader("Authorization","Bearer "+token);
        instance.get(apiUrl,params,handler);
    }

    /*
    * Method to give points of interest en route between origin and destination
    *
    * */
    public List<PointEnRoute> getPointsOfInterestEnRoute() {
        // TODO: Replace test data with real data.
        List<PointEnRoute> pointsOfInterestList = getTestData();
        return pointsOfInterestList;
    }

    public List<PointEnRoute> getTestData() {
        List<PointEnRoute> pointsOfInterestList = new ArrayList<>();
        double[] latitudes = {37.4274, 37.4282, 37.4278, 37.4274};
        double[] longitudes = {-121.9079, -121.9065, -121.9115, -121.9132};
        for (int i = 0; i < 4; i++) {
            PointEnRoute poi = new PointEnRoute();
            poi.setLatitude(latitudes[i]);
            poi.setLongitude(longitudes[i]);
            poi.setNameOfPlace("Point of Interest " + i);
            poi.setDescription("Desc" + i);
            pointsOfInterestList.add(poi);
        }
        return pointsOfInterestList;
    }

    protected String getApiUrl(String path) {
        return this.YELP_API_V3_BASE_URL + "/" + path;
    }

    public static YelpClient getInstance(){
        if(instance == null){
            instance = new YelpClient();
        }
        return instance;
    }
}
