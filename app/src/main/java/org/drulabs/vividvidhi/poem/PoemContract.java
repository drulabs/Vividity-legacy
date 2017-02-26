package org.drulabs.vividvidhi.poem;

import org.drulabs.vividvidhi.BasePresenter;
import org.drulabs.vividvidhi.BaseView;
import org.drulabs.vividvidhi.dto.Picture;
import org.drulabs.vividvidhi.dto.Poem;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kaushald on 21/02/17.
 */

public interface PoemContract {

    interface View extends BaseView<Presenter> {
        void showLoading();

        void hideLoading();

        void loadPoems(Map<String, Poem> poemMap);

        void onError(String message);

        void reset();
    }

    interface Presenter extends BasePresenter {
        void reset();

        void loadNextBatch();

        void onPicClicked(String key, Poem poem);

        void onLikeClicked(String key, Poem poem, boolean liked);

        void onCommentsClicked(String key, Poem poem);

        void onShareClicked(String key, Poem poem);

        void onDownloadClicked(String key, Poem poem);
    }
}
