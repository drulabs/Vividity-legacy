package org.drulabs.vividvidhi.landing;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.drulabs.vividvidhi.PresenterCreator;
import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.dto.Picture;
import org.drulabs.vividvidhi.ui.NotificationToast;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * Use the {@link PicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PicsFragment extends Fragment implements PicsContract.View {

    private static final String TAG = "PicsFrag";

    private static final String ARG_SECTION_NAME = "section_name";
    private static final String ARG_FILTER_TEXT = "filter_text";

    // TODO: Rename and change types of parameters
    private String mSectionName;
    private String mFilterText;

    // Presenter for this view
    private PicsContract.Presenter mPresenter;

    // other required variables
    private PicsAdapter picsAdapter;
    private int totalItemCount, visibleItemCount, firstVisibleItem, previousTotal;
    private boolean isLoading = false;
    private static final int VISIBLE_THRESHOLD = 0;
    private boolean userInitiatedScroll = false;

    // UI References
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;

    public PicsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionName name of the section to be viewed.
     * @param filterText  fiter text if any, else pass null.
     * @return A new instance of fragment PicsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PicsFragment newInstance(String sectionName, String filterText) {
        PicsFragment fragment = new PicsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_NAME, sectionName);
        args.putString(ARG_FILTER_TEXT, filterText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSectionName = getArguments().getString(ARG_SECTION_NAME);
            mFilterText = getArguments().getString(ARG_FILTER_TEXT);
            Log.d(TAG, "section: " + mSectionName + ", filter: " + mFilterText);
        } else {
            Log.d(TAG, "No arguments passed");
        }
        PresenterCreator.createPicsPresenter(getActivity(), this);
        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_holder_layout, container, false);
        initializeUI(view);
        return view;
    }

    @Override
    public void setPresenter(PicsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    private void initializeUI(View view) {
        picsAdapter = new PicsAdapter(getActivity(), mPresenter);
        recyclerView = (RecyclerView) view.findViewById(R.id.content_holder);

        mLayoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(picsAdapter);

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
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
            this.mPresenter = null;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
    public void reset() {
        mPresenter.reset();
        picsAdapter.reset();
    }

    @Override
    public void loadPics(HashMap<String, Picture> photos) {
        picsAdapter.append(photos);

        if (recyclerView.getVisibility() != View.VISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAlpha(1.0f);
        }
        hideLoading();
    }

    @Override
    public void onLoadError(String msg) {
        NotificationToast.showToast(getActivity(), msg);
    }
}
