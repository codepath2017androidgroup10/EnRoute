package com.codepath.enroute.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.enroute.R;
import com.codepath.enroute.models.YelpBusiness;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by bear&bear on 10/14/2017.
 */

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder>{

    private List<YelpBusiness> mRestaurants;
    private Context mContext;
    FragmentManager mFragmentManager;

    public RestaurantAdapter(Context context, List<YelpBusiness> restaurants, FragmentManager fragmentManager) {
        this.mRestaurants = restaurants;
        mContext = context;
        this.mFragmentManager = fragmentManager;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvName;
        RatingBar ratingBar;
        TextView tvOpen;
        TextView tvDistance;
        TextView tvPrice;
        TextView tvReviewCount;
        TextView tvCategory;
        TextView tvAddress;


        ViewHolder(View itemView) {
            super(itemView);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvOpen = (TextView) itemView.findViewById(R.id.tvOpen);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAddress = itemView.findViewById(R.id.tvAddress);

        }
/*        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position
            if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
//                User user = users.get(position);
                // We can access the data within the views
                Toast.makeText(mContext, "hello", Toast.LENGTH_SHORT).show();
            }
        }
*/
    }

    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View restaurantView = inflater.inflate(R.layout.item_restaurant, parent, false);
        ViewHolder viewHolder = new ViewHolder(restaurantView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantAdapter.ViewHolder holder, int position) {
        YelpBusiness restaurant = mRestaurants.get(position);
        holder.tvName.setText(restaurant.getName());
        if (!restaurant.isOpenNow()) {
            holder.tvOpen.setText("closed");
            holder.tvOpen.setTextColor(Color.RED);
        }
        else {
            holder.tvOpen.setText("open");
            holder.tvOpen.setTextColor(Color.parseColor("#8BC34A"));
        }
//        holder.tvOpen.setText(restaurant.isOpenNow()? "open" : "closed");
//        holder.tvDistance.setText(String.valueOf(restaurant.distance) + " mi");
        holder.tvDistance.setText(new DecimalFormat("##.#").format(restaurant.distance) + " mi");
        holder.tvPrice.setText(restaurant.getPrice_level());
        holder.tvReviewCount.setText(String.valueOf(restaurant.getReview_count()) + " reviews");
        holder.tvCategory.setText(restaurant.getCategories());
        holder.tvAddress.setText(restaurant.getDisplay_address());
        holder.ratingBar.setRating((float)restaurant.getRating());

        if (restaurant.getImage_url()!=null && restaurant.getImage_url().length()>10) {
            Picasso.with(mContext).load(restaurant.getImage_url()).placeholder(R.mipmap.ic_launcher).transform(new RoundedCornersTransformation(10, 10)).into(holder.ivProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }
}
