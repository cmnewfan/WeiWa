package com.weiwa.ljl.weiwa.WeiboModel;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hzfd on 2017/1/17.
 */
public class WeiboPojo {
    private String uve_blank;

    private String since_id;

    private String next_cursor;

    private String max_id;

    private String interval;

    private String has_unread;

    private String previous_cursor;

    private String total_number;

    private String[] ad;

    private String[] advertises;

    private Statuses[] statuses;

    private String hasvisible;

    public String getUve_blank ()
    {
        return uve_blank;
    }

    public void setUve_blank (String uve_blank)
    {
        this.uve_blank = uve_blank;
    }

    public String getSince_id ()
    {
        return since_id;
    }

    public void setSince_id (String since_id)
    {
        this.since_id = since_id;
    }

    public String getNext_cursor ()
    {
        return next_cursor;
    }

    public void setNext_cursor (String next_cursor)
    {
        this.next_cursor = next_cursor;
    }

    public String getMax_id ()
    {
        return max_id;
    }

    public void setMax_id (String max_id)
    {
        this.max_id = max_id;
    }

    public String getInterval ()
    {
        return interval;
    }

    public void setInterval (String interval)
    {
        this.interval = interval;
    }

    public String getHas_unread ()
    {
        return has_unread;
    }

    public void setHas_unread (String has_unread)
    {
        this.has_unread = has_unread;
    }

    public String getPrevious_cursor ()
    {
        return previous_cursor;
    }

    public void setPrevious_cursor (String previous_cursor)
    {
        this.previous_cursor = previous_cursor;
    }

    public String getTotal_number ()
    {
        return total_number;
    }

    public void setTotal_number (String total_number)
    {
        this.total_number = total_number;
    }

    public String[] getAd ()
    {
        return ad;
    }

    public void setAd (String[] ad)
    {
        this.ad = ad;
    }

    public String[] getAdvertises ()
    {
        return advertises;
    }

    public void setAdvertises (String[] advertises)
    {
        this.advertises = advertises;
    }

    public Statuses[] getStatuses ()
    {
        return statuses;
    }

    public void setStatuses (Statuses[] statuses)
    {
        this.statuses = statuses;
    }

    public String getHasvisible ()
    {
        return hasvisible;
    }

    public void setHasvisible (String hasvisible)
    {
        this.hasvisible = hasvisible;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [uve_blank = "+uve_blank+", since_id = "+since_id+", next_cursor = "+next_cursor+", max_id = "+max_id+", interval = "+interval+", has_unread = "+has_unread+", previous_cursor = "+previous_cursor+", total_number = "+total_number+", ad = "+ad+", advertises = "+advertises+", statuses = "+statuses+", hasvisible = "+hasvisible+"]";
    }

    public class Statuses{
        private Pic_urls[] pic_urls;

        private Visible visible;

        private String textLength;

        private String attitudes_count;

        private String[] darwin_tags;

        public Retweeted_status getRetweeted_status() {
            if(retweeted_status!=null) {
                return retweeted_status;
            }else{
                return new Retweeted_status();
            }
        }

        public void setRetweeted_status(Retweeted_status retweeted_status) {
            this.retweeted_status = retweeted_status;
        }

        private Retweeted_status retweeted_status;

        private String isLongText;

        private String in_reply_to_screen_name;

        private String mlevel;

        private String source_type;

        private String truncated;

        private String userType;

        private String id;

        private String thumbnail_pic;

        private String idstr;

        private String original_pic;

        private String picStatus;

        private String in_reply_to_status_id;

        private String created_at;

        private String reposts_count;

        private String positive_recom_flag;

        private String gif_ids;

        private String hasActionTypeCard;

        private String text;

        private String comments_count;

        private String[] text_tag_tips;

        private String source_allowclick;

        private Geo geo;

        private String bmiddle_pic;

        private String[] hot_weibo_tags;

        private String source;

        private String rid;

        private String favorited;

        private String biz_feature;

        private String in_reply_to_user_id;

        private String mid;

        private Annotations[] annotations;

        private String is_show_bulletin;

        private User user;

        public Pic_urls[] getPic_urls ()
        {
            return pic_urls;
        }

        public void setPic_urls (Pic_urls[] pic_urls)
        {
            this.pic_urls = pic_urls;
        }

        public Uri[] convertToUris(){
            if(this.getPic_urls()!=null && this.getPic_urls().length>0){
                Uri[] tUris = new Uri[this.getPic_urls().length];
                for(int i=0;i<this.getPic_urls().length;i++){
                    tUris[i] = this.getPic_urls()[i].convertToUri();
                }
                return tUris;
            }else{
                return  null;
            }
        }

        public Visible getVisible ()
        {
            return visible;
        }

        public void setVisible (Visible visible)
        {
            this.visible = visible;
        }

        public String getTextLength ()
        {
            return textLength;
        }

        public void setTextLength (String textLength)
        {
            this.textLength = textLength;
        }

        public String getAttitudes_count ()
        {
            return attitudes_count;
        }

        public void setAttitudes_count (String attitudes_count)
        {
            this.attitudes_count = attitudes_count;
        }

        public String[] getDarwin_tags ()
        {
            return darwin_tags;
        }

        public void setDarwin_tags (String[] darwin_tags)
        {
            this.darwin_tags = darwin_tags;
        }

        public String getIsLongText ()
        {
            return isLongText;
        }

        public void setIsLongText (String isLongText)
        {
            this.isLongText = isLongText;
        }

        public String getIn_reply_to_screen_name ()
        {
            return in_reply_to_screen_name;
        }

        public void setIn_reply_to_screen_name (String in_reply_to_screen_name)
        {
            this.in_reply_to_screen_name = in_reply_to_screen_name;
        }

        public String getMlevel ()
        {
            return mlevel;
        }

        public void setMlevel (String mlevel)
        {
            this.mlevel = mlevel;
        }

        public String getSource_type ()
        {
            return source_type;
        }

        public void setSource_type (String source_type)
        {
            this.source_type = source_type;
        }

        public String getTruncated ()
        {
            return truncated;
        }

        public void setTruncated (String truncated)
        {
            this.truncated = truncated;
        }

        public String getUserType ()
        {
            return userType;
        }

        public void setUserType (String userType)
        {
            this.userType = userType;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getThumbnail_pic ()
        {
            return thumbnail_pic;
        }

        public void setThumbnail_pic (String thumbnail_pic)
        {
            this.thumbnail_pic = thumbnail_pic;
        }

        public String getIdstr ()
        {
            return idstr;
        }

        public void setIdstr (String idstr)
        {
            this.idstr = idstr;
        }

        public String getOriginal_pic ()
        {
            return original_pic;
        }

        public void setOriginal_pic (String original_pic)
        {
            this.original_pic = original_pic;
        }

        public String getPicStatus ()
        {
            return picStatus;
        }

        public void setPicStatus (String picStatus)
        {
            this.picStatus = picStatus;
        }

        public String getIn_reply_to_status_id ()
        {
            return in_reply_to_status_id;
        }

        public void setIn_reply_to_status_id (String in_reply_to_status_id)
        {
            this.in_reply_to_status_id = in_reply_to_status_id;
        }

        public String getCreated_at ()
        {
            return created_at;
        }

        public void setCreated_at (String created_at)
        {
            this.created_at = created_at;
        }

        public String getReposts_count ()
        {
            return reposts_count;
        }

        public void setReposts_count (String reposts_count)
        {
            this.reposts_count = reposts_count;
        }

        public String getPositive_recom_flag ()
        {
            return positive_recom_flag;
        }

        public void setPositive_recom_flag (String positive_recom_flag)
        {
            this.positive_recom_flag = positive_recom_flag;
        }

        public String getGif_ids ()
        {
            return gif_ids;
        }

        public void setGif_ids (String gif_ids)
        {
            this.gif_ids = gif_ids;
        }

        public String getHasActionTypeCard ()
        {
            return hasActionTypeCard;
        }

        public void setHasActionTypeCard (String hasActionTypeCard)
        {
            this.hasActionTypeCard = hasActionTypeCard;
        }

        public String getText ()
        {
            return text;
        }

        public void setText (String text)
        {
            this.text = text;
        }

        public String getComments_count ()
        {
            return comments_count;
        }

        public void setComments_count (String comments_count)
        {
            this.comments_count = comments_count;
        }

        public String[] getText_tag_tips ()
        {
            return text_tag_tips;
        }

        public void setText_tag_tips (String[] text_tag_tips)
        {
            this.text_tag_tips = text_tag_tips;
        }

        public String getSource_allowclick ()
        {
            return source_allowclick;
        }

        public void setSource_allowclick (String source_allowclick)
        {
            this.source_allowclick = source_allowclick;
        }

        public Geo getGeo ()
        {
            return geo;
        }

        public void setGeo (Geo geo)
        {
            this.geo = geo;
        }

        public String getBmiddle_pic ()
        {
            return bmiddle_pic;
        }

        public void setBmiddle_pic (String bmiddle_pic)
        {
            this.bmiddle_pic = bmiddle_pic;
        }

        public String[] getHot_weibo_tags ()
        {
            return hot_weibo_tags;
        }

        public void setHot_weibo_tags (String[] hot_weibo_tags)
        {
            this.hot_weibo_tags = hot_weibo_tags;
        }

        public String getSource ()
        {
            return source;
        }

        public void setSource (String source)
        {
            this.source = source;
        }

        public String getRid ()
        {
            return rid;
        }

        public void setRid (String rid)
        {
            this.rid = rid;
        }

        public String getFavorited ()
        {
            return favorited;
        }

        public void setFavorited (String favorited)
        {
            this.favorited = favorited;
        }

        public String getBiz_feature ()
        {
            return biz_feature;
        }

        public void setBiz_feature (String biz_feature)
        {
            this.biz_feature = biz_feature;
        }

        public String getIn_reply_to_user_id ()
        {
            return in_reply_to_user_id;
        }

        public void setIn_reply_to_user_id (String in_reply_to_user_id)
        {
            this.in_reply_to_user_id = in_reply_to_user_id;
        }

        public String getMid ()
        {
            return mid;
        }

        public void setMid (String mid)
        {
            this.mid = mid;
        }

        public Annotations[] getAnnotations ()
        {
            return annotations;
        }

        public void setAnnotations (Annotations[] annotations)
        {
            this.annotations = annotations;
        }

        public String getIs_show_bulletin ()
        {
            return is_show_bulletin;
        }

        public void setIs_show_bulletin (String is_show_bulletin)
        {
            this.is_show_bulletin = is_show_bulletin;
        }

        public User getUser ()
        {
            if(user!=null) {
                return user;
            }else{
                return new User();
            }
        }

        public void setUser (User user)
        {
            this.user = user;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [pic_urls = "+pic_urls+", visible = "+visible+", textLength = "+textLength+", attitudes_count = "+attitudes_count+", darwin_tags = "+darwin_tags+", isLongText = "+isLongText+", in_reply_to_screen_name = "+in_reply_to_screen_name+", mlevel = "+mlevel+", source_type = "+source_type+", truncated = "+truncated+", userType = "+userType+", id = "+id+", thumbnail_pic = "+thumbnail_pic+", idstr = "+idstr+", original_pic = "+original_pic+", picStatus = "+picStatus+", in_reply_to_status_id = "+in_reply_to_status_id+", created_at = "+created_at+", reposts_count = "+reposts_count+", positive_recom_flag = "+positive_recom_flag+", gif_ids = "+gif_ids+", hasActionTypeCard = "+hasActionTypeCard+", text = "+text+", comments_count = "+comments_count+", text_tag_tips = "+text_tag_tips+", source_allowclick = "+source_allowclick+", geo = "+geo+", bmiddle_pic = "+bmiddle_pic+", hot_weibo_tags = "+hot_weibo_tags+", source = "+source+", rid = "+rid+", favorited = "+favorited+", biz_feature = "+biz_feature+", in_reply_to_user_id = "+in_reply_to_user_id+", mid = "+mid+", annotations = "+annotations+", is_show_bulletin = "+is_show_bulletin+", user = "+user+"]";
        }
    }
    public class User{
        private String block_app;

        private String location;

        private String remark;

        private String verified_contact_email;

        private String verified_reason;

        private String statuses_count;

        private String city;

        private String favourites_count;

        private String idstr;

        private String description;

        private String verified;

        private String province;

        private String verified_contact_name;

        private String gender;

        private String weihao;

        private String cover_image;

        private String verified_reason_modified;

        private Insecurity insecurity;

        private String mbrank;

        private String url;

        private String verified_level;

        private String cover_image_phone;

        private String verified_state;

        private String friends_count;

        private String profile_image_url;

        private String follow_me;

        private String ptype;

        private String verified_source_url;

        private String verified_type;

        private String lang;

        private String verified_source;

        private String verified_contact_mobile;

        private String credit_score;

        private String id;

        private String verified_trade;

        private String verified_type_ext;

        private String following;

        private String name;

        private String domain;

        private String created_at;

        private String user_ability;

        private String followers_count;

        private String online_status;

        private String profile_url;

        private String bi_followers_count;

        private String geo_enabled;

        private String star;

        private String urank;

        private String allow_all_comment;

        private String avatar_hd;

        private String allow_all_act_msg;

        private String avatar_large;

        private String pagefriends_count;

        private String verified_reason_url;

        private String mbtype;

        private String screen_name;

        private String block_word;

        public String getBlock_app ()
        {
            return block_app;
        }

        public void setBlock_app (String block_app)
        {
            this.block_app = block_app;
        }

        public String getLocation ()
        {
            return location;
        }

        public void setLocation (String location)
        {
            this.location = location;
        }

        public String getRemark ()
        {
            return remark;
        }

        public void setRemark (String remark)
        {
            this.remark = remark;
        }

        public String getVerified_contact_email ()
        {
            return verified_contact_email;
        }

        public void setVerified_contact_email (String verified_contact_email)
        {
            this.verified_contact_email = verified_contact_email;
        }

        public String getVerified_reason ()
        {
            return verified_reason;
        }

        public void setVerified_reason (String verified_reason)
        {
            this.verified_reason = verified_reason;
        }

        public String getStatuses_count ()
        {
            return statuses_count;
        }

        public void setStatuses_count (String statuses_count)
        {
            this.statuses_count = statuses_count;
        }

        public String getCity ()
        {
            return city;
        }

        public void setCity (String city)
        {
            this.city = city;
        }

        public String getFavourites_count ()
        {
            return favourites_count;
        }

        public void setFavourites_count (String favourites_count)
        {
            this.favourites_count = favourites_count;
        }

        public String getIdstr ()
        {
            return idstr;
        }

        public void setIdstr (String idstr)
        {
            this.idstr = idstr;
        }

        public String getDescription ()
        {
            return description;
        }

        public void setDescription (String description)
        {
            this.description = description;
        }

        public String getVerified ()
        {
            return verified;
        }

        public void setVerified (String verified)
        {
            this.verified = verified;
        }

        public String getProvince ()
        {
            return province;
        }

        public void setProvince (String province)
        {
            this.province = province;
        }

        public String getVerified_contact_name ()
        {
            return verified_contact_name;
        }

        public void setVerified_contact_name (String verified_contact_name)
        {
            this.verified_contact_name = verified_contact_name;
        }

        public String getGender ()
        {
            return gender;
        }

        public void setGender (String gender)
        {
            this.gender = gender;
        }

        public String getWeihao ()
        {
            return weihao;
        }

        public void setWeihao (String weihao)
        {
            this.weihao = weihao;
        }

        public String getCover_image ()
        {
            return cover_image;
        }

        public void setCover_image (String cover_image)
        {
            this.cover_image = cover_image;
        }

        public String getVerified_reason_modified ()
        {
            return verified_reason_modified;
        }

        public void setVerified_reason_modified (String verified_reason_modified)
        {
            this.verified_reason_modified = verified_reason_modified;
        }


        public Insecurity getInsecurity ()
        {
            return insecurity;
        }

        public void setInsecurity (Insecurity insecurity)
        {
            this.insecurity = insecurity;
        }

        public String getMbrank ()
        {
            return mbrank;
        }

        public void setMbrank (String mbrank)
        {
            this.mbrank = mbrank;
        }

        public String getUrl ()
        {
            return url;
        }

        public void setUrl (String url)
        {
            this.url = url;
        }

        public String getVerified_level ()
        {
            return verified_level;
        }

        public void setVerified_level (String verified_level)
        {
            this.verified_level = verified_level;
        }

        public String getCover_image_phone ()
        {
            return cover_image_phone;
        }

        public void setCover_image_phone (String cover_image_phone)
        {
            this.cover_image_phone = cover_image_phone;
        }

        public String getVerified_state ()
        {
            return verified_state;
        }

        public void setVerified_state (String verified_state)
        {
            this.verified_state = verified_state;
        }

        public String getFriends_count ()
        {
            return friends_count;
        }

        public void setFriends_count (String friends_count)
        {
            this.friends_count = friends_count;
        }

        public String getProfile_image_url ()
        {
            return profile_image_url;
        }

        public void setProfile_image_url (String profile_image_url)
        {
            this.profile_image_url = profile_image_url;
        }

        public String getFollow_me ()
        {
            return follow_me;
        }

        public void setFollow_me (String follow_me)
        {
            this.follow_me = follow_me;
        }

        public String getPtype ()
        {
            return ptype;
        }

        public void setPtype (String ptype)
        {
            this.ptype = ptype;
        }

        public String getVerified_source_url ()
        {
            return verified_source_url;
        }

        public void setVerified_source_url (String verified_source_url)
        {
            this.verified_source_url = verified_source_url;
        }

        public String getVerified_type ()
        {
            return verified_type;
        }

        public void setVerified_type (String verified_type)
        {
            this.verified_type = verified_type;
        }

        public String getLang ()
        {
            return lang;
        }

        public void setLang (String lang)
        {
            this.lang = lang;
        }

        public String getVerified_source ()
        {
            return verified_source;
        }

        public void setVerified_source (String verified_source)
        {
            this.verified_source = verified_source;
        }

        public String getVerified_contact_mobile ()
        {
            return verified_contact_mobile;
        }

        public void setVerified_contact_mobile (String verified_contact_mobile)
        {
            this.verified_contact_mobile = verified_contact_mobile;
        }

        public String getCredit_score ()
        {
            return credit_score;
        }

        public void setCredit_score (String credit_score)
        {
            this.credit_score = credit_score;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getVerified_trade ()
        {
            return verified_trade;
        }

        public void setVerified_trade (String verified_trade)
        {
            this.verified_trade = verified_trade;
        }

        public String getVerified_type_ext ()
        {
            return verified_type_ext;
        }

        public void setVerified_type_ext (String verified_type_ext)
        {
            this.verified_type_ext = verified_type_ext;
        }

        public String getFollowing ()
        {
            return following;
        }

        public void setFollowing (String following)
        {
            this.following = following;
        }

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        public String getDomain ()
        {
            return domain;
        }

        public void setDomain (String domain)
        {
            this.domain = domain;
        }

        public String getCreated_at ()
        {
            return created_at;
        }

        public void setCreated_at (String created_at)
        {
            this.created_at = created_at;
        }

        public String getUser_ability ()
        {
            return user_ability;
        }

        public void setUser_ability (String user_ability)
        {
            this.user_ability = user_ability;
        }

        public String getFollowers_count ()
        {
            return followers_count;
        }

        public void setFollowers_count (String followers_count)
        {
            this.followers_count = followers_count;
        }

        public String getOnline_status ()
        {
            return online_status;
        }

        public void setOnline_status (String online_status)
        {
            this.online_status = online_status;
        }

        public String getProfile_url ()
        {
            return profile_url;
        }

        public void setProfile_url (String profile_url)
        {
            this.profile_url = profile_url;
        }

        public String getBi_followers_count ()
        {
            return bi_followers_count;
        }

        public void setBi_followers_count (String bi_followers_count)
        {
            this.bi_followers_count = bi_followers_count;
        }

        public String getGeo_enabled ()
        {
            return geo_enabled;
        }

        public void setGeo_enabled (String geo_enabled)
        {
            this.geo_enabled = geo_enabled;
        }

        public String getStar ()
        {
            return star;
        }

        public void setStar (String star)
        {
            this.star = star;
        }

        public String getUrank ()
        {
            return urank;
        }

        public void setUrank (String urank)
        {
            this.urank = urank;
        }

        public String getAllow_all_comment ()
        {
            return allow_all_comment;
        }

        public void setAllow_all_comment (String allow_all_comment)
        {
            this.allow_all_comment = allow_all_comment;
        }

        public String getAvatar_hd ()
        {
            return avatar_hd;
        }

        public void setAvatar_hd (String avatar_hd)
        {
            this.avatar_hd = avatar_hd;
        }

        public String getAllow_all_act_msg ()
        {
            return allow_all_act_msg;
        }

        public void setAllow_all_act_msg (String allow_all_act_msg)
        {
            this.allow_all_act_msg = allow_all_act_msg;
        }

        public String getAvatar_large ()
        {
            return avatar_large;
        }

        public void setAvatar_large (String avatar_large)
        {
            this.avatar_large = avatar_large;
        }

        public String getPagefriends_count ()
        {
            return pagefriends_count;
        }

        public void setPagefriends_count (String pagefriends_count)
        {
            this.pagefriends_count = pagefriends_count;
        }

        public String getVerified_reason_url ()
        {
            return verified_reason_url;
        }

        public void setVerified_reason_url (String verified_reason_url)
        {
            this.verified_reason_url = verified_reason_url;
        }

        public String getMbtype ()
        {
            return mbtype;
        }

        public void setMbtype (String mbtype)
        {
            this.mbtype = mbtype;
        }

        public String getScreen_name ()
        {
            return screen_name;
        }

        public void setScreen_name (String screen_name)
        {
            this.screen_name = screen_name;
        }

        public String getBlock_word ()
        {
            return block_word;
        }

        public void setBlock_word (String block_word)
        {
            this.block_word = block_word;
        }

    }
    class Visible{
        private String type;

        private String list_id;

        public String getType ()
        {
            return type;
        }

        public void setType (String type)
        {
            this.type = type;
        }

        public String getList_id ()
        {
            return list_id;
        }

        public void setList_id (String list_id)
        {
            this.list_id = list_id;
        }
    }
    class Insecurity{
        private String sexual_content;

        public String getSexual_content ()
        {
            return sexual_content;
        }

        public void setSexual_content (String sexual_content)
        {
            this.sexual_content = sexual_content;
        }
    }
    public class Pic_urls{
        private String thumbnail_pic;
        public String getThumbnail_pic ()
        {
            return thumbnail_pic;
        }
        public Uri convertToUri(){
            String uri = thumbnail_pic;
            uri = thumbnail_pic.replace("thumbnail","large");
            return Uri.parse(uri);
        }

        public void setThumbnail_pic (String thumbnail_pic)
        {
            this.thumbnail_pic = thumbnail_pic;
        }
    }
    class Annotations{
        private Place place;

        private String client_mblogid;

        public Place getPlace ()
        {
            return place;
        }

        public void setPlace (Place place)
        {
            this.place = place;
        }

        public String getClient_mblogid ()
        {
            return client_mblogid;
        }

        public void setClient_mblogid (String client_mblogid)
        {
            this.client_mblogid = client_mblogid;
        }
    }
    class Place {
        private String poiid;

        private String title;

        private String lon;

        private String type;

        private String lat;

        public String getPoiid ()
        {
            return poiid;
        }

        public void setPoiid (String poiid)
        {
            this.poiid = poiid;
        }

        public String getTitle ()
        {
            return title;
        }

        public void setTitle (String title)
        {
            this.title = title;
        }

        public String getLon ()
        {
            return lon;
        }

        public void setLon (String lon)
        {
            this.lon = lon;
        }

        public String getType ()
        {
            return type;
        }

        public void setType (String type)
        {
            this.type = type;
        }

        public String getLat ()
        {
            return lat;
        }

        public void setLat (String lat)
        {
            this.lat = lat;
        }
    }
    class Geo{
        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity_name() {
            return city_name;
        }

        public void setCity_name(String city_name) {
            this.city_name = city_name;
        }

        public String getProvince_name() {
            return province_name;
        }

        public void setProvince_name(String province_name) {
            this.province_name = province_name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPinyin() {
            return pinyin;
        }

        public void setPinyin(String pinyin) {
            this.pinyin = pinyin;
        }

        public String getMore() {
            return more;
        }

        public void setMore(String more) {
            this.more = more;
        }

        private String longitude;
        private String latitude;
        private String city;
        private String province;
        private String city_name;
        private String province_name;
        private String address;
        private String pinyin;
        private String more;
    }

    public class Retweeted_status
    {
        private Pic_urls[] pic_urls;

        private Visible visible;

        private String textLength;

        private String attitudes_count;

        private String[] darwin_tags;

        private String isLongText;

        private String in_reply_to_screen_name;

        private String mlevel;

        private String source_type;

        private String truncated;

        private String userType;

        private String id;

        private String thumbnail_pic;

        private String idstr;

        private String original_pic;

        private String in_reply_to_status_id;

        private String created_at;

        private String reposts_count;

        private String positive_recom_flag;

        private String gif_ids;

        private String hasActionTypeCard;

        private String text;

        private String comments_count;

        private String[] text_tag_tips;

        private String source_allowclick;

        private Geo geo;

        private String bmiddle_pic;

        private String[] hot_weibo_tags;

        private String source;

        private String favorited;

        private String biz_feature;

        private String in_reply_to_user_id;

        private String mid;

        private String is_show_bulletin;

        private User user;

        public Pic_urls[] getPic_urls ()
        {
            return pic_urls;
        }

        public void setPic_urls (Pic_urls[] pic_urls)
        {
            this.pic_urls = pic_urls;
        }

        public Uri[] convertToUris(){
            if(this.getPic_urls()!=null && this.getPic_urls().length>0){
                Uri[] tUris = new Uri[this.getPic_urls().length];
                for(int i=0;i<this.getPic_urls().length;i++){
                    tUris[i] = this.getPic_urls()[i].convertToUri();
                }
                return tUris;
            }else{
                return  null;
            }
        }

        public Visible getVisible ()
        {
            return visible;
        }

        public void setVisible (Visible visible)
        {
            this.visible = visible;
        }

        public String getTextLength ()
        {
            return textLength;
        }

        public void setTextLength (String textLength)
        {
            this.textLength = textLength;
        }

        public String getAttitudes_count ()
        {
            return attitudes_count;
        }

        public void setAttitudes_count (String attitudes_count)
        {
            this.attitudes_count = attitudes_count;
        }

        public String[] getDarwin_tags ()
        {
            return darwin_tags;
        }

        public void setDarwin_tags (String[] darwin_tags)
        {
            this.darwin_tags = darwin_tags;
        }

        public String getIsLongText ()
        {
            return isLongText;
        }

        public void setIsLongText (String isLongText)
        {
            this.isLongText = isLongText;
        }

        public String getIn_reply_to_screen_name ()
        {
            return in_reply_to_screen_name;
        }

        public void setIn_reply_to_screen_name (String in_reply_to_screen_name)
        {
            this.in_reply_to_screen_name = in_reply_to_screen_name;
        }

        public String getMlevel ()
        {
            return mlevel;
        }

        public void setMlevel (String mlevel)
        {
            this.mlevel = mlevel;
        }

        public String getSource_type ()
        {
            return source_type;
        }

        public void setSource_type (String source_type)
        {
            this.source_type = source_type;
        }

        public String getTruncated ()
        {
            return truncated;
        }

        public void setTruncated (String truncated)
        {
            this.truncated = truncated;
        }

        public String getUserType ()
        {
            return userType;
        }

        public void setUserType (String userType)
        {
            this.userType = userType;
        }

        public String getId ()
        {
            return id;
        }

        public void setId (String id)
        {
            this.id = id;
        }

        public String getThumbnail_pic ()
        {
            return thumbnail_pic;
        }

        public void setThumbnail_pic (String thumbnail_pic)
        {
            this.thumbnail_pic = thumbnail_pic;
        }

        public String getIdstr ()
        {
            return idstr;
        }

        public void setIdstr (String idstr)
        {
            this.idstr = idstr;
        }

        public String getOriginal_pic ()
        {
            return original_pic;
        }

        public void setOriginal_pic (String original_pic)
        {
            this.original_pic = original_pic;
        }

        public String getIn_reply_to_status_id ()
        {
            return in_reply_to_status_id;
        }

        public void setIn_reply_to_status_id (String in_reply_to_status_id)
        {
            this.in_reply_to_status_id = in_reply_to_status_id;
        }

        public String getCreated_at ()
        {
            return created_at;
        }

        public void setCreated_at (String created_at)
        {
            this.created_at = created_at;
        }

        public String getReposts_count ()
        {
            return reposts_count;
        }

        public void setReposts_count (String reposts_count)
        {
            this.reposts_count = reposts_count;
        }

        public String getPositive_recom_flag ()
        {
            return positive_recom_flag;
        }

        public void setPositive_recom_flag (String positive_recom_flag)
        {
            this.positive_recom_flag = positive_recom_flag;
        }

        public String getGif_ids ()
        {
            return gif_ids;
        }

        public void setGif_ids (String gif_ids)
        {
            this.gif_ids = gif_ids;
        }

        public String getHasActionTypeCard ()
        {
            return hasActionTypeCard;
        }

        public void setHasActionTypeCard (String hasActionTypeCard)
        {
            this.hasActionTypeCard = hasActionTypeCard;
        }

        public String getText ()
        {
            return text;
        }

        public void setText (String text)
        {
            this.text = text;
        }

        public String getComments_count ()
        {
            return comments_count;
        }

        public void setComments_count (String comments_count)
        {
            this.comments_count = comments_count;
        }

        public String[] getText_tag_tips ()
        {
            return text_tag_tips;
        }

        public void setText_tag_tips (String[] text_tag_tips)
        {
            this.text_tag_tips = text_tag_tips;
        }

        public String getSource_allowclick ()
        {
            return source_allowclick;
        }

        public void setSource_allowclick (String source_allowclick)
        {
            this.source_allowclick = source_allowclick;
        }

        public Geo getGeo ()
    {
        return geo;
    }

        public void setGeo (Geo geo)
        {
            this.geo = geo;
        }

        public String getBmiddle_pic ()
        {
            return bmiddle_pic;
        }

        public void setBmiddle_pic (String bmiddle_pic)
        {
            this.bmiddle_pic = bmiddle_pic;
        }

        public String[] getHot_weibo_tags ()
        {
            return hot_weibo_tags;
        }

        public void setHot_weibo_tags (String[] hot_weibo_tags)
        {
            this.hot_weibo_tags = hot_weibo_tags;
        }

        public String getSource ()
        {
            return source;
        }

        public void setSource (String source)
        {
            this.source = source;
        }

        public String getFavorited ()
        {
            return favorited;
        }

        public void setFavorited (String favorited)
        {
            this.favorited = favorited;
        }

        public String getBiz_feature ()
        {
            return biz_feature;
        }

        public void setBiz_feature (String biz_feature)
        {
            this.biz_feature = biz_feature;
        }

        public String getIn_reply_to_user_id ()
        {
            return in_reply_to_user_id;
        }

        public void setIn_reply_to_user_id (String in_reply_to_user_id)
        {
            this.in_reply_to_user_id = in_reply_to_user_id;
        }

        public String getMid ()
        {
            return mid;
        }

        public void setMid (String mid)
        {
            this.mid = mid;
        }

        public String getIs_show_bulletin ()
        {
            return is_show_bulletin;
        }

        public void setIs_show_bulletin (String is_show_bulletin)
        {
            this.is_show_bulletin = is_show_bulletin;
        }

        public User getUser ()
        {
            if(user!=null) {
                return user;
            }else{
                return new User();
            }
        }

        public void setUser (User user)
        {
            this.user = user;
        }
    }
}
