package org.drulabs.vividvidhi.like;

import android.content.Context;

import org.drulabs.vividvidhi.dto.Like;
import org.drulabs.vividvidhi.firebase.LikesHandler;

/**
 * Created by kaushald on 25/02/17.
 */

public class LikesPresenter implements LikesContract.Presenter, LikesHandler.Callback {

    private Context mContext;
    private LikesContract.View view;
    private String artifactId;
    private String artifactType;

    private LikesHandler likesHandler;

    public LikesPresenter(Context context, LikesContract.View view, String artifactId, String
            artifactType) {
        this.mContext = context;
        this.view = view;
        this.artifactId = artifactId;
        this.artifactType = artifactType;

        this.view.setPresenter(this);
        this.likesHandler = new LikesHandler(mContext, this, this.artifactId);
    }

    @Override
    public void fetchLikes() {
        view.showLoading();
        likesHandler.fetchLikes();
    }

    @Override
    public void start() {
        fetchLikes();
    }

    @Override
    public void destroy() {
        this.view = null;
        this.likesHandler = null;
    }

    @Override
    public void onLikeUpdated(boolean isLiked, boolean isSuccess) {

    }

    @Override
    public void onLikeFetched(Like like) {
        if (view != null) {
            view.hideLoading();
            view.onLikeFetched(like);
        }
    }
}
