package com.hsleiden.iiatimd;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#createInstance(String)} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private static final String USER_NAME = "userName";

    private String mUserName;

    public static ProfileFragment createInstance(String userName) {
        ProfileFragment fragment = new ProfileFragment();

        // Add the provided username to the fragment's arguments
        Bundle args = new Bundle();
        args.putString(USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserName = getArguments().getString(USER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        Button buttonLogout = profileView.findViewById(R.id.buttonLogout);
        TextView userName = profileView.findViewById(R.id.home_page_username);

        // If there is a username, replace the "Please sign in" with the username
        if (mUserName != null) {
            userName.setText(mUserName);
        } else {
            buttonLogout.setVisibility(View.GONE);
        }

        buttonLogout.setOnClickListener(v -> {
            ((HomeActivity) Objects.requireNonNull(getActivity())).signOut();
        });


        return profileView;
    }
}