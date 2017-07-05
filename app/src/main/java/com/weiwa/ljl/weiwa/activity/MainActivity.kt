package com.weiwa.ljl.weiwa.activity

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.DecodeFormat
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.auth.sso.SsoHandler
import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.WeiwaApplication
import com.weiwa.ljl.weiwa.fragment.EditFragment
import com.weiwa.ljl.weiwa.fragment.MainFragment
import com.weiwa.ljl.weiwa.fragment.UserWeiboFragment
import com.weiwa.ljl.weiwa.listener.OnUserUpdatedListener
import com.weiwa.ljl.weiwa.listener.onUserWeiboListener
import com.weiwa.ljl.weiwa.listener.onWeiboUpdatedListener
import com.weiwa.ljl.weiwa.network.WeiWaAuthListener
import com.weiwa.ljl.weiwa.network.WeiboClient
import com.weiwa.ljl.weiwa.network.WeiboCommentPojo
import com.weiwa.ljl.weiwa.network.WeiboPojo
import com.weiwa.ljl.weiwa.view.CirclePortraitView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import okhttp3.MediaType
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mAuthInfo: AuthInfo? = null
    private var mSsoHandler: SsoHandler? = null
    private var mAuthListener: WeiWaAuthListener? = null
    private var back_count = 0
    private var backLastClick = 0.0.toLong()
    private var mRunning = false
    private var weibo_max_id: String? = null
    private var weibo_user_max_id: String? = null
    private var isLastOne = false
    private var mLastClick = 0.0.toLong()
    private var isUserLastOne = false
    private var mScreenName: TextView? = null
    private var mDescription: TextView? = null
    private var mLocation: TextView? = null
    private var currentWeibo: WeiboPojo? = null
    private val userWeibo: WeiboPojo? = null
    private var lastWeibo: WeiboPojo? = null
    private var workMode: Int = 0
    private var onWeiboPOJOUpdated: onWeiboUpdatedListener? = null
    private var onUserWeiboListener: onUserWeiboListener? = null
    private var onUserUpdatedListener: OnUserUpdatedListener? = null
    private var mMainFragment: MainFragment? = null
    private var apiStores: WeiboClient.ApiStores? = null
    private val fabStatus = Stack<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initDrawerLayout()
        setSupportActionBar(toolbar)
        val animation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim)
        fab.setOnClickListener {
            //get the latest data
            fab.startAnimation(animation)
            weibo_max_id = null
            getWeiboData(WEIBO_GET_NEW, null)
        }
        //init Image Button
        edit.setOnClickListener {
            val editFragment = EditFragment()
            add.visibility = View.VISIBLE
            edit.visibility = View.GONE
            editFragment.setOnPost(object : onPost {
                override fun post(content: String) {
                    update(content, null, 0, WEIBO_UPDATE_NO_PIC)
                }

                override fun post(content: String, pic: ByteArray) {
                    update(content, pic, 0, WEIBO_UPLOAD_WITH_PIC)
                }
            })
            setFragment(editFragment)
        }
        //Auth for weibo
        initAuth()
        //init Glide
        GlideBuilder(this)
                .setDecodeFormat(DecodeFormat.PREFER_ARGB_8888)
        mMainFragment = MainFragment()
        setDefaultFragment()
        fab.startAnimation(animation)
        getUserinfo(MainActivity.uid)
        getWeiboData(WEIBO_GET_NEW, null)
    }

    private fun initDrawerLayout() {
        portrait.setOnClickListener {
            if (!drawer_layout.isDrawerOpen(navigation_view)) {
                drawer_layout.openDrawer(Gravity.START)
            } else {
                drawer_layout.closeDrawers()
            }
        }
        navigation_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.my -> {
                    getWeiboData(WEIBO_GET_USER, null)
                    drawer_layout.closeDrawers()
                }
                R.id.atme -> {
                    getWeiboData(WEIBO_GET_ATME, null)
                    drawer_layout.closeDrawers()
                }
                else -> drawer_layout.closeDrawers()
            }
            true
        }
        mScreenName = navigation_view.getHeaderView(0).findViewById(R.id.screen_name) as TextView
        mLocation = navigation_view.getHeaderView(0).findViewById(R.id.location) as TextView
        mDescription = navigation_view.getHeaderView(0).findViewById(R.id.description) as TextView
    }

    fun setOnWeiboPOJOUpdated(listener: onWeiboUpdatedListener) {
        this.onWeiboPOJOUpdated = listener
    }

    fun setOnUserUpdatedListener(listener: OnUserUpdatedListener) {
        this.onUserUpdatedListener = listener
    }

    fun setOnUserWeiboListener(listener: onUserWeiboListener) {
        this.onUserWeiboListener = listener
        if (userWeibo != null) {
            this.onUserWeiboListener!!.onUpdate(userWeibo, WEIBO_GET_USER)
        }
    }

    fun setOnAddButtonClickListener(listener: View.OnClickListener) {
        add.setOnClickListener(listener)
    }

    fun popBack() {
        if (fragmentManager.findFragmentById(R.id.main_fragment) is UserWeiboFragment) {
            this.onUserWeiboListener = null
        }
        fragmentManager.popBackStack()
        add.visibility = View.GONE
        edit.visibility = View.VISIBLE
        if (fragmentManager.findFragmentById(R.id.main_fragment).isHidden) {
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentById(R.id.main_fragment)).commit()
        }
        if (fabStatus.size > 1) {
            fabStatus.pop()
            if (fabStatus.lastElement()) {
                fab.visibility = View.VISIBLE
            } else {
                fab.visibility = View.GONE
            }
        }
    }

    private fun initAuth() {
        val sharedPreferences = getSharedPreferences("weibo_preferences", Activity.MODE_PRIVATE)
        MainActivity.token = sharedPreferences.getString("Token", "")
        MainActivity.uid = sharedPreferences.getString("Uid", "")
        if (MainActivity.token == "" || MainActivity.uid == "") {
            mAuthInfo = AuthInfo(this, APP_KEY, REDIRECT_URL, SCOPE)
            mSsoHandler = SsoHandler(this, mAuthInfo!!)
            mAuthListener = WeiWaAuthListener(this)
            mSsoHandler!!.authorizeWeb(mAuthListener)
        }
    }

    private fun setDefaultFragment() {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main_fragment, mMainFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        fabStatus.push(true)
    }

    fun setFragment(fragment: Fragment) {
        when (fragment.javaClass.canonicalName) {
            "com.weiwa.ljl.weiwa.fragment.UserViewFragment" -> {
                fab.visibility = View.VISIBLE
                fabStatus.push(true)
                add.visibility = View.GONE
                edit.visibility = View.VISIBLE
            }
            "com.weiwa.ljl.weiwa.fragment.EditFragment" -> {
                add.visibility = View.VISIBLE
                edit.visibility = View.GONE
                fab.visibility = View.GONE
                fabStatus.push(false)
            }
            "com.weiwa.ljl.weiwa.fragment.WeiboCommentFragment", "com.weiwa.ljl.weiwa.fragment.WeiboRetweetCommentFragment" -> {
                if (fragmentManager.findFragmentById(R.id.main_fragment) !is MainFragment) {
                    return
                }
                if (fragmentManager.findFragmentById(R.id.main_fragment) is UserWeiboFragment) {
                    return
                }
                fab.visibility = View.GONE
                fabStatus.push(false)
                add.visibility = View.GONE
                edit.visibility = View.VISIBLE
            }
            "com.weiwa.ljl.weiwa.fragment.UserWeiboFragment" -> {
                if (fragmentManager.findFragmentById(R.id.main_fragment) is UserWeiboFragment) {
                    return
                }
                fab.visibility = View.GONE
                fabStatus.push(false)
                add.visibility = View.GONE
                edit.visibility = View.VISIBLE
            }
            else -> {
                fab!!.visibility = View.GONE
                fabStatus.push(false)
                add.visibility = View.GONE
                edit.visibility = View.VISIBLE
            }
        }
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.hide(fragmentManager.findFragmentById(R.id.main_fragment))
        fragmentTransaction.add(R.id.main_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // weibo_drawable you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            R.id.action_settings -> {
                initAuth()
                return true
            }
            R.id.clear_cache -> {
                Toast.makeText(this, "开始清除缓存", Toast.LENGTH_SHORT).show()
                async(CommonPool) {
                    if (WeiwaApplication.CacheCategory.exists()) {
                        val files = WeiwaApplication.CacheCategory.listFiles()
                        for (file in files) {
                            file.delete()
                        }
                    }
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "已经清除完毕", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragmentManager.backStackEntryCount == 1) {
                if (workMode == WEIBO_GET_USER || workMode == WEIBO_GET_ATME) {
                    onWeiboPOJOUpdated!!.onUpdate(lastWeibo!!, WEIBO_GET_NEW)
                    workMode = WEIBO_GET_NEW
                    return true
                }
                fab.visibility = View.VISIBLE
                if (event.repeatCount == 0 && back_count == 0) {
                    back_count++
                    backLastClick = System.currentTimeMillis()
                    Toast.makeText(applicationContext, "再次点击后退键退出程序", Toast.LENGTH_SHORT).show()
                } else if (event.repeatCount == 0 && back_count == 1) {
                    if (System.currentTimeMillis() - backLastClick > 1000) {
                        backLastClick = System.currentTimeMillis()
                        Toast.makeText(applicationContext, "再次点击后退键退出程序", Toast.LENGTH_SHORT).show()
                    } else {
                        back_count--
                        val intent = Intent(applicationContext, SplashActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.putExtra("EXIT", true)
                        startActivity(intent)
                    }
                }
            } else {
                popBack()
            }
        }
        return false
    }

    private fun setToolbar(user: WeiboPojo.User) {
        mScreenName!!.text = user.screen_name
        mScreenName!!.paintFlags = mScreenName!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        mDescription!!.text = user.description
        mDescription!!.paintFlags = mDescription!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        mLocation!!.text = user.location
        mLocation!!.paintFlags = mLocation!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        portrait.addDownloadTask(user.profile_image_url!!, this@MainActivity, user)
        val circlePortraitView = navigation_view.getHeaderView(0).findViewById(R.id.portrait) as CirclePortraitView
        circlePortraitView.addDownloadTask(user.profile_image_url!!, this@MainActivity, user)
    }

    fun getWeiboComment(id: String) {
        if (mRunning) {
            return
        }
        if (System.currentTimeMillis() - mLastClick <= 1000) {
            return
        } else {
            mLastClick = System.currentTimeMillis()
            mRunning = true
            getWeiboComment(WEIBO_GET_COMMENT, id)
        }
    }

    private fun getUserinfo(uid: String) {
        var call: Call<WeiboPojo.User>? = null
        if (apiStores == null) {
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores::class.java)
        }
        call = apiStores!!.getUserinfo(token, Integer.parseInt(uid))
        call.enqueue(object : Callback<WeiboPojo.User> {
            override fun onResponse(response: Response<WeiboPojo.User>) {
                setToolbar(response.body())
            }

            override fun onFailure(t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * create comment of specified weibo(id)
     * @param id id of commented weibo
     * *
     * @param content content of comment
     */
    fun createComment(id: String, content: String) {
        var call: Call<WeiboCommentPojo>? = null
        if (apiStores == null) {
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores::class.java)
        }
        call = apiStores!!.createComment(MainActivity.token, id, content)
        call.enqueue(object : Callback<WeiboCommentPojo> {
            override fun onResponse(response: Response<WeiboCommentPojo>) {
                val comments = response.body()
                Toast.makeText(this@MainActivity, "Comment Success", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(t: Throwable) {
                //do nothing
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.e("", t.message)
            }
        })
    }

    /**
     * create repost of specified weibo
     * @param id id of reposted weibo
     * *
     * @param content content of repost
     */
    fun createRepost(id: String, content: String) {
        var call: Call<WeiboPojo.Statuses>? = null
        if (apiStores == null) {
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores::class.java)
        }
        call = apiStores!!.createRepost(MainActivity.token, id, content)
        call.enqueue(object : Callback<WeiboPojo.Statuses> {
            override fun onResponse(response: Response<WeiboPojo.Statuses>) {
                Toast.makeText(this@MainActivity, "Repost Success", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(t: Throwable) {
                //do nothing
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun stopAnimation() {
        fab.clearAnimation()
    }

    /**
     * get comments of specified weibo
     * @param updateType not used
     * *
     * @param id id of specified weibo
     */
    private fun getWeiboComment(updateType: Int, id: String) {
        var call: Call<WeiboCommentPojo>? = null
        if (apiStores == null) {
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores::class.java)
        }
        when (updateType) {
            WEIBO_GET_COMMENT -> call = apiStores!!.getWeiboCommentData(MainActivity.token, id)
            else -> {
            }
        }
        call!!.enqueue(object : Callback<WeiboCommentPojo> {
            override fun onResponse(response: Response<WeiboCommentPojo>) {
                val comments = response.body()
                Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                mRunning = false
                onWeiboPOJOUpdated!!.onComment(comments)
            }

            override fun onFailure(t: Throwable) {
                //do nothing
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * create new weibo
     * @param content content of new weibo
     * *
     * @param pic byte array of pic if has one
     * *
     * @param visible not used
     * *
     * @param type with pic or without pic
     */
    fun update(content: String, pic: ByteArray?, visible: Int, type: Int) {
        var call: Call<WeiboPojo>? = null
        if (apiStores == null) {
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores::class.java)
        }
        when (type) {
            WEIBO_UPDATE_NO_PIC -> call = apiStores!!.createWeibo(token, content, 0)
            WEIBO_UPLOAD_WITH_PIC -> {
                val token_body = MultipartBody.create(MediaType.parse("text/plain"), token)
                val content_body = MultipartBody.create(MediaType.parse("text/plain"), content)
                val visible_body = MultipartBody.create(MediaType.parse("text/plain"), "0")
                val pic_body = MultipartBody.create(MediaType.parse("image/png"), pic!!)
                call = apiStores!!.createWeibo(token_body, content_body, visible_body, pic_body)
            }
            else -> {
            }
        }
        if (call != null) {
            call.enqueue(object : Callback<WeiboPojo> {
                override fun onResponse(response: Response<WeiboPojo>) {
                    if (response.errorBody() != null) {
                        try {
                            Toast.makeText(this@MainActivity, response.errorBody().string(), Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    } else {
                        Toast.makeText(this@MainActivity, "发布成功", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    /**
     * get weibo of your following
     * @param updateType get the first 20 weibos or the following
     * *
     * @param user_id id of app user
     */
    @JvmOverloads fun getWeiboData(updateType: Int = WEIBO_GET_NEXT, user_id: String? = null) {
        var call: Call<WeiboPojo>? = null
        if (apiStores == null) {
            apiStores = WeiboClient.retrofit().create(WeiboClient.ApiStores::class.java)
        }
        when (updateType) {
            WEIBO_GET_NEW -> {
                call = apiStores!!.getWeiboData(MainActivity.token)
                isLastOne = false
            }
            WEIBO_GET_NEXT -> if (!isLastOne) {
                call = apiStores!!.getWeiboData(MainActivity.token, weibo_max_id!!)
            }
            WEIBO_GET_USER_NEW -> {
                call = apiStores!!.getUserWeiboData(MainActivity.token, user_id!!)
                isUserLastOne = false
            }
            WEIBO_GET_USER -> call = apiStores!!.getUserWeibo(MainActivity.token)
            WEIBO_GET_ATME -> call = apiStores!!.getWeiboAtMe(MainActivity.token)
            else -> {
            }
        }
        if (call != null) {
            call.enqueue(object : Callback<WeiboPojo> {
                override fun onResponse(response: Response<WeiboPojo>) {
                    workMode = updateType
                    currentWeibo = response.body()
                    if (currentWeibo == null) {
                        Toast.makeText(this@MainActivity, "请先验证微博用户", Toast.LENGTH_SHORT).show()
                    } else {
                        when (updateType) {
                            WEIBO_GET_NEXT, WEIBO_GET_NEW -> {
                                lastWeibo = currentWeibo
                                if (weibo_max_id != null && weibo_max_id != "") {
                                    val last_id = java.lang.Long.parseLong(currentWeibo!!.max_id)
                                    val init_id = java.lang.Long.parseLong(weibo_max_id)
                                    if (init_id < last_id) {
                                        Toast.makeText(this@MainActivity, last_id.toString() + "  " + init_id.toString(), Toast.LENGTH_SHORT).show()
                                        isLastOne = true
                                        return
                                    }
                                }
                                weibo_max_id = currentWeibo!!.max_id
                                Toast.makeText(this@MainActivity, response.body().statuses!!.size.toString(), Toast.LENGTH_SHORT).show()
                                onWeiboPOJOUpdated!!.onUpdate(currentWeibo!!, updateType)
                            }
                            WEIBO_GET_USER_NEW, WEIBO_GET_USER, WEIBO_GET_USER_NEXT, WEIBO_GET_ATME -> {
                                if (weibo_user_max_id != null && weibo_user_max_id != "") {
                                    val last_id = java.lang.Long.parseLong(currentWeibo!!.max_id)
                                    val init_id = java.lang.Long.parseLong(weibo_max_id)
                                    if (init_id < last_id) {
                                        Toast.makeText(this@MainActivity, last_id.toString() + "  " + init_id.toString(), Toast.LENGTH_SHORT).show()
                                        isUserLastOne = true
                                        return
                                    }
                                }
                                weibo_user_max_id = currentWeibo!!.max_id
                                Toast.makeText(this@MainActivity, response.body().statuses!!.size.toString(), Toast.LENGTH_SHORT).show()
                                onWeiboPOJOUpdated!!.onUpdate(currentWeibo!!, updateType)
                            }
                            else -> {
                            }
                        }
                    }
                }

                override fun onFailure(t: Throwable) {
                    //do nothing
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    /**
     * to get the uti of selected pic
     * @param requestCode
     * *
     * @param resultCode
     * *
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // 如果获取成功，resultCode为-1
        if (requestCode == 0 && resultCode == -1) {
            // 获取原图的Uri，它是一个内容提供者
            val uri = data.data
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    interface onPost {
        fun post(content: String)
        fun post(content: String, pic: ByteArray)
    }

    companion object {
        private val WEIBO_GET_NEW = 0
        private val WEIBO_GET_NEXT = 1
        private val WEIBO_GET_COMMENT = 2
        private val WEIBO_GET_USER_NEW = 3
        private val WEIBO_GET_USER_NEXT = 4
        private val WEIBO_UPDATE_NO_PIC = 5
        private val WEIBO_UPLOAD_WITH_PIC = 6
        private val WEIBO_GET_USER = 7
        private val WEIBO_GET_ATME = 8
        var token: String = ""
        var uid: String = ""
        val APP_KEY = "1492233661" // 应用的APP_KEY
        val REDIRECT_URL = "https://api.weibo.com/oauth2/default.html"// 应用的回调页
        val SCOPE = // 应用申请的高级权限
                "email,direct_messages_read,direct_messages_write," +
                        "friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
                        "follow_app_official_microblog," + "invitation_write,timeline_batch"
    }
}
