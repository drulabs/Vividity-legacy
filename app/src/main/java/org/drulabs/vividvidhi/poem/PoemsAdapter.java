package org.drulabs.vividvidhi.poem;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.db.DBHandler;
import org.drulabs.vividvidhi.dto.Poem;
import org.drulabs.vividvidhi.firebase.FirebaseImageHelper;
import org.drulabs.vividvidhi.utils.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaushald on 23/02/17.
 */

public class PoemsAdapter extends RecyclerView.Adapter<PoemsAdapter.PoemsVH> {

    private List<Poem> poems;
    private Map<String, Poem> poemMap;
    private PoemContract.Presenter mPresenter;
    private Context mContext;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd, yyyy " +
            "(HH:mm:ss)");

    private DBHandler dbHandler;

    public PoemsAdapter(Context context, PoemContract.Presenter presenter) {

        this.poemMap = new HashMap<>();
        this.poems = new ArrayList<>();
        this.mContext = context;
        this.mPresenter = presenter;

        dbHandler = DBHandler.getHandle(mContext);

    }

    @Override
    public PoemsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View poemView = LayoutInflater.from(context).inflate(R.layout
                .poem_item_layout, parent, false);
        return new PoemsVH(poemView);
    }

    @Override
    public void onBindViewHolder(PoemsVH holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return poems == null ? 0 : poems.size();
    }

    public void append(Map<String, Poem> poemMap) {
        if (this.poems != null && this.poems.size() >= 0 && poemMap != null) {
            this.poemMap.putAll(poemMap);
            this.poems.addAll(getValueListFromMap(poemMap));
            this.notifyDataSetChanged();
            //TODO db insert operation
        }
    }

    public void append(String key, Poem poem) {
        if (this.poems != null && this.poems.size() >= 0 && poemMap != null) {
            this.poemMap.put(key, poem);
            this.poems.add(poem);
            this.notifyDataSetChanged();
            //TODO db insert operation
        }
    }

    public void reset() {
        this.poemMap.clear();
        this.poems.clear();
        notifyDataSetChanged();
    }

    class PoemsVH extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvPoem;
        TextView tvWrittenBy;
        ImageView imgPoem;
        TextView likes, comments, share;
        boolean isLiked = false;

        public PoemsVH(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.txt_the_title);
            tvPoem = (TextView) itemView.findViewById(R.id.txt_the_poem);
            tvWrittenBy = (TextView) itemView.findViewById(R.id.txt_image_credit);
            imgPoem = (ImageView) itemView.findViewById(R.id.item_image);
            likes = (TextView) itemView.findViewById(R.id.txt_like_count);
            comments = (TextView) itemView.findViewById(R.id.txt_comment_count);
            share = (TextView) itemView.findViewById(R.id.txt_share_btn);
        }

        public void bind(int position) {
            final Poem poem = poems.get(position);
            final String poemKey = getKeyForPoem(poem);

            isLiked = dbHandler.isPoemLiked(poemKey);

            likes.setText(String.valueOf(poem.getLikesCount()));
            likes.setCompoundDrawablesWithIntrinsicBounds(isLiked ? R.mipmap.ic_like : R.mipmap
                    .ic_unlike, 0, 0, 0);

            comments.setText(String.valueOf(poem.getCommentsCount()));

            tvTitle.setText(poem.getName());

            tvPoem.setText(Utility.getSpannedFromHtml(poem.getText()));

            tvPoem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    boolean isPoemExpanded = tvPoem.getLayoutParams().height == ViewGroup
                            .LayoutParams.WRAP_CONTENT;
                    if (!isPoemExpanded) {
                        ViewGroup.LayoutParams tvPoemLayoutParams = tvPoem.getLayoutParams();
                        tvPoemLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        tvPoem.setLayoutParams(tvPoemLayoutParams);
                    } else {
                        ViewGroup.LayoutParams tvPoemLayoutParams = tvPoem.getLayoutParams();
                        tvPoemLayoutParams.height = 300;
                        tvPoem.setLayoutParams(tvPoemLayoutParams);
                    }

                }
            });


            // Load Image
            FirebaseImageHelper.loadImageIn(mContext, poem.getImageName(), imgPoem);

            comments.setText(String.valueOf(poem.getCommentsCount()));
            tvWrittenBy.setText(mContext.getString(R.string.created_on_text) + "(" + dateFormat
                    .format(new Date(poem.getPublishDate())) + ")\n" + poem.getWrittenBy());

            imgPoem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onPicClicked(poemKey, poem);
                }
            });

            likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    isLiked = !isLiked;

                    likes.setCompoundDrawablesWithIntrinsicBounds(isLiked ? R.mipmap.ic_like : R
                            .mipmap.ic_unlike, 0, 0, 0);

                    if (isLiked) {
                        poem.setLikesCount(poem.getLikesCount() + 1);

                        likes.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_like, 0, 0, 0);
                    } else {
                        poem.setLikesCount(poem.getLikesCount() - 1);

                        likes.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_unlike, 0, 0, 0);
                    }
                    mPresenter.onLikeClicked(poemKey, poem, isLiked);
                    likes.setText(String.valueOf(poem.getLikesCount()));
                }
            });

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onCommentsClicked(poemKey, poem);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onShareClicked(poemKey, poem);
                }
            });
        }
    }

    private List<Poem> getValueListFromMap(Map<String, Poem> poemMap) {

        List<Poem> localPoems = new ArrayList<>();

        for (Map.Entry<String, Poem> map : poemMap.entrySet()) {
            localPoems.add(map.getValue());
        }

        Collections.sort(localPoems);

        return localPoems;
    }

    @Nullable
    private String getKeyForPoem(Poem poem) {
        if (poemMap == null || !poemMap.containsValue(poem)) {
            return null;
        }

        for (Map.Entry<String, Poem> map : poemMap.entrySet()) {
            if (map.getValue() == poem) {
                return map.getKey();
            }
        }

        return null;
    }

}
