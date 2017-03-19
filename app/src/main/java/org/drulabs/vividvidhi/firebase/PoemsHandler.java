package org.drulabs.vividvidhi.firebase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

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
import org.drulabs.vividvidhi.dto.Poem;
import org.drulabs.vividvidhi.poem.PoemContract;
import org.drulabs.vividvidhi.utils.Store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaushald on 24/02/17.
 */

public class PoemsHandler {

    // 100 poems are too much anyways
    private static final int LIMIT = 100;

    private Context mContext;
    private PoemContract.Presenter mPresenter;
    private Callback callback;

    private boolean hasMoreItems = true;

    //Firebase references
    private DatabaseReference poemsDB;
    private Query poemQuery;
    private FirebaseAnalytics mAnalytics;

    public PoemsHandler(Context mContext, PoemContract.Presenter mPresenter, Callback callback) {
        this.mContext = mContext;
        this.mPresenter = mPresenter;
        this.callback = callback;

        poemsDB = FirebaseDatabase.getInstance().getReference().child(Constants
                .POEMS_DB);
        mAnalytics = FirebaseAnalytics.getInstance(mContext);
    }

    // There aren't going to be many poems, fetch all at once, no pagination is required as of now
    public void fetch() {
        if (hasMoreItems) {
            poemQuery = poemsDB.orderByChild(Constants.POEMS_PUBLISH_DATE).limitToFirst(LIMIT);
            poemQuery.addValueEventListener(poemsListener);
        }
    }

    public void fetchLikedPoems(){
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

    public boolean hasMoreItems() {
        return hasMoreItems;
    }

    private ValueEventListener poemsListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot != null && snapshot.hasChildren()) {

                Map<String, Poem> poemMap = new HashMap<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.getKey();
                    Poem poem = ds.getValue(Poem.class);
                    poemMap.put(key, poem);
                }

                if (poemMap.size() < LIMIT) {
                    hasMoreItems = false;
                }

                callback.onPoemsFetched(poemMap);
                DBHandler.getHandle(mContext).addPoem(poemMap);

                // Now fetch all the poems liked by current user
                fetchLikedPoems();


            } else {
                callback.onError(mContext.getString(R.string.no_more_poems_available));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.onError(mContext.getString(R.string.something_went_wrong));
        }
    };

    public void updateLikesCount(@NonNull final String key, final boolean isLiked) {

        // Update likes count
        poemsDB.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Poem poem = dataSnapshot.getValue(Poem.class);
                    int likesCount = poem.getLikesCount();

                    likesCount = (isLiked) ? likesCount + 1 : likesCount - 1;

                    poem.setLikesCount(likesCount);

                    poemsDB.child(key).setValue(poem);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Updating likes DB
        (new LikesHandler(mContext, null, key)).updateLikeStatus(isLiked,
                System.currentTimeMillis());

        // Updating local DB
        DBHandler.getHandle(mContext).setLikedForPoem(key, isLiked);

    }

    public void reset() {
        hasMoreItems = true;
    }

    public interface Callback {
        void onPoemsFetched(Map<String, Poem> poemMap);

        void onError(String message);
    }
}
