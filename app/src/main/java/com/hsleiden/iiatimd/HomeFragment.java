package com.hsleiden.iiatimd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private static final String USER_NAME = "userName";
    private static final String USER_BIRTHDAY = "userBirthday";
    private static final String USER_EDUCATION = "userEducation";
    private static final String USER_VALID = "userValid";
    private static final String USER_STNUMBER = "userStNumber";

    private String mUserName;
    private String mUserBirthday;
    private String mUserEducation;
    private String mUserValid;
    private String mUserStNumber;

    public static HomeFragment createInstance(String userName, String userBirthday, String userEducation, String userValid, String userStNumber) {
        HomeFragment fragment = new HomeFragment();

        // Add the provided username to the fragment's arguments
        Bundle args = new Bundle();
        args.putString(USER_NAME, userName);
        args.putString(USER_BIRTHDAY, userBirthday);
        args.putString(USER_EDUCATION, userEducation);
        args.putString(USER_VALID, userValid);
        args.putString(USER_STNUMBER, userStNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserName = getArguments().getString(USER_NAME);
            mUserBirthday = getArguments().getString(USER_BIRTHDAY);
            mUserEducation = getArguments().getString(USER_EDUCATION);
            mUserValid = getArguments().getString(USER_VALID);
            mUserStNumber = getArguments().getString(USER_STNUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView myCardImage = homeView.findViewById(R.id.myCardImage);
        TextView userName = homeView.findViewById(R.id.userName);
        TextView userBirthday = homeView.findViewById(R.id.userBirthday);
        TextView userEducation = homeView.findViewById(R.id.userEducation);
        TextView userValid = homeView.findViewById(R.id.userValid);
        TextView userStNumber = homeView.findViewById(R.id.userStNumber);

        userName.setText(mUserName);
        userBirthday.setText(mUserBirthday);
        userEducation.setText(mUserEducation);
        userValid.setText(mUserValid);
        userStNumber.setText(mUserStNumber);

        ScaleAnimation scaleCard = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scaleCard.setDuration(300);
        scaleCard.setInterpolator(new OvershootInterpolator());
        myCardImage.startAnimation(scaleCard);

        ScaleAnimation scaleDetails = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .1f, ScaleAnimation.RELATIVE_TO_SELF, .1f);
        scaleDetails.setDuration(300);
        scaleDetails.setInterpolator(new OvershootInterpolator());
        userName.startAnimation(scaleDetails);
        userBirthday.startAnimation(scaleDetails);
        userEducation.startAnimation(scaleDetails);
        userValid.startAnimation(scaleDetails);
        userStNumber.startAnimation(scaleDetails);

        return homeView;
    }
}