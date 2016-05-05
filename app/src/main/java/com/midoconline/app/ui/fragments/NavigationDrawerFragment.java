package com.midoconline.app.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.midoconline.app.R;
import com.midoconline.app.Util.SharePreferences;
import com.midoconline.app.ui.activities.DoctorHistoryActivity;
import com.midoconline.app.ui.activities.MyProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {


    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containerView;
    private SharePreferences mSharePreferences;



    private LinearLayout mAccountWrapper, mHistoryWrapper;
    public NavigationDrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mSharePreferences = new SharePreferences(getActivity());
        initiView(view);
        return view;
    }

    private void initiView(View view) {
        mAccountWrapper = (LinearLayout) view.findViewById(R.id.account_wrapper);
        mHistoryWrapper = (LinearLayout) view.findViewById(R.id.history_wrapper);

        mAccountWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                if (mSharePreferences.isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                    startActivity(intent);
                }
            }
        });

        mHistoryWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                if (mSharePreferences.isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), DoctorHistoryActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
              //  toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        if (mSharePreferences.isLoggedIn()){
            setDrawerState(true);
        }else {
            setDrawerState(false);
        }

    }

    public void setDrawerState(boolean isEnabled) {
        if ( isEnabled ) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
            mDrawerToggle.syncState();

        }
        else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerToggle.syncState();
            mDrawerToggle.onDrawerStateChanged(DrawerLayout.STATE_IDLE);
        }
    }

}
