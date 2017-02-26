package org.drulabs.vividvidhi.landing;

import android.widget.ImageView;

import org.drulabs.vividvidhi.BasePresenter;
import org.drulabs.vividvidhi.BaseView;
import org.drulabs.vividvidhi.dto.Picture;

import java.util.HashMap;

/**
 * Created by kaushald on 05/02/17.
 */

public interface PicsContract {

    public interface View extends BaseView<Presenter> {
        void showLoading();

        void hideLoading();

        void reset();

        void loadPics(HashMap<String, Picture> photos);

        void onLoadError(String msg);
    }

    public interface Presenter extends BasePresenter {
        void loadImageIn(ImageView img);

        void reset();

        void loadNextBatch();

        void onPicClicked(String key, Picture pic);

        void onLikeClicked(String key, Picture pic, boolean liked);

        void onCommentsClicked(String key, Picture pic);

        void onShareClicked(String key, Picture pic);

        void onDownloadClicked(String key, Picture pic);
    }

}
