package com.weiwa.ljl.weiwa;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.weiwa.ljl.weiwa.WeiboModel.WeiboCommentPojo;
import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Stack;

import okhttp3.HttpUrl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private AuthInfo mAuthInfo;
    private SsoHandler mSsoHandler;
    private WeiWaAuthListener mAuthListener;
    private FragmentManager fragmentManager;
    private int back_count=0;
    private String weibo_max_id;
    private boolean isLastOne=false;
    private String weibo_since_id;
    private WeiboPojo currentWeibo;
    private onWeiboUpdatedListener onWeiboPOJOUpdated;
    private MainFragment mMainFragment;
    private FloatingActionButton fab;
    private WeiboClient.ApiStores apiStores;
    private Stack<Boolean> fabStatus = new Stack<>();
    private final static int WEIBO_GET_NEW = 0;
    private final static int WEIBO_GET_NEXT = 1;
    private final static int WEIBO_GET_COMMENT = 2;
    private final static int WEIBO_COMMENT = 3;
    private final static int WEIBO_REPOST = 4;
    public static String token;
    public static final String APP_KEY = "1492233661"; // 应用的APP_KEY
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";// 应用的回调页
    public static final String SCOPE = // 应用申请的高级权限
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weibo_max_id = null;
                getWeiboData(WEIBO_GET_NEW);
            }
        });
        //Auth for weibo
        initAuth();
        //init Glide
        new GlideBuilder(this)
                .setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
        mMainFragment = new MainFragment();
        setDefaultFragment();
        getWeiboData(WEIBO_GET_NEW);
    }

    public void setOnWeiboPOJOUpdated(onWeiboUpdatedListener listener){
        this.onWeiboPOJOUpdated = listener;
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

    public void setFragment(Fragment currentFragment, Fragment fragment) {
        fab.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.hide(currentFragment);
        fragmentTransaction.add(R.id.main_fragment,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fabStatus.push(false);
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
                if(WeatherApplication.CacheCategory.exists()){
                    WeatherApplication.CacheCategory.delete();
                    Toast.makeText(this,"缓存已清除完毕",Toast.LENGTH_SHORT).show();
                }
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
                fragmentManager.popBackStack();
                if(fabStatus.size()>1){
                    fabStatus.pop();
                    if(fabStatus.lastElement()){
                        fab.setVisibility(View.VISIBLE);
                    }else{
                        fab.setVisibility(View.GONE);
                    }
                }
            }
        }
        return false;
    }

    public void getWeiboData(){
        getWeiboData(WEIBO_GET_NEXT);
    }

    public void getWeiboComment(String id){
        getWeiboComment(WEIBO_GET_COMMENT, id);
    }

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
                Log.e("",t.getMessage());
            }
        });
    }

    public void createRepost(String id, String content){
        Call<WeiboPojo.Statuses> call = null;
        if(apiStores == null){
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores.class);
        }
        call = apiStores.createRepost(MainActivity.token,id,content);
        call.enqueue(new Callback<WeiboPojo.Statuses>() {
            @Override
            public void onResponse(Response<WeiboPojo.Statuses> response) {
                WeiboPojo.Statuses comments = response.body();
                Toast.makeText(MainActivity.this,"Repost Success",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Throwable t) {
                //do nothing
                Log.e("",t.getMessage());
            }
        });
    }

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

    private void getWeiboData(final int updateType) {
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
}
