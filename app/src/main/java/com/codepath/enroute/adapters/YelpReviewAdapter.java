package com.codepath.enroute.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.codepath.enroute.R;
import com.codepath.enroute.models.YelpReview;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.R.attr.width;

/**
 * Created by qunli on 10/21/17.
 */

public class YelpReviewAdapter extends RecyclerView.Adapter<YelpReviewAdapter.ViewHolder>{

    private List<YelpReview> mYelpReviews;
    private Context mContext;



    public YelpReviewAdapter(Context mContext, List<YelpReview> mYelpReviews) {
        this.mYelpReviews = mYelpReviews;
        this.mContext = mContext;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View YelpReviewView = inflater.inflate(R.layout.item_yelp_review,parent,false);
        ViewHolder viewHolder = new ViewHolder(YelpReviewView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        YelpReview aYelpReview = mYelpReviews.get(position);


        holder.ivYelpReview.measure(
//                View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED)
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT

        );
        int height = holder.ivYelpReview.getMeasuredHeight();
        int width = holder.ivYelpReview.getMeasuredWidth();
        height=500;
        width=500;
        holder.pbYelpPhoto.setVisibility(View.VISIBLE);
        Picasso.with(mContext)
                .load(aYelpReview.getPhotoUrl())
                .placeholder(R.mipmap.ic_launcher)
                .resize(width,height)
                .transform(new RoundedCornersTransformation(10, 10))
                .into(holder.ivYelpReview, new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {
                        if (holder.pbYelpPhoto!=null){
                            holder.pbYelpPhoto.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mYelpReviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivYelpReview;
        ProgressBar pbYelpPhoto;

        ViewHolder(View itemView){
            super(itemView);
            ivYelpReview = (ImageView)itemView.findViewById(R.id.ivYelpPhoto);
            pbYelpPhoto = (ProgressBar) itemView.findViewById(R.id.pbYelpPhoto);
        }
    }
}
