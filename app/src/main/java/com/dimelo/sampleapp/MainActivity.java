package com.dimelo.sampleapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.dimelo.dimelosdk.main.Dimelo;
import com.dimelo.dimelosdk.main.DimeloConnection;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String SENDER_ID = BuildConfig.GCM_API_KEY; // GCM ID to be defined in gradle.properties

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

//        // Get GCM Token
        registerInBackground();

        // Setup Dimelo
        Dimelo dimelo = setupDimelo(this, true);
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

    static Dimelo setupDimelo(Context context, Boolean setUserId) {
        JSONObject foo = new JSONObject();
        try {
            foo.put("KEY", "VALUE\nWITH\nNEWLINE");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("FOO", foo.toString());
        String secret = BuildConfig.DIMELO_SDK_SECRET; //edit in gradle.properties
        Dimelo.setup(context);
        Dimelo dimelo = Dimelo.getInstance();
        dimelo.setApiSecret(secret);
        dimelo.setDebug(true);
        if (setUserId) {
            dimelo.setUserName("John Doe");
            dimelo.setUserIdentifier("John Doe");
        }

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

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        final Context mContext = getApplicationContext();
        AsyncTask<Object, Void, String> task = new AsyncTask<Object, Void, String>() {

            private String mGcmRegistrationId;

            @Override
            protected String doInBackground(Object... params) {
                String msg;
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
                    mGcmRegistrationId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + mGcmRegistrationId;
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d("DimeloSampleApp", msg);
                Dimelo.getInstance().setDeviceToken(mGcmRegistrationId);
            }
        };
        task.execute(null, null, null);
    }

}




