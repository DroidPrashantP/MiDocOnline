package com.midoconline.app.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.midoconline.app.R;
import com.midoconline.app.beans.Doctor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prashant on 28/2/16.
 */
public class DoctorListAdapter extends ArrayAdapter<String> {

    private Context activity;
    private ArrayList<Doctor> data;
    public Resources res;

    public DoctorListAdapter(Context ctx, int txtViewResourceId, ArrayList<Doctor> objects) {
        super(ctx, txtViewResourceId, (List)objects);
        this.activity = ctx;
        this.data = objects;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mySpinner = inflater.inflate(R.layout.custom_spinner_view, parent, false);
        TextView main_text = (TextView) mySpinner.findViewById(R.id.speciality_textView);
        Doctor doctor = data.get(position);
        main_text.setText(doctor.username);
        return mySpinner;
    }
}