package com.weiwa.ljl.weiwa;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.icu.math.BigDecimal;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import retrofit2.http.Url;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageViewFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View mView;
    private File downloadFile;
    private File downloadCategory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Weiwa");
    private CustomImageView mSimpleView;
    private TextView mProgressText;
    private ImageButton mShareButton;
    private Uri[] uris;
    private int currentIndex = 0;
    private Matrix lastMatrix;
    private float downX = 0;
    private float downY = 0;
    private int[] initPoint;
    private float topPoint;
    private float endPoint;
    private Boolean DoubleTapMode;
    private float fingerOneY;
    private float fingerTwoX;
    private float fingerTwoY;
    private float lastScale=1.0f;
    private Boolean isGif = true;
    private OnFragmentInteractionListener mListener;
    private PhotoViewAttacher attacher;

    public ImageViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImageViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImageViewFragment newInstance(String param1, String param2) {
        ImageViewFragment fragment = new ImageViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!downloadCategory.exists()){
            downloadCategory.mkdir();
        }
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            //init Uris
            uris = (Uri[]) (getArguments().getParcelableArray("Uris"));
            currentIndex = getArguments().getInt("Index");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        mView = inflater.inflate(R.layout.fragment_image_view, container, false);
        mShareButton = (ImageButton) mView.findViewById(R.id.button_share);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    share();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        mSimpleView = (CustomImageView) mView.findViewById(R.id.simple_view);
        mProgressText = (TextView) mView.findViewById(R.id.progress_text);
        mProgressText.setText(currentIndex+1+"/"+uris.length);
        mSimpleView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        attacher = new PhotoViewAttacher(mSimpleView);
        attacher.setAllowParentInterceptOnEdge(true);
        /*mSimpleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //recognize gesture
                if(mSimpleView.mGestureDetector.onTouchEvent(event)){
                    DoubleTapMode = true;
                }else{
                    DoubleTapMode = false;
                }
                //recognize action
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(event.getPointerCount()==1) {
                            downX = event.getRawX();
                            downY = event.getRawY();
                        }else{
                            return false;
                        }
                        return false;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if(event.getPointerCount()>1){
                            return false;
                        }
                        lastScale = mSimpleView.getScaleX();
                        if(mSimpleView.getScaleX()!=1 || mSimpleView.getScaleY()!=1){
                            return mSimpleView.onTouchEvent(event);
                        }
                        float upX = event.getRawX();
                        float upY = event.getRawY();
                        if (Math.abs(upY - downY) < 120) {
                            //move right
                            if (upX - downX > 60) {
                                if (currentIndex == 0) {
                                    Toast.makeText(getActivity(), "已到第一张图片", Toast.LENGTH_SHORT).show();
                                } else {
                                    mSimpleView.setScaleX(1);
                                    mSimpleView.setScaleY(1);
                                    try {
                                        LoadGlideImage(uris[--currentIndex]);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    mProgressText.setText(currentIndex+1+"/"+uris.length);
                                }
                                return false;
                            } else if (downX - upX > 60) {
                                if (currentIndex == uris.length-1) {
                                    Toast.makeText(getActivity(), "已到最后一张图片", Toast.LENGTH_SHORT).show();
                                } else {
                                    mSimpleView.setScaleX(1);
                                    mSimpleView.setScaleY(1);
                                    try {
                                        LoadGlideImage(uris[++currentIndex]);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    mProgressText.setText(currentIndex+1+"/"+uris.length);
                                }
                                return false;
                            }
                        } else {
                            Toast.makeText(getActivity(), "Y轴变动太大", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                }
                return false;
            }
        });*/
        if(isGif(uris[currentIndex])){
            try {
                LoadGlideImage(uris[currentIndex]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else {
            try {
                LoadGlideImage(uris[currentIndex]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return mView;
    }

    private void LoadGlideImage(Uri targetUri) throws MalformedURLException {
        if(isGif(targetUri)){
            isGif = true;
            //BitmapDownloader bitmapDownloader = new BitmapDownloader();
            //bitmapDownloader.execute(new URL[]{(new URL(uris[currentIndex].toString()))});
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(mSimpleView);
            Glide.with(this).load(targetUri).downloadOnly(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                    try {
                        FileInputStream inputStream = new FileInputStream(resource);
                        downloadFile = new File(downloadCategory,WeatherApplication.getFileName(uris[currentIndex])+".gif");
                        FileOutputStream outputStream = new FileOutputStream(downloadFile);
                        int bytesRead = 0;
                        byte[] buffer = new byte[8192];
                        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            });
            Glide.with(this).load(uris[currentIndex]).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(imageViewTarget);
        }else {
            isGif = false;
            BitmapDownloader bitmapDownloader = new BitmapDownloader();
            bitmapDownloader.execute(new URL[]{(new URL(uris[currentIndex].toString()))});
            /*Glide.with(this).load(targetUri).downloadOnly(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                    downloadFile = resource;
                }
            });
            Glide.with(this).load(uris[currentIndex]).asBitmap().into(mSimpleView);*/
        }
    }

    private void share() throws ExecutionException, InterruptedException {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        //set intent type
        sendIntent.setType("image/*");
        Uri targetUri = Uri.fromFile(downloadFile);
        if (targetUri != null) {
            sendIntent.putExtra(Intent.EXTRA_STREAM, targetUri);
        }
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(sendIntent, "share"));
    }

    @NonNull
    private Boolean isGif(Uri uri){
        if(uri.toString().toLowerCase().endsWith("gif")){
            return true;
        }else{
            return false;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class BitmapDownloader extends AsyncTask<URL,Bitmap,Bitmap>{

        @Override
        protected Bitmap doInBackground(URL... params) {
            Bitmap bitmap;
            try {
                //获得连接
                HttpURLConnection conn = (HttpURLConnection) params[0].openConnection();
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
                downloadFile = new File(downloadCategory,WeatherApplication.getFileName(uris[currentIndex]));
                OutputStream os = new FileOutputStream(downloadFile);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.close();
                //解析得到图片
                bitmap = BitmapFactory.decodeFile(downloadFile.getAbsolutePath());
                //关闭数据流
                is.close();
                return bitmap;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            //downloadFile = ShareHelper.saveBitmapToFile(Environment.getExternalStorageDirectory(),getFileName(uris[currentIndex]), result, Bitmap.CompressFormat.PNG, 100);
            /*if(isGif){
                mSimpleView.setIsGif(true,downloadFile);
            }*/
            if(result.getHeight()> GL11.GL_MAX_TEXTURE_SIZE){
                int newHeight = (int) ( result.getHeight() * (512.0 / result.getWidth()) );
                Bitmap putImage = Bitmap.createScaledBitmap(result, 512, newHeight, true);
                mSimpleView.setImageBitmap(putImage);
            }else {
                mSimpleView.setImageBitmap(result);
            }
            attacher.update();
            mSimpleView.invalidate();
        }
    }
}
