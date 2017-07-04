package com.weiwa.ljl.weiwa.network

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable

/**
 * Created by hzfd on 2017/2/7.
 */
class WeiboCommentPojo protected constructor(`in`: Parcel) : Parcelable {
    var since_id: String? = null

    var next_cursor: String? = null

    var max_id: String? = null

    var status: WeiboPojo.Statuses? = null

    var marks: Array<String>? = null

    var previous_cursor: String? = null

    var total_number: String? = null

    var comments: Array<Comments>? = null

    var hasvisible: String? = null

    init {
        val bundle = `in`?.readBundle()
        if (bundle?.getString("RetweetedPortrait", "") == "") {
            status!!.getUser().profile_image_url = bundle.getString("RetweetPortrait")
            status!!.created_at = bundle.getString("RetweetDate")
            status!!.getUser().name = bundle.getString("RetweetUser")
            status!!.text = bundle.getString("RetweetText")
            status!!.comments_count = bundle.getString("CommentCount")
            status!!.reposts_count = bundle.getString("RepostCount")
            status!!.pic_urls = arrayOfNulls<WeiboPojo.Pic_urls>(bundle.getStringArray("RetweetedPic")!!.size)
            setPic_urls(bundle.getStringArray("RetweetedPic"), status!!.pic_urls)
            setCommentContents(bundle)
        } else {
            status!!.retweeted_status!!.created_at = bundle!!.getString("RetweetedDate")
            status!!.retweeted_status!!.user!!.profile_image_url = "RetweetedPortrait"
            status!!.retweeted_status!!.user!!.name = "RetweetedUser"
            status!!.retweeted_status!!.text = "RetweetedText"
            status!!.getUser().profile_image_url = bundle.getString("RetweetPortrait")
            status!!.created_at = bundle.getString("RetweetDate")
            status!!.getUser().name = bundle.getString("RetweetUser")
            status!!.text = bundle.getString("RetweetText")
            status!!.comments_count = bundle.getString("CommentCount")
            status!!.reposts_count = bundle.getString("RepostCount")
            status!!.retweeted_status!!.pic_urls = arrayOfNulls<WeiboPojo.Pic_urls>(bundle.getStringArray("RetweetedPic")!!.size)
            setPic_urls(bundle.getStringArray("RetweetedPic"), status!!.retweeted_status!!.pic_urls)
            setCommentContents(bundle)
        }
    }

    private fun getPic_urls(pic_urls: Array<WeiboPojo.Pic_urls?>?): Array<String>? {
        if (pic_urls != null && pic_urls.size > 0) {
            return pic_urls.map { it.toString() }.toTypedArray()
        } else {
            return null
        }
    }

    private val commentContents: Array<String>?
        get() {
            if (comments != null && comments!!.size > 0) {
                return comments!!.map { it.text.toString() }.toTypedArray()
            } else {
                return null
            }
        }

    private fun setCommentContents(bundle: Bundle) {
        comments = arrayOfNulls<Comments>(bundle.getStringArray("CommentPortrait").size).map { Comments() }.toTypedArray()
        for (i in comments!!.indices) {
            comments!![i].text = bundle.getStringArray("CommentPortrait")!![i]
            comments!![i].user!!.name = bundle.getStringArray("CommentUser")!![i]
            comments!![i].created_at = bundle.getStringArray("CommentDate")!![i]
            comments!![i].user!!.profile_image_url = bundle.getStringArray("CommentPortrait")!![i]
        }
    }

    private var commentUsers: Array<String?>?
        get() {
            if (comments != null && comments!!.size > 0) {
                return comments!!.map { it.user!!.name }.toTypedArray()
            } else {
                return null
            }
        }
        set(contents) {
            comments = arrayOfNulls<Comments>(contents!!.size).map { Comments() }.toTypedArray()
            for (i in comments!!.indices) {
                comments!![i].text = contents[i]
            }
        }

    private val commentDates: Array<String>?
        get() {
            if (comments != null && comments!!.size > 0) {
                return comments!!.map { it.created_at!! }.toTypedArray()
            } else {
                return null
            }
        }

    private val commentPics: Array<String?>?
        get() {
            if (comments != null && comments!!.size > 0) {
                return comments!!.map { it.user!!.profile_image_url }.toTypedArray()
            } else {
                return null
            }
        }

    private fun setPic_urls(urls: Array<String>, pic_urls: Array<WeiboPojo.Pic_urls?>?) {
        for (i in urls.indices) {
            pic_urls!![i]!!.thumbnail_pic = urls[i]
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        val bundle = Bundle()
        if (status!!.retweeted_status != null) {
            bundle.putString("RetweetedPortrait", status!!.retweeted_status!!.user!!.profile_image_url)
            bundle.putString("RetweetedDate", status!!.retweeted_status!!.created_at)
            bundle.putString("RetweetedUser", status!!.retweeted_status!!.user!!.name)
            bundle.putString("RetweetedText", status!!.retweeted_status!!.text)
            bundle.putStringArray("RetweetedPic", getPic_urls(status!!.retweeted_status!!.pic_urls))
            bundle.putString("RetweetPortrait", status!!.getUser().profile_image_url)
            bundle.putString("RetweetDate", status!!.created_at)
            bundle.putString("RetweetUser", status!!.getUser().name)
            bundle.putString("RetweetText", status!!.text)
            bundle.putString("CommentCount", status!!.comments_count)
            bundle.putString("RepostCount", status!!.reposts_count)
        } else {
            bundle.putString("RetweetPortrait", status!!.getUser().profile_image_url)
            bundle.putString("RetweetDate", status!!.created_at)
            bundle.putString("RetweetUser", status!!.getUser().name)
            bundle.putString("RetweetText", status!!.text)
            bundle.putStringArray("RetweetPic", getPic_urls(status!!.pic_urls))
            bundle.putString("CommentCount", status!!.comments_count)
            bundle.putString("RepostCount", status!!.reposts_count)
        }
        bundle.putStringArray("CommentPortrait", commentPics)
        bundle.putStringArray("CommentDate", commentDates)
        bundle.putStringArray("CommentUser", commentUsers)
        bundle.putStringArray("CommentText", commentContents)
        dest.writeBundle(bundle)
    }

    inner class Comments {
        var rootid: String? = null

        var floor_number: String? = null

        var text: String? = null

        var source_allowclick: String? = null

        var reply_comment: Reply_comment? = null

        var status: WeiboPojo.Statuses? = null

        var reply_original_text: String? = null

        var source_type: String? = null

        var id: String? = null

        var source: String? = null

        var idstr: String? = null

        var created_at: String? = null

        var mid: String? = null

        var user: WeiboPojo.User? = null

    }

    class Reply_comment {
        var id: String? = null

        var text: String? = null

        var floor_number: String? = null

        var rootid: String? = null

        var source_allowclick: String? = null

        var idstr: String? = null

        var source: String? = null

        var source_type: String? = null

        var created_at: String? = null

        var mid: String? = null

        var user: WeiboPojo.User? = null
    }

    class Visible {
        var type: String? = null

        var list_id: String? = null
    }

    companion object {

        val CREATOR: Parcelable.Creator<WeiboCommentPojo> = object : Parcelable.Creator<WeiboCommentPojo> {
            override fun createFromParcel(`in`: Parcel): WeiboCommentPojo {
                return WeiboCommentPojo(`in`)
            }

            override fun newArray(size: Int): Array<WeiboCommentPojo?> {
                return arrayOfNulls<WeiboCommentPojo>(size)
            }
        }
    }
}
