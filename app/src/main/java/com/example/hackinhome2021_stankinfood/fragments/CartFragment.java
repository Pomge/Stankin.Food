package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.example.hackinhome2021_stankinfood.models.Product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class CartFragment extends Fragment {

    private static final String ORDER = "order";

    private Order order;

    private TextView textViewRealAddress;
    private TextView textViewPickupTime;
    private TextView textViewRealPickupTime;
    private TextView textViewRealTotalPrice;

    public CartFragment() {
    }

    public static CartFragment newInstance(Order order) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putParcelable(ORDER, order);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            order = getArguments().getParcelable(ORDER);
        }
        if (savedInstanceState != null) {
            order = savedInstanceState.getParcelable(ORDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        initTextViews(view);
        initTextViewPickupTime(view);
        initTextViewTotalPrice(view);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ORDER, order);
        super.onSaveInstanceState(outState);
    }

    private void initTextViewPickupTime(View view) {
        textViewPickupTime = view.findViewById(R.id.textViewPickupTime);

        textViewRealPickupTime = view.findViewById(R.id.textViewRealPickupTime);

        if (order.getPickupTime() != null) {
            String date = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(order.getPickupTime());
            textViewRealPickupTime.setText(date);
        } else textViewRealPickupTime.setText(getResources().getString(R.string.change_time));
    }

    private void initTextViews(View view) {
        textViewRealAddress = view.findViewById(R.id.textViewRealAddress);

    }

    private void initTextViewTotalPrice(View view) {
        textViewRealTotalPrice = view.findViewById(R.id.textViewRealTotalPrice);
        textViewRealTotalPrice.setText(getTotalPriceToString());
    }

    private String getTotalPriceToString() {
        int totalPrice = 0;
        String currency = getResources().getString(R.string.currency);

        for (Product product : order.getPositions()) {
            totalPrice += product.getPrice() * product.getCountForOrder();
        }

        return totalPrice + " " + currency;
    }

    private void initRecyclerViewProducts() {
        
    }
}