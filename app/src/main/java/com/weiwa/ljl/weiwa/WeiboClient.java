package com.weiwa.ljl.weiwa;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboCommentPojo;
import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by hzfd on 2017/1/17.
 */
public class WeiboClient {
    static Retrofit mRetrofit;
    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api.weibo.com/2/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    public interface ApiStores {
        @GET("statuses/friends_timeline.json")
        Call<WeiboPojo> getWeiboData(@Query("access_token") String token);
        @GET("statuses/friends_timeline.json")
        Call<WeiboPojo> getWeiboData(@Query("access_token") String token,@Query("max_id") String max_id);
        @GET("comments/show.json")
        Call<WeiboCommentPojo> getWeiboCommentData(@Query("access_token") String token,@Query("id") String id);
        @GET("comments/show.json")
        Call<WeiboCommentPojo> getWeiboCommentData(@Query("access_token") String token,@Query("id") String id,@Query("max_id") String max_id);
        @FormUrlEncoded
        @POST("comments/create.json")
        Call<WeiboCommentPojo> createComment(@Field("access_token") String token, @Field("id") String id, @Field("comment") String content);
        @FormUrlEncoded
        @POST("statuses/repost.json")
        Call<WeiboPojo.Statuses> createRepost(@Field("access_token") String token, @Field("id") String id, @Field("status") String content);
        @GET("statuses/user_timeline.json")
        Call<WeiboPojo> getUserWeiboData(@Query("access_token") String token, @Query("screen_name") String uid);
        @GET("statuses/user_timeline.json")
        Call<WeiboPojo> getUserWeiboData(@Query("access_token") String token,@Query("uid") String uid, @Query("max_id") String max_id);
        @FormUrlEncoded
        @POST("statuses/update.json")
        Call<WeiboPojo> createWeibo(@Field("access_token") String token, @Field("status") String content, @Field("visible") int type);
        @Multipart
        @POST("statuses/upload.json")
        Call<WeiboPojo> createWeibo(@Part("access_token") RequestBody token, @Part("status") RequestBody content, @Part("visible") RequestBody type, @Part("pic\"; filename=\"image\" ") RequestBody pic);
    }
}
