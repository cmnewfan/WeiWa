package com.weiwa.ljl.weiwa;

import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboCommentPojo;

/**
 * Created by hzfd on 2017/2/8.
 */
public class CommentAdapter extends RecyclerView.Adapter {

    private WeiboCommentPojo commentPojo;
    private Fragment mContext;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_weibo, parent, false);
        return new CommentViewHolder(view,mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position>=0) {
            CommentViewHolder item = (CommentViewHolder) holder;
            item.refresh(commentPojo.getComments()[position]);
        }
    }

    @Override
    public int getItemCount() {
        if(commentPojo.getComments()==null){
            return -1;
        }else {
            return commentPojo.getComments().length;
        }
    }

    public CommentAdapter(WeiboCommentPojo pojo, Fragment context){
        commentPojo = pojo;
        mContext = context;
    }
}

class CommentViewHolder extends RecyclerView.ViewHolder{

    Fragment mContext;
    PortraitView comment_portrait;
    TextView comment_name;
    TextView comment_date;
    TextView comment_text;

    public CommentViewHolder(View itemView, Fragment context) {
        super(itemView);
        comment_date = (TextView) itemView.findViewById(R.id.weibo_date);
        comment_name = (TextView) itemView.findViewById(R.id.single_user);
        comment_text = (TextView) itemView.findViewById(R.id.comment_text);
        comment_portrait = (PortraitView) itemView.findViewById(R.id.single_user_portrait);
        mContext = context;
    }

    public void refresh(final WeiboCommentPojo.Comments comment){
        comment_name.setText(comment.getUser().getName());
        comment_date.setText(comment.getCreated_at());
        comment_text.setText(comment.getText());
        comment_portrait.addDownloadTask(comment.getUser().getProfile_image_url(), 1, 1);
    }
}
