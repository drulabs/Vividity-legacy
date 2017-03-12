package org.drulabs.vividvidhi.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.firebase.FirebaseImageHelper;
import org.drulabs.vividvidhi.firebase.LoginHandler;
import org.drulabs.vividvidhi.landing.LandingPage;
import org.drulabs.vividvidhi.utils.Utility;
import org.drulabs.vividvidhi.utils.ValidationUtils;

/**
 * Created by kaushald on 24/01/17.
 */
public class LoginPresenter implements LoginContract.Presenter, LoginHandler.LoginCallback {

    LoginContract.View view;
    Activity activity;
    LoginHandler loginHandler;

    private boolean isLoggingIn = false;

    private static final int SD_CARD_REQ_CODE = 12;

    public LoginPresenter(Activity context, LoginContract.View view) {
        this.view = view;
        this.activity = context;
        this.view.setPresenter(this);
        this.loginHandler = new LoginHandler(activity, this);
    }

    @Override
    public void handleLogin(String username, String password) {

        if (!ValidationUtils.isValidUserName(username)) {
            view.onUserNameError(activity.getString(R.string.username_check_failed));
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            view.onPasswordError(activity.getString(R.string.password_check_failed));
            return;
        }

        //Validate credentials
        loginHandler.handleLogin(username, password);
        isLoggingIn = true;
        view.showLoading();
    }

    @Override
    public void fetchWelcomeImageIn(final ImageView imageView) {
        FirebaseImageHelper.loadLatestImageIn(activity, imageView, new FirebaseImageHelper.DownloadURIListener() {
            @Override
            public void onDownloadURIFetched(Uri downloadUri) {

                FirebaseImageHelper.loadImageIn(activity, downloadUri, imageView);
            }

            @Override
            public void onError(Exception e) {
                // do nothing as default placeholder is already visible
            }
        });
        //FirebaseImageHelper
    }

    @Override
    public void handleRequestAccess() {
        Intent requestAccessIntent = new Intent(activity, ReqAccessActivity.class);
        activity.startActivity(requestAccessIntent);
    }

    @Override
    public void cancelLogin() {
        // cancel the login request
        if (isLoggingIn) {
            loginHandler.cancelLoginRequest();
        }
    }

    @Override
    public void navigateToHome() {
        Intent homeIntent = new Intent(activity, LandingPage.class);
        activity.startActivity(homeIntent);
        if (view != null) {
            view.destroyWithMessage(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == SD_CARD_REQ_CODE) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equalsIgnoreCase(permissions[0]) &&
                    grantResults[0] == PackageManager.PERMISSION_DENIED) {
                view.destroyWithMessage(activity.getString(R.string.required_permission_denied));
            }
        }
    }

    @Override
    public void requestSDCardPermission() {
        boolean isSDPermissionGranted = Utility.checkPermission(Manifest.permission
                .WRITE_EXTERNAL_STORAGE, activity);
        if (!isSDPermissionGranted) {
            Utility.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    SD_CARD_REQ_CODE, activity);
        }
    }

//    Target picassoTarget = null;

    @Override
    public void start() {

        // Welcome image download has been shifted to fetchWelcomeImage method

        /*picassoTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (view != null && bitmap != null) {
                    view.onWelcomeImageDownload(bitmap);
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        ImageHelper.loadImage(activity, Constants.LOGIN_IMAGE_URL, picassoTarget);*/
    }

    @Override
    public void destroy() {
        view = null;
//        picassoTarget = null;
        loginHandler = null;
    }

    @Override
    public void onInvalidUsername() {
        if (view != null) {
            view.hideLoading();
            view.clearUsername();
            view.onUserNameError(activity.getString(R.string.error_invalid_username));
        }
        isLoggingIn = false;
    }

    @Override
    public void onInvalidPassword() {
        if (view != null) {
            view.hideLoading();
            view.clearPassword();
            view.onPasswordError(activity.getString(R.string.error_invalid_password));
        }
        isLoggingIn = false;
    }

    @Override
    public void onDatabaseError() {
        if (view != null) {
            view.onLoginFailure();
            isLoggingIn = false;
            view.hideLoading();
        }
    }

    @Override
    public void onSuccess(String username, String password) {
        if (view != null) {
            view.onLoginSuccess();
            view.hideLoading();
            isLoggingIn = false;
        }
    }
}
