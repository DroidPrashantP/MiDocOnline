package com.midoconline.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.midoconline.app.R;

/**
 * Created by sandeep on 18/10/15.
 */
public class HistoryCustomAdapter extends BaseAdapter {
    private Context mContext;
    public HistoryCustomAdapter(Context context) {
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomViewHolder holder;
        View view = null;
        if (convertView == null) {
            holder = new CustomViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_custom_history_row, null);
            holder.txtDate = (TextView) view.findViewById(R.id.text_date);
            holder.txtDoctorName = (TextView) view.findViewById(R.id.text_doctor_name);
            holder.txtDoctorOccupation = (TextView) view.findViewById(R.id.text_doctor_speciality);
            holder.txtDoctorCharge = (TextView) view.findViewById(R.id.text_prize);
            holder.mBtnChatHistory = (Button) view.findViewById(R.id.btn_chatHistory);
            holder.mBtnCall = (Button) view.findViewById(R.id.btn_call);
        }

        return view;
    }

    public  class CustomViewHolder {
        public TextView txtDate;
        public TextView txtDoctorName;
        public TextView txtDoctorOccupation;
        public TextView txtDoctorCharge;
        public Button mBtnChatHistory;
        public Button mBtnCall;

    }
}
