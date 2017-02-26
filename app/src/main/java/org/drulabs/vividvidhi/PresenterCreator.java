package org.drulabs.vividvidhi;

import android.app.Activity;

import org.drulabs.vividvidhi.add.AddPicContract;
import org.drulabs.vividvidhi.add.AddPicPresenter;
import org.drulabs.vividvidhi.comment.CommentContract;
import org.drulabs.vividvidhi.comment.CommentsPresenter;
import org.drulabs.vividvidhi.landing.PicsContract;
import org.drulabs.vividvidhi.landing.PicsPresenter;
import org.drulabs.vividvidhi.like.LikesContract;
import org.drulabs.vividvidhi.like.LikesPresenter;
import org.drulabs.vividvidhi.login.LoginContract;
import org.drulabs.vividvidhi.login.LoginPresenter;
import org.drulabs.vividvidhi.login.ReqAccessContract;
import org.drulabs.vividvidhi.login.ReqAccessPresenter;
import org.drulabs.vividvidhi.poem.PoemContract;
import org.drulabs.vividvidhi.poem.PoemsPresenter;

/**
 * Created by kaushald on 25/01/17.
 */

public class PresenterCreator {

    public static LoginContract.Presenter createLoginPresenter(Activity context, LoginContract.View
            view) {
        return new LoginPresenter(context, view);
    }

    public static ReqAccessContract.Presenter createReqAccessPresenter(Activity context,
                                                                       ReqAccessContract.View view) {
        return new ReqAccessPresenter(context, view);
    }

    public static PicsContract.Presenter createPicsPresenter(Activity activity, PicsContract.View
            view) {
        return new PicsPresenter(activity, view);
    }

    public static AddPicContract.Presenter createAddPicPresenter(Activity activity,
                                                                 AddPicContract.View view) {
        return new AddPicPresenter(activity, view);
    }

    public static CommentContract.Presenter createCommentsPresenter(Activity activity, CommentContract
            .View view, String artifactId, String artifactType) {
        return new CommentsPresenter(activity, view, artifactId, artifactType);
    }

    public static PoemContract.Presenter createPoemsPresenter(Activity activity, PoemContract
            .View view) {
        return new PoemsPresenter(activity, view);
    }

    public static LikesContract.Presenter createLikesPresenter(Activity activity, LikesContract
            .View view, String artifactId, String artifactType) {
        return new LikesPresenter(activity, view, artifactId, artifactType);
    }
}
