package com.example.hackinhome2021_stankinfood.fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.example.hackinhome2021_stankinfood.adapters.ProductRecyclerViewAdapter;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.example.hackinhome2021_stankinfood.models.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class CartFragment extends Fragment implements
        View.OnClickListener,
        OnRecyclerViewClickListener {

    private static final String ORDER = "order";

    private Order order;

    private TextView textViewRealAddress;
    private TextView textViewPickupTime;
    private TextView textViewRealPickupTime;
    private TextView textViewClearCart;
    private TextView textViewRealTotalPrice;

    private ProductRecyclerViewAdapter productRecyclerViewAdapter;

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

        initTextViewPickupTime(view);
        initTextViewAddress(view);
        initTextViewClearCart(view);
        initTextViewTotalPrice(view);
        initRecyclerViewProducts(view);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(ORDER, order);
        super.onSaveInstanceState(outState);
    }

    private void initTextViewAddress(View view) {
        textViewRealAddress = view.findViewById(R.id.textViewRealAddress);
    }

    private void initTextViewPickupTime(View view) {
        textViewPickupTime = view.findViewById(R.id.textViewPickupTime);

        textViewRealPickupTime = view.findViewById(R.id.textViewRealPickupTime);

        if (order.getPickupTime() != null) {
            String date = new SimpleDateFormat("HH:mm", Locale.ENGLISH).format(order.getPickupTime());
            textViewRealPickupTime.setText(date);
        } else textViewRealPickupTime.setText(getResources().getString(R.string.change_time));
    }

    private void initTextViewClearCart(View view) {
        textViewClearCart = view.findViewById(R.id.textViewClearCart);
        textViewClearCart.setOnClickListener(this);
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

    private void initRecyclerViewProducts(View view) {
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.getItemAnimator().setChangeDuration(0);
        productRecyclerViewAdapter = new ProductRecyclerViewAdapter(order.getPositions(), this);
        recyclerViewProducts.setAdapter(productRecyclerViewAdapter);
    }


    private void showAlertDialogClearCart() {
        String title = getResources().getString(R.string.clear_cart);
        String message = getResources().getString(R.string.cart_clear_message);
        String buttonOkString = getResources().getString(R.string.ok);
        String buttonCancelString = getResources().getString(R.string.cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonOkString, (dialog, id) -> clearCart());
        builder.setNegativeButton(buttonCancelString, (dialog, id) -> {
        });
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    private void clearCart() {
        order.setPositions(new ArrayList<>());
//        ((MainActivity) getActivity()).removeCartFragment();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.textViewClearCart) {
            showAlertDialogClearCart();
        }
    }


    private void showAlertDialogItemWillRemoved(String productId, int position) {
        String title = getResources().getString(R.string.cart_item_remove_title);
        String message = getResources().getString(R.string.cart_item_remove_message);
        String buttonOkString = getResources().getString(R.string.ok);
        String buttonCancelString = getResources().getString(R.string.cancel);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(buttonOkString, (dialog, id) -> removeItem(productId, position));
        builder.setNegativeButton(buttonCancelString, (dialog, id) -> restoreItem(position));
        builder.setCancelable(false);
        builder.create();
        builder.show();
    }

    private void restoreItem(int position) {
        order.getPositions().get(position).setCountForOrder(1);
        productRecyclerViewAdapter.notifyItemChanged(position);
    }

    private void removeItem(String productId, int position) {
        Log.d("LOG_MESSAGE", "productId: " + productId);
        Log.d("LOG_MESSAGE", "position: " + position);
        productRecyclerViewAdapter.notifyItemRemoved(position);
        productRecyclerViewAdapter.notifyItemRangeChanged(position, order.getPositions().size());
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        Product currentProduct = order.getPositions().get(position);
        int currentCount = currentProduct.getCountForOrder();

        if (id == R.id.imageButtonMinus) {
            if (currentCount - 1 == 0) {
                showAlertDialogItemWillRemoved(currentProduct.getProductId(), position);
            }
            currentProduct.setCountForOrder(currentCount - 1);
        } else if (id == R.id.imageButtonPlus) {
            currentProduct.setCountForOrder(currentCount + 1);
        }
        productRecyclerViewAdapter.notifyItemChanged(position);
    }
}