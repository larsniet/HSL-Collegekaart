package com.hsleiden.iiatimd;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#createInstance(String)} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private static final String USER_NAME = "userName";
    private static final String USER_EMAIL = "userEmail";
    private static final String USER_STNUMBER = "userStNumber";

    private String mUserName;
    private String mUserEmail;
    private String mUserStNumber;

    public static SettingsFragment createInstance(String userName, String userEmail, String userStNumber) {
        SettingsFragment fragment = new SettingsFragment();

        // Add the provided username to the fragment's arguments
        Bundle args = new Bundle();
        args.putString(USER_NAME, userName);
        args.putString(USER_EMAIL, userEmail);
        args.putString(USER_STNUMBER, userStNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserName = getArguments().getString(USER_NAME);
            mUserEmail = getArguments().getString(USER_EMAIL);
            mUserStNumber = getArguments().getString(USER_STNUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View settingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        LinearLayout settings_layout = settingsView.findViewById(R.id.settings_layout);
        TextView userName = settingsView.findViewById(R.id.settings_userName);
        TextView userEmail = settingsView.findViewById(R.id.settings_userEmail);
        TextView userStNumber = settingsView.findViewById(R.id.settings_stNumber);

        // If there is a username, replace the "Please sign in" with the username
        if (mUserName != null) {
            userName.setText(mUserName);
            userEmail.setText(mUserEmail);
            userStNumber.setText(mUserStNumber);
        }

        Animation fadeInBottom = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_bottom);
        settings_layout.startAnimation(fadeInBottom);

        return settingsView;
    }
}