package com.weiwa.ljl.weiwa;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by hzfd on 2017/1/17.
 */
public class WeiboAdapter extends RecyclerView.Adapter {

    private WeiboPojo weibo_data;
    private Fragment mContext;
    private final int SINGLE_WB = 0;
    private final int RETWEETED_WB = 1;
    private onAdapterEvent onNeedInsert;
    private Animation animation;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SINGLE_WB){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_weibo, parent, false);
            return new SingleWB_ViewHolder(view,mContext,onNeedInsert);
        }else if(viewType == RETWEETED_WB){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retweeted_weibo, parent, false);
            return new Retweeted_ViewHolder(view,mContext,onNeedInsert);
        }else{
            throw new NullPointerException();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(weibo_data.getStatuses()[position].getRetweeted_status()!=null && weibo_data.getStatuses()[position].getRetweeted_status().getId()!=null){
            return RETWEETED_WB;
        }else{
            return SINGLE_WB;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                if (mContext instanceof MainFragment) {
                    ((MainFragment) mContext).fadeRecyclerView(position);
                }
                onNeedInsert.onNeedComment(weibo_data.getStatuses()[position].getId());
            }
        });
        switch (holder.getItemViewType()){
            case SINGLE_WB:
                SingleWB_ViewHolder single_item = (SingleWB_ViewHolder) holder;
                single_item.refresh(weibo_data.getStatuses()[position]);
                single_item.setId(weibo_data.getStatuses()[position].getId());
                single_item.setUser(weibo_data.getStatuses()[position].getUser());
                break;
            case RETWEETED_WB:
                Retweeted_ViewHolder retweet_item = (Retweeted_ViewHolder) holder;
                retweet_item.refresh(weibo_data.getStatuses()[position]);
                retweet_item.setRetweetId(weibo_data.getStatuses()[position].getId());
                retweet_item.setId(weibo_data.getStatuses()[position].getRetweeted_status().getId());
                retweet_item.setUser(weibo_data.getStatuses()[position].getUser(),weibo_data.getStatuses()[position].getRetweeted_status().getUser());
                break;
            default:
                break;
        }
        if(position==(weibo_data.getStatuses().length-1)){
            onNeedInsert.onNeedInsert();
        }
    }

    @Override
    public int getItemCount() {
        return weibo_data.getStatuses().length;
    }

    public WeiboAdapter(WeiboPojo weibo_pojo, Fragment context, onAdapterEvent onNeed){
        this.weibo_data = weibo_pojo;
        this.mContext = context;
        this.onNeedInsert = onNeed;
        this.animation = AnimationUtils.loadAnimation(mContext.getActivity(), R.anim.fade_in);
    }

    public void insertNewData(WeiboPojo newData){
        int length = weibo_data.getStatuses().length;
        WeiboPojo.Statuses[] updatedData = new WeiboPojo.Statuses[weibo_data.getStatuses().length+newData.getStatuses().length];
        for(int i=0;i<updatedData.length;i++){
            if(i<weibo_data.getStatuses().length){
                updatedData[i] = weibo_data.getStatuses()[i];
            }else{
                updatedData[i] = newData.getStatuses()[i-weibo_data.getStatuses().length];
            }
        }
        newData.setStatuses(updatedData);
        this.weibo_data = newData;
        notifyItemRangeInserted(length,newData.getStatuses().length);
        //notifyDataSetChanged();
    }
}

class Retweeted_ViewHolder extends CustomViewHolder{
    TextView retweet_user;
    TextView retweeted_user;
    PortraitView retweet_user_portrait;
    PortraitView retweeted_user_portrait;
    TextView retweet_text;
    TextView retweet_date;
    TextView retweeted_text;
    TextView retweeted_date;
    TextView retweeted_repost;
    TextView retweeted_comment;
    GridLayout retweeted_line_1;
    GridLayout retweeted_line_2;
    GridLayout retweeted_line_3;
    String retweet_id;
    ImageButton option;
    public Retweeted_ViewHolder(View itemView,Fragment context, onAdapterEvent event) {
        super(itemView,context,event);
        initView(itemView);
        mParentView = itemView;
    }

    public Retweeted_ViewHolder(View itemView,Fragment context, onAdapterEvent event,String wid) {
        super(itemView,context,event,wid);
        initView(itemView);
        mParentView = itemView;
    }

    public void setRetweetId(String id){
        retweet_id = id;
    }

    private void initView(View itemView) {
        retweeted_user = (TextView) itemView.findViewById(R.id.retweeted_user);
        retweet_user = (TextView) itemView.findViewById(R.id.retweet_user);
        retweet_user_portrait = (PortraitView) itemView.findViewById(R.id.retweet_user_portrait);
        retweeted_user_portrait = (PortraitView) itemView.findViewById(R.id.retweeted_user_portrait);
        retweet_text = (TextView) itemView.findViewById(R.id.retweet_weibo_text);
        retweet_date = (TextView) itemView.findViewById(R.id.retweet_weibo_date);
        retweeted_text = (TextView) itemView.findViewById(R.id.retweeted_weibo_text);
        retweeted_date = (TextView) itemView.findViewById(R.id.retweeted_weibo_date);
        retweeted_repost = (TextView) itemView.findViewById(R.id.retweeted_repost_text);
        retweeted_comment = (TextView) itemView.findViewById(R.id.retweeted_comment_text);
        retweeted_line_1 = (GridLayout) itemView.findViewById(R.id.retweeted_weibo_image_line_1);
        retweeted_line_2 = (GridLayout) itemView.findViewById(R.id.retweeted_weibo_image_line_2);
        retweeted_line_3 = (GridLayout) itemView.findViewById(R.id.retweeted_weibo_image_line_3);
        option = (ImageButton) itemView.findViewById(R.id.option);
    }

    public void refresh(final WeiboPojo.Statuses statuses){
        this.retweet_date.setText(statuses.getCreated_at());
        this.retweet_text.setText(statuses.getText());
        this.retweeted_date.setText(statuses.getRetweeted_status().getCreated_at());
        this.retweeted_text.setText(statuses.getRetweeted_status().getText());
        this.retweet_user.setText(statuses.getUser().getName());
        this.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(getContext().getActivity(),option);
                popupMenu.inflate(R.menu.menu_weibo);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.comment_this:
                                createPopupWindow(PopupWindow_Comment,null,null,retweet_id).showAtLocation(retweet_user,Gravity.CENTER,0,0);
                                break;
                            case R.id.repost_this:
                                createPopupWindow(PopupWindow_Repost,retweet_user.getText().toString(),retweet_text.getText().toString(),null).showAtLocation(retweet_user,Gravity.CENTER,0,0);
                                break;
                            case R.id.share_this:
                                Uri targetUri = getUriOfBitmap(getBitmapFromView(mParentView));
                                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                //set intent type
                                sendIntent.setType("image/*");
                                sendIntent.putExtra(Intent.EXTRA_STREAM, targetUri);
                                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Retweeted_ViewHolder.this.getContext().startActivity(Intent.createChooser(sendIntent, "share"));
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        this.retweeted_user.setText(statuses.getRetweeted_status().getUser().getName());
        this.retweet_user_portrait.addDownloadTask(statuses.getUser().getProfile_image_url(), 1, 1);
        this.retweeted_user_portrait.addDownloadTask(statuses.getRetweeted_status().getUser().getProfile_image_url(), 1, 1);
        if(statuses.getRetweeted_status().getPic_urls()!=null && statuses.getRetweeted_status().getPic_urls().length==1){
            //retweeted_line_1.setGravity(Gravity.CENTER);
        }
        refreshImage(statuses.getRetweeted_status().getPic_urls(), statuses.getRetweeted_status().convertToUris(), this.retweeted_line_1, this.retweeted_line_2, this.retweeted_line_3);
    }

    public void setId(String w_id){
        id = w_id;
    }

    public void setUser(WeiboPojo.User repostUser, WeiboPojo.User repostedUser){
        retweeted_user_portrait.setData((MainActivity) getContext().getActivity(),repostedUser);
        retweet_user_portrait.setData((MainActivity) getContext().getActivity(),repostUser);
    }
}

 class SingleWB_ViewHolder extends CustomViewHolder {
     TextView user_name;
     PortraitView user_portrait;
     TextView text;
     TextView date;
     TextView repost;
     TextView comment;
     GridLayout line_1;
     GridLayout line_2;
     GridLayout line_3;
     WeiboPojo.User user;
     ImageButton option;

     public SingleWB_ViewHolder(View itemView, Fragment context, onAdapterEvent event) {
         super(itemView, context, event);
         mParentView = itemView;
         initView(itemView);
    }

     public SingleWB_ViewHolder(View itemView,Fragment context, onAdapterEvent event,String wid) {
         super(itemView,context,event,wid);
         mParentView = itemView;
         initView(itemView);
     }

     private void initView(View itemView) {
         user_name = (TextView) itemView.findViewById(R.id.single_user);
         option = (ImageButton) itemView.findViewById(R.id.option);
         option.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 final PopupMenu popupMenu = new PopupMenu(getContext().getActivity(),option);
                 popupMenu.inflate(R.menu.menu_weibo);
                 popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                     @Override
                     public boolean onMenuItemClick(MenuItem item) {
                         switch (item.getItemId()){
                             case R.id.comment_this:
                                 createPopupWindow(PopupWindow_Comment,null,null,null).showAtLocation(user_name,Gravity.CENTER,0,0);
                                 break;
                             case R.id.repost_this:
                                 createPopupWindow(PopupWindow_Repost,null,null,null).showAtLocation(user_name,Gravity.CENTER,0,0);
                                 break;
                             case R.id.share_this:
                                 Uri targetUri = getUriOfBitmap(getBitmapFromView(mParentView));
                                 Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                 //set intent type
                                 sendIntent.setType("image/*");
                                 sendIntent.putExtra(Intent.EXTRA_STREAM, targetUri);
                                 sendIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                                 sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                 SingleWB_ViewHolder.this.getContext().startActivity(Intent.createChooser(sendIntent, "share"));
                                 break;
                         }
                         return true;
                     }
                 });
                 popupMenu.show();
             }
         });
         user_portrait = (PortraitView) itemView.findViewById(R.id.single_user_portrait);
         text = (TextView) itemView.findViewById(R.id.weibo_text);
         date = (TextView) itemView.findViewById(R.id.weibo_date);
         repost = (TextView) itemView.findViewById(R.id.weibo_repost_count);
         repost.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 setRepostUnderline();
             }
         });
         comment = (TextView) itemView.findViewById(R.id.weibo_comment_count);
         comment.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 setCommentUnderline();
             }
         });
         line_1 = (GridLayout) itemView.findViewById(R.id.weibo_image_line_1);
         line_2 = (GridLayout) itemView.findViewById(R.id.weibo_image_line_2);
         line_3 = (GridLayout) itemView.findViewById(R.id.weibo_image_line_3);
     }

     public void refresh(final WeiboPojo.Statuses statuses){
         this.user_name.setText(statuses.getUser().getName());
         this.user_portrait.addDownloadTask(statuses.getUser().getProfile_image_url(), 1, 1);
         this.text.setText(statuses.getText());
         this.date.setText(statuses.getCreated_at());
         this.repost.setText(statuses.getReposts_count());
         this.comment.setText(statuses.getComments_count());
         if (statuses.getPic_urls() != null && statuses.getPic_urls().length == 1) {
             //line_1.setGravity(Gravity.CENTER);
         }
         refreshImage(statuses.getPic_urls(), statuses.convertToUris(), line_1, line_2, line_3);
    }
     public void setCommentUnderline(){
         comment.setPaintFlags(comment.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
         repost.setPaintFlags(repost.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
     }
     public void setRepostUnderline(){
         repost.setPaintFlags(repost.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
         comment.setPaintFlags(comment.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
     }

     public void setUser(WeiboPojo.User wUser){
         user_portrait.setData((MainActivity) getContext().getActivity(),wUser);
     }
}

class CustomViewHolder extends RecyclerView.ViewHolder {
    private Fragment mContext;
    static int PopupWindow_Comment = 1;
    static int PopupWindow_Repost = 2;
    public View mParentView;
    onAdapterEvent adapterEvent;
    String id;
    public CustomViewHolder(View itemView, Fragment context, onAdapterEvent event) {
        super(itemView);
        mContext = context;
        adapterEvent = event;
    }

    public CustomViewHolder(View itemView, Fragment context, onAdapterEvent event,String wid) {
        super(itemView);
        mContext = context;
        adapterEvent = event;
        id = wid;
    }

    public Fragment getContext(){
        return mContext;
    }

    public Uri getUriOfBitmap(Bitmap tempBitmap) {
        File png_file = new File(WeiwaApplication.CacheCategory + "/temp.png");
        if (png_file.exists()) {
            png_file.delete();
        }
        try {
            Boolean result;
            result = png_file.createNewFile();
            FileOutputStream fos = new FileOutputStream(png_file);
            result = tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return Uri.fromFile(png_file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            //bgDrawable.draw(canvas);
            canvas.drawColor(Color.WHITE);
        } else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    public void setAdapterEvent(onAdapterEvent event){
        adapterEvent = event;
    }

    public void SetOnClickListener(View.OnClickListener listener){
        itemView.setOnClickListener(listener);
    }

    public void setId(String w_id){
        id = w_id;
    }

    void refreshImage(final WeiboPojo.Pic_urls[] urls, final Uri[] convert_uris, GridLayout line_1, GridLayout line_2, GridLayout line_3) {
        line_1.setVisibility(View.GONE);
        line_2.setVisibility(View.GONE);
        line_3.setVisibility(View.GONE);
        line_1.removeAllViews();
        line_2.removeAllViews();
        line_3.removeAllViews();
        if(urls!=null && urls.length>0){
            int count = 0;
            for (final WeiboPojo.Pic_urls url :
                    urls) {
                PortraitView imageView = new PortraitView(mContext.getActivity(), url.getThumbnail_pic(), urls.length, 0);
                final int index = count;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageFragment fragment = new ImageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArray("Uris",convert_uris);
                        bundle.putInt("Index",index);
                        fragment.setArguments(bundle);
                        ((MainActivity)mContext.getActivity()).setFragment(fragment);
                    }
                });
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                View view = new View(mContext.getActivity());
                view.setLayoutParams(new ViewGroup.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT));
                view.setBackground(null);
                if(count<3) {
                    line_1.addView(imageView);
                    line_1.addView(view);
                    line_1.setVisibility(View.VISIBLE);
                }else if(count<6){
                    line_2.addView(imageView);
                    line_2.addView(view);
                    line_2.setVisibility(View.VISIBLE);
                }else{
                    line_3.addView(imageView);
                    line_3.addView(view);
                    line_3.setVisibility(View.VISIBLE);
                }
                count++;
            }
        }
    }

    /**
     *
     * @param type comment or repost
     *             分为转发和评论两种,根据type选择event
     * @param repost_user_name only use for repost RetweetedWeibo, other set null
     *                         只在转发RetweetedWeibo时有用
     * @param repost_content only use for repost RetweetedWeibo, other set null
     *                         只在转发RetweetedWeibo时有用
     * @param true_id only use for comment RetweetedWeibo, other set null. Cause this comment needs id of commented weibo, not original weibo.
     *                need to refactor.
     *                只在评论RetweetedWeibo时有用，因为其余Weibo的id都是原Weibo的ID，但在评论RetweetedWeibo时,需要切换为RetweetWeibo的ID。
     *                结构混乱，后期重构。
     * @return created popup window
     */
    public PopupWindow createPopupWindow(final int type, final String repost_user_name, final String repost_content,final String true_id){
        final PopupWindow popupWindow = new PopupWindow();
        View view = ((LayoutInflater) (getContext().getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))).inflate(R.layout.popup_input, null);
        Button confirm = (Button) view.findViewById(R.id.confirm);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        final EditText input = (EditText) view.findViewById(R.id.input);
        if(type == PopupWindow_Comment){
            input.setHint(R.string.comment_this);
        }else if(type == PopupWindow_Repost){
            input.setHint(R.string.repost_this);
        }
        if(repost_user_name!=null){
            input.setText("//@" + repost_user_name + ":" + repost_content);
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().length() == 0) {
                    if(type == PopupWindow_Comment) {
                        if(true_id!=null){
                            adapterEvent.onComment(getContext().getActivity().getResources().getString(R.string.comment_this), true_id);
                        }else{
                            adapterEvent.onComment(getContext().getActivity().getResources().getString(R.string.comment_this), id);
                        }
                    }else if(type == PopupWindow_Repost){
                        if(repost_user_name==null){
                            adapterEvent.onRepost(getContext().getActivity().getResources().getString(R.string.repost_this),id);
                        }else {
                            adapterEvent.onRepost(getContext().getActivity().getResources().getString(R.string.repost_this), id);
                        }
                    }
                } else if (input.getText().length() < 140) {
                    if(type == PopupWindow_Comment) {
                        if(true_id!=null){
                            adapterEvent.onComment(input.getText().toString(), true_id);
                        }else{
                            adapterEvent.onComment(input.getText().toString(), id);
                        }
                    }else if(type == PopupWindow_Repost){
                        if(repost_user_name==null){
                            adapterEvent.onRepost(input.getText().toString(),id);
                        }else {
                            adapterEvent.onRepost(input.getText().toString(), id);
                        }
                    }
                } else {
                    Toast.makeText(getContext().getActivity(), "超出字数限制"+String.valueOf(input.getText().length()-140)+"字", Toast.LENGTH_SHORT).show();
                }
                popupWindow.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getContext().getActivity().getResources().getDrawable(R.drawable.popupwindow_drawable));
        return popupWindow;
    }
}