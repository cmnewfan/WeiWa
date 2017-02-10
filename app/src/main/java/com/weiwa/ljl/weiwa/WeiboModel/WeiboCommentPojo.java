package com.weiwa.ljl.weiwa.WeiboModel;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by hzfd on 2017/2/7.
 */
public class WeiboCommentPojo implements Parcelable {
    private String since_id;

    private String next_cursor;

    private String max_id;

    private WeiboPojo.Statuses status;

    private String[] marks;

    private String previous_cursor;

    private String total_number;

    private Comments[] comments;

    private String hasvisible;

    protected WeiboCommentPojo(Parcel in) {
        Bundle bundle = in.readBundle();
        if(bundle.getString("RetweetedPortrait","").equals("")){
            status.getUser().setProfile_image_url(bundle.getString("RetweetPortrait"));
            status.setCreated_at(bundle.getString("RetweetDate"));
            status.getUser().setName(bundle.getString("RetweetUser"));
            status.setText(bundle.getString("RetweetText"));
            status.setComments_count(bundle.getString("CommentCount"));
            status.setReposts_count(bundle.getString("RepostCount"));
            status.setPic_urls(new WeiboPojo.Pic_urls[bundle.getStringArray("RetweetedPic").length]);
            setPic_urls(bundle.getStringArray("RetweetedPic"),status.getPic_urls());
            setCommentContents(bundle);
        }else{
            status.getRetweeted_status().setCreated_at(bundle.getString("RetweetedDate"));
            status.getRetweeted_status().getUser().setProfile_image_url("RetweetedPortrait");
            status.getRetweeted_status().getUser().setName("RetweetedUser");
            status.getRetweeted_status().setText("RetweetedText");
            status.getUser().setProfile_image_url(bundle.getString("RetweetPortrait"));
            status.setCreated_at(bundle.getString("RetweetDate"));
            status.getUser().setName(bundle.getString("RetweetUser"));
            status.setText(bundle.getString("RetweetText"));
            status.setComments_count(bundle.getString("CommentCount"));
            status.setReposts_count(bundle.getString("RepostCount"));
            status.getRetweeted_status().setPic_urls(new WeiboPojo.Pic_urls[bundle.getStringArray("RetweetedPic").length]);
            setPic_urls(bundle.getStringArray("RetweetedPic"),status.getRetweeted_status().getPic_urls());
            setCommentContents(bundle);
        }
    }

    public static final Creator<WeiboCommentPojo> CREATOR = new Creator<WeiboCommentPojo>() {
        @Override
        public WeiboCommentPojo createFromParcel(Parcel in) {
            return new WeiboCommentPojo(in);
        }

        @Override
        public WeiboCommentPojo[] newArray(int size) {
            return new WeiboCommentPojo[size];
        }
    };

    public String getSince_id ()
    {
        return since_id;
    }

    public void setSince_id (String since_id)
    {
        this.since_id = since_id;
    }

    public String getNext_cursor ()
    {
        return next_cursor;
    }

    public void setNext_cursor (String next_cursor)
    {
        this.next_cursor = next_cursor;
    }

    public String getMax_id ()
    {
        return max_id;
    }

    public void setMax_id (String max_id)
    {
        this.max_id = max_id;
    }

    public WeiboPojo.Statuses getStatus ()
    {
        return status;
    }

    public void setStatus (WeiboPojo.Statuses status)
    {
        this.status = status;
    }

    public String[] getMarks ()
    {
        return marks;
    }

    public void setMarks (String[] marks)
    {
        this.marks = marks;
    }

    public String getPrevious_cursor ()
    {
        return previous_cursor;
    }

    public void setPrevious_cursor (String previous_cursor)
    {
        this.previous_cursor = previous_cursor;
    }

    public String getTotal_number ()
    {
        return total_number;
    }

    public void setTotal_number (String total_number)
    {
        this.total_number = total_number;
    }

    public Comments[] getComments ()
    {
        return comments;
    }

    public void setComments (Comments[] comments)
    {
        this.comments = comments;
    }

    public String getHasvisible ()
    {
        return hasvisible;
    }

    public void setHasvisible (String hasvisible)
    {
        this.hasvisible = hasvisible;
    }

    private String[] getPic_urls(WeiboPojo.Pic_urls[] pic_urls){
        if(pic_urls!=null && pic_urls.length>0) {
            String[] urls = new String[pic_urls.length];
            for (int i = 0; i < urls.length; i++) {
                urls[i] = pic_urls[i].toString();
            }
            return urls;
        }else{
            return null;
        }
    }

    private String[] getCommentContents(){
        if(getComments()!=null && getComments().length>0) {
            String[] comments = new String[getComments().length];
            for (int i = 0; i < comments.length; i++) {
                comments[i] = getComments()[i].getText();
            }
            return comments;
        }else{
            return null;
        }
    }

    private void setCommentContents(Bundle bundle){
        comments = new Comments[bundle.getStringArray("CommentPortrait").length];
        for(int i=0;i<comments.length;i++){
            comments[i].setText(bundle.getStringArray("CommentPortrait")[i]);
            comments[i].getUser().setName(bundle.getStringArray("CommentUser")[i]);
            comments[i].setCreated_at(bundle.getStringArray("CommentDate")[i]);
            comments[i].getUser().setProfile_image_url(bundle.getStringArray("CommentPortrait")[i]);
        }
    }

    private String[] getCommentUsers(){
        if(getComments()!=null && getComments().length>0) {
            String[] comments = new String[getComments().length];
            for (int i = 0; i < comments.length; i++) {
                comments[i] = getComments()[i].getUser().getName();
            }
            return comments;
        }else{
            return null;
        }
    }

    private void setCommentUsers(String[] contents){
        comments = new Comments[contents.length];
        for(int i=0;i<comments.length;i++){
            comments[i].setText(contents[i]);
        }
    }

    private String[] getCommentDates(){
        if(getComments()!=null && getComments().length>0) {
            String[] comments = new String[getComments().length];
            for (int i = 0; i < comments.length; i++) {
                comments[i] = getComments()[i].getCreated_at();
            }
            return comments;
        }else{
            return null;
        }
    }

    private String[] getCommentPics(){
        if(getComments()!=null && getComments().length>0) {
            String[] comments = new String[getComments().length];
            for (int i = 0; i < comments.length; i++) {
                comments[i] = getComments()[i].getUser().getProfile_image_url();
            }
            return comments;
        }else{
            return null;
        }
    }

    private void setPic_urls(String[] urls, WeiboPojo.Pic_urls[] pic_urls){
        for(int i=0;i<urls.length;i++){
            pic_urls[i].setThumbnail_pic(urls[i]);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle();
        if(getStatus().getRetweeted_status()!=null){
            bundle.putString("RetweetedPortrait",getStatus().getRetweeted_status().getUser().getProfile_image_url());
            bundle.putString("RetweetedDate",getStatus().getRetweeted_status().getCreated_at());
            bundle.putString("RetweetedUser",getStatus().getRetweeted_status().getUser().getName());
            bundle.putString("RetweetedText",getStatus().getRetweeted_status().getText());
            bundle.putStringArray("RetweetedPic",getPic_urls(getStatus().getRetweeted_status().getPic_urls()));
            bundle.putString("RetweetPortrait",getStatus().getUser().getProfile_image_url());
            bundle.putString("RetweetDate",getStatus().getCreated_at());
            bundle.putString("RetweetUser",getStatus().getUser().getName());
            bundle.putString("RetweetText",getStatus().getText());
            bundle.putString("CommentCount",getStatus().getComments_count());
            bundle.putString("RepostCount",getStatus().getReposts_count());
        }else{
            bundle.putString("RetweetPortrait",getStatus().getUser().getProfile_image_url());
            bundle.putString("RetweetDate",getStatus().getCreated_at());
            bundle.putString("RetweetUser",getStatus().getUser().getName());
            bundle.putString("RetweetText",getStatus().getText());
            bundle.putStringArray("RetweetPic",getPic_urls(getStatus().getPic_urls()));
            bundle.putString("CommentCount",getStatus().getComments_count());
            bundle.putString("RepostCount",getStatus().getReposts_count());
        }
        bundle.putStringArray("CommentPortrait",getCommentPics());
        bundle.putStringArray("CommentDate",getCommentDates());
        bundle.putStringArray("CommentUser",getCommentUsers());
        bundle.putStringArray("CommentText",getCommentContents());
        dest.writeBundle(bundle);
    }

    public class Comments{
        private String rootid;

        private String floor_number;

        private String text;

        private String source_allowclick;

        private Reply_comment reply_comment;

        private WeiboPojo.Statuses status;

        private String reply_original_text;

        private String source_type;

        private String id;

        private String source;

        private String idstr;

        private String created_at;

        private String mid;

        private WeiboPojo.User user;

        public String getRootid ()
        {
            return rootid;
        }

        public void setRootid (String rootid)
        {
            this.rootid = rootid;
        }

        public String getFloor_number ()
        {
            return floor_number;
        }

        public void setFloor_number (String floor_number)
        {
            this.floor_number = floor_number;
        }

        public String getText ()
        {
            return text;
        }

        public void setText (String text)
        {
            this.text = text;
        }

        public String getSource_allowclick ()
        {
            return source_allowclick;
        }

        public void setSource_allowclick (String source_allowclick)
        {
            this.source_allowclick = source_allowclick;
        }

        public Reply_comment getReply_comment ()
        {
            return reply_comment;
        }

        public void setReply_comment (Reply_comment reply_comment)
        {
            this.reply_comment = reply_comment;
        }

        public WeiboPojo.Statuses getStatus ()
        {
            return status;
        }

        public void setStatus (WeiboPojo.Statuses status)
        {
            this.status = status;
        }

        public String getReply_original_text ()
        {
            return reply_original_text;
        }

        public void setReply_original_text (String reply_original_text)
        {
            this.reply_original_text = reply_original_text;
        }

        public String getSource_type ()
        {
            return source_type;
        }

        public void setSource_type (String source_type)
        {
            this.source_type = source_type;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getSource ()
        {
            return source;
        }

        public void setSource (String source)
        {
            this.source = source;
        }

        public String getIdstr ()
        {
            return idstr;
        }

        public void setIdstr (String idstr)
        {
            this.idstr = idstr;
        }

        public String getCreated_at ()
        {
            return created_at;
        }

        public void setCreated_at (String created_at)
        {
            this.created_at = created_at;
        }

        public String getMid ()
        {
            return mid;
        }

        public void setMid (String mid)
        {
            this.mid = mid;
        }

        public WeiboPojo.User getUser ()
        {
            return user;
        }

        public void setUser (WeiboPojo.User user)
        {
            this.user = user;
        }

    }

    class Reply_comment{
        private String id;

        private String text;

        private String floor_number;

        private String rootid;

        private String source_allowclick;

        private String idstr;

        private String source;

        private String source_type;

        private String created_at;

        private String mid;

        private WeiboPojo.User user;

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getText ()
        {
            return text;
        }

        public void setText (String text)
        {
            this.text = text;
        }

        public String getFloor_number ()
        {
            return floor_number;
        }

        public void setFloor_number (String floor_number)
        {
            this.floor_number = floor_number;
        }

        public String getRootid ()
        {
            return rootid;
        }

        public void setRootid (String rootid)
        {
            this.rootid = rootid;
        }

        public String getSource_allowclick ()
        {
            return source_allowclick;
        }

        public void setSource_allowclick (String source_allowclick)
        {
            this.source_allowclick = source_allowclick;
        }

        public String getIdstr ()
        {
            return idstr;
        }

        public void setIdstr (String idstr)
        {
            this.idstr = idstr;
        }

        public String getSource ()
        {
            return source;
        }

        public void setSource (String source)
        {
            this.source = source;
        }

        public String getSource_type ()
        {
            return source_type;
        }

        public void setSource_type (String source_type)
        {
            this.source_type = source_type;
        }

        public String getCreated_at ()
        {
            return created_at;
        }

        public void setCreated_at (String created_at)
        {
            this.created_at = created_at;
        }

        public String getMid ()
        {
            return mid;
        }

        public void setMid (String mid)
        {
            this.mid = mid;
        }

        public WeiboPojo.User getUser ()
        {
            return user;
        }

        public void setUser (WeiboPojo.User user)
        {
            this.user = user;
        }
    }

    class Visible{
        private String type;

        private String list_id;

        public String getType ()
        {
            return type;
        }

        public void setType (String type)
        {
            this.type = type;
        }

        public String getList_id ()
        {
            return list_id;
        }

        public void setList_id (String list_id)
        {
            this.list_id = list_id;
        }
    }
}
