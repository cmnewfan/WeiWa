package com.weiwa.ljl.weiwa;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by hzfd on 2017/2/14.
 */
public class CustomPhotoViewAttacher extends PhotoViewAttacher {
    public CustomPhotoViewAttacher(ImageView imageView) {
        super(imageView);
    }

    public CustomPhotoViewAttacher(ImageView imageView, boolean zoomable) {
        super(imageView, zoomable);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev){
        return super.onTouch(v,ev);
    }
}
