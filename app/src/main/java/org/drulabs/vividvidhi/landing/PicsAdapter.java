package org.drulabs.vividvidhi.landing;

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
import org.drulabs.vividvidhi.dto.Picture;
import org.drulabs.vividvidhi.firebase.FirebaseImageHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaushald on 05/02/17.
 */

public class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.PicsVH> {

    private List<Picture> photos;
    private Map<String, Picture> photoMap;
    private PicsContract.Presenter mPresenter;
    private Context mContext;

    private DBHandler dbHandler;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd, yyyy " +
            "(HH:mm:ss)");

    public PicsAdapter(Context context, PicsContract.Presenter picsPresenter) {
        photos = new ArrayList<>();
        photoMap = new HashMap<>();
        this.mContext = context;
        this.mPresenter = picsPresenter;
        dbHandler = DBHandler.getHandle(mContext);
    }

    @Override
    public PicsVH onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View picView = LayoutInflater.from(context).inflate(R.layout
                .item_layout, parent, false);
        return new PicsVH(picView);
    }

    @Override
    public void onBindViewHolder(PicsVH holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.size();
    }

    public void append(HashMap<String, Picture> photos) {
        if (this.photos != null && this.photos.size() >= 0 && photos != null) {
            photoMap.putAll(photos);
            this.photos.addAll(getValueListFromMap(photos));
            this.notifyDataSetChanged();
            dbHandler.addPic(photos);
        }
    }

    public void append(String key, Picture photo) {
        if (photos != null && photos.size() >= 0 && photo != null) {
            this.photos.add(photo);
            this.photoMap.put(key, photo);
            this.notifyDataSetChanged();
            dbHandler.addPic(key, photo);
        }
    }

    public void reset() {
        this.photos.clear();
        this.photoMap.clear();
        notifyDataSetChanged();
    }

    class PicsVH extends RecyclerView.ViewHolder {

        ImageView image;
        TextView likes;
        TextView comments;
        TextView share;
        TextView download;
        TextView imageDetails;
        private boolean isLiked = false;

        public PicsVH(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.item_image);
            imageDetails = (TextView) itemView.findViewById(R.id.txt_image_credit);
            likes = (TextView) itemView.findViewById(R.id.txt_like_count);
            comments = (TextView) itemView.findViewById(R.id.txt_comment_count);
            share = (TextView) itemView.findViewById(R.id.txt_share_btn);
            download = (TextView) itemView.findViewById(R.id.txt_download_btn);
        }

        public void bind(int index) {

            final Picture pic = photos.get(index);
            final String key = getKeyForPicture(pic);

            likes.setText(String.valueOf(pic.getLikesCount()));
            comments.setText(String.valueOf(pic.getCommentsCount()));
            imageDetails.setText(pic.getPhotoCredit() + " (" + pic.getRelationshipWithPhotographer
                    () + ")" + "\n" + dateFormat.format(new Date(pic.getDateTaken())));

//            FirebaseImageHelper.loadImageWithThumbnail(mContext, pic.getPicURL(), pic.getThumbURL
//                    (), image);
            FirebaseImageHelper.loadImageIn(mContext, pic.getPicURL(), image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onPicClicked(key, pic);
                }
            });

            isLiked = dbHandler.isPicLiked(key);
            likes.setCompoundDrawablesWithIntrinsicBounds(isLiked ? R.mipmap.ic_like : R.mipmap
                    .ic_unlike, 0, 0, 0);

            likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    isLiked = !isLiked;

                    likes.setCompoundDrawablesWithIntrinsicBounds(isLiked ? R.mipmap.ic_like : R.mipmap
                            .ic_unlike, 0, 0, 0);

                    if (isLiked) {
                        pic.setLikesCount(pic.getLikesCount() + 1);

                        likes.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_like, 0, 0, 0);
                    } else {
                        pic.setLikesCount(pic.getLikesCount() - 1);

                        likes.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_unlike, 0, 0, 0);
                    }
                    mPresenter.onLikeClicked(key, pic, isLiked);
                    likes.setText(String.valueOf(pic.getLikesCount()));
                }
            });

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onCommentsClicked(key, pic);
                }
            });

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onShareClicked(key, pic);
                }
            });

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onDownloadClicked(key, pic);
                }
            });
            // change background color here
            // itemView.set
        }
    }

    private List<Picture> getValueListFromMap(Map<String, Picture> picMap) {

        List<Picture> localPics = new ArrayList<>();

        for (Map.Entry<String, Picture> map : picMap.entrySet()) {
            localPics.add(map.getValue());
        }

        Collections.sort(localPics);

        return localPics;
    }

    @Nullable
    private String getKeyForPicture(Picture picture) {
        if (photoMap == null || !photoMap.containsValue(picture)) {
            return null;
        }

        for (Map.Entry<String, Picture> map : photoMap.entrySet()) {
            if (map.getValue() == picture) {
                return map.getKey();
            }
        }

        return null;
    }
}
