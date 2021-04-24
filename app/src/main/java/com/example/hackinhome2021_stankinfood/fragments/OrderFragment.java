package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.adapters.MyRecyclerViewAdapter;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.example.hackinhome2021_stankinfood.models.Product;


public class OrderFragment extends Fragment implements OnRecyclerViewClickListener {
    private static final String ORDER = "order"; //true - кассир, false - клиент

    private boolean isOrder; //true- кассир, false - клиент
    private Order order;
    private ImageView imageViewQR;
    private Button buttonCloseOrder;


    public OrderFragment() {
        // Required empty public constructor
    }

    public static OrderFragment newInstance(Order order) {
        OrderFragment fragment = new OrderFragment();
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

        View view = inflater.inflate(R.layout.fragment_order, container, false);

        initTextViewOrderId(view);
        initTextViewRealTotalPrice(view);
        initRecyclerViewProducts(view);
        initImageViewOR(view);
        initButtonCloseOrder(view);


        return view;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ORDER, order);
        super.onSaveInstanceState(outState);
    }

    private void initTextViewOrderId(View view) {
        TextView textViewOrderId = view.findViewById(R.id.textViewOrderId);
        String orderId = view.getResources().getString(R.string.order_id);
        String realId = order.getOrderId();
        String resultId = orderId + " " + realId;
        textViewOrderId.setText(resultId);
    }

    private void initTextViewRealTotalPrice(View view) {
        TextView textViewRealTotalPrice = view.findViewById(R.id.textViewRealTotalPrice);
        String currency = view.getResources().getString(R.string.currency);
        String result = getTotalPriceToString() + " " + currency;
        textViewRealTotalPrice.setText(result);
    }

    private String getTotalPriceToString() {
        int result = 0;
        for (Product product : order.getPositions()) {
            result += product.getCountForOrder() * product.getPrice();
        }
        return String.valueOf(result);
    }

    private void initRecyclerViewProducts(View view) {
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.getItemAnimator().setChangeDuration(0);
        MyRecyclerViewAdapter myRecyclerViewAdapter = new MyRecyclerViewAdapter(
                order.getPositions(), this);
        recyclerViewProducts.setAdapter(myRecyclerViewAdapter);
    }

    private void initImageViewOR(View view) {
        imageViewQR = view.findViewById(R.id.imageViewQR);
        if (isOrder) {
            imageViewQR.setVisibility(View.GONE);
        }
    }

    private void initButtonCloseOrder(View view) {
        buttonCloseOrder = view.findViewById(R.id.buttonCloseOrder);
        if (isOrder) {
            buttonCloseOrder.setVisibility(View.VISIBLE);
        }
    }


    private void setImageViewQR() {
        //TODO установить QR на ImageView
    }

    private void initTextViewPickupTime() {
        //TODO установить время
    }


    @Override
    public void onItemClick(View view, int position) {

    }
}
