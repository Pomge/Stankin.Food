package com.example.hackinhome2021_stankinfood.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.hackinhome2021_stankinfood.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String COLLECTION_RESTAURANTS = "restaurants";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_PRODUCTS = "products";
    private static final String COLLECTION_FAVORITE_ORDERS = "favoriteOrders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}