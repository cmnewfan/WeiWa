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

    private ImageView[] ImageViews;
    private PhotoViewAttacher[] attachers;
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
        ImageViews = new ImageView[uris.length];
        attachers = new PhotoViewAttacher[uris.length];
        for(int i=0;i<uris.length;i++){
            ImageViews[i] = new ImageView(mContext);
            ImageViews[i].setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            ImageViews[i].setAdjustViewBounds(true);
            ImageViews[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            attachers[i] = new PhotoViewAttacher(ImageViews[i]);
            attachers[i].setAllowParentInterceptOnEdge(true);
        }
        InitViewAdapter();
        mIndex = index;
        setCurrentItem(mIndex);
    }

    public void setImageView(int position,Bitmap bitmap){
        ImageViews[position].setImageBitmap(bitmap);
        attachers[position].update();
        ImageViews[position].invalidate();
    }

    public ImageView getImageView(int position){
        return ImageViews[position];
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
                View view = new View(mContext);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                ImageView imageView = new ImageView(mContext);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                /*View view = LayoutInflater.from(container.getContext()).inflate(R.layout.image_view, container, false);
                ImageView imageView = (ImageView) view.findViewById(R.id.guide_image_1);*/
                container.addView(imageView);
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
