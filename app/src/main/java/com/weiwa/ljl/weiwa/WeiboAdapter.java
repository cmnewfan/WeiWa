package com.weiwa.ljl.weiwa;

import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hzfd on 2017/1/17.
 */
public class WeiboAdapter extends RecyclerView.Adapter {

    private WeiboPojo weibo_data;
    private Fragment mContext;
    private DisplayMetrics mDisplayMetrics;
    private final int SINGLE_WB = 0;
    private final int RETWEETED_WB = 1;
    private onAdapterEvent onNeedInsert;

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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNeedInsert.onNeedComment(weibo_data.getStatuses()[position].getId());
            }
        });
        switch (holder.getItemViewType()){
            case SINGLE_WB:
                SingleWB_ViewHolder single_item = (SingleWB_ViewHolder) holder;
                single_item.refresh(weibo_data.getStatuses()[position]);
                single_item.setId(weibo_data.getStatuses()[position].getId());
                break;
            case RETWEETED_WB:
                Retweeted_ViewHolder retweet_item = (Retweeted_ViewHolder) holder;
                retweet_item.refresh(weibo_data.getStatuses()[position]);
                retweet_item.setRetweetId(weibo_data.getStatuses()[position].getId());
                retweet_item.setId(weibo_data.getStatuses()[position].getRetweeted_status().getId());
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
        this.mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        this.onNeedInsert = onNeed;
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
    ImageView retweet_user_portrait;
    ImageView retweeted_user_portrait;
    TextView retweet_text;
    TextView retweet_date;
    TextView retweeted_text;
    TextView retweeted_date;
    TextView retweeted_repost;
    TextView retweeted_comment;
    LinearLayout retweeted_line_1;
    LinearLayout retweeted_line_2;
    LinearLayout retweeted_line_3;
    String retweet_id;
    public Retweeted_ViewHolder(View itemView,Fragment context, onAdapterEvent event) {
        super(itemView,context,event);
        initView(itemView);
    }

    public Retweeted_ViewHolder(View itemView,Fragment context, onAdapterEvent event,String wid) {
        super(itemView,context,event,wid);
        initView(itemView);
    }

    public void setRetweetId(String id){
        retweet_id = id;
    }

    private void initView(View itemView) {
        retweeted_user = (TextView) itemView.findViewById(R.id.retweeted_user);
        retweet_user = (TextView) itemView.findViewById(R.id.retweet_user);
        retweet_user_portrait = (ImageView) itemView.findViewById(R.id.retweet_user_portrait);
        retweeted_user_portrait = (ImageView) itemView.findViewById(R.id.retweeted_user_portrait);
        retweet_text = (TextView) itemView.findViewById(R.id.retweet_weibo_text);
        retweet_date = (TextView) itemView.findViewById(R.id.retweet_weibo_date);
        retweeted_text = (TextView) itemView.findViewById(R.id.retweeted_weibo_text);
        retweeted_date = (TextView) itemView.findViewById(R.id.retweeted_weibo_date);
        retweeted_repost = (TextView) itemView.findViewById(R.id.retweeted_repost_text);
        retweeted_comment = (TextView) itemView.findViewById(R.id.retweeted_comment_text);
        retweeted_line_1 = (LinearLayout) itemView.findViewById(R.id.retweeted_weibo_image_line_1);
        retweeted_line_2 = (LinearLayout) itemView.findViewById(R.id.retweeted_weibo_image_line_2);
        retweeted_line_3 = (LinearLayout) itemView.findViewById(R.id.retweeted_weibo_image_line_3);
    }

    public void refresh(final WeiboPojo.Statuses statuses){
        this.retweet_date.setText(statuses.getCreated_at());
        this.retweet_text.setText(statuses.getText());
        this.retweeted_date.setText(statuses.getRetweeted_status().getCreated_at());
        this.retweeted_text.setText(statuses.getRetweeted_status().getText());
        this.retweet_user.setText(statuses.getUser().getName());
        retweet_user.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                int motionEvent = event.getAction();
                if (motionEvent == MotionEvent.ACTION_UP || motionEvent == MotionEvent.ACTION_DOWN) {
                    if (event.getRawX() >= (v.getRight() - ((TextView) v).getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here
                        PopupMenu popupMenu = new PopupMenu(getContext().getActivity(),retweet_user,Gravity.RIGHT);
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
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                        return true;
                    }
                }
                return false;
            }
        });
        this.retweeted_user.setText(statuses.getRetweeted_status().getUser().getName());
        ImageDownloader downloaderRetweet = new ImageDownloader(getContext());
        downloaderRetweet.execute(new Object[]{statuses.getUser().getProfile_image_url(),retweet_user_portrait,1,1});
        ImageDownloader downloaderRetweeted = new ImageDownloader(getContext());
        downloaderRetweeted.execute(new Object[]{statuses.getRetweeted_status().getUser().getProfile_image_url(),retweeted_user_portrait,1,1});
        refreshImage(statuses,statuses.getRetweeted_status().getPic_urls(),statuses.getRetweeted_status().convertToUris(),this.retweeted_line_1,this.retweeted_line_2,this.retweeted_line_3);
    }

    public void setId(String w_id){
        id = w_id;
    }
}

 class SingleWB_ViewHolder extends CustomViewHolder {
    TextView user_name;
    ImageView user_portrait;
    TextView text;
    TextView date;
    TextView repost;
    TextView comment;
    LinearLayout line_1;
    LinearLayout line_2;
    LinearLayout line_3;
    public SingleWB_ViewHolder(View itemView,Fragment context, onAdapterEvent event) {
        super(itemView,context,event);
        initView(itemView);
    }

     public SingleWB_ViewHolder(View itemView,Fragment context, onAdapterEvent event,String wid) {
         super(itemView,context,event,wid);
         initView(itemView);
     }

     private void initView(View itemView) {
         user_name = (TextView) itemView.findViewById(R.id.single_user);
         user_name.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 final int DRAWABLE_LEFT = 0;
                 final int DRAWABLE_TOP = 1;
                 final int DRAWABLE_RIGHT = 2;
                 final int DRAWABLE_BOTTOM = 3;
                 int motionEvent = event.getAction();
                 if (motionEvent == MotionEvent.ACTION_UP || motionEvent == MotionEvent.ACTION_DOWN) {
                     if (event.getRawX() >= (v.getRight() - ((TextView) v).getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                         // your action here
                         PopupMenu popupMenu = new PopupMenu(getContext().getActivity(),user_name, Gravity.RIGHT);
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
                                     default:
                                         break;
                                 }
                                 return false;
                             }
                         });
                         popupMenu.show();
                         return true;
                     }
                 }
                 return false;
             }
         });
         user_portrait = (ImageView) itemView.findViewById(R.id.single_user_portrait);
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
         line_1 = (LinearLayout) itemView.findViewById(R.id.weibo_image_line_1);
         line_2 = (LinearLayout) itemView.findViewById(R.id.weibo_image_line_2);
         line_3 = (LinearLayout) itemView.findViewById(R.id.weibo_image_line_3);
     }

     public void refresh(final WeiboPojo.Statuses statuses){
        this.user_name.setText(statuses.getUser().getName());
        ImageDownloader downloader = new ImageDownloader(getContext());
        downloader.execute(new Object[]{statuses.getUser().getProfile_image_url(),user_portrait,1,1});
        this.text.setText(statuses.getText());
        this.date.setText(statuses.getCreated_at());
        this.repost.setText(statuses.getReposts_count());
        this.comment.setText(statuses.getComments_count());
        refreshImage(statuses,statuses.getPic_urls(),statuses.convertToUris(),line_1,line_2,line_3);
    }
     public void setCommentUnderline(){
         comment.setPaintFlags(comment.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
         repost.setPaintFlags(repost.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
     }
     public void setRepostUnderline(){
         repost.setPaintFlags(repost.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
         comment.setPaintFlags(comment.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
     }


}

class CustomViewHolder extends RecyclerView.ViewHolder {
    private Fragment mContext;
    static int PopupWindow_Comment = 1;
    static int PopupWindow_Repost = 2;
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

    public void setAdapterEvent(onAdapterEvent event){
        adapterEvent = event;
    }

    public void SetOnClickListener(View.OnClickListener listener){
        itemView.setOnClickListener(listener);
    }

    public void setId(String w_id){
        id = w_id;
    }

    void refreshImage(final WeiboPojo.Statuses statuses, final WeiboPojo.Pic_urls[] urls, final Uri[] convert_uris, LinearLayout line_1, LinearLayout line_2, LinearLayout line_3){
        line_1.setVisibility(View.GONE);
        line_2.setVisibility(View.GONE);
        line_3.setVisibility(View.GONE);
        line_1.removeAllViews();
        line_2.removeAllViews();
        line_3.removeAllViews();
        if(urls!=null && urls.length>0){
            int count = 0;
            for (WeiboPojo.Pic_urls url :
                    urls) {
                CustomImageView imageView = new CustomImageView(mContext.getActivity());
                final int index = count;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageViewFragment fragment = new ImageViewFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArray("Uris",convert_uris);
                        bundle.putInt("Index",index);
                        fragment.setArguments(bundle);
                        ((MainActivity)mContext.getActivity()).setFragment(mContext,fragment);
                    }
                });
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
                ImageDownloader downloader = new ImageDownloader(getContext());
                downloader.execute(new Object[]{url.getThumbnail_pic(),imageView,urls.length,0});
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
        popupWindow.setFocusable(true);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getContext().getActivity().getResources().getDrawable(R.drawable.popupwindow_drawable));
        return popupWindow;
    }
}


class ImageDownloader extends AsyncTask<Object,Bitmap,Bitmap>{
    private final DisplayMetrics mDisplayMetrics;
    ImageView view;
    int count;
    int divider = 80;
    int downloadType;
    private final int IMAGE=0;
    private final int PORTRAIT=1;
    public ImageDownloader(Fragment mContext){
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
    }
    @Override
    protected Bitmap doInBackground(Object... params) {
        try{
            //URL myFileURL = new URL(params[0].toString().replace("thumbnail","large"));
            //URL myFileURL = new URL(params[0].toString());
            URL myFileURL = new URL(params[0].toString().replace("thumbnail","bmiddle"));
            view = (ImageView) params[1];
            count = (int) params[2];
            downloadType = (int) params[3];
            if(view == null){
                Log.e("ss","s");
            }
            //if image cached
            File cachedFile = getCachedImage(WeatherApplication.getFileName(myFileURL));
            if(cachedFile!=null){
                Bitmap bitmap = BitmapFactory.decodeFile(cachedFile.getAbsolutePath());
                return bitmap;
            }
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            //得到数据流
            InputStream is = conn.getInputStream();
            File downloadFile = new File(WeatherApplication.CacheCategory,WeatherApplication.getFileName(myFileURL));
            OutputStream os = new FileOutputStream(downloadFile);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            //解析得到图片
            Bitmap bitmap = BitmapFactory.decodeFile(downloadFile.getAbsolutePath());
            //关闭数据流
            is.close();
            return bitmap;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if(downloadType==IMAGE) {
            if (count == 1) {
                view.getLayoutParams().width = mDisplayMetrics.widthPixels - divider;
            } else if (count == 2) {
                view.getLayoutParams().width = (mDisplayMetrics.widthPixels - divider) / 2;
            } else {
                view.getLayoutParams().width = (mDisplayMetrics.widthPixels - divider) / 3;
            }
            view.setImageBitmap(result);
        }else if(downloadType==PORTRAIT){
            int width = view.getLayoutParams().width;
            view.setImageBitmap(result);
        }
    }

    private File getCachedImage(String name){
        if(WeatherApplication.CacheCategory.exists()){
            for (File file :
                    WeatherApplication.CacheCategory.listFiles()) {
                String fileName = file.getName();
                if (fileName.equals(name)){
                    return file;
                }
            }
            return null;
        }else{
            return null;
        }
    }
}