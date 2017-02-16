package com.weiwa.ljl.weiwa;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import java.util.concurrent.ExecutionException;

import javax.microedition.khronos.opengles.GL11;

import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {

    private View mView;
    private File downloadFile;
    private File downloadCategory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Weiwa");
    private TextView mProgressText;
    private ImageButton mShareButton;
    private ImageViewPager mViewPager;
    private Uri[] uris;
    private int currentIndex = 0;

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!downloadCategory.exists()){
            downloadCategory.mkdir();
        }
        if (getArguments() != null) {
            //init Uris
            uris = (Uri[]) (getArguments().getParcelableArray("Uris"));
            currentIndex = getArguments().getInt("Index");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_image, container, false);
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
        mProgressText = (TextView) mView.findViewById(R.id.progress_text);
        mProgressText.setText(currentIndex+1+"/"+uris.length);
        mViewPager = (ImageViewPager) mView.findViewById(R.id.guide_viewpager);
        mViewPager.init(getActivity(), uris, new ImageViewPager.onImageDownladingListener() {
            @Override
            public void onDownload(Uri uri, ImageView imageView) {
                try {
                    LoadGlideImage(uri,imageView);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        },currentIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setProgressText((position+1)+"/"+uris.length);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return mView;
    }

    public void setProgressText(String content){
        mProgressText.setText(content);
    }

    public void LoadGlideImage(Uri targetUri, ImageView imageView) throws MalformedURLException {
        if(isGif(targetUri)){
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
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
            BitmapDownloader bitmapDownloader = new BitmapDownloader();
            bitmapDownloader.execute(new Object[]{(new URL(targetUri.toString())),imageView});
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

    class BitmapDownloader extends AsyncTask<Object,Bitmap,Bitmap> {

        private ImageView imageView;
        @Override
        protected Bitmap doInBackground(Object... params) {
            Bitmap bitmap;
            try {
                //获得连接
                URL url = (URL) params[0];
                imageView = (ImageView)params[1];
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
            if(result.getHeight()> GL11.GL_MAX_TEXTURE_SIZE){
                int newHeight = (int) ( result.getHeight() * (512.0 / result.getWidth()) );
                Bitmap putImage = Bitmap.createScaledBitmap(result, 512, newHeight, true);
                //mViewPager.setImageView(position,putImage);
                imageView.setImageBitmap(putImage);
                imageView.invalidate();
            }else {
                //mViewPager.setImageView(position,result);
                imageView.setImageBitmap(result);
                imageView.invalidate();
            }
            PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
            attacher.setAllowParentInterceptOnEdge(true);
            //mViewPager.getImageView(position).invalidate();
        }
    }
}
