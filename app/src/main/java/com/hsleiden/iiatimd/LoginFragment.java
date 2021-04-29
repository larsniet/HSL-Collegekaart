package com.hsleiden.iiatimd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class LoginFragment extends Fragment {

    public LoginFragment() {

    }

    public static LoginFragment createInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View loginView = inflater.inflate(R.layout.fragment_login, container, false);

        Button buttonLogin = loginView.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(v -> {
            ((HomeActivity) Objects.requireNonNull(getActivity())).signIn();
        });

        return loginView;
    }

}