package org.drulabs.vividvidhi.firebase;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.db.DBHandler;
import org.drulabs.vividvidhi.dto.Like;
import org.drulabs.vividvidhi.dto.Picture;
import org.drulabs.vividvidhi.utils.Store;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kaushald on 06/02/17.
 */

public class PicsHandler {

    private static final int BATCH_SIZE = 3;

    private long lastTimestamp = System.currentTimeMillis();
    private boolean hasMorePics = true;

    private Context mContext;
    private Callback mListener;

    // Firebase variables
    private DatabaseReference picsDB;
    private FirebaseAnalytics mAnalytics;

    private Query picsQuery;

    public PicsHandler(Context cxt, Callback callback) {
        this.mContext = cxt;
        this.mListener = callback;

        picsDB = FirebaseDatabase.getInstance().getReference().child(Constants
                .PICS_DB);
        mAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    public void fetchPics() {
        if (hasMorePics) {
            picsQuery = picsDB.orderByChild(Constants.PICS_DATE_TAKEN).endAt(lastTimestamp)
                    .limitToLast(BATCH_SIZE);
            picsQuery.addListenerForSingleValueEvent(picsListener);
        } else {
            mListener.onAllPicsFetched();
        }
    }

    public void fetchLikedPics() {
        LikesHandler.fetchLikedArtifacts(mContext, new LikesHandler.IArtifactsCallback() {
            @Override
            public void onArtifactsFetched(List<Like> myLikes) {
                DBHandler.getHandle(mContext).updateLikedArtifacts(myLikes);
            }

            @Override
            public void onError() {
                Store store = Store.getInstance(mContext);
                Bundle logs = new Bundle();
                logs.putBoolean("hasLogs", false);
                mAnalytics.logEvent(store.getMyName() + "(" + store.getMyKey() + ")", null);
            }
        });
    }

    public boolean hasMorePics() {
        return hasMorePics;
    }

    public void updateLikesCount(final String picKey, final boolean isLiked) {

        // Update likes count
        picsDB.child(picKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Picture pic = dataSnapshot.getValue(Picture.class);
                    int likesCount = pic.getLikesCount();

                    likesCount = (isLiked) ? likesCount + 1 : likesCount - 1;

                    pic.setLikesCount(likesCount);

                    picsDB.child(picKey).setValue(pic);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Updating likes DB
        (new LikesHandler(mContext, null, picKey)).updateLikeStatus(isLiked,
                System.currentTimeMillis());

        // Updating local DB
        DBHandler.getHandle(mContext).setLikedForPic(picKey, isLiked);
    }

    private ValueEventListener picsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot != null && snapshot.getValue() != null) {

                HashMap<String, Picture> pics = new HashMap<>();
                String firstElementKey = null;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Picture pic = dataSnapshot.getValue(Picture.class);
                    pic.setPicURL(Constants.PICS_DB + "/" + pic.getPicName() + ".jpg");
                    pic.setThumbURL(Constants.PICS_DB + "/" + pic.getPicName() + "_thumb.jpg");
                    pics.put(dataSnapshot.getKey(), pic);
                    if (firstElementKey == null) {
                        firstElementKey = dataSnapshot.getKey();
                    }
                }

                hasMorePics = pics != null ? pics.size() >= BATCH_SIZE : false;
                lastTimestamp = pics != null && pics.size() > 0 ? (pics.get(firstElementKey)
                        .getDateTaken() - 1) : 0;

                // Collections.reverse(pics);

                mListener.onPhotosFetched(pics);

                // Fetch all the pics liked by user (metadata only)
                fetchLikedPics();

                //picsQuery.removeEventListener(this);

            } else {
                mListener.onError(mContext.getString(R.string.something_went_wrong));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            mListener.onError(mContext.getString(R.string.something_went_wrong));
        }
    };

    public void resetLastTimestamp() {
        lastTimestamp = System.currentTimeMillis();
        hasMorePics = true;
    }

    public interface Callback {
        void onPhotosFetched(HashMap<String, Picture> photos);

        void onAllPicsFetched();

        void onError(String message);
    }

}
