package org.drulabs.vividvidhi.services;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.ui.NotificationToast;
import org.drulabs.vividvidhi.utils.Store;

import java.util.Map;

/**
 * Authored by KaushalD on 10/10/2016.
 */

public class FCMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMessagingService";

    private static final String KEY_SUSPEND_ACCOUNT = "suspend_account";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Store store = Store.getInstance(this);
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Log.d(TAG, remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            Map<String, String> receivedMap = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + receivedMap);

            Bundle bundle = new Bundle();
            bundle.putString("FCM_data", receivedMap.toString());

            firebaseAnalytics.logEvent(store.getUserEmail(), bundle);
            handleFCM(receivedMap);

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            Bundle bundle = new Bundle();
            bundle.putString("FCM_notif", remoteMessage.getNotification().getBody());

            firebaseAnalytics.logEvent(store.getUserEmail(), bundle);

            //FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("");
        }
    }

    private void handleFCM(Map<String, String> receivedMap) {
        if (receivedMap != null && receivedMap.containsKey(KEY_SUSPEND_ACCOUNT)) {
            boolean suspendAccount = Boolean.parseBoolean(receivedMap.get
                    (KEY_SUSPEND_ACCOUNT));
            if (suspendAccount) {
                Store.getInstance(getApplicationContext()).clearData();
                FirebaseAuth.getInstance().signOut();
                NotificationToast.showToast(getApplicationContext(), getString(R.string
                        .account_suspended_msg));
            }
        }
    }

//    private void handleFCM(Map<String, String> receivedMap) {
//        FirebaseCrash.report(new Throwable("One weird exception: FCM received"));
//        int fcmType = Integer.parseInt(receivedMap.get(Constants.KEY_FCM_TYPE));
//        switch (fcmType) {
//            case Constants.TYPE_NOTE_COLLAB:
//
//                String requesterName = receivedMap.get(Constants.KEY_USER_NAME);
//                String noteTitle = receivedMap.get(Constants.KEY_NOTE_TITLE);
//                String noteId = receivedMap.get(Constants.KEY_NOTE_ID);
//
//                final String content = getString(R.string.note_collab_notif_content, requesterName,
//                        noteTitle);
//
//                final String notificationTitle = getString(R.string.note_collab_notif_title);
//
//                final Intent notifActionIntent = new Intent(this, NoteEditorActivity.class);
//
//                FirebaseDatabase.getInstance().getReference().child
//                        (Constants.NOTES_DATABASE).child(noteId).addListenerForSingleValueEvent
//                        (new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                Note note = dataSnapshot.getValue(Note.class);
//                                notifActionIntent.putExtra(NoteEditorActivity.KEY_NOTE_ITEM, note);
//
//                                showNotification(notificationTitle, content, notifActionIntent);
//
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
//
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void showNotification(String title, String content, Intent actionIntent) {
//
//        FirebaseCrash.log("Notification created from data");
//
//        PendingIntent pendingActionIntent = PendingIntent.getActivity(this, 0, actionIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon
//                (R.mipmap.ic_list_img).setContentTitle(title).setContentText(content)
//                .setContentIntent(pendingActionIntent).setAutoCancel(true);
//        NotificationManager notifMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        notifMgr.notify((int) (System.currentTimeMillis() / 1000), mBuilder.build());
//    }

}
