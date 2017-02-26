package org.drulabs.vividvidhi.like;

import org.drulabs.vividvidhi.BasePresenter;
import org.drulabs.vividvidhi.BaseView;
import org.drulabs.vividvidhi.dto.Like;

import java.util.List;

/**
 * Created by kaushald on 25/02/17.
 */

public interface LikesContract {

    interface View extends BaseView<Presenter> {
        void showLoading();
        void hideLoading();
        void onLikeFetched(Like like);
    }

    interface Presenter extends BasePresenter {
        void fetchLikes();
    }

}
