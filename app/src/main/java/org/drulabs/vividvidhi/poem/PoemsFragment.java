package org.drulabs.vividvidhi.poem;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.drulabs.vividvidhi.PresenterCreator;
import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.dto.Poem;
import org.drulabs.vividvidhi.ui.NotificationToast;

import java.util.Map;

public class PoemsFragment extends Fragment implements PoemContract.View {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_WRITTEN_BY = "written_by";
    private static final String ARG_FILTER_TEXT = "filter_text";
    private static final String ARG_SHRINK_LIST = "shrink_list";

    // UI References
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;

    // Other variables
    private int totalItemCount, visibleItemCount, firstVisibleItem;
    private static final int VISIBLE_THRESHOLD = 0;
    private boolean userInitiatedScroll = false;
    private PoemsAdapter mAdapter;

    // local vars
    private String writtenBy = null;
    private String filterText = null;
    private boolean shrinkList = true;

    private PoemContract.Presenter mPresenter = null;

    public PoemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param writtenBy  Give me poems written by this poet/poetess only.
     * @param filterText any extra filter text, if any, null otherwise.
     * @return A new instance of fragment PoemsFragment.
     */
    public static PoemsFragment newInstance(String writtenBy, String filterText, boolean shrinkList) {
        PoemsFragment fragment = new PoemsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WRITTEN_BY, writtenBy);
        args.putString(ARG_FILTER_TEXT, filterText);
        args.putBoolean(ARG_SHRINK_LIST, shrinkList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            writtenBy = getArguments().getString(ARG_WRITTEN_BY);
            filterText = getArguments().getString(ARG_FILTER_TEXT);
            shrinkList = getArguments().getBoolean(ARG_SHRINK_LIST);
        }
        PresenterCreator.createPoemsPresenter(getActivity(), this);
        mPresenter.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
            this.mPresenter = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.list_holder_layout, container, false);
        initializeUI(view);
        return view;
    }

    private void initializeUI(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.content_holder);

        mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setAdapter(picsAdapter);

        mAdapter = new PoemsAdapter(getActivity(), mPresenter);
        recyclerView.setAdapter(mAdapter);

        progressBar = (ProgressBar) view.findViewById(R.id.list_holder_progressbar);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // If scroll state is touch scroll then set userScrolled
                // true
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    userInitiatedScroll = true;

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView
                            .getLayoutManager();

                    totalItemCount = layoutManager.getItemCount();
                    visibleItemCount = layoutManager.getChildCount();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (userInitiatedScroll && (totalItemCount - visibleItemCount)
                            <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
                        // End has been reached

                        userInitiatedScroll = false;
                        mPresenter.loadNextBatch();
                    }
                }

            }
        });

    }

    @Override
    public void setPresenter(PoemContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(0.2f);
        }
    }

    @Override
    public void hideLoading() {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setAlpha(1.0f);
        }
    }

    @Override
    public void loadPoems(Map<String, Poem> poemMap) {
        mAdapter.append(poemMap);
        if (recyclerView.getVisibility() != View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(1.0f);
        }
        hideLoading();
    }

    @Override
    public void onError(String message) {
        NotificationToast.showToast(getActivity(), message);
    }

    @Override
    public void reset() {
        if (mAdapter != null) {
            mAdapter.reset();
        }
        if (mPresenter != null) {
            mPresenter.reset();
        }
    }
}
