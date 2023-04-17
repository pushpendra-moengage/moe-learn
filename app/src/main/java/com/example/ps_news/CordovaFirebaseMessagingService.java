package com.example.ps_news;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moengage.firebase.MoEFireBaseHelper;

public class CordovaFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        MoEFireBaseHelper.getInstance().passPushPayload(getApplicationContext(), message.getData());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }
}
