package com.example.hackinhome2021_stankinfood.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.fragments.AuthRegChooseFragment;
import com.example.hackinhome2021_stankinfood.fragments.AuthRegFragment;
import com.example.hackinhome2021_stankinfood.fragments.CartFragment;
import com.example.hackinhome2021_stankinfood.fragments.MenuFragment;
import com.example.hackinhome2021_stankinfood.fragments.ProductFragment;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.example.hackinhome2021_stankinfood.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
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
    private static final String TAG = "LOG_MESSAGE";

    private static final String AUTH_REG_CHOOSE_FRAGMENT = "AUTH_REG_CHOOSE_FRAGMENT";
    private static final String AUTH_REG_FRAGMENT = "AUTH_REG_FRAGMENT";
    private static final String MENU_FRAGMENT = "MENU_FRAGMENT";
    private static final String PRODUCT_FRAGMENT = "PRODUCT_FRAGMENT";

    public static final int MENU_HEADER = 0;
    public static final int MENU_PRODUCT_INACTIVE = 1;
    public static final int MENU_PRODUCT_ACTIVE = 2;
    public static final int ORDER_PRODUCT_INACTIVE = 3;
    public static final int ORDER_PRODUCT_ACTIVE = 4;

    private static final String COLLECTION_RESTAURANTS = "restaurants";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String COLLECTION_PRODUCTS = "products";
    private static final String COLLECTION_FAVORITE_ORDERS = "favoriteOrders";
    private static final String COLLECTION_USERS = "users";


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
    private Order userOrder;
    private List<Product> canteenProductList;
    private List<Product> fastFoodProductList;

    private int previousDirection = 0;
    private int previousBottomNavigationTabId;

    private BottomNavigationView bottomNavigationView;

    private FirebaseUser firebaseUser = null;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBottomNavigationView();
        previousBottomNavigationTabId = R.id.menuItemCanteen;

        generateMenu();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            hideBottomNavigationView(true);
            fragmentTransaction.replace(R.id.mainContainer, new AuthRegChooseFragment(),
                    AUTH_REG_CHOOSE_FRAGMENT);
        } else {
            fragmentTransaction.replace(R.id.mainContainer, MenuFragment.newInstance(
                    true, canteenProductList), MENU_FRAGMENT);
        }
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        CurrentTimeGetterThread currentTimeGetterThread = new CurrentTimeGetterThread();
        currentTimeGetterThread.start();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void initBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
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
                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
            Product productFastFood = new Product(
                    getRandomString(50), null, categoriesNames.get(2), drinkName,
                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);

            canteenProductListTaken.add(productCanteen);
            fastFoodProductListTaken.add(productFastFood);
        }

        for (String meatName : meatNames) {
            Product productCanteen = new Product(
                    getRandomString(50), null, categoriesNames.get(1), meatName,
                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
            Product productFastFood = new Product(
                    getRandomString(50), null, categoriesNames.get(1), meatName,
                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);

            canteenProductListTaken.add(productCanteen);
            fastFoodProductListTaken.add(productFastFood);
        }

        for (String soupName : soupNames) {
            Product productCanteen = new Product(
                    getRandomString(50), null, categoriesNames.get(0), soupName,
                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(1, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
            Product productFastFood = new Product(
                    getRandomString(50), null, categoriesNames.get(0), soupName,
                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
                    getRandomInteger(100, 500), getRandomInteger(1, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);

            canteenProductListTaken.add(productCanteen);
            fastFoodProductListTaken.add(productFastFood);
        }

        for (Product product : canteenProductListTaken) {
            product.setRating(((float) product.getLikesCount()) / ((float) canteenProductListTaken.size()));
        }

        for (Product product : fastFoodProductListTaken) {
            product.setRating(((float) product.getLikesCount()) / ((float) fastFoodProductListTaken.size()));
        }

        Collections.sort(canteenProductListTaken, Product.PRODUCT_COMPARATOR);
        Collections.sort(fastFoodProductListTaken, Product.PRODUCT_COMPARATOR);

        userOrder = new Order("Order id", "Order name", new Timestamp(new Date().getTime()), false);
        userOrder = new Order("Order id", "Order name", null, false);

        for (int i = 0; i < 5; i++) {
            userOrder.addNewPosition(canteenProductListTaken.get(getRandomInteger(0, canteenProductListTaken.size())));
            userOrder.addNewPosition(fastFoodProductListTaken.get(getRandomInteger(0, fastFoodProductListTaken.size())));
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


    public void createUserWithEmailAndPassword(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmailAndPassword(): Task Successful!");
                        hideKeyboard(this);

                        firebaseUser = firebaseAuth.getCurrentUser();
                        firebaseUser.sendEmailVerification();

                        Fragment fragment = getSupportFragmentManager().findFragmentByTag(AUTH_REG_FRAGMENT);
                        ((AuthRegFragment) fragment).showAlertDialogVerificationMessage(email);
                    } else {
                        Log.d(TAG, "createUserWithEmailAndPassword(): Task Failure!");
                    }
                });
    }

    public void authUserWithEmailAndPassword(String email, String password) {
        hideKeyboard(this);

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        firebaseUser = firebaseAuth.getCurrentUser();
                        if (!firebaseUser.isEmailVerified()) {
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(AUTH_REG_FRAGMENT);
                            ((AuthRegFragment) fragment).showSnackBarEmailNotVerified();
                            return;
                        } else findUserInDatabase();
                        Log.d(TAG, "authUserWithEmailAndPassword(): Task Successful!");
                    } else {
                        Log.d(TAG, "authUserWithEmailAndPassword(): Task Failure!");
                    }
                });
    }


    public void signInWithGoogle() {
        Intent signInWithGoogle = googleSignInClient.getSignInIntent();
        startActivityForResult(signInWithGoogle, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("LOG_MESSAGE", "onActivityResult: " + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException apiException) {
                apiException.printStackTrace();
            }
        }
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential:success");
                            firebaseUser = firebaseAuth.getCurrentUser();
                            findUserInDatabase();
//                            updateUI(user);
                        } else {

                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }


    private void findUserInDatabase() {
        User user = new User();
        user.setUserId(firebaseUser.getUid());
        user.setRestaurantId(null);

        firebaseFirestore.collection(COLLECTION_USERS).whereEqualTo(
                "userId", firebaseUser.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "findUserInDatabase(): Task Successful!");
                        if (task.getResult().isEmpty()) {
                            createUserInDatabase(user);
                        } else setFragmentMenuFragment();
                    } else {
                        Log.d(TAG, "findUserInDatabase(): Task Failure!");
                    }
                });
    }

    private void createUserInDatabase(User user) {
        firebaseFirestore.collection(COLLECTION_USERS).document().set(user)
                .addOnCompleteListener(taskInner -> {
                    if (taskInner.isSuccessful()) {
                        Log.d(TAG, "createUserInDatabase(): Task Successful!");
                        setFragmentMenuFragment();
                    } else {
                        Log.d(TAG, "createUserInDatabase(): Task Failure!");
                    }
                });
    }

    public void sendResetPasswordByEmail(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(AUTH_REG_FRAGMENT);
                    ((AuthRegFragment) fragment).showSnackBarResetPassword(email);
                })
                .addOnFailureListener(e -> Log.d("LOG_MESSAGE", "sendResetPasswordByEmail(): Failture!"));
    }


    private void setFragmentMenuFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentManager.findFragmentByTag(AUTH_REG_FRAGMENT));
        fragmentTransaction.remove(fragmentManager.findFragmentByTag(AUTH_REG_CHOOSE_FRAGMENT));
        fragmentTransaction.replace(R.id.mainContainer,
                MenuFragment.newInstance(true, canteenProductList), MENU_FRAGMENT);
        fragmentTransaction.commit();
        hideBottomNavigationView(false);
    }

    public void addFragmentAuthRegFragment(boolean isRegistration) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (isRegistration) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_left, R.anim.exit_to_right,
                    R.anim.enter_from_right, R.anim.exit_to_left);
        }
        fragmentTransaction.hide(fragmentManager.findFragmentByTag(AUTH_REG_CHOOSE_FRAGMENT));
        fragmentTransaction.add(R.id.mainContainer,
                AuthRegFragment.newInstance(isRegistration), AUTH_REG_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void switchToFragmentAuthFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentManager.findFragmentByTag(AUTH_REG_FRAGMENT));
        fragmentTransaction.commit();

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_left, R.anim.exit_to_right,
                R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.mainContainer,
                AuthRegFragment.newInstance(false), AUTH_REG_FRAGMENT);
        fragmentTransaction.commit();
    }

    public void addFragmentProductFragment(List<Product> productList, int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_bottom, R.anim.exit_to_top,
                R.anim.enter_from_top, R.anim.exit_to_bottom);
        fragmentTransaction.hide(fragmentManager.findFragmentByTag(MENU_FRAGMENT));
        fragmentTransaction.add(R.id.mainContainer, ProductFragment.newInstance(
                productList.get(position)), PRODUCT_FRAGMENT);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void restoreCardViewClickListener() {
        Fragment menuFragment = getSupportFragmentManager().findFragmentByTag(MENU_FRAGMENT);
        ((MenuFragment) menuFragment).restoreCardViewClick();
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideBottomNavigationView(boolean hide) {
        if (hide) {
            bottomNavigationView.setVisibility(View.GONE);
        } else bottomNavigationView.setVisibility(View.VISIBLE);
    }


    public void removeCartFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(
                R.anim.enter_from_left, R.anim.exit_to_right,
                R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.mainContainer, MenuFragment.newInstance(
                true, canteenProductList), MENU_FRAGMENT);
        fragmentTransaction.commit();

        setBottomNavigationViewToZeroPosition();
    }

    private void setBottomNavigationViewToZeroPosition() {
        previousDirection = 0;
        previousBottomNavigationTabId = R.id.menuItemCanteen;
        bottomNavigationView.setOnNavigationItemSelectedListener(null);
        bottomNavigationView.setSelectedItemId(R.id.menuItemCanteen);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id != previousBottomNavigationTabId) {
            int currentDirection = 0;
            Fragment fragment = MenuFragment.newInstance(true, canteenProductList);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            if (id == R.id.menuItemCanteen) {
                fragment = MenuFragment.newInstance(true, canteenProductList);
            } else if (id == R.id.menuItemCafe) {
                currentDirection = 1;
                fragment = MenuFragment.newInstance(true, fastFoodProductList);
            } else if (id == R.id.menuItemOrders) {
                currentDirection = 2;

            } else if (id == R.id.menuItemFavoriteProducts) {
                currentDirection = 3;
                fragment = MenuFragment.newInstance(false, fastFoodProductList);
            } else if (id == R.id.menuItemCart) {
                currentDirection = 4;
                fragment = CartFragment.newInstance(userOrder);
            }

            if (previousDirection < currentDirection) {
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_right, R.anim.exit_to_left);
            } else {
                fragmentTransaction.setCustomAnimations(
                        R.anim.enter_from_left, R.anim.exit_to_right,
                        R.anim.enter_from_left, R.anim.exit_to_right);
            }

            if (id == R.id.menuItemCanteen || id == R.id.menuItemCafe) {
                fragmentTransaction.replace(R.id.mainContainer, fragment, MENU_FRAGMENT);
            } else fragmentTransaction.replace(R.id.mainContainer, fragment);
            fragmentTransaction.commit();

            previousDirection = currentDirection;
            previousBottomNavigationTabId = id;
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainContainer);
        if (!(fragment instanceof OnBackPressedFragment) ||
                !((OnBackPressedFragment) fragment).onBackPressed()) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                finish();
            } else super.onBackPressed();
        }
    }
}