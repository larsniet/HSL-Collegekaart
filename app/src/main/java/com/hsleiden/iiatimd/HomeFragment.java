package com.hsleiden.iiatimd;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;
import java.util.Objects;

import me.virtualiz.blurshadowimageview.BlurShadowImageView;

public class HomeFragment extends Fragment {

    private static final String USER_NAME = "userName";
    private static final String USER_BIRTHDAY = "userBirthday";
    private static final String USER_EDUCATION = "userEducation";
    private static final String USER_VALID = "userValid";
    private static final String USER_STNUMBER = "userStNumber";

    Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
    Writer codeWriter = new Code128Writer();
    BitMatrix byteMatrix = null;

    private String mUserName;
    private String mUserBirthday;
    private String mUserEducation;
    private String mUserValid;
    private String mUserStNumber;

    public static HomeFragment createInstance(String userName, String userBirthday, String userEducation, String userValid, String userStNumber) {
        HomeFragment fragment = new HomeFragment();

        // Add the provided details to the fragment's arguments
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

        // Setup
        RelativeLayout cardLayout = homeView.findViewById(R.id.cardLayout);
        BlurShadowImageView myCardImage = homeView.findViewById(R.id.myCardImage);
        ImageView myBarcode = homeView.findViewById(R.id.myBarcode);
        TextView userName = homeView.findViewById(R.id.userName);
        TextView userBirthday = homeView.findViewById(R.id.userBirthday);
        TextView userEducation = homeView.findViewById(R.id.userEducation);
        TextView userValid = homeView.findViewById(R.id.userValid);
        TextView userStNumber = homeView.findViewById(R.id.userStNumber);

        // Details setters
        userName.setText(mUserName);
        userBirthday.setText(mUserBirthday);
        userEducation.setText(mUserEducation);
        userValid.setText(mUserValid);
        userStNumber.setText(mUserStNumber);

        // Animations for the cardImage
        ScaleAnimation scaleCard = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scaleCard.setDuration(300);
        scaleCard.setInterpolator(new OvershootInterpolator());
        myCardImage.startAnimation(scaleCard);

        // Animations for the card details
        ScaleAnimation scaleDetails = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .1f, ScaleAnimation.RELATIVE_TO_SELF, .1f);
        scaleDetails.setDuration(300);
        scaleDetails.setInterpolator(new OvershootInterpolator());
        userName.startAnimation(scaleDetails);
        userBirthday.startAnimation(scaleDetails);
        userEducation.startAnimation(scaleDetails);
        userValid.startAnimation(scaleDetails);
        userStNumber.startAnimation(scaleDetails);
        myBarcode.startAnimation(scaleDetails);

        // Code for creating and placing barcode
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        try {
            byteMatrix = codeWriter.encode(mUserStNumber, BarcodeFormat.CODE_128,700, 150, hintMap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = byteMatrix.getWidth();
        int height = byteMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }
        myBarcode.setImageBitmap(bitmap);

        return homeView;
    }
}