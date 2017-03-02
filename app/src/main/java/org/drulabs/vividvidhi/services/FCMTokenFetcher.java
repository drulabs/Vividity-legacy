package org.drulabs.vividvidhi.services;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.utils.Store;

/**
 * Authored by KaushalD on 02/03/2016.
 */

public class FCMTokenFetcher extends FirebaseInstanceIdService {

    private static final String TAG = "FCMTokenFetcher";

    @Override
    public void onTokenRefresh() {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        saveToken(fcmToken);
    }

    private void saveToken(String fcmToken) {

        Log.d(TAG, "FCMTokenFetcher: token: " + fcmToken);

        Store store = Store.getInstance(this);
        store.setFcmToken(fcmToken);

        if (store.getMyKey() != null) {
            FirebaseDatabase.getInstance().getReference().child(Constants
                    .USER_BASE).child(store.getMyKey()).child(Constants.UB_FCM_TOKEN)
                    .setValue(fcmToken);
        }
    }
}
