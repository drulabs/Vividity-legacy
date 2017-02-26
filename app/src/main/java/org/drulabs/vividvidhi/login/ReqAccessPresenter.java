package org.drulabs.vividvidhi.login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.dto.User;
import org.drulabs.vividvidhi.firebase.AccessHandler;
import org.drulabs.vividvidhi.ui.NotificationToast;
import org.drulabs.vividvidhi.utils.Compression;
import org.drulabs.vividvidhi.utils.Store;
import org.drulabs.vividvidhi.utils.Utility;
import org.drulabs.vividvidhi.utils.ValidationUtils;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kaushald on 26/01/17.
 */

public class ReqAccessPresenter implements ReqAccessContract.Presenter, AccessHandler.Callback {

    private static final int GALLERY_CODE = 22;
    private static final int CAMERA_CODE = 23;

    ReqAccessContract.View view;
    Activity context;

    AccessHandler accessHandler;

    private Bitmap imageBitmap;

    private boolean selectPicSuccessful = false;

    public ReqAccessPresenter(Activity context, ReqAccessContract.View view) {
        this.context = context;
        this.view = view;
        this.view.setPresenter(this);
        this.accessHandler = new AccessHandler(context, this);
    }

    @Override
    public void launchImagePicker() {
        boolean cameraPermissionGranted = Utility.checkPermission(Manifest.permission.CAMERA,
                context);
        selectPicSuccessful = false;
        if (cameraPermissionGranted) {
            Utility.getProfilePicSelectionDialog(context, GALLERY_CODE, CAMERA_CODE).show();
        } else {
            Utility.requestPermission(Manifest.permission.CAMERA, CAMERA_CODE, context);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (Manifest.permission.CAMERA.equalsIgnoreCase(permissions[0]) && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            launchImagePicker();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    if (data != null && data.getData() != null) {
                        Uri picUri = data.getData();

                        CropImage.activity(picUri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .start(context);
//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context
//                                    .getContentResolver(), picUri);
//                            this.imageBitmap = bitmap;
//                            view.onImageAvailable(imageBitmap);
//                            selectPicSuccessful = true;
//                            thumbnail = Bitmap.createScaledBitmap(imageBitmap, Constants
//                                    .THUMB_SIZE, Constants.THUMB_SIZE, false);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                    break;
                case CAMERA_CODE:
                    try {
                        final File imageFile = new File(context.getExternalFilesDir(Environment
                                .DIRECTORY_PICTURES), Store.getInstance(context).getPicName() + ".jpg");
                        Uri imageUri = Uri.fromFile(imageFile);

//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context
//                                .getContentResolver(), imageUri);
//                        this.imageBitmap = bitmap;
//                        view.onImageAvailable(imageBitmap);
//                        selectPicSuccessful = true;
//                        if(data.getExtras()!=null){
//                            thumbnail = (Bitmap) data.getExtras().get("data");
//                        }

                        CropImage.activity(imageUri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .start(context);

                    } catch (Exception e) {
                        Store.getInstance(context).setPicName(null);
                        e.printStackTrace();
                    }

                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();

                        try {
                            String compressedImagePath = Compression.compressImage(context,
                                    resultUri);

                            Bitmap bitmap = BitmapFactory.decodeFile(compressedImagePath);

                            this.imageBitmap = bitmap;
                            view.onImageAvailable(imageBitmap);
                            selectPicSuccessful = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            NotificationToast.showToast(context, context.getString(R.string
                                    .something_went_wrong));
                        }

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                        error.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void handleAccessRequest(User user) {
        if (!ValidationUtils.isValidUserName(user.getUsername())) {
            view.onUsernameError(context.getString(R.string.username_check_failed));
            return;
        }

        if (!ValidationUtils.isValidName(user.getName())) {
            view.onNameError(context.getString(R.string.name_check_failed));
            return;
        }

        if (!ValidationUtils.isValidPassword(user.getPassword())) {
            view.onPasswordError(context.getString(R.string.password_check_failed));
            return;
        }

        if (user.getRelationShip() == null || user.getRelationShip().isEmpty()) {
            view.onRelationshipEmptyError();
            return;
        }

//        if (!selectPicSuccessful) {
//            view.onNoProfilePicError();
//            return;
//        }

        view.showLoadingIndicator();
        accessHandler.requestAccess(user, imageBitmap);
    }

    @Override
    public void start() {
        // Any initialization goes in here
        // currently none
    }

    @Override
    public void destroy() {
        view = null;
        context = null;
        imageBitmap = null;
    }

    @Override
    public void onAlreadyRequested() {
        if (view != null) {
            view.hideLoadingIndicator();
            view.onAlreadyRegistered();
        }
    }

    @Override
    public void onAlreadyGranted() {
        if (view != null) {
            view.hideLoadingIndicator();
            view.onAlreadyGranted();
        }
    }

    @Override
    public void onRequestSentSuccessfully() {
        if (view != null) {
            view.hideLoadingIndicator();
            view.onRequestSentSuccessfully();
        }
    }

    @Override
    public void onError(String displayMessage) {
        if (view != null) {
            view.hideLoadingIndicator();
            view.onError(displayMessage);
        }
    }
}
