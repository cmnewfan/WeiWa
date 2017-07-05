package com.weiwa.ljl.weiwa.network

import android.net.Uri

/**
 * Created by hzfd on 2017/1/17.
 */
class WeiboPojo {
    var uve_blank: String? = null

    var since_id: String? = null

    var next_cursor: String? = null

    var max_id: String? = null

    var interval: String? = null

    var has_unread: String? = null

    var previous_cursor: String? = null

    var total_number: String? = null

    var ad: Array<String>? = null

    var advertises: Array<String>? = null

    var statuses: Array<Statuses>? = null

    var hasvisible: String? = null

    override fun toString(): String {
        return "ClassPojo [uve_blank = $uve_blank, since_id = $since_id, next_cursor = $next_cursor, max_id = $max_id, interval = $interval, has_unread = $has_unread, previous_cursor = $previous_cursor, total_number = $total_number, ad = $ad, advertises = $advertises, statuses = $statuses, hasvisible = $hasvisible]"
    }

    inner class Statuses {
        var pic_urls: Array<Pic_urls?>? = null

        var visible: Visible? = null

        var textLength: String? = null

        var attitudes_count: String? = null

        var darwin_tags: Array<String>? = null

        var retweeted_status: Retweeted_status? = null
            get() {
                if (field != null) {
                    return field
                } else {
                    return Retweeted_status()
                }
            }

        var isLongText: String? = null

        var in_reply_to_screen_name: String? = null

        var mlevel: String? = null

        var source_type: String? = null

        var truncated: String? = null

        var userType: String? = null

        var id: String? = null

        var thumbnail_pic: String? = null

        var idstr: String? = null

        var original_pic: String? = null

        var picStatus: String? = null

        var in_reply_to_status_id: String? = null

        var created_at: String? = null

        var reposts_count: String? = null

        var positive_recom_flag: String? = null

        var gif_ids: String? = null

        var hasActionTypeCard: String? = null

        var text: String? = null

        var comments_count: String? = null

        var text_tag_tips: Array<String>? = null

        var source_allowclick: String? = null

        var geo: Geo? = null

        var bmiddle_pic: String? = null

        var hot_weibo_tags: Array<String>? = null

        var source: String? = null

        var rid: String? = null

        var favorited: String? = null

        var biz_feature: String? = null

        var in_reply_to_user_id: String? = null

        var mid: String? = null

        var annotations: Array<Annotations>? = null

        var is_show_bulletin: String? = null

        var user: User? = null
            get() {
                if (field != null) {
                    return field
                } else {
                    return User()
                }
            }

        fun convertToUris(): Array<Uri>? {
            if (this.pic_urls != null && this.pic_urls!!.isNotEmpty()) {
                val tUris = arrayOfNulls<Uri>(this.pic_urls!!.size)
                for (i in 0..this.pic_urls!!.size - 1) {
                    tUris[i] = this.pic_urls!![i]!!.convertToUri()
                }
                return pic_urls!!.map { it!!.convertToUri() }.toTypedArray()
            } else {
                return null
            }
        }

        override fun toString(): String {
            return "ClassPojo [pic_urls = $pic_urls, visible = $visible, textLength = $textLength, attitudes_count = $attitudes_count, darwin_tags = $darwin_tags, isLongText = $isLongText, in_reply_to_screen_name = $in_reply_to_screen_name, mlevel = $mlevel, source_type = $source_type, truncated = $truncated, userType = $userType, id = $id, thumbnail_pic = $thumbnail_pic, idstr = $idstr, original_pic = $original_pic, picStatus = $picStatus, in_reply_to_status_id = $in_reply_to_status_id, created_at = $created_at, reposts_count = $reposts_count, positive_recom_flag = $positive_recom_flag, gif_ids = $gif_ids, hasActionTypeCard = $hasActionTypeCard, text = $text, comments_count = $comments_count, text_tag_tips = $text_tag_tips, source_allowclick = $source_allowclick, geo = $geo, bmiddle_pic = $bmiddle_pic, hot_weibo_tags = $hot_weibo_tags, source = $source, rid = $rid, favorited = $favorited, biz_feature = $biz_feature, in_reply_to_user_id = $in_reply_to_user_id, mid = $mid, annotations = $annotations, is_show_bulletin = $is_show_bulletin, user = $user]"
        }
    }

    inner class User {
        var block_app: String? = null

        var location: String? = null

        var remark: String? = null

        var verified_contact_email: String? = null

        var verified_reason: String? = null

        var statuses_count: String? = null

        var city: String? = null

        var favourites_count: String? = null

        var idstr: String? = null

        var description: String? = null

        var verified: String? = null

        var province: String? = null

        var verified_contact_name: String? = null

        var gender: String? = null

        var weihao: String? = null

        var cover_image: String? = null

        var verified_reason_modified: String? = null

        var insecurity: Insecurity? = null

        var mbrank: String? = null

        var url: String? = null

        var verified_level: String? = null

        var cover_image_phone: String? = null

        var verified_state: String? = null

        var friends_count: String? = null

        var profile_image_url: String? = null

        var follow_me: String? = null

        var ptype: String? = null

        var verified_source_url: String? = null

        var verified_type: String? = null

        var lang: String? = null

        var verified_source: String? = null

        var verified_contact_mobile: String? = null

        var credit_score: String? = null

        var id: String? = null

        var verified_trade: String? = null

        var verified_type_ext: String? = null

        var following: String? = null

        var name: String? = null

        var domain: String? = null

        var created_at: String? = null

        var user_ability: String? = null

        var followers_count: String? = null

        var online_status: String? = null

        var profile_url: String? = null

        var bi_followers_count: String? = null

        var geo_enabled: String? = null

        var star: String? = null

        var urank: String? = null

        var allow_all_comment: String? = null

        var avatar_hd: String? = null

        var allow_all_act_msg: String? = null

        var avatar_large: String? = null

        var pagefriends_count: String? = null

        var verified_reason_url: String? = null

        var mbtype: String? = null

        var screen_name: String? = null

        var block_word: String? = null

    }

    class Visible {
        var type: String? = null

        var list_id: String? = null
    }

    class Insecurity {
        var sexual_content: String? = null
    }

    class Pic_urls {
        var thumbnail_pic: String? = null
        fun convertToUri(): Uri {
            var uri: String = thumbnail_pic!!
            uri = thumbnail_pic!!.replace("thumbnail", "large")
            return Uri.parse(uri)
        }
    }

    class Annotations {
        var place: Place? = null

        var client_mblogid: String? = null
    }

    class Place {
        var poiid: String? = null

        var title: String? = null

        var lon: String? = null

        var type: String? = null

        var lat: String? = null
    }

    class Geo {

        var longitude: String? = null
        var latitude: String? = null
        var city: String? = null
        var province: String? = null
        var city_name: String? = null
        var province_name: String? = null
        var address: String? = null
        var pinyin: String? = null
        var more: String? = null
    }

    inner class Retweeted_status {
        var pic_urls: Array<Pic_urls?>? = null

        var visible: Visible? = null

        var textLength: String? = null

        var attitudes_count: String? = null

        var darwin_tags: Array<String>? = null

        var isLongText: String? = null

        var in_reply_to_screen_name: String? = null

        var mlevel: String? = null

        var source_type: String? = null

        var truncated: String? = null

        var userType: String? = null

        var id: String? = null

        var thumbnail_pic: String? = null

        var idstr: String? = null

        var original_pic: String? = null

        var in_reply_to_status_id: String? = null

        var created_at: String? = null

        var reposts_count: String? = null

        var positive_recom_flag: String? = null

        var gif_ids: String? = null

        var hasActionTypeCard: String? = null

        var text: String? = null

        var comments_count: String? = null

        var text_tag_tips: Array<String>? = null

        var source_allowclick: String? = null

        var geo: Geo? = null

        var bmiddle_pic: String? = null

        var hot_weibo_tags: Array<String>? = null

        var source: String? = null

        var favorited: String? = null

        var biz_feature: String? = null

        var in_reply_to_user_id: String? = null

        var mid: String? = null

        var is_show_bulletin: String? = null

        var user: User? = null
            get() {
                if (field != null) {
                    return field
                } else {
                    return User()
                }
            }

        fun convertToUris(): Array<Uri>? {
            if (this.pic_urls != null && this.pic_urls!!.isNotEmpty()) {
                return pic_urls!!.map { it!!.convertToUri() }.toTypedArray()
            } else {
                return null
            }
        }
    }
}
