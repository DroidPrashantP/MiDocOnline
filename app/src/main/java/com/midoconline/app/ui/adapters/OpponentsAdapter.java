package com.midoconline.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.midoconline.app.R;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Prashant on 30.12.15.
 */
public class OpponentsAdapter extends BaseAdapter {

    private static final String TAG = OpponentsAdapter.class.getName();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private TextView mFooterTextView;
    private ProgressBar mProgressBar;
    private List<QBUser> opponents;
    private LayoutInflater inflater;
    public static int i;
    public List<QBUser> selected = new ArrayList<>();

    //it will tell us weather to load more items or not
    boolean mLoadingMore = false;

    public OpponentsAdapter(Context context, List<QBUser> QBUsers) {
        this.opponents = QBUsers;
        this.inflater = LayoutInflater.from(context);
    }

    public List<QBUser> getSelected() {
        return selected;
    }

    public int getCount() {
        return opponents.size();
    }

    public QBUser getItem(int position) {
        return opponents.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(final int position, View convertView, final ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_opponents, null);
            holder = new ViewHolder();
            holder.opponentsNumber = (TextView) convertView.findViewById(R.id.opponentsNumber);
            holder.opponentsName = (TextView) convertView.findViewById(R.id.opponentsName);
            holder.opponentsEmail = (TextView) convertView.findViewById(R.id.opponentEmail);
            holder.opponentsRadioButton = (RadioButton) convertView.findViewById(R.id.opponentsCheckBox);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final QBUser QBUser = opponents.get(position);


        if (QBUser != null) {

            holder.opponentsNumber.setText(String.valueOf(QBUser.getLogin().substring(0, 1).toUpperCase()));
            holder.opponentsNumber.setBackgroundResource(resourceSelector(QBUser.getId()));
            holder.opponentsEmail.setText(QBUser.getLogin());
            holder.opponentsName.setText(""+QBUser.getFullName());
            holder.opponentsRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        i = QBUser.getId();
                        selected.removeAll(selected);
                        selected.add(QBUser);
                    } else {
                        if (i == QBUser.getId()) {
                            i = 0;
                        }
                        selected.remove(QBUser);
                        holder.opponentsRadioButton.setChecked(false);
                    }
                    notifyDataSetChanged();
                }
            });

            holder.opponentsRadioButton.setChecked(i == QBUser.getId());

        }

        return convertView;
    }

    public static int resourceSelector(int number) {
        int resStr = -1;
        switch (number) {
            case 0:
                resStr = R.drawable.shape_oval_spring_bud;
                break;
            case 1:
                resStr = R.drawable.shape_oval_orange;
                break;
            case 2:
                resStr = R.drawable.shape_oval_water_bondi_beach;
                break;
            case 3:
                resStr = R.drawable.shape_oval_blue_green;
                break;
            case 4:
                resStr = R.drawable.shape_oval_lime;
                break;
            case 5:
                resStr = R.drawable.shape_oval_mauveine;
                break;
            case 6:
                resStr = R.drawable.shape_oval_gentianaceae_blue;
                break;
            case 7:
                resStr = R.drawable.shape_oval_blue;
                break;
            case 8:
                resStr = R.drawable.shape_oval_blue_krayola;
                break;
            case 9:
                resStr = R.drawable.shape_oval_coral;
                break;
            default:
                resStr = resourceSelector(number % 10);
        }
        return resStr;
    }




    public static class ViewHolder {
        TextView opponentsNumber;
        TextView opponentsName;
        TextView opponentsEmail;
        RadioButton opponentsRadioButton;
    }



    public void addItem(ArrayList<QBUser> newUserList){
        opponents.addAll(newUserList);
        notifyDataSetChanged();
    }
}
