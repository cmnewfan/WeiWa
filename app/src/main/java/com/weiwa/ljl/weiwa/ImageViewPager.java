package com.weiwa.ljl.weiwa;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.net.MalformedURLException;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by hzfd on 2017/2/15.
 */
public class ImageViewPager extends ViewPager {

    private Uri[] mUris;
    private Context mContext;
    private onImageDownladingListener mListener;
    private int mIndex;

    public ImageViewPager(Context context) {
        super(context);
    }

    public ImageViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context,Uri[] uris,onImageDownladingListener listener,int index){
        mContext = context;
        mUris = uris;
        mListener = listener;
        InitViewAdapter();
        mIndex = index;
        setCurrentItem(mIndex);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            //child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) {
                height = h;
            }
        }
        if(height == 0){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }else {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void InitViewAdapter() {
        final ArrayList<String> titleList = new ArrayList<String>();
        titleList.add("");
        titleList.add("");
        PagerAdapter guide_pager_adapter = new PagerAdapter() {
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == ((View) arg1);
            }

            @Override
            public int getCount() {
                return mUris.length;
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getItemPosition(Object object) {
                return super.getItemPosition(object);
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                container.addView(imageView);
                //container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mListener.onDownload(mUris[position],imageView);
                return imageView;
            }
        };
        setAdapter(guide_pager_adapter);
    }

    public interface onImageDownladingListener{
        void onDownload(Uri uri, ImageView imageView);
    }
}
