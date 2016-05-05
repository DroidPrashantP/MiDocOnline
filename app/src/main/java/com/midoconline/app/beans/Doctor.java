package com.midoconline.app.beans;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Prashant on 28/2/16.
 */
public class Doctor {
    public int id;
    public String username;
    public String email;
    public String gender;
    public String secret_key;
    public String key;
    public String authentication_token;
    public String dob;
    public String mobile_no;
    public String type;
    public String location;
    public String specialize;
    public String surname;
    public String licence;
    public String country;
    public String city;
    public String qb_user_id;
    public String qb_login;
    public String qb_password;
    public String qb_name;
    public String emergency;
    public String image_url;
    public String full_name;
    public String profile_image;

    public Doctor(JSONObject obj){
        try {
            this.id = obj.getInt("id");
            this.username = obj.getString("username");
            this.email = obj.getString("email");
            this.gender = obj.getString("gender");
            this.secret_key = obj.getString("secret_key");
            this.key = obj.getString("key");
            this.authentication_token = obj.getString("authentication_token");
            this.dob = obj.getString("dob");
            this.mobile_no = obj.getString("mobile_no");
            this.type = obj.getString("type");
            this.location = obj.getString("location");
            this.specialize = obj.getString("specialize");
            this.surname = obj.getString("surname");
            this.licence = obj.getString("licence");
            this.country = obj.getString("country");
            this.city = obj.getString("city");
            this.qb_user_id = obj.getString("qb_user_id");
            this.qb_login = obj.getString("qb_login");
            this.qb_password = obj.getString("qb_password");
            this.qb_name = obj.getString("qb_name");
            this.emergency = obj.getString("emergency");
            this.image_url = obj.getString("image_url");
            this.full_name = obj.getString("full_name");
            this.profile_image = obj.getString("profile_image");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
