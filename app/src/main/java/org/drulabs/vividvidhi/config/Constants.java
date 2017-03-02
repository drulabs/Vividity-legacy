package org.drulabs.vividvidhi.config;

/**
 * Created by kaushald on 22/01/17.
 */

public interface Constants {

    String FILE_PROVIDER_AUTHORITY = "org.drulabs.vividvidhi.provider";

    // google constants
    String GOOGLE_CLIENT_ID = "560496245142-j6rabe001bm25r159m1ifft22iuc34lo.apps.googleusercontent.com";

    int MIN_USERNAME_LENGTH = 6;
    int MIN_NAME_LENGTH = 5;
    int MIN_PASSWORD_LENGTH = 6;

    String LOGIN_IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/holycrab-528ac.appspot" +
            ".com/o/note_images%2FIMG_20161229_075229" +
            ".jpg?alt=media&token=0f798462-742f-492c-9f8e-69ff1101a6a6";
    String USER_BASE = "users";
    String REQUESTED_USER_BASE = "requested_users";
    String UB_USERNAME = "username";
    String UB_FCM_TOKEN = "fcmToken";
    String UB_FIREBASE_UID = "firebaseUID";
    String UB_FIREBASE_PIC = "firebasePicUrl";
    String UB_USER_EMAIL = "userEmail";

    String IMAGE_BUCKET = "vivid-vidhi.appspot.com";
    String USER_IMAGE_FOLDER = "user_images";

    // Image processing related
    int THUMB_SIZE = 144;
    String PICS_DB = "photos";
    String PICS_DATE_TAKEN = "dateTaken";

    String COMMENTS_DB = "comments";
    String COMMENTS_TIMESTAMP = "timestamp";
    String COMMENTS_ARTIFACT_ID = "artifactId";
    String KEY_COMMENTS_COUNT = "commentsCount";
    String LIKES_DB = "likes";
    String LIKES_ARTIFACT_ID = "artifactId";
    String LIKES_LIKEDBY_ID = "likedById";
    String LIKES_LIKEDBY_NAME = "likedByName";
    String LIKES_LIKEDBY_PIC = "likerPic";
    String LIKES_LIKEDBY_DATE = "likedOn";
    String LIKES_ARTIFACT_UNIQUEID = "likesUniqueIdentifier";

    String POEMS_DB = "poems";
    String POEMS_PUBLISH_DATE = "publishDate";
}
