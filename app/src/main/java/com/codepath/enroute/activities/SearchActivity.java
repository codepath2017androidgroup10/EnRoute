package com.codepath.enroute.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.codepath.enroute.R;
import com.codepath.enroute.connection.YelpClient;
import com.codepath.enroute.databinding.ActivitySearchBinding;
import com.codepath.enroute.fragments.SettingFragment;
import com.crashlytics.android.Crashlytics;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.fabric.sdk.android.Fabric;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding mBinding;
    private String fromLocation;
    private String toLocation;

    private static final String KEY_FROM = "FROM";
    private static final String KEY_TO = "TO";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        setContentView(R.layout.activity_search);
        setUpViews();
        YelpClient client = YelpClient.getInstance();

        //The following is an example how to use YelpApi.
//        client.getSearchResult(new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                super.onSuccess(statusCode, headers, response);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                super.onSuccess(statusCode, headers, responseString);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//            }
//        });


    }

    private void setUpViews() {
        EditText etFromLocation = mBinding.etFrom;
        fromLocation = etFromLocation.getText().toString();
        EditText etToLocation = mBinding.etTo;
        toLocation = etToLocation.getText().toString();
    }

    /*
    * Click Listener for Search button. Opens the map view on click.
    *
    * */

    public void onButtonSearch(View view) {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra(KEY_FROM, fromLocation);
        intent.putExtra(KEY_TO, toLocation);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);

        return super.onCreateOptionsMenu(menu);
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SettingFragment settingFragment = SettingFragment.newInstance();
            FragmentManager fm = getSupportFragmentManager();
            settingFragment.show(fm, "fragment_setting");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
