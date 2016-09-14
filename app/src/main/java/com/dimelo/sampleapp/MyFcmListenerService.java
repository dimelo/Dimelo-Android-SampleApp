package com.dimelo.sampleapp;

import android.os.Bundle;

import com.dimelo.dimelosdk.main.Dimelo;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by cylix on 13/09/16.
 */
public class MyFcmListenerService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String type = message.getMessageType();

        if (!data.isEmpty()) {
            MainActivity.setupDimelo(this);
            if (Dimelo.consumeReceivedRemoteNotification(this, mapToBundle(data), null)){
                // Cool !
            }
            else {
                // Not a dimelo Notification.
            }
        }
    }

    private Bundle mapToBundle(Map<String, String> data) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        return bundle;
    }

}