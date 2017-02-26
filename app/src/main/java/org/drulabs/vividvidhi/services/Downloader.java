package org.drulabs.vividvidhi.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.ui.NotificationToast;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class Downloader extends IntentService {

    private static final String TAG = "Downloader";

    private static final String ACTION_DOWNLOAD = "org.drulabs.vividvidhi.services.action" +
            ".DOWNLOAD_ARTIFACT";
    private static final String ACTION_DOWNLOAD_WITH_PROGRESS = "org.drulabs.vividvidhi.services" +
            ".action.DOWNLOAD_ARTIFACT_WITH_PROGRESS";
    private static final String ACTION_DOWNLOAD_AND_SHARE = "org.drulabs.vividvidhi.services" +
            ".action.DOWNLOAD_AND_SHARE";

    // TODO: Rename parameters
    private static final String EXTRA_SHOW_NOTIFICATION = "org.drulabs.vividvidhi.services.extra" +
            ".SHOW_NOTIFICATION";
    private static final String EXTRA_DOWNLOAD_URI = "org.drulabs.vividvidhi.services.extra" +
            ".DOWNLOAD_URI";
    private static final String EXTRA_OUTPUT_FILE = "org.drulabs.vividvidhi.services.extra" +
            ".OUTPUT_FILE";

    public Downloader() {
        super("Downloader");
    }

    public static void startDownload(Context context, Uri downloadURI, String
            outputFileWithPath, boolean showNotification) {
        Intent intent = new Intent(context, Downloader.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_SHOW_NOTIFICATION, showNotification);
        intent.putExtra(EXTRA_DOWNLOAD_URI, downloadURI);
        intent.putExtra(EXTRA_OUTPUT_FILE, outputFileWithPath);
        context.startService(intent);
    }

    public static void downloadAndShare(Context context, Uri downloadURI, String
            outputFileWithPath, boolean showNotification) {
        Intent intent = new Intent(context, Downloader.class);
        intent.setAction(ACTION_DOWNLOAD_AND_SHARE);
        intent.putExtra(EXTRA_SHOW_NOTIFICATION, showNotification);
        intent.putExtra(EXTRA_DOWNLOAD_URI, downloadURI);
        intent.putExtra(EXTRA_OUTPUT_FILE, outputFileWithPath);
        context.startService(intent);
    }

    public static void startDownloadWithProgress(Context context, Uri downloadURI, String
            outputFileWithPath, boolean showNotification) {
        Intent intent = new Intent(context, Downloader.class);
        intent.setAction(ACTION_DOWNLOAD_WITH_PROGRESS);
        intent.putExtra(EXTRA_SHOW_NOTIFICATION, showNotification);
        intent.putExtra(EXTRA_DOWNLOAD_URI, downloadURI);
        intent.putExtra(EXTRA_OUTPUT_FILE, outputFileWithPath);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                Uri downloadURI = intent.getParcelableExtra(EXTRA_DOWNLOAD_URI);
                String outputFilePath = intent.getStringExtra(EXTRA_OUTPUT_FILE);
                boolean showNotification = intent.getBooleanExtra(EXTRA_SHOW_NOTIFICATION, true);
                handleDownload(downloadURI, outputFilePath, showNotification);
            } else if (ACTION_DOWNLOAD_AND_SHARE.equalsIgnoreCase(action)) {
                Uri downloadURI = intent.getParcelableExtra(EXTRA_DOWNLOAD_URI);
                String outputFilePath = intent.getStringExtra(EXTRA_OUTPUT_FILE);
                boolean showNotification = intent.getBooleanExtra(EXTRA_SHOW_NOTIFICATION, true);
                handleDownloadAndShare(downloadURI, outputFilePath, showNotification);

            } else if (ACTION_DOWNLOAD_WITH_PROGRESS.equals(action)) {
                Uri downloadURI = intent.getParcelableExtra(EXTRA_DOWNLOAD_URI);
                String outputFilePath = intent.getStringExtra(EXTRA_OUTPUT_FILE);
                boolean showNotification = intent.getBooleanExtra(EXTRA_SHOW_NOTIFICATION, true);
                handleDownloadWithProgress(downloadURI, outputFilePath, showNotification);
            }
        }
    }

    private boolean handleDownload(Uri downloadURI, String outputFilePath, boolean
            showNotification) {

        boolean fileExists = (new File(outputFilePath)).exists();

        String fileName = outputFilePath.substring(outputFilePath.lastIndexOf("/") + 1);

        int notificationId = (int) System.currentTimeMillis() / 1000;

        if (showNotification && fileExists) {
            displayCompletionNotification(notificationId, fileName, true, outputFilePath);
            // If file already exists in the specified location just return form here no need to
            // download it again. File name should remain same for app life time.
            return true;
        } else if (showNotification) {
            displayDownloadingNotification(notificationId, fileName);
        }

        DataInputStream dis = null;
        FileOutputStream fos = null;

        boolean isDownloadSuccessful;

        try {

            if (!(new File(outputFilePath)).getParentFile().exists()) {
                (new File(outputFilePath)).getParentFile().mkdirs();
            }

            URL u = new URL(downloadURI.toString());
            InputStream is = u.openStream();

            dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            fos = new FileOutputStream(new File(outputFilePath));
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

            isDownloadSuccessful = true;

            if (showNotification) {
                displayCompletionNotification(notificationId, fileName, isDownloadSuccessful,
                        outputFilePath);
            }

        } catch (MalformedURLException mue) {
            Log.e(TAG, "Malformed: " + mue.toString());
            mue.printStackTrace();
            isDownloadSuccessful = false;
            displayCompletionNotification(notificationId, fileName, isDownloadSuccessful, null);
        } catch (IOException ioe) {
            Log.e(TAG, "IOException: " + ioe.toString());
            ioe.printStackTrace();
            isDownloadSuccessful = false;
            displayCompletionNotification(notificationId, fileName, isDownloadSuccessful, null);
        } catch (SecurityException se) {
            Log.e(TAG, "Security: " + se.toString());
            se.printStackTrace();
            isDownloadSuccessful = false;
            displayCompletionNotification(notificationId, fileName, isDownloadSuccessful, null);
        } catch (Exception ex) {
            Log.e(TAG, "Security: " + ex.toString());
            ex.printStackTrace();
            isDownloadSuccessful = false;
            displayCompletionNotification(notificationId, fileName, isDownloadSuccessful, null);
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isDownloadSuccessful;

    }

    private void handleDownloadAndShare(Uri downloadURI, String outputFilePath, boolean
            showNotification) {

        boolean isSuccessful = (new File(outputFilePath)).exists();

        if (!isSuccessful) {
            // file does not exist locally download it before it can be shared
            isSuccessful = handleDownload(downloadURI, outputFilePath, showNotification);
        }

        // now the status of isSuccessful flag determines where the file exists locally or not,
        // as the download may have failed

        try {
            if (isSuccessful) {
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

//                Uri imageUri = FileProvider.getUriForFile(getApplicationContext(), Constants
//                        .FILE_PROVIDER_AUTHORITY, new File(outputFilePath));

                String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                        outputFilePath, "", null);

                Uri imageUri = Uri.parse(path);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_via_vivi));

                shareIntent.setType(mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
                        .getFileExtensionFromUrl(outputFilePath)));

                if (shareIntent.resolveActivity(getPackageManager()) != null) {

                    Intent chooserIntent = Intent.createChooser(shareIntent, getString(R.string
                            .text_share_now));
                    chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(chooserIntent);
                } else {
                    NotificationToast.showToast(getApplicationContext(), getString(R.string
                            .unable_to_share));
                }
            } else {
                NotificationToast.showToast(getApplicationContext(), getString(R.string
                        .something_went_wrong));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            NotificationToast.showToast(getApplicationContext(), getString(R.string
                    .something_went_wrong));
        }
    }

    private void handleDownloadWithProgress(Uri downloadURI, String outputFilePath, boolean
            showNotification) {
        // TODO: FINISH IT OR NOT. WHATEVER.
    }

    private void displayDownloadingNotification(int notificationId, String fileName) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_download)
                .setContentTitle("Downloading " + fileName)
                .setProgress(100, 0, true)
                .setTicker("Downloading " + fileName)
                .setAutoCancel(false)
                .setContentIntent(null);

        NotificationManager notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void displayCompletionNotification(int notificationId, String fileName, boolean
            isDownloadComplete, String fullFilePath) {

        PendingIntent openFilePendingIntent = null;

        if (isDownloadComplete) {

            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

            Uri imageUri = FileProvider.getUriForFile(getApplicationContext(), Constants
                    .FILE_PROVIDER_AUTHORITY, new File(fullFilePath));

            Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
            openFileIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            openFileIntent.setDataAndType(imageUri, mimeTypeMap.getMimeTypeFromExtension
                    (MimeTypeMap.getFileExtensionFromUrl(fullFilePath)));

            if (openFileIntent.resolveActivity(getPackageManager()) != null) {
                openFilePendingIntent = PendingIntent.getActivity(this, 0, openFileIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }

        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(isDownloadComplete ? R.mipmap.ic_download : R.mipmap.ic_error)
                .setContentTitle(isDownloadComplete ? "Download complete: " + fileName :
                        "Download error")
                .setTicker(isDownloadComplete ? "Download complete: " + fileName :
                        "Download error")
                .setContentText(isDownloadComplete ? getString(R.string.tap_to_open) :
                        "Download interrupted")
                .setAutoCancel(true)
                .setContentIntent(openFilePendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
