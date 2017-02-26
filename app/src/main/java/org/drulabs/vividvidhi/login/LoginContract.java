package org.drulabs.vividvidhi.login;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import org.drulabs.vividvidhi.BasePresenter;
import org.drulabs.vividvidhi.BaseView;

/**
 * Created by kaushald on 24/01/17.
 */

public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void showLoading();

        void hideLoading();

        void onUserNameError(String message);

        void onPasswordError(String message);

        void onLoginFailure();

        void onLoginSuccess();

        void onRequestTimeout();

        void clearUsername();

        void clearPassword();

        void destroyWithMessage(String message);
    }

    interface Presenter extends BasePresenter {

        void handleLogin(String username, String password);

        void fetchWelcomeImageIn(ImageView imageView);

        void handleRequestAccess();

        void cancelLogin();

        void navigateToHome();

        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
                int[] grantResults);

        void requestSDCardPermission();
    }
}
