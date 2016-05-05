package com.midoconline.app.beans;

/**
 * Created by Prashant on 12/3/16.
 */
public class HistoryHandler {
    public static HistoryHandler instance;
    public History getCallHistoryObj;

    public static HistoryHandler getInstance() {

        if (instance == null) {
            instance = new HistoryHandler();
        }
        return instance;
    }

    public void setCallHistoryObj(History history){
        getCallHistoryObj = history;
    }
}
