package org.drulabs.vividvidhi.comment;

import org.drulabs.vividvidhi.BasePresenter;
import org.drulabs.vividvidhi.BaseView;
import org.drulabs.vividvidhi.dto.Comment;

import java.util.List;

/**
 * Created by kaushald on 10/02/17.
 */

public interface CommentContract {

    public interface View extends BaseView<Presenter> {
        void showLoading();

        void hideLoading();

        void loadComments(List<Comment> comments);

        void loadComment(Comment comment);

        void onLoadError(String message);

        void onCommentSaved();
    }

    public interface Presenter extends BasePresenter {
        void loadNextBatch();

        void addComment(Comment comment);
    }

}
