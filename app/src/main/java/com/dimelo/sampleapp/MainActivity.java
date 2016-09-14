package com.dimelo.sampleapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.dimelo.dimelosdk.main.Dimelo;
import com.dimelo.dimelosdk.main.DimeloConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Setup Dimelo
        Dimelo dimelo = setupDimelo(this);
        dimelo.setDimeloListener(dimeloListener);

        FragmentManager supportFragmentManager = getSupportFragmentManager();
        SlidingTabFragment mSlidingFragment = (SlidingTabFragment) supportFragmentManager.findFragmentByTag("mSlidingFragment");
        if (mSlidingFragment == null) {
            mSlidingFragment = new SlidingTabFragment();
            mSlidingFragment.setRetainInstance(true);
            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.slider_container, mSlidingFragment, "mSlidingFragment");
            fragmentTransaction.commit();
        }
    }

    Dimelo.DimeloListener dimeloListener = new Dimelo.DimeloListener() {

        @Override
        public void dimeloChatMessageSendFail(DimeloConnection.DimeloError error) {
            // Something went wrong
            // Minimal error management

            String message = "An error occurred";
            if (error.statusCode == DimeloConnection.DimeloError.NO_CONNECTION_ERROR) {
                message = "Please check your Internet connection and try again later.";
            } else if (error.statusCode == DimeloConnection.DimeloError.TIMEOUT_ERROR) {
                message = "The server is not responding, please try again later";
            }
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    static Dimelo setupDimelo(Context context) {
        String secret = BuildConfig.DIMELO_SDK_SECRET; //edit in gradle.properties
        Dimelo.setup(context);
        Dimelo dimelo = Dimelo.getInstance();
        dimelo.setApiSecret(secret);
        dimelo.setDebug(true);
        dimelo.setUserName("John Doe");

        JSONObject authInfo = new JSONObject();
        try {
            authInfo.put("CustomerId", "0123456789");
            authInfo.put("Dimelo", "Rocks!");
        } catch (JSONException e) {
        }

        dimelo.setAuthenticationInfo(authInfo);
        return dimelo;
    }

    @Override
    public void onBackPressed() {
        SlidingTabFragment mSlidingFragment = (SlidingTabFragment) getSupportFragmentManager().findFragmentByTag("mSlidingFragment");
        if (mSlidingFragment != null && mSlidingFragment.isHandlingBack())
            return;
        super.onBackPressed();
    }

}




