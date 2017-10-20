package com.codepath.enroute.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.enroute.R;
import com.codepath.enroute.activities.DetailActivity;
import com.codepath.enroute.adapters.RestaurantAdapter;
import com.codepath.enroute.databinding.FragmentListBinding;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.ItemClickSupport;

import org.parceler.Parcels;

import java.util.ArrayList;


public class ListFragment extends PointsOfInterestFragment {

    RecyclerView rvRestaurants;
    RestaurantAdapter restaurantAdapter;
    private FragmentListBinding mBinding;
    ArrayList<YelpBusiness> yelpBusinessArrayListist;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void postYelpSearch() {
       restaurantAdapter.notifyDataSetChanged();
        ItemClickSupport.addTo(rvRestaurants).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                YelpBusiness yelpBusiness = yelpBusinessList.get(position);
                Intent i = new Intent(getContext(), DetailActivity.class);
                i.putExtra("YELP_BUSINESS", Parcels.wrap(yelpBusiness));
                startActivity(i);
            }
        });
    }

    public static ListFragment newInstance(ArrayList<YelpBusiness> list) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putParcelable("list", Parcels.wrap(list));
        fragment.setArguments(args);
        fragment.yelpBusinessArrayListist=list;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parcelable p = getArguments().getParcelable("list");
        Parcels.unwrap(p);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //rvRestaurants = mBinding.rvRestaurants;

        rvRestaurants = (RecyclerView) getView().findViewById(R.id.rvRestaurants);
        restaurantAdapter = new RestaurantAdapter(getContext(), yelpBusinessArrayListist);
        rvRestaurants.setAdapter(restaurantAdapter);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}
