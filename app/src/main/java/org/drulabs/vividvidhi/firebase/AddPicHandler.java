package org.drulabs.vividvidhi.firebase;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.Comment;
import org.drulabs.vividvidhi.dto.Picture;
import org.drulabs.vividvidhi.utils.Store;

import java.io.ByteArrayOutputStream;

/**
 * Created by kaushald on 09/02/17.
 */

public class AddPicHandler {

    private Context context;
    private Callback callback;

    // Firebase elements
    private DatabaseReference picsDB;
    private DatabaseReference commentsDB;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference;

    public AddPicHandler(@NonNull Context context, @NonNull Callback callback) {
        this.context = context;
        this.callback = callback;

        picsDB = FirebaseDatabase.getInstance().getReference().child(Constants.PICS_DB);
        commentsDB = FirebaseDatabase.getInstance().getReference().child(Constants.COMMENTS_DB);
    }

    public void uploadPic(Bitmap image, Bitmap thumbnail, String comment, Picture picture) {

        mStorageReference = mFirebaseStorage.getReferenceFromUrl("gs://" + Constants
                .IMAGE_BUCKET);
        // uploading pic
        if (image != null) {

            StorageReference tempRef = mStorageReference.child(Constants
                    .PICS_DB).child(picture.getPicName() + ".jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            tempRef.putBytes(imageData).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("AccessHandler", "Image uploaded");
                            callback.onUploadSuccessful();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("AccessHandler", "Image upload failure");
                    callback.onFailure();
                }
            });
        }

        // uploading thumbnail
        if (thumbnail != null) {

            StorageReference tempRef = mStorageReference.child(Constants.PICS_DB).child
                    (picture.getPicName() + "_thumb.jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] thumbData = baos.toByteArray();

            tempRef.putBytes(thumbData).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("AccessHandler", "Image thumb uploaded");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("AccessHandler", "Image thumb upload failure");
                }
            });
        }

        // uploading picture metadata
        //String picKey = picsDB.push().getKey();
        DatabaseReference tempDBRef = picsDB.push();
        String picKey = tempDBRef.getKey();
        tempDBRef.setValue(picture);

        // upload picture Comment
        Comment picComment = new Comment();
        picComment.setText(comment);
        picComment.setArtifactId(picKey);
        picComment.setTimestamp(System.currentTimeMillis());
        picComment.setCommenter(Store.getInstance(context).getMyName());
        picComment.setCommenterPic(Store.getInstance(context).getPicName());
        commentsDB.push().setValue(picComment);

    }

    public interface Callback {
        void onAlreadyUploaded();

        void onUploadSuccessful();

        void onFailure();
    }
}
