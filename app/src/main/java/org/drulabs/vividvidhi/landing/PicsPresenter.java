package org.drulabs.vividvidhi.landing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.comment.CommentsActivity;
import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.Picture;
import org.drulabs.vividvidhi.firebase.FirebaseImageHelper;
import org.drulabs.vividvidhi.firebase.LikesHandler;
import org.drulabs.vividvidhi.firebase.PicsHandler;
import org.drulabs.vividvidhi.like.LikesActivity;
import org.drulabs.vividvidhi.services.Downloader;
import org.drulabs.vividvidhi.ui.NotificationToast;

import java.util.HashMap;

/**
 * Created by kaushald on 05/02/17.
 */

public class PicsPresenter implements PicsContract.Presenter, PicsHandler.Callback {

    private PicsContract.View view;
    private Activity activity;

    private PicsHandler picsHandler;
    private LikesHandler likesHandler;

    public PicsPresenter(Activity activity, PicsContract.View view) {
        this.view = view;
        this.activity = activity;
        picsHandler = new PicsHandler(this.activity, this);
        view.setPresenter(this);
    }

    @Override
    public void loadImageIn(ImageView img) {
        // bloody useless method
    }

    @Override
    public void loadNextBatch() {
        if (picsHandler.hasMorePics()) {
            picsHandler.fetchPics();
            if (view != null) {
                view.showLoading();
            }
        }
    }

    @Override
    public void start() {
        loadNextBatch();
    }

    @Override
    public void destroy() {
        this.view = null;
        this.picsHandler = null;
    }

    @Override
    public void onPhotosFetched(HashMap<String, Picture> photos) {
        if (view != null) {
            view.loadPics(photos);
            view.hideLoading();
        }
    }

    @Override
    public void onAllPicsFetched() {
        view.hideLoading();
    }

    @Override
    public void onError(String message) {
        if (view != null) {
            view.hideLoading();
            view.onLoadError(message);
        }
    }

    @Override
    public void reset() {
        picsHandler.resetLastTimestamp();
        loadNextBatch();
    }

    @Override
    public void onPicClicked(String key, Picture pic) {
        Intent likesIntent = new Intent(activity, LikesActivity.class);
        likesIntent.putExtra(LikesActivity.KEY_TITLE, pic.getPicName());
        likesIntent.putExtra(LikesActivity.KEY_ARTIFACT_ID, key);
        likesIntent.putExtra(LikesActivity.KEY_ARTIFACT_TYPE, Constants.PICS_DB);
        likesIntent.putExtra(LikesActivity.KEY_LIKES_COUNT, pic.getLikesCount());
        activity.startActivity(likesIntent);
    }

    @Override
    public void onLikeClicked(String key, Picture pic, boolean liked) {
        picsHandler.updateLikesCount(key, liked);
    }

    @Override
    public void onCommentsClicked(String key, Picture pic) {
        Intent commentIntent = new Intent(activity, CommentsActivity.class);
        commentIntent.putExtra(CommentsActivity.KEY_TITLE, pic.getPicName());
        commentIntent.putExtra(CommentsActivity.KEY_ARTIFACT_ID, key);
        commentIntent.putExtra(CommentsActivity.KEY_ARTIFACT_TYPE, Constants.PICS_DB);
        activity.startActivity(commentIntent);
    }

    @Override
    public void onShareClicked(String key, final Picture pic) {
//        NotificationToast.showToast(activity, activity.getString(R.string.feature_coming_soon));

        FirebaseImageHelper.getDownloadURI(pic, new FirebaseImageHelper.DownloadURIListener() {
            @Override
            public void onDownloadURIFetched(Uri downloadUri) {
                NotificationToast.showToast(activity, activity.getString(R.string
                        .text_downloading));

                String outputFilePath = Environment.getExternalStorageDirectory() + "/vivi/" +
                        pic.getPicName() + ".jpg";

                Downloader.downloadAndShare(activity, downloadUri, outputFilePath, false);
            }

            @Override
            public void onError(Exception e) {
                NotificationToast.showToast(activity, activity.getString(R.string
                        .something_went_wrong));
            }
        });
    }

    @Override
    public void onDownloadClicked(String key, final Picture pic) {

        FirebaseImageHelper.getDownloadURI(pic, new FirebaseImageHelper.DownloadURIListener() {
            @Override
            public void onDownloadURIFetched(Uri downloadUri) {
                NotificationToast.showToast(activity, activity.getString(R.string
                        .text_downloading));

                String outputFilePath = Environment.getExternalStorageDirectory() + "/vivi/" +
                        pic.getPicName() + ".jpg";

                Downloader.startDownload(activity, downloadUri, outputFilePath, true);
            }

            @Override
            public void onError(Exception e) {
                NotificationToast.showToast(activity, activity.getString(R.string
                        .something_went_wrong));
            }
        });
    }
}
