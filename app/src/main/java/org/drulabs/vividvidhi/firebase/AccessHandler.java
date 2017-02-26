package org.drulabs.vividvidhi.firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.User;

import java.io.ByteArrayOutputStream;

/**
 * Created by kaushald on 27/01/17.
 */

public class AccessHandler {

    private Callback callback;
    private Context context;
    private User requestLoginUser;
    private Bitmap userPic;

    // Firebase variables
    private DatabaseReference userDB;
    private DatabaseReference requestedUserDB;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference;

    public AccessHandler(@NonNull Context context, @NonNull Callback callback) {
        this.context = context;
        this.callback = callback;
        userDB = FirebaseDatabase.getInstance().getReference().child(Constants.USER_BASE);
        requestedUserDB = FirebaseDatabase.getInstance().getReference().child(Constants
                .REQUESTED_USER_BASE);
    }

    public void requestAccess(User user, Bitmap userImage/*, Bitmap imageThumb*/) {
        // firebase call here
        this.requestLoginUser = user;
        this.userPic = userImage;
//        this.thumbnail = imageThumb;

        Query userQuery = userDB.orderByChild(Constants.UB_USERNAME).equalTo
                (user.getUsername()).limitToLast(1);
        userQuery.addListenerForSingleValueEvent(userDBListener);
    }

    private ValueEventListener userDBListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                callback.onAlreadyGranted();
            } else {
                // permission not granted yet. see if already requested
                Query reqUserQuery = requestedUserDB.orderByChild(Constants.UB_USERNAME).equalTo
                        (requestLoginUser.getUsername()).limitToLast(1);
                reqUserQuery.addListenerForSingleValueEvent(requestedDBListener);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.onError(context.getString(R.string.something_went_wrong));
        }
    };

    private ValueEventListener requestedDBListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                callback.onAlreadyRequested();
            } else {
                // user has not requested yet. Go ahead and register this user
                if (requestLoginUser != null) {
                    requestedUserDB.push().setValue(requestLoginUser);
                    uploadProfilePic();
                    callback.onRequestSentSuccessfully();
                } else {
                    callback.onError(context.getString(R.string.something_went_wrong));
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.onError(context.getString(R.string.something_went_wrong));
        }
    };

    private void uploadProfilePic() {

        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://" + Constants
                .IMAGE_BUCKET);

        if (userPic != null) {

            mStorageReference = mStorageReference.child(Constants.USER_IMAGE_FOLDER).child
                    (requestLoginUser.getPicName() + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            userPic.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            mStorageReference.putBytes(imageData).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("AccessHandler", "Image uploaded");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("AccessHandler", "Image upload failure");
                }
            });
        }

        // Upload thumbnail too
//        if (thumbnail != null) {
//            mStorageReference = mStorageReference.child(Constants.USER_IMAGE_FOLDER).child
//                    (requestLoginUser.getPicName() + "_thumb" + ".jpg");
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] thumbData = baos.toByteArray();
//
//            mStorageReference.putBytes(thumbData).addOnSuccessListener(
//                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Log.d("AccessHandler", "THUMB Image uploaded");
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.d("AccessHandler", "THUMB Image upload failure");
//                }
//            });
//        }
    }

    public interface Callback {

        void onAlreadyRequested();

        void onAlreadyGranted();

        void onRequestSentSuccessfully();

        void onError(String displayMessage);

    }
}
