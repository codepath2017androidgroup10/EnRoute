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
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.codepath.enroute.R.id.tvReview1;
import static com.codepath.enroute.R.id.tvReview2;
import static com.codepath.enroute.R.id.tvReview3;


public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding mBinding;
    YelpClient yelpClient;
    ImageView ivProfileImage;
    RatingBar ratingBar;
    TextView tvReviewCount;
    TextView tvDistance;
    TextView tvPhone;
    TextView tvName;
    TextView tvAddress;
    TextView tvCategory;
    TextView tvOpen;
    ImageView ivDirection;
    TextView tvReview[];
    ImageView ivWriteReview;
    ArrayList<YelpBusiness> list;
    YelpBusiness yelpBusiness;
    String[] reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        yelpClient = YelpClient.getInstance();
        tvReview = new TextView[3];
        //setContentView(R.layout.activity_detail);
        yelpBusiness = (YelpBusiness) Parcels.unwrap(getIntent().getParcelableExtra("YELP_BUSINESS"));
        setupView();
        getData();
        updateView();
    }

    public void setupView() {
        ivProfileImage = mBinding.ivProfileImage;
        ratingBar = mBinding.ratingBar;
        tvReviewCount = mBinding.tvReviewCount;
        tvDistance = mBinding.tvDistance;
        tvPhone = mBinding.tvPhone;
        tvName = mBinding.tvName;
        tvAddress = mBinding.tvAddress;
        tvOpen = mBinding.tvOpen;
        ivDirection = mBinding.ivDirection;
        tvReview[0] = mBinding.tvReview1;
        tvReview[1] = mBinding.tvReview2;
        tvReview[2] = mBinding.tvReview3;
        ivWriteReview = mBinding.ivWriteReview;
        tvCategory = mBinding.tvCategory;
    }

    public void getData() {

        reviews = new String[3];
        for (int i = 0; i < 3; i++) {
            reviews[i] = "";
        }
        yelpClient.getReviews(yelpBusiness.getId(), new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("reviews");
                    for (int i = 0; i < 3 && i < jsonArray.length(); i++) {
                        reviews[i] = "\"" + jsonArray.getJSONObject(i).getString("text") + "\"";
 //                       reviews[i] = "what is going on?";
                        tvReview[i].setText(reviews[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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
        tvDistance.setText(new DecimalFormat("##.#").format(yelpBusiness.getDistance()) + " mi");
        tvReviewCount.setText(String.valueOf(yelpBusiness.getReview_count()) + " reviews");
        tvCategory.setText(yelpBusiness.getCategories());
        tvAddress.setText(yelpBusiness.getDisplay_address());
        ratingBar.setRating((float)yelpBusiness.getRating());
        tvPhone.setText(yelpBusiness.getPhone_number());

        Picasso.with(this).load(yelpBusiness.getImage_url()).placeholder(R.mipmap.ic_launcher).transform(new RoundedCornersTransformation(10, 10)).into(ivProfileImage);
    }
}
