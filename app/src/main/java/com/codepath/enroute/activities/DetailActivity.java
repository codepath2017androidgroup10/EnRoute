package com.codepath.enroute.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.enroute.R;
import com.codepath.enroute.adapters.YelpReviewAdaptor;
import com.codepath.enroute.adapters.YelpReviewPhotoAdapter;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.databinding.ActivityDetailBinding;
import com.codepath.enroute.fragments.ReviewBottomSheetDialog;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.models.YelpReview;
import com.codepath.enroute.models.YelpTextReview;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


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
    List<YelpTextReview> mYelpTextReviews;
    RecyclerView rvYelpReview;
    RecyclerView mReviewRecyclerView; //this is for yelp Review text;
    List<YelpReview> mYelpReviews;
    public ReviewBottomSheetDialog bottomSheetDialog;

    public static final int RC_PHOTO_PICKER = 2;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mYelpReviewDatabaseReference;
    private StorageReference mYelpPhotoStorageReference;
    private ChildEventListener mChildEventListener;
    private YelpReviewPhotoAdapter mYelpReviewAdapter;

    public final String APP_TAG = "EnRoute";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    YelpReviewAdaptor mYelpReviewAdaptor;

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
        rvYelpReview = mBinding.rvYelpPhoto;
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        llm.setMeasurementCacheEnabled(false);


        rvYelpReview.setLayoutManager(llm);

        mYelpReviews = new ArrayList<>();
        mYelpReviewAdapter = new YelpReviewPhotoAdapter(this,mYelpReviews);
        //mYelpReviews.add(0,new YelpReview("camera","https://maxcdn.icons8.com/Share/icon/Photo_Video//camera1600.png"));
        //mYelpReviews.add(0,new YelpReview("camera","https://lh3.googleusercontent.com/DPKR9wd6oDY6dbsJum0AwEq1Od7wHH1AuccSg7qI_E643rgCgH7CPotucBEh_qLO40kx=w300-rw"));
        mYelpReviews.add(0,new YelpReview("camera","https://cdn0.iconfinder.com/data/icons/summer-set-2/64/Camera-128.png"));

        rvYelpReview.setAdapter(mYelpReviewAdapter);

//        ivWriteReview.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // Open the bottom sheet modal dialog
//
//                bottomSheetDialog = new ReviewBottomSheetDialog();
//                FragmentManager fm = getSupportFragmentManager();
//                bottomSheetDialog.show(fm, "Choose an option");
//            }
//        });
        getData();
        updateView();


        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                YelpReview aReview = dataSnapshot.getValue(YelpReview.class);
                //
                //YelpReviewPhotoAdapter.add(aReview);
                mYelpReviews.add(0,aReview);
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
        mYelpTextReviews = new ArrayList<YelpTextReview>() ;
        mReviewRecyclerView=(RecyclerView)findViewById(R.id.rvReviews);
        mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mYelpReviewAdaptor = new YelpReviewAdaptor(this,mYelpTextReviews);
        mReviewRecyclerView.setAdapter(mYelpReviewAdaptor);
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

        //ivWriteReview = mBinding.ivWriteReview;
        tvCategory = mBinding.tvCategory;
    }

    public void getData() {



        yelpClient.getReviews(yelpBusiness.getId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("reviews");
                    for (int i = 0; i < 10 && i < jsonArray.length(); i++) {

                        mYelpTextReviews.add(new YelpTextReview(jsonArray.getJSONObject(i).getString("text")));

                        mYelpReviewAdaptor.notifyDataSetChanged();

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

        Picasso.with(this)
                .load(yelpBusiness.getImage_url())
                .placeholder(R.mipmap.ic_launcher)
                .transform(new RoundedCornersTransformation(10, 10))
                .fit()
                .into(ivProfileImage);
    }

    public void onUploadFromGalleryClick(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    public void onTakePhotoClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        if (photoFile != null) {
            Uri fileProvider = FileProvider.getUriForFile(DetailActivity.this, "com.codepath.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(getPackageManager()) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }
            // Return the file target for the photo based on filename
            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

            return file;
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = intent.getData();
            StorageReference photoRef = mYelpPhotoStorageReference.child(selectedImageUri.getLastPathSegment());
            UploadTask uploadTask = photoRef.putFile(selectedImageUri);

            uploadTask.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), "Something went wrong, Please try again!", Toast.LENGTH_SHORT).show();
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
            bottomSheetDialog.dismiss();
        }
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getPath());
                takenImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bArray = baos.toByteArray();
                StorageReference photoRef = mYelpPhotoStorageReference.child(photoFile.getPath());

                UploadTask uploadTask = photoRef.putBytes(bArray);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Something went wrong, Please try again!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        YelpReview yelpReview = new YelpReview(yelpBusiness.getId(), downloadUrl.toString());
                        mYelpReviewDatabaseReference.push().setValue(yelpReview);
                    }
                });

                bottomSheetDialog.dismiss();
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }




}
