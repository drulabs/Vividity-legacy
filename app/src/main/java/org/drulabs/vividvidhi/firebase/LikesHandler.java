package org.drulabs.vividvidhi.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.Like;
import org.drulabs.vividvidhi.utils.Store;

import java.util.LinkedList;
import java.util.List;

/**
 * This handler is only responsible for updating the Likes database. Individual artifacts should
 * update likes count by them selves
 * Created by kaushald on 23/02/17.
 */
public class LikesHandler {

    private Context mContext;
    private Callback mListener;
    private String artifactId;

    private boolean hasMoreItems = true;

    private Store store;

    // Firebase variables
    private DatabaseReference likesDB;

    public LikesHandler(@NonNull Context context, Callback mListener, @NonNull String
            artifactId) {
        this.mContext = context;
        this.mListener = mListener;
        this.artifactId = artifactId;

        store = Store.getInstance(mContext);

        likesDB = FirebaseDatabase.getInstance().getReference().child(Constants
                .LIKES_DB);
    }

    public void fetchLikes() {
        if (hasMoreItems && mListener != null) {
            Query likesQuery = likesDB.orderByChild(Constants.LIKES_ARTIFACT_ID).equalTo
                    (artifactId);
            likesQuery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                        Like singleLike = dataSnapshot.getValue(Like.class);
                        mListener.onLikeFetched(singleLike);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public static void fetchLikedArtifacts(Context context, final IArtifactsCallback callback) {
        DatabaseReference likesDB = FirebaseDatabase.getInstance().getReference().child(Constants
                .LIKES_DB);
        Query likedArtifactsQuery = likesDB.orderByChild(Constants.LIKES_LIKEDBY_ID).equalTo
                (Store.getInstance(context).getMyKey());
        likedArtifactsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {

                    List<Like> likesList = new LinkedList<>();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Like like = snapshot.getValue(Like.class);
                        likesList.add(like);
                    }

                    callback.onArtifactsFetched(likesList);

                } else {
                    callback.onError();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError();
            }
        });
    }

    public void updateLikeStatus(final boolean isLiked, long fireDate) {

        String likesKey = artifactId + "_" + store.getMyKey();

        if (isLiked) {

            Like likeObj = new Like();
            likeObj.setArtifactId(artifactId);
            likeObj.setLikedById(store.getMyKey());
            likeObj.setLikesUniqueIdentifier(likesKey);
            likeObj.setLikedByName(store.getMyName());
            likeObj.setLikerPic(store.getPicName());
            likeObj.setLikedOn(fireDate);

            likesDB.child(likesKey).setValue(likeObj).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (mListener != null) {
                        mListener.onLikeUpdated(isLiked, true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mListener != null) {
                        mListener.onLikeUpdated(isLiked, false);
                    }
                }
            });
        } else {
            likesDB.child(likesKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (mListener != null) {
                        mListener.onLikeUpdated(isLiked, true);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (mListener != null) {
                        mListener.onLikeUpdated(isLiked, false);
                    }
                }
            });
        }
    }

    public interface Callback {
        void onLikeUpdated(boolean isLiked, boolean isSuccess);

        void onLikeFetched(Like like);
        //TODO implement pagination for likes
    }

    public interface IArtifactsCallback {
        void onArtifactsFetched(List<Like> myLikes);

        void onError();
    }

}
