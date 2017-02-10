package com.weiwa.ljl.weiwa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by hzfd on 2017/1/23.
 */
public class CustomImageView extends ImageView{
    public GestureDetector mGestureDetector;
    private int count = 0;
    private float scale;
    private Context mContext;
    private long movieStart;
    private Movie movie;
    private boolean isGif;
    private File gifFile;

    private void initGestureDetector(final Context context){
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if(getScaleType() == ScaleType.MATRIX){
                    setScaleType(ScaleType.FIT_CENTER);
                }else{
                    float trueWidth = context.getResources().getDisplayMetrics().widthPixels;
                    float imageWidth = getDrawable().getIntrinsicWidth();
                    setScaleType(ScaleType.MATRIX);
                    Matrix matrix = new Matrix();
                    scale = trueWidth/imageWidth;
                    setScaleX(scale);
                    setScaleY(scale);
                    matrix.postScale(scale,scale);
                    setImageMatrix(matrix);
                }
                /*if(getScaleX()==1.0f){
                    float trueWidth = context.getResources().getDisplayMetrics().widthPixels;
                    float imageWidth = getDrawable().getIntrinsicWidth();
                    scale = trueWidth/imageWidth;
                    setScaleX(scale);
                    setScaleY(scale);
                }else{
                    setScaleX(1.0f);
                    setScaleY(1.0f);
                }*/
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(isGif){
            movie = Movie.decodeFile(gifFile.getAbsolutePath());
            long curTime=android.os.SystemClock.uptimeMillis();
            if (movieStart == 0) {
                movieStart = curTime;
            }
            if (movie != null) {
                int duraction = movie.duration();
                int relTime = (int) ((curTime-movieStart)%duraction);
                movie.setTime(relTime);
                movie.draw(canvas, 0, 0);
                invalidate();
            }
        }
        super.onDraw(canvas);
    }

    public CustomImageView(Context context) {
        super(context);
        mContext = context;
        initGestureDetector(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initGestureDetector(context);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initGestureDetector(context);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initGestureDetector(context);
    }

    public void setIsGif(boolean gif, File file){
        if(gif){
            isGif = true;
            gifFile = file;
        }else{
            isGif = false;
        }
    }
}
