package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.models.Product;

public class ProductFragment extends Fragment implements View.OnClickListener {

    private static final String PRODUCT = "product";

    private Product product;

    private TextView textViewName;
    private ImageView imageViewProductImage;
    private RatingBar ratingBar;
    private TextView textViewProductDescription;
    private Button buttonProductPrice;

    private TextView textViewSinglePrice;
    private TextView textViewTotalPrice;
    private TextView textViewRealSinglePrice;
    private TextView textViewRealTotalPrice;
    private TextView textViewProductsLeft;
    private TextView textViewRealProductsLeft;

    private ImageButton imageButtonMinus;
    private TextView textViewCount;
    private ImageButton imageButtonPlus;

    public ProductFragment() {
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

        initPriceButton(view);
        initImageButton(view);
        initTextViews(view);
        initRatingBar(view);
        initImageView(view);

        return view;
    }

    private void initPriceButton(View view) {
        buttonProductPrice = view.findViewById(R.id.buttonProductPrice);
        String price = product.getPrice() + " " + view.getResources().getString(R.string.currency);
        buttonProductPrice.setText(price);

        buttonProductPrice.setOnClickListener(this);
    }

    private void initImageButton(View view) {
        imageButtonMinus = view.findViewById(R.id.imageButtonMinus);
        imageButtonPlus = view.findViewById(R.id.imageButtonPlus);

        imageButtonMinus.setOnClickListener(this);
        imageButtonPlus.setOnClickListener(this);
    }

    private void initTextViews(View view) {
        textViewName = view.findViewById(R.id.textViewName);
        textViewName.setText(product.getProductName());

        textViewProductDescription = view.findViewById(R.id.textViewProductDescription);
        textViewProductDescription.setText(product.getDescription());

        textViewSinglePrice = view.findViewById(R.id.textViewSinglePrice);
        textViewTotalPrice = view.findViewById(R.id.textViewTotalPrice);

        String singlePrice = product.getPrice() + " " +
                view.getResources().getString(R.string.currency);
        textViewRealSinglePrice = view.findViewById(R.id.textViewRealSinglePrice);
        textViewRealSinglePrice.setText(singlePrice);

        textViewRealTotalPrice = view.findViewById(R.id.textViewRealTotalPrice);
        textViewProductsLeft = view.findViewById(R.id.textViewProductsLeft);
        textViewRealProductsLeft = view.findViewById(R.id.textViewRealProductsLeft);
        textViewCount = view.findViewById(R.id.textViewCount);
    }

    private void initRatingBar(View view) {
        ratingBar = view.findViewById(R.id.ratingBar);
        ratingBar.setRating(product.getRating());
    }

    private void initImageView(View view) {
        imageViewProductImage = view.findViewById(R.id.imageViewProductImage);
    }

    private void setImageForImageView() {
        //TODO установить картинку
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();;

        if (id == R.id.buttonProductPrice) {
            String totalPrice = product.getPrice() + " " +
                    getResources().getString(R.string.currency);

            textViewRealTotalPrice.setText(totalPrice);
            textViewRealProductsLeft.setText(String.valueOf(product.getProductsLeft()));

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
        } else if (id == R.id.imageButtonPlus) {
            int productLeft = product.getProductsLeft() - 1;
            product.setProductsLeft(productLeft);

            if (productLeft > 0) {
                int count = product.getCountForOrder() + 1;
                int totalPrice = product.getPrice() * count;

                product.setCountForOrder(count);
                textViewCount.setText(String.valueOf(product.getCountForOrder()));

                String totalPriceString = totalPrice + " " +
                        v.getResources().getString(R.string.currency);
                textViewRealTotalPrice.setText(totalPriceString);
            }
            textViewRealProductsLeft.setText(String.valueOf(product.getProductsLeft()));

        } else if (id == R.id.imageButtonMinus) {
            int count = product.getCountForOrder() - 1;
            int productLeft = product.getProductsLeft() + 1;
            int realTotalPrice = product.getPrice() * count;

            product.setCountForOrder(count);
            product.setProductsLeft(productLeft);

            textViewCount.setText(String.valueOf(product.getCountForOrder()));
            textViewRealProductsLeft.setText(String.valueOf(product.getProductsLeft()));

            String totalPrice = realTotalPrice + " " +
                    getResources().getString(R.string.currency);
            textViewRealTotalPrice.setText(totalPrice);

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