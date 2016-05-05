package com.midoconline.app.ui.interfaces;

/**
 * Created by Prashant on 28/11/15.
 */
public interface PaymentForm {
    public String getCardNumber();
    public String getCvc();
    public Integer getExpMonth();
    public Integer getExpYear();
}