package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;


public class OrderFragment extends Fragment implements OnRecyclerViewClickListener {
    private static final String KEY = "orderClient";
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance(String param1) {
        OrderFragment fragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}