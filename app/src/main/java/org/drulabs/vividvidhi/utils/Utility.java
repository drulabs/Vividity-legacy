package org.drulabs.vividvidhi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.ui.NotificationToast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Authored by KaushalD on 8/27/2016.
 */
public class Utility {

    private static final String MIME_IMAGE = "image/*";

    public static void requestPermission(String strPermission, int perCode, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, strPermission)) {
            NotificationToast.showToast(activity, strPermission + " is required for app to " +
                    "function");
            ActivityCompat.requestPermissions(activity, new String[]{strPermission}, perCode);
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{strPermission}, perCode);
        }
    }

    public static boolean checkPermission(String strPermission, Context _c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ContextCompat.checkSelfPermission(_c, strPermission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static AlertDialog getProfilePicSelectionDialog(final Activity activity, final int
            galleryCode, final int cameraCode) {

        final String AUTHORITY = "org.drulabs.vividvidhi.provider";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getString(R.string.add_profile_pic));
        String[] types = {activity.getString(R.string.camera_capture), activity.getString(R.string
                .pick_from_galery)};
        alertDialog.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format
                        (new Date());
                String profilePicName = "JPEG_" + timeStamp + "_";

                Store.getInstance(activity).setPicName(profilePicName);

                switch (which) {
                    // Camera selected
                    case 0:
                        File imageFile = null;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                            try {
                                imageFile = new File(activity.getExternalFilesDir(Environment
                                        .DIRECTORY_PICTURES), profilePicName + ".jpg");

                                if (imageFile != null) {
                                    //Uri photoUri = Uri.fromFile(imageFile);
                                    Uri photoUri = FileProvider.getUriForFile(activity, AUTHORITY,
                                            imageFile);
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                    activity.startActivityForResult(cameraIntent, cameraCode);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("vivi", "No camera support found");
                            NotificationToast.showToast(activity, activity.getString(R.string
                                    .no_camera_found));
                        }
                        break;
                    // Picking image from gallery
                    case 1:
                        Intent galleryIntent = new Intent();
                        galleryIntent.setType(MIME_IMAGE);
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        activity.startActivityForResult(Intent.createChooser(galleryIntent,
                                activity.getString(R.string.pick_from_galery_title)), galleryCode);
                        break;
                }
            }

        });

        return (alertDialog.create());

    }

    public static void composeEmail(Context context, String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static AlertDialog getImageSrcSelectionDialog(final Activity activity, final int
            galleryCode, final int cameraCode) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(activity.getString(R.string.add_profile_pic));
        String[] types = {activity.getString(R.string.camera_capture), activity.getString(R.string
                .pick_from_galery)};
        alertDialog.setItems(types, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format
                        (new Date());
                String picName = "JPEG_" + timeStamp + "_";

                switch (which) {
                    // Camera selected
                    case 0:
                        File imageFile = null;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                            try {
                                imageFile = new File(activity.getExternalFilesDir(Environment
                                        .DIRECTORY_PICTURES), picName + ".jpg");

                                if (imageFile != null) {
                                    //Uri photoUri = Uri.fromFile(imageFile);
                                    Uri photoUri = FileProvider.getUriForFile(activity, Constants
                                            .FILE_PROVIDER_AUTHORITY, imageFile);
                                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                    // saving pic name temporarily in store's pic url method
                                    Store.getInstance(activity).setPicUrl(picName);
                                    activity.startActivityForResult(cameraIntent, cameraCode);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.e("vivi", "No camera support found");
                            NotificationToast.showToast(activity, activity.getString(R.string
                                    .no_camera_found));
                        }
                        break;
                    // Picking image from gallery
                    case 1:
                        Intent galleryIntent = new Intent();
                        galleryIntent.setType(MIME_IMAGE);
                        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                        activity.startActivityForResult(Intent.createChooser(galleryIntent,
                                activity.getString(R.string.pick_from_galery_title)), galleryCode);
                        break;
                }
            }

        });

        return (alertDialog.create());

    }

    /**
     * returns the bytesize of the give bitmap
     */
    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else {
            return bitmap.getByteCount();
        }
    }

    public static String getFilePathFromURI(Context context, Uri fileUri) {

        try {
            Cursor cursor = context.getContentResolver().query(fileUri, new String[]{MediaStore
                    .Images.Media.DATA}, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                String filePath = cursor.getString(columnIndex);
                cursor.close();
                return filePath;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static Spanned getSpannedFromHtml(String html){

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.M){
            return Html.fromHtml(html);
        } else {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        }

    }

//    public static AlertDialog getLoadedImageOptionDialog(final Activity activity, final String
//            noteId) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
//        alertDialog.setTitle("Note image options");
//        String[] types = {"View image", "Remove"};
//        alertDialog.setItems(types, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                dialog.dismiss();
//                switch (which) {
//                    case 0:
//                        Intent viewImageIntent = new Intent(activity, NoteImageActivity.class);
//                        viewImageIntent.putExtra(NoteImageActivity.KEY_NOTE_ID, noteId);
//                        activity.startActivity(viewImageIntent);
//                        break;
//                    case 1:
//                        NotificationToast.showToast(activity, "Under construction...");
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//        });
//
//        return (alertDialog.create());
//    }
}