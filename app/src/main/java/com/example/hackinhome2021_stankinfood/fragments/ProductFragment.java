package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.models.Product;

public class ProductFragment extends Fragment implements View.OnClickListener {

    private static final String PRODUCT = "product";

    private Product product;

    private TextView textViewName;
    private ImageView imageViewProductImage;
    private RatingBar ratingBar;
    private TextView textViewProductDescription;
    private TextView textViewSinglePrice;
    private TextView textViewTotalPrice;
    private TextView textViewRealSinglePrice;
    private TextView textViewRealTotalPrice;
    private TextView textViewProductsLeft;
    private TextView textViewRealProductsLeft;
    private Button buttonProductPrice;
    private ImageButton imageButtonMinus;
    private TextView textViewCount;
    private ImageButton imageButtonPlus;

    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance(Product product) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putParcelable(PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable(PRODUCT);
        }
        if (savedInstanceState != null) {
            product = savedInstanceState.getParcelable(PRODUCT);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(PRODUCT, product);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        initButton(view);
        initImageButton(view);
        initTexts(view);
        initRatingBar(view);
        initImage(view);
        return view;
    }

    private void initButton(View view) {
        buttonProductPrice = view.findViewById(R.id.buttonProductPrice);
        String string = product.getPrice() + " " + view.getResources().getString(R.string.currency);
        buttonProductPrice.setText(string);
    }

    private void initImageButton(View view) {
        imageButtonMinus = view.findViewById(R.id.imageButtonMinus);
        imageButtonPlus = view.findViewById(R.id.imageButtonPlus);
    }

    private void initTexts(View view) {
        String stringRealSinglePrice = product.getPrice() +
                " " + view.getResources().getString(R.string.currency);
        textViewName = view.findViewById(R.id.textViewName);
        textViewName.setText(product.getProductName());
        textViewProductDescription = view.findViewById(R.id.textViewProductDescription);
        textViewProductDescription.setText(product.getDescription());
        textViewSinglePrice = view.findViewById(R.id.textViewSinglePrice);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);
        textViewRealSinglePrice = view.findViewById(R.id.textViewRealSinglePrice);
        textViewRealSinglePrice.setText(stringRealSinglePrice);
        textViewRealTotalPrice = view.findViewById(R.id.textViewRealTotalPrice);
        textViewProductsLeft = view.findViewById(R.id.textViewProductsLeft);
        textViewRealProductsLeft = view.findViewById(R.id.textViewRealProductsLeft);
        textViewCount = view.findViewById(R.id.textViewCount);
    }

    private void initRatingBar(View view) {
        ratingBar = view.findViewById(R.id.ratingBar);
    }

    private void initImage(View view) {
        imageViewProductImage = view.findViewById(R.id.imageViewProductImage);
    }

    private void setImageViewProductImage() {
        //TODO установить картинку
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.buttonProductPrice) {
            String stringRealTotalPrice = product.getPrice() +
                    " " + v.getResources().getString(R.string.currency);
            textViewRealTotalPrice.setText(stringRealTotalPrice);
            textViewRealProductsLeft.setText(product.getProductsLeft());
            product.setCountForOrder(1);
            textViewCount.setText(String.valueOf(product.getCountForOrder()));
            buttonProductPrice.setVisibility(View.GONE);
            imageButtonPlus.setVisibility(View.VISIBLE);
            imageButtonMinus.setVisibility(View.VISIBLE);
            textViewCount.setVisibility(View.VISIBLE);
            textViewRealTotalPrice.setVisibility(View.VISIBLE);
            textViewRealSinglePrice.setVisibility(View.VISIBLE);
            textViewTotalPrice.setVisibility(View.VISIBLE);
            textViewSinglePrice.setVisibility(View.VISIBLE);
            textViewProductsLeft.setVisibility(View.VISIBLE);
            textViewRealProductsLeft.setVisibility(View.VISIBLE);
        }
        else if (id == R.id.imageButtonPlus) {
            int productLeft = product.getProductsLeft() - 1;
            product.setProductsLeft(productLeft);
            if (productLeft > 0) {
                int count = product.getCountForOrder() + 1;
                int realTotalPrice = product.getPrice() * count;
                product.setCountForOrder(count);
                textViewCount.setText(String.valueOf(product.getCountForOrder()));
                String stringRealTotalPrice = realTotalPrice +
                        " " + v.getResources().getString(R.string.currency);
                textViewRealTotalPrice.setText(stringRealTotalPrice);
            }
            textViewRealProductsLeft.setText(String.valueOf(product.getProductsLeft()));
        }
        else if (id == R.id.imageButtonMinus) {
            int count = product.getCountForOrder() - 1;
            int productLeft = product.getProductsLeft() - 1;
            int realTotalPrice = product.getPrice() * count;
            product.setCountForOrder(count);
            textViewCount.setText(String.valueOf(product.getCountForOrder()));
            product.setProductsLeft(productLeft);
            textViewRealProductsLeft.setText(String.valueOf(product.getProductsLeft()));
            String stringRealTotalPrice = realTotalPrice +
                    " " + v.getResources().getString(R.string.currency);
            textViewRealTotalPrice.setText(stringRealTotalPrice);
            if (count == 0) {
                buttonProductPrice.setVisibility(View.VISIBLE);
                imageButtonPlus.setVisibility(View.GONE);
                imageButtonMinus.setVisibility(View.GONE);
                textViewCount.setVisibility(View.GONE);
                textViewRealTotalPrice.setVisibility(View.GONE);
                textViewRealSinglePrice.setVisibility(View.GONE);
                textViewTotalPrice.setVisibility(View.GONE);
                textViewSinglePrice.setVisibility(View.GONE);
                textViewProductsLeft.setVisibility(View.GONE);
                textViewRealProductsLeft.setVisibility(View.GONE);
            }
        }
    }
}