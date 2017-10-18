package com.codepath.enroute.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.enroute.R;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.databinding.ActivityDetailBinding;
import com.codepath.enroute.models.YelpBusiness;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    YelpClient yelpClient;
    ImageView ivProfileImage;
    RatingBar ratingBar;
    TextView tvReviewCount;
    TextView tvDistance;
    ImageView ivPhone;
    TextView tvPhone;
    TextView tvName;
    TextView tvAddress;
    TextView tvCategory;
    TextView tvOpen;
    ImageView ivDirection;
    TextView tvReview1;
    TextView tvReview2;
    TextView tvReview3;
    ImageView ivWriteReview;
    ArrayList<YelpBusiness> list;
    YelpBusiness yelpBusiness;
    String[] reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        setContentView(R.layout.activity_detail);
        yelpClient = YelpClient.getInstance();
        setupView();
        getData();
        updateView();
    }

    public void setupView() {
        ivProfileImage = mBinding.ivProfileImage;
        ratingBar = mBinding.ratingBar;
        tvReviewCount = mBinding.tvReviewCount;
        tvDistance = mBinding.tvDistance;
        ivPhone = mBinding.ivPhone;
        tvPhone = mBinding.tvPhone;
        tvName = mBinding.tvName;
        tvAddress = mBinding.tvAddress;
        tvOpen = mBinding.tvOpen;
        ivDirection = mBinding.ivDirection;
        tvReview1 = mBinding.tvReview1;
        tvReview2 = mBinding.tvReview2;
        tvReview3 = mBinding.tvReview3;
        ivWriteReview = mBinding.ivWriteReview;
        tvCategory = mBinding.tvCategory;
    }

    public void getData() {
        RequestParams params = new RequestParams();
        params.put("term","restaurants");
        params.put("location","880 West Maude Avenue, Sunnyvale, CA");
        list = new ArrayList<>();
        yelpBusiness = new YelpBusiness();
        yelpClient.getSearchResult(params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    list = YelpBusiness.fromJSONArray(response.getJSONArray("businesses"));
                    yelpBusiness = list.get(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
        reviews = new String[3];
        for (int i = 0; i < 3; i++) {
            reviews[i] = "";
        }
        yelpClient.getReviews(yelpBusiness.getId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = (response.getJSONArray("reviews"));
                    for (int i = 0; i < 3 && i < jsonArray.length(); i++) {
                        reviews[i] = "\"" + jsonArray.getJSONObject(i).getString("text") + "\"";
                    }
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
    public void updateView() {
        tvName.setText(yelpBusiness.getName());
        if (!yelpBusiness.isOpenNow()) {
            tvOpen.setText("closed");
            tvOpen.setTextColor(Color.RED);
        }
        else {
            tvOpen.setText("open");
            tvOpen.setTextColor(Color.GREEN);
        }
        tvDistance.setText(new DecimalFormat("##.#").format(yelpBusiness.distance) + " mi");
        tvReviewCount.setText(String.valueOf(yelpBusiness.getReview_count()) + " reviews");
        tvCategory.setText(yelpBusiness.getCategories());
        tvAddress.setText(yelpBusiness.getDisplay_address());
        ratingBar.setRating((float)yelpBusiness.getRating());
        tvPhone.setText(yelpBusiness.getPhone_number());
        tvReview1.setText(reviews[0]);
        tvReview2.setText(reviews[1]);
        tvReview3.setText(reviews[2]);
        Picasso.with(this).load(yelpBusiness.getImage_url()).placeholder(R.mipmap.ic_launcher).transform(new RoundedCornersTransformation(10, 10)).into(ivProfileImage);
    }
}
