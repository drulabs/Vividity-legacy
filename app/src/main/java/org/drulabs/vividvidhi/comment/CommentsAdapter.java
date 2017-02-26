package org.drulabs.vividvidhi.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.config.Constants;
import org.drulabs.vividvidhi.dto.Comment;
import org.drulabs.vividvidhi.firebase.FirebaseImageHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by kaushald on 10/02/17.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsVH> {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-dd, yyyy " +
            "(HH:mm:ss)");

    private List<Comment> comments;
    private Context mContext;

    CommentsAdapter(Context context) {
        this.mContext = context;
        this.comments = new ArrayList<>();
    }

    @Override
    public CommentsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View commentView = LayoutInflater.from(context).inflate(R.layout.comment_item, parent,
                false);
        return new CommentsVH(commentView);
    }

    @Override
    public void onBindViewHolder(CommentsVH holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    void append(List<Comment> comments) {
        if (this.comments != null && this.comments.size() >= 0 && comments != null) {
            this.comments.addAll(comments);

            Collections.sort(this.comments);

            this.notifyDataSetChanged();
        }
    }

    void append(Comment comment) {
        if (comments != null && comments.size() >= 0 && comment != null) {
            this.comments.add(comment);
            Collections.sort(this.comments);
            this.notifyDataSetChanged();
        }
    }

    class CommentsVH extends RecyclerView.ViewHolder {

        TextView tvTimestamp;
        TextView tvComment;
        TextView tvCommenter;
        ImageView imgCommenter;

        public CommentsVH(View itemView) {
            super(itemView);
            tvTimestamp = (TextView) itemView.findViewById(R.id.comment_timestamp);
            tvComment = (TextView) itemView.findViewById(R.id.comment_text);
            tvCommenter = (TextView) itemView.findViewById(R.id.commenter_name);
            imgCommenter = (ImageView) itemView.findViewById(R.id.img_commenter);
        }

        public void bind(int position) {

            Comment comment = comments.get(position);

            tvTimestamp.setText(dateFormat.format(new Date(comment.getTimestamp())));
            tvComment.setText(comment.getText());
            tvCommenter.setText(comment.getCommenter() + " " + mContext.getString(R.string
                    .commented_on_suffix));

            String picFirebaseUrl = Constants.USER_IMAGE_FOLDER + "/" + comment.getCommenterPic()
                    + ".jpg";
            FirebaseImageHelper.loadImageIn(mContext, picFirebaseUrl, imgCommenter);

        }
    }
}
