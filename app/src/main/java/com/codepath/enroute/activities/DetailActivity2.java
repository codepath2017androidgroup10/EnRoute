package com.codepath.enroute.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.enroute.R;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.fragments.PhotoViewerFragment;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.OnSwipeTouchListener;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DetailActivity2 extends AppCompatActivity {

    ImageView ivBusinessPhoto;
    TextView tvName;
    YelpBusiness yelpBusiness;
    ArrayList<String> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        tvName = (TextView) findViewById(R.id.tvBusinessName);
        ivBusinessPhoto = (ImageView) findViewById(R.id.ivHeader);

        yelpBusiness = (YelpBusiness) Parcels.unwrap(getIntent().getParcelableExtra("YELP_BUSINESS"));
        tvName.setText(yelpBusiness.getName());

        YelpClient client = YelpClient.getInstance();
        client.getBusiness(yelpBusiness.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {

                    images = new ArrayList<String>();
                    JSONArray jArray = response.getJSONArray("photos");
                    if (jArray != null) {
                        for (int i=0;i<jArray.length();i++){
                            images.add(jArray.getString(i));
                            //mYelpReviews.add(0,new YelpReview(yelpBusiness.getId(),jArray.getString(i)));
                            //mYelpReviewAdapter.notifyDataSetChanged();
                        }
                    }

                    yelpBusiness.setPhotosList(images);
                    Picasso.with(getApplicationContext()).load(images.get(0)).fit().centerCrop().placeholder(R.drawable.ic_food_placeholder).into(ivBusinessPhoto);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

        ivBusinessPhoto.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeDown() {
                Toast.makeText(getApplicationContext(), "Swiping down", Toast.LENGTH_SHORT).show();
                PhotoViewerFragment photoViewerFragment = PhotoViewerFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Bundle b = new Bundle();
                b.putStringArrayList("photos", images);
                photoViewerFragment.setArguments(b);
                ft.replace(R.id.photo_viewer_placeholder, photoViewerFragment);
                ft.commit();
            }
        });

    }

}
