package com.codepath.enroute.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.codepath.enroute.R;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.models.OpenHour;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.DatabaseTable;
import com.codepath.enroute.util.MapUtil;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.phoneNumber;
import static android.content.Context.MODE_PRIVATE;
import static com.codepath.enroute.util.DatabaseTable.COL_STATION;

/**
 * Created by vidhya on 10/17/17.
 */

public abstract class PointsOfInterestFragment extends Fragment {
    public ArrayList<YelpBusiness> yelpBusinessList;
    private YelpClient client;
    protected Map<LatLng,YelpBusiness> mPointsOfInterest;
    String searchTerm;
    String searchCategory;
    protected JSONObject directionsJson;
    SharedPreferences settingPreference;
    DatabaseTable db ;

    public PointsOfInterestFragment() {
        mPointsOfInterest = new HashMap<>();
        yelpBusinessList = new ArrayList<>();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db= new DatabaseTable(context);


    }

    public void setSearchCategory(String aSearchCategory){
        searchCategory = aSearchCategory;
    }

    public void setSearchTerm(String aSearchTerm){
        searchTerm=aSearchTerm;
    }

    //protected void getYelpBusinesses(JSONObject response) {
    public void getYelpBusinesses() {
        //TESTME Jim
        settingPreference = getContext().getSharedPreferences(String.valueOf(R.string.setting_preference), MODE_PRIVATE);

        int range = settingPreference.getInt("range", 5);
        final int rating = settingPreference.getInt("rating", 1);
        List<LatLng> googlePoints = null;
        try {
            googlePoints = MapUtil.getLatLngFromOverView(directionsJson, 1609);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (yelpBusinessList.size() > 0) {
            yelpBusinessList.clear();
            mPointsOfInterest.clear();
        }
        //The following is an example how to use YelpApi.
        client = YelpClient.getInstance();
        RequestParams params = new RequestParams();
        params.put("term", searchTerm);
        if (searchCategory!=null){
            params.put("category",searchCategory);
        }
        params.put("radius", 1609 * range);
//       params.put("radius", 1000);
        for (int i = 0; i < googlePoints.size(); i++) {

            params.put("latitude", googlePoints.get(i).latitude);
            params.put("longitude", googlePoints.get(i).longitude);
            client.getSearchResult(params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        JSONArray yelpBusinesses = response.getJSONArray("businesses");
                        BitmapDescriptor icon =
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                        for (int i = 0; i < yelpBusinesses.length(); i++) {
                            final YelpBusiness aYelpBusiness = YelpBusiness.fromJson(yelpBusinesses.getJSONObject(i));

                            LatLng newLatLng = new LatLng(aYelpBusiness.getLatitude(),aYelpBusiness.getLongitude());
                                //Skip if there is a duplicate.
                            if (!mPointsOfInterest.containsKey(newLatLng) && aYelpBusiness.getRating() >= rating){
                                mPointsOfInterest.put(newLatLng,aYelpBusiness);
                                // Add data to arraylist
                                yelpBusinessList.add(aYelpBusiness);

                                //get the gas price if it is gas station.
                                if(searchCategory=="servicestations") {
                                    new RetrieveStationTask().execute(aYelpBusiness);
                                }

                                client.getBusiness(aYelpBusiness.getId(), new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Log.e("at least i am here", response.toString());
                                        try {
                                            if (response.optJSONArray("hours") != null) {
                                                JSONArray jsonArray = response.optJSONArray("hours");
                                                aYelpBusiness.setOpenNow(jsonArray.getJSONObject(0).getBoolean("is_open_now"));
                                                ArrayList<ArrayList<OpenHour>> lists = new ArrayList<>();
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    lists.add(OpenHour.fromJSONArray(jsonArray.getJSONObject(i).getJSONArray("open")));
                                                }
                                                aYelpBusiness.setOpenHourSummary(lists);
                                            }

                                            //float gasPrice = getGasPrice(getStationID(aYelpBusiness.getPhone_number()));
                                            //aYelpBusiness.setGasPrice(gasPrice);
//                                            if(searchCategory=="servicestations") {
//                                                new RetrieveStationTask().execute(aYelpBusiness);
//                                            }


                                            ArrayList<String> images = new ArrayList<String>();
                                            JSONArray jArray = response.getJSONArray("photos");
                                            if (jArray != null) {
                                                for (int i=0;i<jArray.length();i++){
                                                    images.add(jArray.getString(i));
                                                    //mYelpReviews.add(0,new YelpReview(yelpBusiness.getId(),jArray.getString(i)));
                                                    //mYelpReviewAdapter.notifyDataSetChanged();
                                                }
                                            }



                                            aYelpBusiness.setPhotosList(images);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        super.onFailure(statusCode, headers, responseString, throwable);
                                    }
                                });
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    postYelpSearch();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        Log.d(this.getClass().toString(), "Yelp businesses: " + response.getJSONObject(0).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d(this.getClass().toString(), "Yelp businesses: " + responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(this.getClass().toString(), "Error fetching Yelp businesses: " + throwable.toString());
                    //super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
/*        for (final YelpBusiness yelpBusiness : yelpBusinessList) {
            String id = yelpBusiness.getId();
                client.getBusiness(id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.e("at least i am here", response.toString());
                        try {
                            yelpBusiness.setOpenNow(response.getJSONArray("hours").getJSONObject(0).getBoolean("is_open_now"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });
        }
        postYelpSearch();*/
    }

    public abstract void postYelpSearch();





    class RetrieveStationTask extends AsyncTask<YelpBusiness, Void, Float> {

        private Exception exception;

        public float getGasPrice(int stationID)  {

            if (stationID==0){
                return 0f;
            }
            Document doc = null;
            try {
                String url = "https://www.gasbuddy.com/Station/" + stationID;
                doc = Jsoup.connect(url).get();
            } catch (IOException e) {
                return 0;
            }

            Element newsHeadline = doc.select("div.price-display.credit-price").first();

            //System.out.print(doc.toString());
            if (newsHeadline!=null) {
                return Float.parseFloat(newsHeadline.text().toString());
            }else{
                return 0f;
            }
        }

        public int getStationID(String phoneNumber){

            if (phoneNumber.startsWith("+1")){
                phoneNumber = phoneNumber.substring(2);
            }

            Cursor c = db.getWordMatches(phoneNumber,null);

            if (c != null ) {
                if  (c.moveToFirst()) {
                    String station = c.getString(c.getColumnIndex(COL_STATION));
                    return Integer.valueOf(station);
                }
                c.close();
            }

            return 0;

        }


        protected Float doInBackground(YelpBusiness... aYelpBusiness) {
            try {
                String phoneNumber = aYelpBusiness[0].getPhone_number();
                int stationId = getStationID(phoneNumber);
                float gasPrice = getGasPrice(stationId);
                aYelpBusiness[0].setGasPrice(gasPrice);
                return gasPrice;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        @Override
        protected void onPostExecute(Float aFloat) {
            super.onPostExecute(aFloat);
            //I don't know if this is correct and/or effective
            //postYelpSearch();
        }
    }

}
