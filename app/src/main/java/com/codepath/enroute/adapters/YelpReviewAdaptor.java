package com.codepath.enroute.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.enroute.R;
import com.codepath.enroute.models.YelpTextReview;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by qunli on 10/21/17.
 */

public class YelpReviewAdaptor extends RecyclerView.Adapter<YelpReviewAdaptor.ViewHolder>{

    private List<YelpTextReview> mYelpTextReviews;
    private Context mContext;



    public YelpReviewAdaptor(Context mContext, List<YelpTextReview> mYelpReviews) {
        this.mYelpTextReviews = mYelpReviews;
        this.mContext = mContext;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View YelpReviewView = inflater.inflate(R.layout.item_yelp_text_review,parent,false);
        ViewHolder viewHolder = new ViewHolder(YelpReviewView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        YelpTextReview aYelpReview = mYelpTextReviews.get(position);
        holder.mtvYelpTextReview.setText(aYelpReview.getText());


    }

    @Override
    public int getItemCount() {
        return mYelpTextReviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
      TextView mtvYelpTextReview;

        ViewHolder(View itemView){
            super(itemView);
            mtvYelpTextReview = (TextView) itemView.findViewById(R.id.ivYelpText);

        }
    }
}
