package org.drulabs.vividvidhi.add;

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

import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.Picture;
import org.drulabs.vividvidhi.firebase.AddPicHandler;
import org.drulabs.vividvidhi.utils.Compression;
import org.drulabs.vividvidhi.utils.Store;
import org.drulabs.vividvidhi.utils.Utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * Created by kaushald on 09/02/17.
 */

public class AddPicPresenter implements AddPicContract.Presenter, AddPicHandler.Callback {

    private Activity activity;
    private AddPicContract.View view;

    private static final int GALLERY_CODE = 221;
    private static final int CAMERA_CODE = 235;

    private Bitmap imageBitmap;
    private Bitmap thumbnail;

    private long lastModified = System.currentTimeMillis();

    private AddPicHandler picHandler;

    public AddPicPresenter(@NonNull Activity activity, @NonNull AddPicContract.View view) {
        this.activity = activity;
        this.view = view;
        this.view.setPresenter(this);
        this.picHandler = new AddPicHandler(activity, this);
    }

    @Override
    public void saveData(Picture picture, String comment) {
        if (picture.getPicName() == null || picture.getDateTaken() == 0 || picture.getPhotoCredit()
                == null || picture.getRelationshipWithPhotographer() == null || comment == null
                || comment.isEmpty() || imageBitmap == null || thumbnail == null) {
            view.onColumnEmpty();
            return;
        }

        view.showLoading();
        picHandler.uploadPic(imageBitmap, thumbnail, comment, picture);
    }

    @Override
    public void launchCameraOrGallery() {
        boolean cameraPermissionGranted = Utility.checkPermission(Manifest.permission.CAMERA,
                activity);
        if (cameraPermissionGranted) {
            Utility.getImageSrcSelectionDialog(activity, GALLERY_CODE, CAMERA_CODE).show();
        } else {
            Utility.requestPermission(Manifest.permission.CAMERA, CAMERA_CODE, activity);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (Manifest.permission.CAMERA.equalsIgnoreCase(permissions[0]) && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) {
            launchCameraOrGallery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_CODE:
                    if (data != null && data.getData() != null) {
                        Uri picUri = data.getData();

                        String selectedFilePath = Compression.getRealPathFromURI(activity, picUri);
                        //Utility.getFilePathFromURI(activity, picUri);

                        if (selectedFilePath != null && !selectedFilePath.isEmpty()) {
                            File selectedFile = new File(selectedFilePath);
                            lastModified = selectedFile.lastModified();
                        }

                        // Using crop image library
                        CropImage.activity(picUri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true)
                                .start(activity);

//                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity
//                                    .getContentResolver(), picUri);
//                            this.imageBitmap = bitmap;
//                            view.onImageAvailable(imageBitmap);
//
//                            thumbnail = Bitmap.createScaledBitmap(imageBitmap, Constants
//                                    .THUMB_SIZE, Constants.THUMB_SIZE, false);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                    break;
                case CAMERA_CODE:
                    try {
                        final File imageFile = new File(activity.getExternalFilesDir(Environment
                                .DIRECTORY_PICTURES), Store.getInstance(activity).getPicUrl() +
                                ".jpg");
                        Uri imageUri = Uri.fromFile(imageFile);

                        // Using crop image library
                        CropImage.activity(imageUri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setCropShape(CropImageView.CropShape.RECTANGLE)
                                .setFixAspectRatio(true)
                                .start(activity);

//                        Store.getInstance(activity).setPicUrl(null);
//
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity
//                                .getContentResolver(), imageUri);
//                        this.imageBitmap = bitmap;
//                        view.onImageAvailable(imageBitmap);
//
//                        if (data.getExtras() != null) {
//                            thumbnail = (Bitmap) data.getExtras().get("data");
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();

                        try {
//                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity
//                                    .getContentResolver(), resultUri);

//                            Bitmap compressedBitmap = compressImage(bitmap);

                            String compressedImagePath = Compression.compressImage(activity,
                                    resultUri);

                            Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedImagePath);

                            this.imageBitmap = compressedBitmap;
                            view.onImageAvailable(imageBitmap, lastModified);

                            thumbnail = Bitmap.createScaledBitmap(compressedBitmap, Constants
                                    .THUMB_SIZE, Constants.THUMB_SIZE, false);

                        } catch (Exception e) {
                            e.printStackTrace();
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
    public void start() {
        String myName = Store.getInstance(activity).getMyName();
        String myRelationship = Store.getInstance(activity).getRelationship();

        view.populateFields(lastModified == 0 ? System.currentTimeMillis() : lastModified, myName,
                myRelationship);
    }

    @Override
    public void destroy() {
        this.view = null;
        this.picHandler = null;
        imageBitmap = null;
        thumbnail = null;
    }

    @Override
    public void onAlreadyUploaded() {
        if (view != null) {
            view.hideLoading();
            view.onAlreadyExists();
        }
    }

    @Override
    public void onUploadSuccessful() {
        if (view != null) {
            this.imageBitmap = null;
            this.thumbnail = null;
            view.hideLoading();
            view.resetFields();
            view.onSaveSuccessful();
        }
    }

    @Override
    public void onFailure() {
        if (view != null) {
            view.hideLoading();
            view.onError();
        }
    }
}
