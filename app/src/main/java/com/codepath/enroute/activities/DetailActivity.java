package com.codepath.enroute.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.enroute.R;
import com.codepath.enroute.adapters.YelpReviewAdapter;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.databinding.ActivityDetailBinding;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.models.YelpReview;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.R.attr.width;
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
    ImageView ivYelpPhoto;
    ArrayList<YelpBusiness> list;
    YelpBusiness yelpBusiness;
    String[] reviews;
    RecyclerView rvYelpReview;
    List<YelpReview> mYelpReviews;


    public static final int RC_PHOTO_PICKER = 2;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mYelpReviewDatabaseReference;
    private StorageReference mYelpPhotoStorageReference;
    private ChildEventListener mChildEventListener;
    private YelpReviewAdapter mYelpReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        yelpClient = YelpClient.getInstance();



        rvYelpReview=(RecyclerView)findViewById(R.id.rvYelpPhoto);

        tvReview = new TextView[3];
        //setContentView(R.layout.activity_detail);
        yelpBusiness = (YelpBusiness) Parcels.unwrap(getIntent().getParcelableExtra("YELP_BUSINESS"));

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        String yelpBusinessID = yelpBusiness.getId();
        mYelpReviewDatabaseReference = mFirebaseDatabase.getReference().child("YelpReview").child(yelpBusinessID);
        mYelpPhotoStorageReference = mFirebaseStorage.getReference().child("YelpPhotos");


        setupView();
        rvYelpReview =mBinding.rvYelpPhoto;
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        llm.setMeasurementCacheEnabled(false);


        rvYelpReview.setLayoutManager(llm);





        mYelpReviews = new ArrayList<>();
        mYelpReviewAdapter = new YelpReviewAdapter(this,mYelpReviews);
        rvYelpReview.setAdapter(mYelpReviewAdapter);

        ivWriteReview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });
        getData();
        updateView();


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                YelpReview aReview = dataSnapshot.getValue(YelpReview.class);
                //
                //YelpReviewAdapter.add(aReview);
                mYelpReviews.add(aReview);
                mYelpReviewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mYelpReviewDatabaseReference.addChildEventListener(mChildEventListener);
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
        yelpClient.getReviews(yelpBusiness.getId(), new JsonHttpResponseHandler() {
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
        } else {
            tvOpen.setText("open");
            tvOpen.setTextColor(Color.GREEN);
        }
        tvDistance.setText(new DecimalFormat("##.#").format(yelpBusiness.getDistance()) + " mi");
        tvReviewCount.setText(String.valueOf(yelpBusiness.getReview_count()) + " reviews");
        tvCategory.setText(yelpBusiness.getCategories());
        tvAddress.setText(yelpBusiness.getDisplay_address());
        ratingBar.setRating((float) yelpBusiness.getRating());
        tvPhone.setText(yelpBusiness.getPhone_number());

        Picasso.with(this).load(yelpBusiness.getImage_url()).placeholder(R.mipmap.ic_launcher).transform(new RoundedCornersTransformation(10, 10)).into(ivProfileImage);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = mYelpPhotoStorageReference.child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(selectedImageUri);

            uploadTask.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    YelpReview yelpReview = new YelpReview(yelpBusiness.getId(), downloadUrl.toString());
                    mYelpReviewDatabaseReference.push().setValue(yelpReview);
                    //Picasso.with(DetailActivity.this).load(downloadUrl.toString()).placeholder(R.mipmap.ic_launcher).transform(new RoundedCornersTransformation(10, 10)).into(ivYelpPhoto);

                }
            });
        }
    }



}
