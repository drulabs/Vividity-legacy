package org.drulabs.vividvidhi.like;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.drulabs.vividvidhi.PresenterCreator;
import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.dto.Like;
import org.drulabs.vividvidhi.ui.NotificationToast;

public class LikesActivity extends AppCompatActivity implements LikesContract.View {

    public static final String KEY_TITLE = "title";
    public static final String KEY_ARTIFACT_ID = "artifactId";
    public static final String KEY_ARTIFACT_TYPE = "artifactType";
    public static final String KEY_LIKES_COUNT = "likesCount";

    private LikesContract.Presenter mPresenter;

    // UI elements
    private RecyclerView recyclerView;
    private View loaderView;
    private LikesAdapter likesAdapter;

    // local vars
    private String artifactId;
    private String artifactType;
    private int likesCount;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_holder_layout);

        Bundle extras = getIntent().getExtras();
        if (extras == null || !extras.containsKey(KEY_TITLE) || !extras.containsKey
                (KEY_ARTIFACT_ID) || !extras.containsKey(KEY_LIKES_COUNT) || !extras.containsKey
                (KEY_ARTIFACT_TYPE)) {
            NotificationToast.showToast(this, getString(R.string.invalid_args_msg));
            this.finish();
            return;
        }

        artifactId = extras.getString(KEY_ARTIFACT_ID);
        title = extras.getString(KEY_TITLE);
        likesCount = extras.getInt(KEY_LIKES_COUNT);
        artifactType = extras.getString(KEY_ARTIFACT_TYPE);

        setToolBarTitle(likesCount + " likes for " + title);

        initializeUI();

        PresenterCreator.createLikesPresenter(this, this, artifactId, artifactType);
        mPresenter.start();
    }

    private void initializeUI() {

        loaderView = findViewById(R.id.list_holder_progressbar);
        recyclerView = (RecyclerView) findViewById(R.id.content_holder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        likesAdapter = new LikesAdapter(LikesActivity.this);

        recyclerView.setAdapter(likesAdapter);

        loaderView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
            mPresenter = null;
        }
    }

    @Override
    public void setPresenter(LikesContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        if (loaderView != null) {
            loaderView.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(0.3f);
        }
    }

    @Override
    public void hideLoading() {
        if (loaderView != null) {
            recyclerView.setAlpha(1.0f);
            loaderView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLikeFetched(Like like) {
        likesAdapter.append(like);
        if (recyclerView.getVisibility() != View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    void setToolBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
