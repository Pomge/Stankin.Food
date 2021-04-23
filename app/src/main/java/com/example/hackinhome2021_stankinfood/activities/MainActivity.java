package com.example.hackinhome2021_stankinfood.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.hackinhome2021_stankinfood.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

    private Date currentDate = null;
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

        fragmentManager = getSupportFragmentManager();

        CurrentTimeGetterThread currentTimeGetterThread = new CurrentTimeGetterThread();
        currentTimeGetterThread.start();
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id != previousBottomNavigationTabId) {
            int currentDirection = 0;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (previousDirection < currentDirection) {
//                fragmentTransaction.setCustomAnimations(
//                        R.anim.enter_from_right, R.anim.exit_to_left,
//                        R.anim.enter_from_right, R.anim.exit_to_left);
            } else {
//                fragmentTransaction.setCustomAnimations(
//                        R.anim.enter_from_left, R.anim.exit_to_right,
//                        R.anim.enter_from_left, R.anim.exit_to_right);
            }
        }

        return true;
    }
}