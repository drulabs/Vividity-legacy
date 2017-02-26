package org.drulabs.vividvidhi.firebase;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.Picture;

/**
 * Created by kaushald on 05/02/17.
 */

public class FirebaseImageHelper {

    private static final String TAG = "ImageHelper";

    public static void loadImageIn(Context context, StorageReference storageRef, ImageView img) {
        // Load the image using Glide
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .placeholder(R.drawable.ic_face_black_800dp)
                .error(R.mipmap.ic_launcher)
                .animate(R.anim.fade_in)
                .listener(new RequestListener<StorageReference, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, StorageReference model,
                                               Target<GlideDrawable> target, boolean
                                                       isFirstResource) {
                        e.printStackTrace();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, StorageReference
                            model, Target<GlideDrawable> target, boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        Log.d(TAG, "Image downloaded");
                        return false;
                    }
                })
                .into(img);
    }

    public static void loadImageIn(Context context, String imageURL, ImageView img) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(("gs://"
                + Constants.IMAGE_BUCKET)).child(imageURL);

        loadImageIn(context, storageRef, img);
    }

    public static void loadProfileImageIn(Context context, String imageURL, ImageView img) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(("gs://"
                + Constants.USER_IMAGE_FOLDER)).child(imageURL);

        loadImageIn(context, storageRef, img);
    }

    public static void loadImageIn(Activity activity, Uri imageUri, ImageView img) {
        Glide.with(activity)
                .load(imageUri.toString())
                .error(R.drawable.ic_face_black_800dp)
                .placeholder(R.drawable.ic_face_black_800dp)
                .animate(R.anim.fade_in)
                .into(img);
    }

//    public static void loadImageWithThumbnail(final Context context, String imageURL,
//                                              String thumbURL, final ImageView img) {
//
//        try {
//
//
//            final StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl
//                    ("gs://" + Constants.IMAGE_BUCKET).child(imageURL);
//            final StorageReference thumbRef = FirebaseStorage.getInstance().getReferenceFromUrl
//                    ("gs://" + Constants.IMAGE_BUCKET).child(thumbURL);
//
//            // Load the thumbnail image before loading actual image
//            Glide.with(context)
//                    .using(new FirebaseImageLoader())
//                    .load(thumbRef)
//                    .listener(new RequestListener<StorageReference, GlideDrawable>() {
//                        @Override
//                        public boolean onException(
//                                Exception e, StorageReference model, Target<GlideDrawable> target,
//                                boolean isFirstResource) {
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(
//                                GlideDrawable resource, StorageReference model, Target<GlideDrawable>
//                                target, boolean isFromMemoryCache, boolean isFirstResource) {
//
//                            // Thumbnail loading successful, load actual image now
//                            loadImageIn(context, imageRef, img);
//
//                            return false;
//                        }
//                    }).into(img);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("FirebaseImageHelper", "Error in loading image/thumb: " + e.toString());
//        }
//
//    }

    public static void loadLatestImageIn(final Context context, final ImageView img, final
    DownloadURIListener listener) {
        Query picsQuery = FirebaseDatabase.getInstance().getReference().child(Constants
                .PICS_DB).orderByChild(Constants.PICS_DATE_TAKEN).limitToLast(1);
        picsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    Picture pic = null;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        pic = snapshot.getValue(Picture.class);
                        pic.setPicURL(Constants.PICS_DB + "/" + pic.getPicName() + ".jpg");
                        pic.setThumbURL(Constants.PICS_DB + "/" + pic.getPicName() + "_thumb.jpg");
                        break;
                    }

                    if (pic != null) {
                        //loadImageIn(context, pic.getPicURL(), img);
                        getDownloadURI(pic, listener);
                    } else {
                        img.setImageResource(R.drawable.ic_face_black_800dp);
                    }

                } else {
                    img.setImageResource(R.drawable.ic_face_black_800dp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("ImageHelper", "latest image fetch error: " + databaseError.toString());
                img.setImageResource(R.drawable.ic_face_black_800dp);
            }
        });
    }

    public static void getDownloadURI(Picture picture, final DownloadURIListener listener) {

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(("gs://"
                + Constants.IMAGE_BUCKET)).child(picture.getPicURL());
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUri) {
                listener.onDownloadURIFetched(downloadUri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onError(e);
            }
        });

    }

    public interface DownloadURIListener {
        void onDownloadURIFetched(Uri downloadUri);

        void onError(Exception e);
    }

}
