package org.drulabs.vividvidhi.poem;

import android.app.Activity;
import android.content.Intent;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.comment.CommentsActivity;
import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.Poem;
import org.drulabs.vividvidhi.firebase.PoemsHandler;
import org.drulabs.vividvidhi.like.LikesActivity;
import org.drulabs.vividvidhi.ui.NotificationToast;

import java.util.Map;

/**
 * Created by kaushald on 24/02/17.
 */

public class PoemsPresenter implements PoemContract.Presenter, PoemsHandler.Callback {

    private PoemContract.View view;
    private Activity activity;

    private PoemsHandler poemsHandler;

    public PoemsPresenter(Activity activity, PoemContract.View view) {
        this.activity = activity;
        this.view = view;

        poemsHandler = new PoemsHandler(this.activity, this, this);
        view.setPresenter(this);
    }

    @Override
    public void reset() {
        poemsHandler.reset();
        loadNextBatch();
    }

    @Override
    public void loadNextBatch() {
        if (poemsHandler.hasMoreItems()) {
            poemsHandler.fetch();
            view.showLoading();
        }
    }

    @Override
    public void onPicClicked(String key, Poem poem) {
        Intent likesIntent = new Intent(activity, LikesActivity.class);
        likesIntent.putExtra(LikesActivity.KEY_TITLE, poem.getName());
        likesIntent.putExtra(LikesActivity.KEY_ARTIFACT_ID, key);
        likesIntent.putExtra(LikesActivity.KEY_ARTIFACT_TYPE, Constants.POEMS_DB);
        likesIntent.putExtra(LikesActivity.KEY_LIKES_COUNT, poem.getLikesCount());
        activity.startActivity(likesIntent);
    }

    @Override
    public void onLikeClicked(String key, Poem poem, boolean liked) {
        poemsHandler.updateLikesCount(key, liked);
    }

    @Override
    public void onCommentsClicked(String key, Poem poem) {
        Intent commentIntent = new Intent(activity, CommentsActivity.class);
        commentIntent.putExtra(CommentsActivity.KEY_TITLE, poem.getName());
        commentIntent.putExtra(CommentsActivity.KEY_ARTIFACT_ID, key);
        commentIntent.putExtra(CommentsActivity.KEY_ARTIFACT_TYPE, Constants.POEMS_DB);
        activity.startActivity(commentIntent);
    }

    @Override
    public void onShareClicked(String key, Poem poem) {
        NotificationToast.showToast(activity, activity.getString(R.string.feature_coming_soon));
    }

    @Override
    public void onDownloadClicked(String key, Poem poem) {
        NotificationToast.showToast(activity, activity.getString(R.string.feature_coming_soon));
    }

    @Override
    public void start() {
        loadNextBatch();
    }

    @Override
    public void destroy() {
        this.view = null;
        this.poemsHandler = null;
    }

    @Override
    public void onPoemsFetched(Map<String, Poem> poemMap) {
        if (view != null) {
            view.hideLoading();
            view.loadPoems(poemMap);
        }
    }

    @Override
    public void onError(String message) {
        if (view != null) {
            view.hideLoading();
            view.onError(message);
        }
    }
}
