package com.codepath.enroute.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.enroute.R;
import com.codepath.enroute.models.YelpBusiness;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by vidhya on 10/25/17.
 */

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context mContext;
    YelpBusiness mYelpBusiness;


    public CustomInfoWindowAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        mYelpBusiness = (YelpBusiness) marker.getTag();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.marker_info_contents, null);
        TextView tvName = (TextView) view.findViewById(R.id.tvPlaceName);
        tvName.setText(mYelpBusiness.getName());

        RatingBar rbReviews = (RatingBar) view.findViewById(R.id.rbPlaceRating);
        rbReviews.setRating((float) mYelpBusiness.getRating());

        TextView tvClosedNow = (TextView) view.findViewById(R.id.tvClosedNow);
        tvClosedNow.setText(mYelpBusiness.isOpenNow() ? "Open" : "Closed Now");

        TextView tvDetourTime = (TextView) view.findViewById(R.id.tvDetourTime);
        String detourStr = "+" + mYelpBusiness.getDistance();
        tvDetourTime.setText(detourStr);
        return view;
    }
}
