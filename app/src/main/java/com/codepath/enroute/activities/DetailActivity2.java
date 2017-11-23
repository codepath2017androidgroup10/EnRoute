package com.codepath.enroute.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.OverScroller;
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

import static android.R.attr.x;
import static android.R.attr.y;

public class DetailActivity2 extends AppCompatActivity {

    ImageView ivBusinessPhoto;

    TextView tvName;
    YelpBusiness yelpBusiness;
    ArrayList<String> images;

    private GestureDetectorCompat gestureDetector;
    private OverScroller overScroller;
    private int positionX = 0;
    private int positionY = 0;
    private View rootView;
    private View layoutHeader;


    private float startY;
    private float startHeight;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail2);
        rootView = (View)findViewById(R.id.activity_detail2);
        layoutHeader = (View)findViewById(R.id.layoutHeader) ;
        tvName = (TextView) findViewById(R.id.tvBusinessName);
        ivBusinessPhoto = (ImageView) findViewById(R.id.ivHeader);


        yelpBusiness = (YelpBusiness) Parcels.unwrap(getIntent().getParcelableExtra("YELP_BUSINESS"));
        tvName.setText(yelpBusiness.getName());


        overScroller = new OverScroller(this);

        rootView.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    startHeight=ivBusinessPhoto.getLayoutParams().height;
                    startY=event.getRawY();
                    return true;
                }else if (event.getAction() ==MotionEvent.ACTION_MOVE){

                    float moveY = event.getRawY()-startY;
                    Log.d("HELLO", "moved "+ moveY);


                    ViewGroup.LayoutParams params = ivBusinessPhoto.getLayoutParams();
                        ViewGroup.LayoutParams paramsLayoutHeader=layoutHeader.getLayoutParams();

                        params.height = (int)(startHeight+moveY);
                        ivBusinessPhoto.setLayoutParams(params);
                        paramsLayoutHeader.height = (int)(startHeight+moveY);


                    int alpha = Math.min(Math.max((153-(int)moveY/4),0),255);
                    layoutHeader.setBackgroundColor(Color.argb(alpha,255,255,255));
                        layoutHeader.setLayoutParams(paramsLayoutHeader);



                    if (moveY>400) {
                        Toast.makeText(getApplicationContext(), "Swiping down", Toast.LENGTH_SHORT).show();
                        PhotoViewerFragment photoViewerFragment = PhotoViewerFragment.newInstance();
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        Bundle b = new Bundle();
                        b.putStringArrayList("photos", images);
                        photoViewerFragment.setArguments(b);
                        ft.replace(R.id.photo_viewer_placeholder, photoViewerFragment);
                        ft.commit();
                    }

                }


                return true;
            }
        });

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

//        ivBusinessPhoto.setOnTouchListener(new OnSwipeTouchListener(this) {
//            @Override
//            public void onSwipeDown() {
//                Toast.makeText(getApplicationContext(), "Swiping down", Toast.LENGTH_SHORT).show();
//                PhotoViewerFragment photoViewerFragment = PhotoViewerFragment.newInstance();
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                Bundle b = new Bundle();
//                b.putStringArrayList("photos", images);
//                photoViewerFragment.setArguments(b);
//                ft.replace(R.id.photo_viewer_placeholder, photoViewerFragment);
//                ft.commit();
//            }
//        });

    }

}
