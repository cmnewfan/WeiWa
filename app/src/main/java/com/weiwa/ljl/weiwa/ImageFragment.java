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
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView mDownloadText;
    private ImageButton mShareButton;
    private ImageViewPager mViewPager;
    private Uri[] uris;
    private int currentIndex = 0;
    private int width;
    private int height;

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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
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
        mDownloadText = (TextView) mView.findViewById(R.id.download_text) ;
        mViewPager = (ImageViewPager) mView.findViewById(R.id.guide_viewpager);
        mViewPager.setMinimumWidth(RecyclerView.LayoutParams.MATCH_PARENT);
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

    public void LoadGlideImage(final Uri targetUri, ImageView imageView) throws MalformedURLException {
        if(isGif(targetUri)){
            GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
            Glide.with(this).load(targetUri).downloadOnly(new SimpleTarget<File>() {
                @Override
                public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                    try {
                        FileInputStream inputStream = new FileInputStream(resource);
                        downloadFile = new File(downloadCategory, WeiwaApplication.getFileName(targetUri)+".gif");
                        FileOutputStream outputStream = new FileOutputStream(downloadFile);
                        int bytesRead = 0;
                        byte[] buffer = new byte[8192];
                        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                            outputStream.write(buffer,0,bytesRead);
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
            Glide.with(this).load(targetUri).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(imageViewTarget);
        }else {
            BitmapDownloader bitmapDownloader = new BitmapDownloader();
            //current item position is used to display progress of item downloading
            bitmapDownloader.execute(new Object[]{(new URL(targetUri.toString())),imageView,mViewPager.getCurrentItem()});
        }
    }

    private void share() throws ExecutionException, InterruptedException {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        //set intent type
        sendIntent.setType("image/*");
        File shareFile = new File(WeiwaApplication.CacheCategory+"/"+WeiwaApplication.getFileName(uris[mViewPager.getCurrentItem()]));
        Uri targetUri = Uri.fromFile(shareFile);
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

    class BitmapDownloader extends AsyncTask<Object,Object,File> {
        private ImageView imageView;
        @Override
        protected File doInBackground(Object... params) {
            Bitmap bitmap;
            try {
                //获得连接
                URL url = (URL) params[0];
                imageView = (ImageView)params[1];
                int position = (int) params[2];
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
                downloadFile = new File(downloadCategory, WeiwaApplication.getFileName(url));
                OutputStream os = new FileOutputStream(downloadFile);
                int bytesRead = 0;
                int count = conn.getContentLength();
                byte[] buffer = new byte[count];
                float progress = 0.0f;
                int offset = 0;
                int read = 8192;
                if(read>count){
                    read = count;
                }
                while ((bytesRead = is.read(buffer, offset, read)) != -1) {
                    os.write(buffer,offset,bytesRead);
                    offset += bytesRead;
                    if(is.available()!=0) {
                        progress = (float)offset / count;
                    }else{
                        progress = (float)offset / count;
                    }
                    if(count-offset<=read){
                        read = count-offset;
                    }
                    if(WeiwaApplication.getFileName(url).equals(WeiwaApplication.getFileName(uris[position]))){
                        publishProgress(progress);
                    }
                }
                os.close();
                is.close();
                return downloadFile;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            int display_progress = (int)((float)values[0]*100);
            mDownloadText.setVisibility(View.VISIBLE);
            mDownloadText.setText(display_progress+"%");
            if(display_progress==1){
                mDownloadText.setText("0");
                mDownloadText.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        protected void onPostExecute(File result) {
            //GL11.GL_MAX_TEXTURE_SIZE
            if(result==null){
                Toast.makeText(ImageFragment.this.getActivity(),"文件下载失败",Toast.LENGTH_SHORT).show();
                return;
            }
            imageView.setImageURI(Uri.fromFile(result));
            if((float)(imageView.getDrawable().getIntrinsicHeight()/imageView.getDrawable().getIntrinsicWidth())>2.0f){
                //int newHeight = (int) ( result.getHeight() * (512.0 / result.getWidth()) );
                //Bitmap putImage = Bitmap.createScaledBitmap(result, 512, newHeight, true);
                //int height = putImage.getHeight();
                //imageView.setImageBitmap(result);
                //imageView.invalidate();
                //int w1 = result.getWidth();
                PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
                float ratio = (float)imageView.getWidth()/(float)(attacher.getImageView().getDrawable().getIntrinsicWidth());
                if(ratio<4 && ratio>2){
                    ratio = ratio/2;
                }
                attacher.setScaleLevels(ratio/2,ratio,ratio*2);
                attacher.setScale(ratio,ratio,ratio,true);
            }else {
                PhotoViewAttacher attacher = new PhotoViewAttacher(imageView);
            }
        }
    }
}
