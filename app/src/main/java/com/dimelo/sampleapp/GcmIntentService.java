package com.dimelo.sampleapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.dimelo.dimelosdk.main.Dimelo;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)
                    || GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){
                // An error occured
            }

            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                MainActivity.setupDimelo(this, false);
                if (Dimelo.consumeReceivedRemoteNotification(this, extras, null)){
                    // Cool !
                }
                else {
                    // Not a dimelo Notification.
                }
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}