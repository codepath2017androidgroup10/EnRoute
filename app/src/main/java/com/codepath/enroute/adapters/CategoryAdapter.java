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

import static java.security.AccessController.getContext;

/**
 * Created by bear&bear on 10/14/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

    private List<String> mCategories;
    private Context mContext;
    FragmentManager mFragmentManager;

    public CategoryAdapter(Context context, List<String> categories, FragmentManager fragmentManager) {
        this.mCategories = categories;
        mContext = context;
        this.mFragmentManager = fragmentManager;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCategory;
        TextView tvCategory;


        ViewHolder(View itemView) {
            super(itemView);
            ivCategory = (ImageView) itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);


        }
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View categoryView = inflater.inflate(R.layout.item_category, parent, false);
        ViewHolder viewHolder = new ViewHolder(categoryView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, int position) {
        String category = mCategories.get(position);

        holder.tvCategory.setText(category);
//        String address = "R.drawable.ic_category_" + category;
        String address = "ic_category_" + category;
//            Picasso.with(mContext).load(address).placeholder(address).transform(new RoundedCornersTransformation(10, 10)).into(holder.ivCategory);
        int id = mContext.getResources().getIdentifier(address, "drawable", mContext.getPackageName());
        holder.ivCategory.setImageResource(id);
//        holder.ivCategory.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(address)));
//        holder.ivCategory.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_category_asian));
//        holder.ivCategory.setImageDrawable(mContext.getResources().getDrawable(Integer.parseInt(address)));
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }
}
