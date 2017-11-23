package com.codepath.enroute.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.codepath.enroute.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by vidhya on 11/20/17.
 */

public class PhotoAdapter extends PagerAdapter {

    ArrayList<String> imageUrls;
    LayoutInflater inflater;
    Context context;
    ImageView ivBusinessPhoto;

    public PhotoAdapter(ArrayList<String> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.layout_full_screen_image,  container, false);
        ivBusinessPhoto = (ImageView) view.findViewById(R.id.ivBusinessPhoto);
        Picasso.with(context).load(imageUrls.get(position))
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_food_placeholder)
                .transform(new RoundedCornersTransformation(10, 10))
                .into(ivBusinessPhoto);
        ((ViewGroup) container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
