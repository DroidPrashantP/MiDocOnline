package com.midoconline.app.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.midoconline.app.R;
import com.midoconline.app.beans.User;

import java.util.List;

/**
 * Created by sandeep on 28/12/15.
 */
public class UsersCustomAdapter extends RecyclerView.Adapter<UsersCustomAdapter.UserHolder> {

    List<User> userList;
    private Context mContext;

    public UsersCustomAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.mContext = context;
    }

    @Override
    public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_custom_row, parent, false);
        return new UserHolder(rootView);
    }

    @Override
    public void onBindViewHolder(UserHolder holder, int position) {
        User user = userList.get(position);
        holder.mUserName.setText(user.full_name);
    }

    @Override
    public int getItemCount() {
         return userList.size();

    }

    public static class UserHolder extends RecyclerView.ViewHolder {
        private TextView mUserName;

        public UserHolder(View itemView) {
            super(itemView);
            mUserName = (TextView) itemView.findViewById(R.id.userName);
        }
    }
}
