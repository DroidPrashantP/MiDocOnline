package com.midoconline.app.beans;

import android.util.Log;

import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

/**
 * Created by Prashant on 1/1/16.
 */
public class DataHolder {
    public static ArrayList<QBUser> usersList  = new ArrayList<>();
    public static final String PASSWORD = "password";

    public static ArrayList<QBUser> getUsersList() {
        return usersList;
    }


    public static String getUserNameByID(Integer callerID) {
        for (QBUser user : getUsersList()) {
            if (user.getId().equals(callerID)) {
                return user.getFullName();
            }
        }
        return "";
    }

    public static String getUserEmailID(Integer callerID) {
        for (QBUser user : getUsersList()) {
            if (user.getId().equals(callerID)) {
                return user.getEmail();
            }
        }
        return "";
    }


    public static String getUserNameByLogin(String login) {
        for (QBUser user : getUsersList()) {
            if (user.getLogin().equals(login)) {
                return user.getFullName();
            }
        }
        return "";
    }

    public static int getUserIndexByID(Integer callerID) {
        for (QBUser user : getUsersList()) {
            if (user.getId().equals(callerID)) {
                return usersList.indexOf(user);
            }
        }
        return -1;
    }

    public static ArrayList<Integer> getIdsAiiUsers (){
        ArrayList<Integer> ids = new ArrayList<>();
        for (QBUser user : getUsersList()){
            ids.add(user.getId());
        }
        return ids;
    }
}
