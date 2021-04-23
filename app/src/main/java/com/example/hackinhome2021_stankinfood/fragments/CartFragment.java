package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hackinhome2021_stankinfood.R;


public class CartFragment extends Fragment {

    private static final String KEY_CART = "cart";

    private String cart;
    private TextView textViewRealAddress;
    private TextView textViewPickupTime;
    private TextView textViewRealPickupTime;
    private TextView textViewRealTotalPrice;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance(String cart) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(KEY_CART, cart);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cart = getArguments().getString(KEY_CART);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        return view;
    }
}