package com.midoconline.app.beans;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Prashant on 28/12/15.
 */
public class User {
    public int id;
    public int owner_id;
    public String full_name;
    public String email;
    public String login;
    public String phone;
    public String website;
    public String createdAt;
    public String updatedAt;
    public String lastRequestAt;
    public String externalUserId;
    public String facebookId;
    public String twitterId;
    public String boldId;
    public String customData;
    public String twitterDigitId;
    public String user_tags ;

    public User(JSONObject userObjct) {
        try {
            this.id = userObjct.getInt("id");
            this.owner_id = userObjct.getInt("owner_id");
            this.full_name = userObjct.getString("full_name");
            this.email = userObjct.getString("email");
            this.login = userObjct.getString("login");
            this.phone = userObjct.getString("phone");
            this.website = userObjct.getString("website");
            this.createdAt = userObjct.getString("created_at");
            this.updatedAt = userObjct.getString("updated_at");
            this.lastRequestAt = userObjct.getString("last_request_at");
            this.externalUserId = userObjct.getString("external_user_id");
            this.facebookId = userObjct.getString("facebook_id");
            this.twitterId = userObjct.getString("twitter_id");
            this.boldId = userObjct.getString("blob_id");
            this.customData = userObjct.getString("custom_data");
            this.twitterDigitId = userObjct.getString("twitter_digits_id");
            this.user_tags = userObjct.getString("user_tags");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
