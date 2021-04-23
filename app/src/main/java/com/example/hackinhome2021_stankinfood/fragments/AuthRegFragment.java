package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hackinhome2021_stankinfood.R;

public class AuthRegFragment extends Fragment implements View.OnClickListener {


    private static final String IS_REGISTRATION = "isRegistration";

    private boolean isRegistration;
    private TextView textViewAuthRegTitle;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRequest;
    private TextView textViewForgotPassword;
    private Button buttonRequestGoogle;
    private Button buttonRequestApple;

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
        initButtons(view);
        initEditTexts(view);
        initTextViews(view);
        return view;
    }

    private void initTextViews(View view) {
        textViewAuthRegTitle = view.findViewById(R.id.textViewAuthRegTitle);
        textViewForgotPassword = view.findViewById(R.id.textViewForgotPassword);
        if (isRegistration) {
            textViewAuthRegTitle.setText(getResources().getString(R.string.registration));
            textViewForgotPassword.setVisibility(View.GONE);
        } else {
            textViewAuthRegTitle.setText(getResources().getString(R.string.authorization));
            textViewForgotPassword.setOnClickListener(this);
        }
    }

    private void initButtons(View view) {
        buttonRequest = view.findViewById(R.id.buttonRequest);
        buttonRequestApple = view.findViewById(R.id.buttonRequestApple);
        buttonRequestGoogle = view.findViewById(R.id.buttonRequestGoogle);
        if (isRegistration) {
            buttonRequest.setText(getResources().getString(R.string.sign_up));
        } else {
            buttonRequest.setText(getResources().getString(R.string.sign_in));
        }
        buttonRequest.setOnClickListener(this);
        buttonRequestApple.setOnClickListener(this);
        buttonRequestGoogle.setOnClickListener(this);

    }

    private void initEditTexts(View view) {
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        if (isRegistration) {
            editTextPassword.setHint(getResources().getString(R.string.password_reg_hint));
        }else {
            editTextPassword.setHint(getResources().getString(R.string.password_auth_hint));
        }
        }

    private boolean isEmailCorrect(){
        String email = editTextEmail.getText().toString();
        return email.contains("@")&& email.contains(".");
    }
    private boolean isPasswordCorrect(){
        String password = editTextPassword.getText().toString();
        return  password.length() >=8;
    }
    @Override
    public void onClick(View v) {

    }
}