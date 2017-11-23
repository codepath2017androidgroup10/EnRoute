package com.codepath.enroute.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.enroute.R;
import com.codepath.enroute.activities.DetailActivity2;
import com.codepath.enroute.adapters.CategoryAdapter;
import com.codepath.enroute.adapters.RestaurantAdapter;
import com.codepath.enroute.databinding.FragmentListBinding;
import com.codepath.enroute.models.YelpBusiness;
import com.codepath.enroute.util.ItemClickSupport;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;


public class ListFragment extends PointsOfInterestFragment {

    RecyclerView rvRestaurants;
    RestaurantAdapter restaurantAdapter;
    private FragmentListBinding mBinding;
    ArrayList<YelpBusiness> yelpBusinessArrayListist;
    FragmentManager fragmentManager;
    ArrayList<String> categories = new ArrayList<>(Arrays.asList("asian", "italian", "american", "veg", "chinese", "seafood", "sandwich", "breakfast", "mexican"));
    RecyclerView rvCategory;
    CategoryAdapter categoryAdapter;
    OnSearchDoneListener listener;

    public ListFragment() {
        // Required empty public constructor
    }
    public interface OnSearchDoneListener {
        public void notifyActivity(ArrayList<YelpBusiness> list);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnSearchDoneListener) context;
    }

    public void setBusinessList(ArrayList<YelpBusiness> list) {
        yelpBusinessList = list;
    }

    @Override
    public void postYelpSearch() {
       restaurantAdapter.notifyDataSetChanged();
        listener.notifyActivity(yelpBusinessList);
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

    public void updateList() {
        restaurantAdapter.notifyDataSetChanged();
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
        rvCategory = getView().findViewById(R.id.rvCategory);
        categoryAdapter = new CategoryAdapter(getContext(), categories, fragmentManager);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvCategory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCategory.setAdapter(categoryAdapter);
        rvCategory.setLayoutManager(layoutManager);
        RecyclerView.ItemDecoration itemDecoration2 = new
                DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
        RecyclerView.ItemDecoration itemDecoration3 = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvCategory.addItemDecoration(itemDecoration2);
        rvCategory.addItemDecoration(itemDecoration3);
        rvCategory.setItemAnimator(new SlideInUpAnimator());
        ItemClickSupport.addTo(rvCategory).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //               Toast.makeText(getContext(), "got it", Toast.LENGTH_LONG).show();
                String term = categories.get(position);
                setSearchTerm(term);
                getYelpBusinesses();
            }
        });




        restaurantAdapter = new RestaurantAdapter(getContext(), yelpBusinessList, fragmentManager);
        rvRestaurants.setAdapter(restaurantAdapter);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvRestaurants.addItemDecoration(itemDecoration);
        rvRestaurants.setItemAnimator(new SlideInUpAnimator());
        ItemClickSupport.addTo(rvRestaurants).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                //               Toast.makeText(getContext(), "got it", Toast.LENGTH_LONG).show();
                YelpBusiness yelpBusiness = yelpBusinessList.get(position);
                Intent i = new Intent(getContext(), DetailActivity2.class);
                i.putExtra("YELP_BUSINESS", Parcels.wrap(yelpBusiness));
                Pair<View, String> p1 = Pair.create(v.findViewById(R.id.ivProfileImage), "profile");
                Pair<View, String> p2 = Pair.create(v.findViewById(R.id.ratingBar), "rBar");
                Pair<View, String> p3 = Pair.create(v.findViewById(R.id.tvName), "name");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), p1, p2, p3);
                startActivity(i, options.toBundle());
            }
        });

        try {
            directionsJson = new JSONObject(getArguments().getString("directionsJson"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
