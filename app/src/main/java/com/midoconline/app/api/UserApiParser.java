package com.midoconline.app.api;

import com.midoconline.app.beans.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Prashant on 28/12/15.
 */
public class UserApiParser {

    public static UserApiParser instance;
    public ArrayList<User> usersList;


    public static UserApiParser getInstance() {
        if (instance == null) {
            instance = new UserApiParser();
        }
        return instance;
    }

    public void ParseAllUserResponse(JSONObject jsonObject) {
        try {
            JSONArray  userArray =  jsonObject.getJSONArray("items");
            setResultStore(userArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setResultStore(JSONArray  userArray) {
        usersList = new ArrayList<User>();
        for (int i = 0; i<userArray.length(); i++) {
            try {
                JSONObject mainObject = userArray.getJSONObject(i);
                usersList.add(new User(mainObject.getJSONObject("user")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public ArrayList<User> getUserList(){
        return usersList;
    }
}
