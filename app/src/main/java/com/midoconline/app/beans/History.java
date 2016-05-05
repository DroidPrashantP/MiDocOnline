package com.midoconline.app.beans;

/**
 * Created by Prashant on 6/3/16.
 */
public class History {
    public String receiver_id;
    public String caller_id;
    public String duration;
    public String chat_type;
    public String call_status;
    public String amount;
    public String currency;
    public String qb_caller_id;
    public String qb_receiver_id;
    public String doctor_id;
    public String patient_id;
    public String doctor_email;
    public String patient_email;
    public String doctor_full_name;
    public String patient_full_name;
    public String doctor_specialization;
    public String call_date;

    public History(String receiver_id, String caller_id, String duration, String chat_type, String call_status, String amount, String currency, String qb_caller_id, String qb_receiver_id, String doctor_id, String patient_id, String doctor_email, String patient_email, String doctor_full_name, String patient_full_name, String doctor_specialization, String call_date) {
        this.receiver_id = receiver_id;
        this.caller_id = caller_id;
        this.duration = duration;
        this.chat_type = chat_type;
        this.call_status = call_status;
        this.amount = amount;
        this.currency = currency;
        this.qb_caller_id = qb_caller_id;
        this.qb_receiver_id = qb_receiver_id;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.doctor_email = doctor_email;
        this.patient_email = patient_email;
        this.doctor_full_name = doctor_full_name;
        this.patient_full_name = patient_full_name;
        this.doctor_specialization = doctor_specialization;
        this.call_date = call_date;
    }
}
