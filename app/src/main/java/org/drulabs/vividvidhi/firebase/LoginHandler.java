package org.drulabs.vividvidhi.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.User;
import org.drulabs.vividvidhi.utils.Store;

/**
 * Created by kaushald on 25/01/17.
 */

public class LoginHandler {

    private LoginCallback callback;
    private String username;
    private String password;
    private Store store;

    // Firebase variables
    private DatabaseReference userDB;


    public LoginHandler(@NonNull Context context, @NonNull LoginCallback callback) {
        this.callback = callback;
        userDB = FirebaseDatabase.getInstance().getReference().child(Constants.USER_BASE);
        store = Store.getInstance(context);
    }

    public void handleLogin(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;

        Query userQuery = userDB.orderByChild(Constants.UB_USERNAME).equalTo
                (username).limitToLast(1);
        userQuery.addListenerForSingleValueEvent(userNameListener);
    }

    public void cancelLoginRequest() {
        if (username != null) {
            userDB.child(username).removeEventListener(userNameListener);
        }
    }

    private ValueEventListener userNameListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                // user exists. Go ahead

                User currentUser = null;
                String currentUserKey = null;
                // Since the query result can be more than 1, using foreach loop, in our case it
                // will be 1
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    currentUser = snapshot.getValue(User.class);
                    currentUserKey = snapshot.getKey();
                }

                if (currentUser != null && !password.equalsIgnoreCase(currentUser.getPassword())) {
                    // Password mismatch
                    callback.onInvalidPassword();
                    return;
                }

                // Username and password verified
                store.saveCurrentUser(currentUserKey, currentUser);
                callback.onSuccess(username, password);

            } else {
                // no such user found
                callback.onInvalidUsername();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.onDatabaseError();
        }
    };

    public interface LoginCallback {
        void onInvalidUsername();

        void onInvalidPassword();

        void onDatabaseError();

        void onSuccess(String username, String password);
    }
}
