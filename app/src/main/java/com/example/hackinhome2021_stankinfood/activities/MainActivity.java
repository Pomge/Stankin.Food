package com.example.hackinhome2021_stankinfood.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.fragments.MenuFragment;
import com.example.hackinhome2021_stankinfood.fragments.ProductFragment;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.example.hackinhome2021_stankinfood.models.Restaurant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    public static final int MENU_HEADER = 0;
    public static final int MENU_PRODUCT_INACTIVE = 1;
    public static final int MENU_PRODUCT_ACTIVE = 2;
    public static final int ORDER_PRODUCT_INACTIVE = 3;
    public static final int ORDER_PRODUCT_ACTIVE = 4;

    private static final String COLLECTION_RESTAURANTS = "restaurants";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_PRODUCTS = "products";
    private static final String COLLECTION_FAVORITE_ORDERS = "favoriteOrders";


    private final Random random = new SecureRandom();
    private final List<String> categoriesNames = Arrays.asList(
            "Супы", "Мясо", "Напитки");
    //    private final List<String> categoriesNames = Arrays.asList(
//            "AAA", "BBB", "CCC");
    private final List<String> soupNames = Arrays.asList(
            "Харчо", "Затируха", "Томатный",
            "Куриный", "Любимый", "Солянка", "Гречневый");
    //    private final List<String> soupNames = Arrays.asList(
//            "AAA", "BBB", "CCC",
//            "DDD", "EEE", "FFF", "GGG");
    private final List<String> meatNames = Arrays.asList(
            "Свиннина", "Говядина", "Телятина",
            "Баранина", "Крольчатина", "Оленина", "Ягнятина");
    //    private final List<String> meatNames = Arrays.asList(
//            "AAA", "BBB", "CCC",
//            "DDD", "EEE", "FFF", "GGG");
    private final List<String> drinkNames = Arrays.asList(
            "7UP", "Lipton", "AQUA",
            "Mirinda", "MountainDew", "Pepsi", "Drive");
//    private final List<String> drinkNames = Arrays.asList(
//            "AAA", "BBB", "CCC",
//            "DDD", "EEE", "FFF", "GGG");


    private Date currentDate = null;
    private List<Product> canteenProductList;
    private List<Product> fastFoodProductList;

    private int previousDirection = 0;
    private int previousBottomNavigationTabId;

    private FragmentManager fragmentManager;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBottomNavigationView();
        previousBottomNavigationTabId = R.id.canteen;

        generateMenu();

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, MenuFragment.newInstance(
                true, canteenProductList));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

//        CurrentTimeGetterThread currentTimeGetterThread = new CurrentTimeGetterThread();
//        currentTimeGetterThread.start();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    private class CurrentTimeGetterThread extends Thread {
        @Override
        public void run() {
            TimeTCPClient client = new TimeTCPClient();

            while (true) {
                Log.d(TAG, "retry");

                try {
                    client.connect("time.nist.gov");
                    client.setKeepAlive(false);

                    currentDate = client.getDate();
                    DateFormat gmtFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                    TimeZone gmtTime = TimeZone.getTimeZone("GMT+3");
                    gmtFormat.setTimeZone(gmtTime);
                    client.disconnect();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                if (currentDate != null) {
                    DateFormat weekdayString = new SimpleDateFormat("EEEE", Locale.ENGLISH);
                    DateFormat weekdayNumber = new SimpleDateFormat("u", Locale.ENGLISH);
                    Log.d(TAG, "weekdayNumber.format(currentDate): " + weekdayNumber.format(currentDate));
                    Log.d(TAG, "weekdayString.format(currentDate): " + weekdayString.format(currentDate));
                    break;
                } else {
                    Log.d(TAG, "currentDate == null!");
                }
            }
        }
    }


    private void generateMenu() {
        canteenProductList = new ArrayList<>();
        fastFoodProductList = new ArrayList<>();

        List<Product> canteenProductListTaken = new ArrayList<>();
        List<Product> fastFoodProductListTaken = new ArrayList<>();

        for (String drinkName : drinkNames) {
            Product productCanteen = new Product(
                    getRandomString(50), null, categoriesNames.get(2), drinkName,
                    getRandomString(255), getRandomInteger(0, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
            Product productFastFood = new Product(
                    getRandomString(50), null, categoriesNames.get(2), drinkName,
                    getRandomString(255), getRandomInteger(0, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);

            canteenProductListTaken.add(productCanteen);
            fastFoodProductListTaken.add(productFastFood);
        }

        for (String meatName : meatNames) {
            Product productCanteen = new Product(
                    getRandomString(50), null, categoriesNames.get(1), meatName,
                    getRandomString(255), getRandomInteger(0, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
            Product productFastFood = new Product(
                    getRandomString(50), null, categoriesNames.get(1), meatName,
                    getRandomString(255), getRandomInteger(0, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);

            canteenProductListTaken.add(productCanteen);
            fastFoodProductListTaken.add(productFastFood);
        }

        for (String soupName : soupNames) {
            Product productCanteen = new Product(
                    getRandomString(50), null, categoriesNames.get(0), soupName,
                    getRandomString(255), getRandomInteger(0, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
            Product productFastFood = new Product(
                    getRandomString(50), null, categoriesNames.get(0), soupName,
                    getRandomString(255), getRandomInteger(0, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);

            canteenProductListTaken.add(productCanteen);
            fastFoodProductListTaken.add(productFastFood);
        }

        Collections.sort(canteenProductListTaken, Product.PRODUCT_COMPARATOR);
        Collections.sort(fastFoodProductListTaken, Product.PRODUCT_COMPARATOR);

        for (Product product : canteenProductListTaken) {
            product.setRating(((float) product.getLikesCount()) / ((float) canteenProductListTaken.size()));
        }

        for (Product product : fastFoodProductListTaken) {
            product.setRating(((float) product.getLikesCount()) / ((float) canteenProductListTaken.size()));
        }

        convertForRecyclerView(canteenProductListTaken);
        convertForRecyclerView(fastFoodProductListTaken);

        canteenProductList.addAll(canteenProductListTaken);
        fastFoodProductList.addAll(fastFoodProductListTaken);
    }

    private void convertForRecyclerView(List<Product> productList) {
        List<String> categoryNamesList = new ArrayList<>();
        String savedCategoryName = productList.get(0).getCategoryName();
        categoryNamesList.add(savedCategoryName);

        for (Product product : productList) {
            if (!product.getCategoryName().equals(savedCategoryName)) {
                savedCategoryName = product.getCategoryName();
                categoryNamesList.add(savedCategoryName);
            }
        }

        int index = 0;
        savedCategoryName = categoryNamesList.get(0);
        productList.add(0, new Product(
                null, null, null,
                savedCategoryName, null, 0,
                0, 0.0f, 0, 0, false, MENU_HEADER));
        index++;

        for (int i = 1; i < productList.size(); i++) {
            if (!productList.get(i).getCategoryName().equals(savedCategoryName)) {
                productList.add(i, new Product(
                        null, null, null,
                        categoryNamesList.get(index), null, 0,
                        0, 0.0f, 0, 0, false, MENU_HEADER));
                savedCategoryName = categoryNamesList.get(index);
                index++;
            }
        }
    }

    private String getRandomString(int length) {
        int leftLimit = 97;     // letter 'a'
        int rightLimit = 122;   // letter 'z'

        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit +
                    (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }

        return buffer.toString();
    }

    private int getRandomInteger(int min, int max) {
        return random.nextInt(max - min) + min;
    }


    public void replaceFragmentToProductFragment(int position) {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_bottom, R.anim.exit_to_top,
                R.anim.enter_from_bottom, R.anim.exit_to_top);
        fragmentTransaction.replace(R.id.mainContainer, ProductFragment.newInstance(
                canteenProductList.get(position)));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void replaceFragmentFromProductFragmentToMenuFragment() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_top, R.anim.exit_to_bottom,
                R.anim.enter_from_top, R.anim.exit_to_bottom);
        fragmentTransaction.replace(R.id.mainContainer, MenuFragment.newInstance(
                true, canteenProductList));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id != previousBottomNavigationTabId) {
            int currentDirection = 0;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (previousDirection < currentDirection) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_right, R.anim.exit_to_left);
            } else {
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_left, R.anim.exit_to_right,
                        R.anim.enter_from_left, R.anim.exit_to_right);
            }
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (!(fragment instanceof OnBackPressedFragment) ||
                !((OnBackPressedFragment) fragment).onBackPressed()) {
            if (fragmentManager.getBackStackEntryCount() == 1) {
                finish();
            } else super.onBackPressed();
        }
    }
}