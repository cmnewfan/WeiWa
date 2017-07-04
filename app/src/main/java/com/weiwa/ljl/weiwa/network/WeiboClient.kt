package com.weiwa.ljl.weiwa.network

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.*


/**
 * Created by hzfd on 2017/1/17.
 */
object WeiboClient {
    internal val mRetrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl("https://api.weibo.com/2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun retrofit(): Retrofit {
        return mRetrofit
    }

    interface ApiStores {
        @GET("statuses/friends_timeline.json")
        fun getWeiboData(@Query("access_token") token: String): Call<WeiboPojo>

        @GET("statuses/friends_timeline.json")
        fun getWeiboData(@Query("access_token") token: String, @Query("max_id") max_id: String): Call<WeiboPojo>

        @GET("comments/show.json")
        fun getWeiboCommentData(@Query("access_token") token: String, @Query("id") id: String): Call<WeiboCommentPojo>

        @GET("comments/show.json")
        fun getWeiboCommentData(@Query("access_token") token: String, @Query("id") id: String, @Query("max_id") max_id: String): Call<WeiboCommentPojo>

        @FormUrlEncoded
        @POST("comments/create.json")
        fun createComment(@Field("access_token") token: String, @Field("id") id: String, @Field("comment") content: String): Call<WeiboCommentPojo>

        @FormUrlEncoded
        @POST("statuses/repost.json")
        fun createRepost(@Field("access_token") token: String, @Field("id") id: String, @Field("status") content: String): Call<WeiboPojo.Statuses>

        @GET("statuses/timeline_batch.json")
        fun getUserWeiboData(@Query("access_token") token: String, @Query("screen_name") uid: String): Call<WeiboPojo>

        @GET("statuses/timeline_batch.json")
        fun getUserWeiboData(@Query("access_token") token: String, @Query("uid") uid: String, @Query("max_id") max_id: String): Call<WeiboPojo>

        @FormUrlEncoded
        @POST("statuses/update.json")
        fun createWeibo(@Field("access_token") token: String, @Field("status") content: String, @Field("visible") type: Int): Call<WeiboPojo>

        @Multipart
        @POST("statuses/upload.json")
        fun createWeibo(@Part("access_token") token: RequestBody, @Part("status") content: RequestBody, @Part("visible") type: RequestBody, @Part("pic\"; filename=\"image\" ") pic: RequestBody): Call<WeiboPojo>

        @GET("/users/show.json")
        fun getUserinfo(@Query("access_token") token: String, @Query("uid") uid: Int): Call<WeiboPojo.User>

        @GET("/statuses/user_timeline.json")
        fun getUserWeibo(@Query("access_token") token: String): Call<WeiboPojo>

        @GET("/statuses/mentions.json")
        fun getWeiboAtMe(@Query("access_token") token: String): Call<WeiboPojo>
    }
}
