package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hackinhome2021_stankinfood.R;

public class AuthRegFragment extends Fragment implements View.OnClickListener {


    private static final String IS_REGISTRATION = "isRegistration";

    private boolean isRegistration;

    public AuthRegFragment() {
        // Required empty public constructor
    }


    public static AuthRegFragment newInstance(Boolean isRegistration) {
        AuthRegFragment fragment = new AuthRegFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_REGISTRATION, isRegistration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isRegistration = getArguments().getBoolean(IS_REGISTRATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_reg, container, false);

        return view;
    }

    @Override
    public void onClick(View v) {
        
    }
}