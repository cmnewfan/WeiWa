package com.weiwa.ljl.weiwa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.microedition.khronos.opengles.GL11;

/**
 * Created by hzfd on 2017/2/13.
 */
public class PortraitView extends ImageView {
    private WeiboPojo.User mUser;
    private MainActivity instance;

    public PortraitView(Context context, final WeiboPojo.User user) {
        super(context);
        instance = (MainActivity) context;
        mUser = user;
    }

    public PortraitView(Context context, String downloadUrl, int downloadCount, int downloadType) {
        super(context);
        DownloadHelper downloadHelper = new DownloadHelper();
        downloadHelper.execute(new Object[]{downloadUrl, downloadCount, downloadType});
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addDownloadTask(String downloadUrl, int downloadCount, int downloadType) {
        DownloadHelper downloadHelper = new DownloadHelper();
        downloadHelper.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[]{downloadUrl, downloadCount, downloadType});
    }

    public void setData(MainActivity context, WeiboPojo.User user){
        instance = context;
        mUser = user;
        /*setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserViewFragment userViewFragment = new UserViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", mUser.getScreen_name());
                bundle.putString("decription",mUser.getDescription());
                bundle.putString("location",mUser.getLocation());
                bundle.putString("gender",mUser.getGender());
                bundle.putString("follow_me",mUser.getFollow_me());
                bundle.putString("uid",mUser.getId());
                userViewFragment.setArguments(bundle);
                instance.setFragment(userViewFragment);
            }
        });*/
    }

    class DownloadHelper extends AsyncTask<Object, Bitmap, Bitmap> {
        private PortraitView mView;
        int count;
        Boolean isGif = true;
        int divider = 100;
        int downloadType;
        private final int IMAGE = 0;
        private final int PORTRAIT = 1;
        private final DisplayMetrics mDisplayMetrics;

        public DownloadHelper() {
            mView = PortraitView.this;
            mDisplayMetrics = getResources().getDisplayMetrics();
        }

        private File getCachedImage(String name, int downloadType) {
            File targetCategory = null;
            switch (downloadType) {
                case PORTRAIT:
                    targetCategory = WeiwaApplication.CachePortrait;
                    break;
                case IMAGE:
                    targetCategory = WeiwaApplication.CacheCategory;
                    break;
            }
            if (targetCategory.exists()) {
                for (File file :
                        targetCategory.listFiles()) {
                    String fileName = file.getName();
                    if (fileName.equals(name)) {
                        return file;
                    }
                }
                return null;
            } else {
                return null;
            }
        }

        private Bitmap adjustBitmap(Bitmap result) {
            if (result.getHeight() > GL11.GL_MAX_TEXTURE_SIZE) {
                int newHeight = GL11.GL_MAX_TEXTURE_SIZE;
                int newWidth = (int) (((float) result.getWidth() / (float) result.getHeight()) * newHeight);
                result = Bitmap.createBitmap(result, 0, 0, newWidth, newHeight);
            }
            return result;
        }

        @Override
        protected Bitmap doInBackground(Object[] params) {
            URL myFileURL = null;
            try {
                myFileURL = new URL(params[0].toString());
                if (!params[0].toString().toLowerCase().endsWith(".gif")) {
                    myFileURL = new URL(params[0].toString().replace("thumbnail", "bmiddle"));
                    isGif = false;
                }
                count = (int) params[1];
                downloadType = (int) params[2];
                //if image cached
                File cachedFile = getCachedImage(WeiwaApplication.getFileName(myFileURL), downloadType);
                if (cachedFile != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(cachedFile.getAbsolutePath());
                    return bitmap;
                }
                //获得连接
                HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
                //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                conn.setConnectTimeout(6000);
                //连接设置获得数据流
                conn.setDoInput(true);
                //不使用缓存
                conn.setUseCaches(false);
                //这句可有可无，没有影响
                //conn.connect();
                //得到数据流
                File downloadFile = null;
                switch (downloadType) {
                    case PORTRAIT:
                        downloadFile = new File(WeiwaApplication.CachePortrait, WeiwaApplication.getFileName(myFileURL));
                        break;
                    case IMAGE:
                        downloadFile = new File(WeiwaApplication.CacheCategory, WeiwaApplication.getFileName(myFileURL));
                        break;
                }
                InputStream is = conn.getInputStream();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                return;
            } else {
                result = adjustBitmap(result);
            }
            if (downloadType == IMAGE) {
                if (count == 1) {
                    mView.getLayoutParams().width = mDisplayMetrics.widthPixels - divider;
                    if (result.getHeight() > GL11.GL_MAX_TEXTURE_SIZE) {
                        mView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {
                        mView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }
                } else if (count == 2) {
                    mView.getLayoutParams().width = (mDisplayMetrics.widthPixels - divider) / 2;
                    mView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                } else {
                    if (mView.getLayoutParams() == null) {
                        mView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    mView.getLayoutParams().width = (mDisplayMetrics.widthPixels - divider) / 3;
                    mView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                mView.setImageBitmap(result);
            } else if (downloadType == PORTRAIT) {
                mView.setImageBitmap(result);
            }
        }
    }
}
