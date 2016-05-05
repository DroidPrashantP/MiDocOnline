package com.midoconline.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.midoconline.app.R;
import com.midoconline.app.Util.Constants;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.beans.History;
import com.midoconline.app.ui.activities.DoctorHistoryActivity;
import com.midoconline.app.ui.activities.HistoryChatActivity;

import java.util.ArrayList;

/**
 * Created by Prashant on 18/10/15.
 */
public class HistoryRecyclerView extends RecyclerView.Adapter<HistoryRecyclerView.CustomViewHolder> {
    private Context mContext;
    private ArrayList<History> mHistoryArrayList;
    public HistoryRecyclerView(Context context, ArrayList<History> historyArrayList) {
        this.mContext = context;
        this.mHistoryArrayList = historyArrayList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_custom_history_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        final History history = mHistoryArrayList.get(i);
        String caller_id = new SharePreferences(mContext).getQBID();
        String caller_email = new SharePreferences(mContext).getQBEmail();
        if (Constants.BundleKeys.DOCTOR.equalsIgnoreCase(new SharePreferences(mContext).getUserType())) {
            customViewHolder.txtDate.setText("" + history.call_date);
            customViewHolder.txtDoctorName.setText("" + history.patient_full_name);
            customViewHolder.txtDoctorCharge.setText("" + history.amount);
            customViewHolder.txtDoctorOccupation.setText("" );
        }else {
            customViewHolder.txtDate.setText("" + history.call_date);
            customViewHolder.txtDoctorName.setText("" + history.doctor_full_name);
            customViewHolder.txtDoctorCharge.setText("" + history.amount);
            customViewHolder.txtDoctorOccupation.setText("" + history.doctor_specialization);
        }


        customViewHolder.mBtnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DoctorHistoryActivity) mContext).ClickOnCallBtn(history);
            }
        });
//        customViewHolder.mBtnChatHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, HistoryChatActivity.class);
//                mContext.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return mHistoryArrayList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public TextView txtDate;
        public TextView txtDoctorName;
        public TextView txtDoctorOccupation;
        public TextView txtDoctorCharge;
        public Button mBtnChatHistory;
        public Button mBtnCall;

        public CustomViewHolder(View view) {
            super(view);
            txtDate = (TextView) view.findViewById(R.id.text_date);
            txtDoctorName = (TextView) view.findViewById(R.id.text_doctor_name);
            txtDoctorOccupation = (TextView) view.findViewById(R.id.text_doctor_speciality);
            txtDoctorCharge = (TextView) view.findViewById(R.id.text_prize);
            mBtnChatHistory = (Button) view.findViewById(R.id.btn_chatHistory);
            mBtnCall = (Button) view.findViewById(R.id.btn_call);

        }
    }
}
