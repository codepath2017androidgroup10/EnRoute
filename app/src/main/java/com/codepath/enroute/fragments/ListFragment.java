package com.codepath.enroute.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.enroute.R;
import com.codepath.enroute.activities.DetailActivity;
import com.codepath.enroute.adapters.RestaurantAdapter;
import com.codepath.enroute.databinding.FragmentListBinding;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.ItemClickSupport;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import static android.R.attr.fragment;


public class ListFragment extends PointsOfInterestFragment {

    RecyclerView rvRestaurants;
    RestaurantAdapter restaurantAdapter;
    private FragmentListBinding mBinding;
    ArrayList<YelpBusiness> yelpBusinessArrayListist;
    FragmentManager fragmentManager;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void postYelpSearch() {
       restaurantAdapter.notifyDataSetChanged();
/*        ItemClickSupport.addTo(rvRestaurants).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
 //               Toast.makeText(getContext(), "got it", Toast.LENGTH_LONG).show();
                YelpBusiness yelpBusiness = yelpBusinessArrayListist.get(position);
                Intent i = new Intent(getContext(), DetailActivity.class);
                i.putExtra("YELP_BUSINESS", Parcels.wrap(yelpBusiness));
                startActivity(i);
            }
        });*/
    }

    public static ListFragment newInstance(ArrayList<YelpBusiness> list) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putParcelable("list", Parcels.wrap(list));
        fragment.setArguments(args);
        fragment.yelpBusinessArrayListist=list;
        return fragment;
    }

    public static ListFragment newInstance(String directionsJson, String encodedPolyLine) {
        ListFragment listFragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("directionsJson", directionsJson);
        args.putString("points", encodedPolyLine);
        listFragment.setArguments(args);
        return listFragment;
    }

    public static ListFragment newInstance(ArrayList<YelpBusiness> list, String directionsJson, String encodedPolyLine) {
        ListFragment listFragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString("directionsJson", directionsJson);
        args.putString("points", encodedPolyLine);
        listFragment.yelpBusinessList=list;
        listFragment.setArguments(args);
        return listFragment;
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
        fragmentManager = getActivity().getSupportFragmentManager();
        rvRestaurants = (RecyclerView) getView().findViewById(R.id.rvRestaurants);
        restaurantAdapter = new RestaurantAdapter(getContext(), yelpBusinessList, fragmentManager);
        rvRestaurants.setAdapter(restaurantAdapter);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemClickSupport.addTo(rvRestaurants).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //               Toast.makeText(getContext(), "got it", Toast.LENGTH_LONG).show();
                YelpBusiness yelpBusiness = yelpBusinessList.get(position);
                Intent i = new Intent(getContext(), DetailActivity.class);
                i.putExtra("YELP_BUSINESS", Parcels.wrap(yelpBusiness));
                startActivity(i);
            }
        });
        try {
            directionsJson = new JSONObject(getArguments().getString("directionsJson"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
