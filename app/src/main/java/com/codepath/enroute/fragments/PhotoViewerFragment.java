package com.codepath.enroute.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.enroute.R;
import com.codepath.enroute.adapters.PhotoAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoViewerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotoViewerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoViewerFragment extends Fragment {

    //private OnFragmentInteractionListener mListener;
    ArrayList<String> listOfPhotos;
    private ViewPager viewPager;
    PhotoAdapter adapter;

    public PhotoViewerFragment() {
        // Required empty public constructor
    }

    public static PhotoViewerFragment newInstance() {
        PhotoViewerFragment fragment = new PhotoViewerFragment();
        Bundle args = new Bundle();
        args.getStringArrayList("photos");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           listOfPhotos = getArguments().getStringArrayList("photos");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_photo_viewer, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        adapter = new PhotoAdapter(listOfPhotos, getContext());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

//        ivYelpBusinessPhoto = view.findViewById(R.id.ivYelpBusinessPhoto);
//        Picasso.with(getContext())
//                .load(listOfPhotos.get(0))
//                .placeholder(R.drawable.ic_food_placeholder)
//                .transform(new RoundedCornersTransformation(10, 10))
//                .fit()
//                .into(ivYelpBusinessPhoto);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
