package org.drulabs.vividvidhi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.drulabs.vividvidhi.dto.User;

/**
 * Created by kaushald on 25/01/17.
 */

public class Store {

    private static final String APP_STORE_LOCAL = "app_prefs";

    private User currentUser;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    private static final String KEY_USERNAME = "username";
    private String username;

    private static final String KEY_PASSWORD = "password";
    private String password;

    private static final String KEY_NAME = "name";
    private String myName;

    private static final String KEY_USERKEY = "mykey";
    private String myKey;

    private static final String KEY_PIC_URL = "pic_url";
    private String picUrl;

    private static final String KEY_RELATIONSHIP = "relationship";
    private String relationship;

    private static final String KEY_ADMIN = "isadmin";
    private boolean isAdmin;

    private static final String KEY_PIC_NAME = "pic_name";
    private String picName;

    private static final String KEY_FCM_TOKEN = "fcm_token";
    private String fcmToken;

    private static final String KEY_FIREBASE_UID = "firebase_uid";
    private String firebaseUID;

    private static final String KEY_FIREBASE_PIC = "firebase_pic_url";
    private String firebasePicUrl;

    private static final String KEY_USER_EMAIL = "user_email";
    private String userEmail;

    private static final String KEY_USERINFO_SYNCED = "is_user_info_synced";
    private boolean isUserInfoSynced;

    private Store(Context context) {
        prefs = context.getSharedPreferences(APP_STORE_LOCAL, Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
    }

    private static Store instance = null;

    public static Store getInstance(Context context) {

        if (instance == null) {
            synchronized (Store.class) {
                if (instance == null) {
                    instance = new Store(context);
                }
            }
        }
        return instance;
    }

    public void saveCurrentUser(String currentUserKey, User currentUser) {
        this.currentUser = currentUser;

        isAdmin = currentUser.isAdmin();
        username = currentUser.getUsername();
        password = currentUser.getPassword();
        myName = currentUser.getName();
        relationship = currentUser.getRelationShip();
        picName = currentUser.getPicName();
        myKey = currentUserKey;

        prefsEditor.putString(KEY_USERNAME, username);
        prefsEditor.putString(KEY_PASSWORD, password);
        prefsEditor.putString(KEY_NAME, myName);
        prefsEditor.putString(KEY_USERKEY, myKey);
        prefsEditor.putString(KEY_RELATIONSHIP, relationship);
        prefsEditor.putString(KEY_PIC_NAME, picName);
        prefsEditor.putBoolean(KEY_ADMIN, isAdmin);

        prefsEditor.apply();
    }

    public void setUsername(String username) {
        this.username = username;
        prefsEditor.putString(KEY_USERNAME, username);
        prefsEditor.apply();
    }

    public void setPassword(String password) {
        this.password = password;
        prefsEditor.putString(KEY_PASSWORD, password);
        prefsEditor.apply();
    }

    public void setMyName(String myName) {
        this.myName = myName;
        prefsEditor.putString(KEY_NAME, myName);
        prefsEditor.apply();
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
        prefsEditor.putString(KEY_PIC_URL, picUrl);
        prefsEditor.apply();
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
        prefsEditor.putString(KEY_RELATIONSHIP, relationship);
        prefsEditor.apply();
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
        prefsEditor.putBoolean(KEY_ADMIN, isAdmin);
        prefsEditor.apply();
    }

    public void setPicName(String picName) {
        this.picName = picName;
        prefsEditor.putString(KEY_PIC_NAME, picName);
        prefsEditor.apply();
    }

    public void setMyKey(String key) {
        this.myKey = key;
        prefsEditor.putString(KEY_USERKEY, key);
        prefsEditor.apply();
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
        prefsEditor.putString(KEY_FCM_TOKEN, fcmToken);
        prefsEditor.apply();
    }

    public void setFirebaseUID(String firebaseUID) {
        this.firebaseUID = firebaseUID;
        prefsEditor.putString(KEY_FIREBASE_UID, firebaseUID);
        prefsEditor.apply();
    }

    public void setFirebasePicUrl(String firebasePicUrl) {
        this.firebasePicUrl = firebasePicUrl;
        prefsEditor.putString(KEY_FIREBASE_PIC, firebasePicUrl);
        prefsEditor.apply();
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        prefsEditor.putString(KEY_USER_EMAIL, userEmail);
        prefsEditor.apply();
    }

    public void setIsUserInfoSynced(boolean isSynced) {
        this.isUserInfoSynced = isSynced;
        prefsEditor.putBoolean(KEY_USERINFO_SYNCED, isSynced);
        prefsEditor.apply();
    }

    public boolean isUserInfoSynced() {
        if (isUserInfoSynced == false) {
            isUserInfoSynced = prefs.getBoolean(KEY_USERINFO_SYNCED, false);
        }
        return isUserInfoSynced;
    }

    public String getUserEmail() {
        if (userEmail == null) {
            userEmail = prefs.getString(KEY_USER_EMAIL, null);
        }
        return userEmail;
    }

    public String getFirebasePicUrl() {
        if (firebasePicUrl == null) {
            firebasePicUrl = prefs.getString(KEY_FIREBASE_PIC, null);
        }
        return firebasePicUrl;
    }

    public String getFirebaseUID() {
        if (firebaseUID == null) {
            firebaseUID = prefs.getString(KEY_FIREBASE_UID, null);
        }
        return firebaseUID;
    }

    public String getFcmToken() {
        if (fcmToken == null) {
            fcmToken = prefs.getString(KEY_FCM_TOKEN, null);
        }
        return fcmToken;
    }

    public String getUsername() {
        if (username == null) {
            username = prefs.getString(KEY_USERNAME, null);
        }
        return username;
    }

    public String getMyKey() {
        if (myKey == null) {
            myKey = prefs.getString(KEY_USERKEY, null);
        }
        return myKey;
    }

    public String getPassword() {
        if (password == null) {
            password = prefs.getString(KEY_PASSWORD, null);
        }
        return password;
    }

    public String getMyName() {
        if (myName == null) {
            myName = prefs.getString(KEY_NAME, null);
        }
        return myName;
    }

    public String getPicUrl() {
        if (picUrl == null) {
            picUrl = prefs.getString(KEY_PIC_URL, null);
        }
        return picUrl;
    }

    public String getRelationship() {
        if (relationship == null) {
            relationship = prefs.getString(KEY_RELATIONSHIP, null);
        }
        return relationship;
    }

    public String getPicName() {
        if (picName == null) {
            picName = prefs.getString(KEY_PIC_NAME, null);
        }
        return picName;
    }

    public boolean isAdmin() {
        isAdmin = prefs.getBoolean(KEY_ADMIN, false);
        return isAdmin;
    }


}
