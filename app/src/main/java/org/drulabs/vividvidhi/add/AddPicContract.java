package org.drulabs.vividvidhi.add;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import org.drulabs.vividvidhi.BasePresenter;
import org.drulabs.vividvidhi.BaseView;
import org.drulabs.vividvidhi.dto.Picture;

/**
 * Created by kaushald on 07/02/17.
 */

public interface AddPicContract {

    public interface View extends BaseView<Presenter> {
        void showLoading();

        void hideLoading();

        void onAlreadyExists();

        void onSaveSuccessful();

        void onColumnEmpty();

        void resetFields();

        void populateFields(long timestamp, String photographer, String relationship);

        void onImageAvailable(Bitmap image, long timestamp);

        void onError();
    }

    public interface Presenter extends BasePresenter {
        void saveData(@NonNull Picture picture, String comment);

        void launchCameraOrGallery();

        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull
                int[] grantResults);

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

}
