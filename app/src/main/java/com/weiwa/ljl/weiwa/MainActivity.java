package com.weiwa.ljl.weiwa;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.weiwa.ljl.weiwa.WeiboModel.WeiboCommentPojo;
import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class MainActivity extends AppCompatActivity {
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private WeiWaAuthListener mAuthListener;
    private FragmentManager fragmentManager;
    private int back_count=0;
    private String weibo_max_id;
    private String weibo_user_max_id;
    private boolean isLastOne=false;
    private ImageButton add;
    private ImageButton upload;
    private boolean isUserLastOne=false;
    private WeiboPojo currentWeibo;
    private onWeiboUpdatedListener onWeiboPOJOUpdated;
    private OnUserUpdatedListener onUserUpdatedListener;
    private MainFragment mMainFragment;
    private FloatingActionButton fab;
    private WeiboClient.ApiStores apiStores;
    private Stack<Boolean> fabStatus = new Stack<>();
    private final static int WEIBO_GET_NEW = 0;
    private final static int WEIBO_GET_NEXT = 1;
    private final static int WEIBO_GET_COMMENT = 2;
    private final static int WEIBO_GET_USER_NEW = 3;
    private final static int WEIBO_GET_USER_NEXT = 4;
    private final static int WEIBO_UPDATE_NO_PIC = 5;
    private final static int WEIBO_UPLOAD_WITH_PIC = 6;
    public static String token;
    public static final String APP_KEY = "1492233661"; // 应用的APP_KEY
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";// 应用的回调页
    public static final String SCOPE = // 应用申请的高级权限
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write,timeline_batch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the newest data
                weibo_max_id = null;
                getWeiboData(WEIBO_GET_NEW,null);
            }
        });
        //init Image Button
        add = (ImageButton) findViewById(R.id.add);
        upload = (ImageButton) findViewById(R.id.edit);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFragment editFragment = new EditFragment();
                add.setVisibility(View.VISIBLE);
                upload.setVisibility(View.GONE);
                editFragment.setOnPost(new onPost() {
                    @Override
                    public void post(String content) {
                        update(content,null,0,WEIBO_UPDATE_NO_PIC);
                    }

                    @Override
                    public void post(String content, byte[] pic) {
                        update(content,pic,0,WEIBO_UPLOAD_WITH_PIC);
                    }
                });
                setFragment(editFragment);
            }
        });
        //Auth for weibo
        initAuth();
        //init Glide
        new GlideBuilder(this)
                .setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        mMainFragment = new MainFragment();
        setDefaultFragment();
        getWeiboData(WEIBO_GET_NEW,null);
    }

    public void setOnWeiboPOJOUpdated(onWeiboUpdatedListener listener){
        this.onWeiboPOJOUpdated = listener;
    }

    public void setOnUserUpdatedListener(OnUserUpdatedListener listener){
        this.onUserUpdatedListener = listener;
    }

    public void setOnAddButtonClickListener(View.OnClickListener listener){
        add.setOnClickListener(listener);
    }

    public void popBack(){
        fragmentManager.popBackStack();
        add.setVisibility(View.GONE);
        upload.setVisibility(View.VISIBLE);
        if(fragmentManager.findFragmentById(R.id.main_fragment).isHidden()){
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentById(R.id.main_fragment)).commit();
        }
        if(fabStatus.size()>1){
            fabStatus.pop();
            if(fabStatus.lastElement()){
                fab.setVisibility(View.VISIBLE);
            }else{
                fab.setVisibility(View.GONE);
            }
        }
    }

    private void initAuth(){
        SharedPreferences sharedPreferences = getSharedPreferences("weibo_preferences", Activity.MODE_PRIVATE);
        MainActivity.token = sharedPreferences.getString("Token","");
        if(MainActivity.token.equals("")) {
            mAuthInfo = new AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE);
            mSsoHandler = new SsoHandler(this, mAuthInfo);
            mAuthListener = new WeiWaAuthListener(this);
            mSsoHandler.authorizeWeb(mAuthListener);
        }
    }

    private void setDefaultFragment(){
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_fragment, mMainFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fabStatus.push(true);
    }

    public void setFragment(Fragment fragment){
        if(fragment instanceof UserViewFragment){
            fab.setVisibility(View.VISIBLE);
            fabStatus.push(true);
            add.setVisibility(View.GONE);
            upload.setVisibility(View.VISIBLE);
        }else if(fragment instanceof EditFragment){
            add.setVisibility(View.VISIBLE);
            upload.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
            fabStatus.push(false);
        }else{
            fab.setVisibility(View.GONE);
            fabStatus.push(false);
            add.setVisibility(View.GONE);
            upload.setVisibility(View.VISIBLE);
        }
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.hide(getFragmentManager().findFragmentById(R.id.main_fragment));
        fragmentTransaction.add(R.id.main_fragment,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // weibo_drawable you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                initAuth();
                return true;
            case R.id.clear_cache:
                Toast.makeText(this,"开始清除缓存",Toast.LENGTH_SHORT).show();
                new ClearTask().execute(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragmentManager == null) {
                fragmentManager = getFragmentManager();
            }
            if (fragmentManager.getBackStackEntryCount() == 1) {
                fab.setVisibility(View.VISIBLE);
                if (event.getRepeatCount() == 0 && back_count == 0) {
                    back_count++;
                    Toast.makeText(getApplicationContext(), "再次点击后退键退出程序", Toast.LENGTH_SHORT).show();
                } else if (event.getRepeatCount() == 0 && back_count == 1) {
                    back_count--;
                    System.exit(0);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            } else{
                popBack();
            }
        }
        return false;
    }

    public void getWeiboData(){
        getWeiboData(WEIBO_GET_NEXT,null);
    }

    public void getWeiboComment(String id){
        getWeiboComment(WEIBO_GET_COMMENT, id);
    }

    /**
     * create comment of specified weibo(id)
     * @param id id of commented weibo
     * @param content content of comment
     */
    public void createComment(String id, String content){
        Call<WeiboCommentPojo> call = null;
        if(apiStores == null){
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores.class);
        }
        call = apiStores.createComment(MainActivity.token,id,content);
        call.enqueue(new Callback<WeiboCommentPojo>() {
            @Override
            public void onResponse(Response<WeiboCommentPojo> response) {
                WeiboCommentPojo comments = response.body();
                Toast.makeText(MainActivity.this,"Comment Success",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Throwable t) {
                //do nothing
                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                Log.e("",t.getMessage());
            }
        });
    }

    /**
     * create repost of specified weibo
     * @param id id of reposted weibo
     * @param content content of repost
     */
    public void createRepost(String id, String content){
        Call<WeiboPojo.Statuses> call = null;
        if(apiStores == null){
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores.class);
        }
        call = apiStores.createRepost(MainActivity.token,id,content);
        call.enqueue(new Callback<WeiboPojo.Statuses>() {
            @Override
            public void onResponse(Response<WeiboPojo.Statuses> response) {
                Toast.makeText(MainActivity.this,"Repost Success",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Throwable t) {
                //do nothing
                Log.e("",t.getMessage());
            }
        });
    }

    /**
     * get comments of specified weibo
     * @param updateType not used
     * @param id id of specified weibo
     */
    private void getWeiboComment(final int updateType, String id) {
        Call<WeiboCommentPojo> call = null;
        if(apiStores == null){
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores.class);
        }
        switch (updateType){
            case WEIBO_GET_COMMENT:
                call = apiStores.getWeiboCommentData(MainActivity.token,id);
                break;
            default:
                break;
        }
        call.enqueue(new Callback<WeiboCommentPojo>() {
            @Override
            public void onResponse(Response<WeiboCommentPojo> response) {
                WeiboCommentPojo comments = response.body();
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                onWeiboPOJOUpdated.onComment(comments);
            }

            @Override
            public void onFailure(Throwable t) {
                //do nothing
                Log.e("",t.getMessage());
            }
        });
    }

    /**
     * create new weibo
     * @param content content of new weibo
     * @param pic byte array of pic if has one
     * @param visible not used
     * @param type with pic or without pic
     */
    public void update(String content,byte[] pic, int visible, int type) {
        Call<WeiboPojo> call = null;
        if (apiStores == null) {
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores.class);
        }
        switch (type) {
            case WEIBO_UPDATE_NO_PIC:
                call = apiStores.createWeibo(token, content, 0);
                break;
            case WEIBO_UPLOAD_WITH_PIC:
                RequestBody token_body = MultipartBody.create(MediaType.parse("text/plain"),token);
                RequestBody content_body = MultipartBody.create(MediaType.parse("text/plain"),content);
                RequestBody visible_body = MultipartBody.create(MediaType.parse("text/plain"),"0");
                RequestBody pic_body = MultipartBody.create(MediaType.parse("image/png"),pic);
                call = apiStores.createWeibo(token_body, content_body, visible_body, pic_body);
                break;
            default:
                break;
        }
        if (call != null) {
            call.enqueue(new Callback<WeiboPojo>() {
                @Override
                public void onResponse(Response<WeiboPojo> response) {
                    if (response.errorBody() != null) {
                        try {
                            Toast.makeText(MainActivity.this, response.errorBody().string(), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * get weibo of your following
     * @param updateType get the first 20 weibos or the following
     * @param user_id id of app user
     */
    public void getWeiboData(final int updateType, final String user_id) {
        Call<WeiboPojo> call = null;
        if(apiStores == null){
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores.class);
        }
        switch (updateType){
            case WEIBO_GET_NEW:
                call = apiStores.getWeiboData(MainActivity.token);
                isLastOne = false;
                break;
            case WEIBO_GET_NEXT:
                if(!isLastOne) {
                    call = apiStores.getWeiboData(MainActivity.token, weibo_max_id);
                }
                break;
            case WEIBO_GET_USER_NEW:
                call = apiStores.getUserWeiboData(MainActivity.token, user_id);
                isUserLastOne = false;
                break;
            default:
                break;
        }
        if(call!=null) {
            call.enqueue(new Callback<WeiboPojo>() {
                @Override
                public void onResponse(Response<WeiboPojo> response) {
                    currentWeibo = response.body();
                    if (currentWeibo == null) {
                        Toast.makeText(MainActivity.this, "请先验证微博用户", Toast.LENGTH_SHORT).show();
                    } else {
                        switch (updateType){
                            case WEIBO_GET_NEXT:
                            case WEIBO_GET_NEW:
                                if (weibo_max_id != null && !weibo_max_id.equals("")) {
                                    long last_id = Long.parseLong(currentWeibo.getMax_id());
                                    long init_id = Long.parseLong(weibo_max_id);
                                    if (init_id < last_id) {
                                        Toast.makeText(MainActivity.this, String.valueOf(last_id) + "  " + String.valueOf(init_id), Toast.LENGTH_SHORT).show();
                                        isLastOne = true;
                                        return;
                                    }
                                }
                                weibo_max_id = currentWeibo.getMax_id();
                                Toast.makeText(MainActivity.this, String.valueOf(response.body().getStatuses().length), Toast.LENGTH_SHORT).show();
                                onWeiboPOJOUpdated.onUpdate(currentWeibo, updateType);
                                break;
                            case WEIBO_GET_USER_NEW:
                            case WEIBO_GET_USER_NEXT:
                                if (weibo_user_max_id != null && !weibo_user_max_id.equals("")) {
                                    long last_id = Long.parseLong(currentWeibo.getMax_id());
                                    long init_id = Long.parseLong(weibo_max_id);
                                    if (init_id < last_id) {
                                        Toast.makeText(MainActivity.this, String.valueOf(last_id) + "  " + String.valueOf(init_id), Toast.LENGTH_SHORT).show();
                                        isUserLastOne = true;
                                        return;
                                    }
                                }
                                weibo_user_max_id = currentWeibo.getMax_id();
                                Toast.makeText(MainActivity.this, String.valueOf(response.body().getStatuses().length), Toast.LENGTH_SHORT).show();
                                onUserUpdatedListener.onUpdate(currentWeibo, updateType);
                                break;
                             default:
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    //do nothing
                    Log.e("", t.getMessage());
                }
            });
        }
    }


    /**
     * to get the uti of selected pic
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 如果获取成功，resultCode为-1
        if (requestCode == 0 && resultCode == -1) {
            // 获取原图的Uri，它是一个内容提供者
            Uri uri = data.getData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public interface onPost{
        void post(String content);
        void post(String content, byte[] pic);
    }

    class ClearTask extends AsyncTask<Object,Object,Object>{

        @Override
        protected Object doInBackground(Object... params) {
            if(WeiwaApplication.CacheCategory.exists()){
                File[] files = WeiwaApplication.CacheCategory.listFiles();
                for(File file : files){
                    file.delete();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            Toast.makeText(MainActivity.this,"已经清除完毕",Toast.LENGTH_SHORT).show();
        }
    }
}
