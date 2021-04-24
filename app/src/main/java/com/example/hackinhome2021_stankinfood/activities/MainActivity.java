package com.example.hackinhome2021_stankinfood.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

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
import com.example.hackinhome2021_stankinfood.fragments.RestaurantsFragment;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.example.hackinhome2021_stankinfood.models.Restaurant;
import com.example.hackinhome2021_stankinfood.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    private final List<String> soupNames = Arrays.asList(
            "Харчо", "Затируха", "Томатный",
            "Куриный", "Любимый", "Солянка", "Гречневый");
    private final List<String> meatNames = Arrays.asList(
            "Свиннина", "Говядина", "Телятина",
            "Баранина", "Крольчатина", "Оленина", "Ягнятина");
    private final List<String> drinkNames = Arrays.asList(
            "7UP", "Lipton", "AQUA",
            "Mirinda", "MountainDew", "Pepsi", "Drive");


    private String currentWeekday;
    private Date currentDate = null;
    private Order userOrder;

    private View parentLayout;
    private ProgressBar progressBar;
    private int previousDirection = 0;
    private int previousBottomNavigationTabId;

    private BottomNavigationView bottomNavigationView;

    private CurrentTimeGetterThread currentTimeGetterThread = null;

    private FirebaseUser firebaseUser = null;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(android.R.id.content);
        initBottomNavigationView();
        progressBar = findViewById(R.id.progressBar);
        previousBottomNavigationTabId = R.id.menuItemRestaurants;

        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            hideBottomNavigationView(true);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.mainContainer, new AuthRegChooseFragment(),
                    AUTH_REG_CHOOSE_FRAGMENT);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            currentTimeGetterThread = new CurrentTimeGetterThread();
            currentTimeGetterThread.start();
        }

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
//                    DateFormat gmtFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
//                    TimeZone gmtTime = TimeZone.getTimeZone("GMT+3");
//                    gmtFormat.setTimeZone(gmtTime);
                    client.disconnect();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                if (currentDate != null) {
                    DateFormat weekdayString = new SimpleDateFormat("EEEE", Locale.ENGLISH);
//                    DateFormat weekdayNumber = new SimpleDateFormat("u", Locale.ENGLISH);
//                    Log.d(TAG, "weekdayNumber.format(currentDate): " + weekdayNumber.format(currentDate));
                    Log.d(TAG, "weekdayString.format(currentDate): " + weekdayString.format(currentDate));
                    currentWeekday = weekdayString.format(currentDate);
                    getRestraintsFromFireStore();
//                    generateMenu();
                    break;
                } else {
                    Log.d(TAG, "currentDate == null!");
                }
            }
        }
    }


//    private void generateMenu() {
//        List<Product> canteenProductListTaken = new ArrayList<>();
//        List<Product> fastFoodProductListTaken = new ArrayList<>();
//
//        for (String drinkName : drinkNames) {
//            Product productCanteen = new Product(
//                    getRandomString(50), null, categoriesNames.get(2), drinkName,
//                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
//                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
//            Product productFastFood = new Product(
//                    getRandomString(50), null, categoriesNames.get(2), drinkName,
//                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
//                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
//
//            canteenProductListTaken.add(productCanteen);
//            fastFoodProductListTaken.add(productFastFood);
//        }
//
//        for (String meatName : meatNames) {
//            Product productCanteen = new Product(
//                    getRandomString(50), null, categoriesNames.get(1), meatName,
//                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
//                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
//            Product productFastFood = new Product(
//                    getRandomString(50), null, categoriesNames.get(1), meatName,
//                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
//                    getRandomInteger(100, 500), getRandomInteger(0, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
//
//            canteenProductListTaken.add(productCanteen);
//            fastFoodProductListTaken.add(productFastFood);
//        }
//
//        for (String soupName : soupNames) {
//            Product productCanteen = new Product(
//                    getRandomString(50), null, categoriesNames.get(0), soupName,
//                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
//                    getRandomInteger(100, 500), getRandomInteger(1, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
//            Product productFastFood = new Product(
//                    getRandomString(50), null, categoriesNames.get(0), soupName,
//                    getRandomString(255), getRandomInteger(1, 10), 0, 0.0f,
//                    getRandomInteger(100, 500), getRandomInteger(1, 100), random.nextBoolean(), MENU_PRODUCT_INACTIVE);
//
//            canteenProductListTaken.add(productCanteen);
//            fastFoodProductListTaken.add(productFastFood);
//        }
//
////        for (Product product : canteenProductListTaken) {
////            product.setRating(((float) product.getLikesCount()) / ((float) canteenProductListTaken.size()));
////        }
//
////        for (Product product : fastFoodProductListTaken) {
////            product.setRating(((float) product.getLikesCount()) / ((float) fastFoodProductListTaken.size()));
////        }
//
//        Collections.sort(canteenProductListTaken, Product.PRODUCT_COMPARATOR);
//        Collections.sort(fastFoodProductListTaken, Product.PRODUCT_COMPARATOR);
//
//        for (Product product : canteenProductListTaken) {
//            addProducts("rkQh04lJYa9cLWto2zx8", product);
//        }
//        for (Product product : fastFoodProductListTaken) {
//            addProducts("XUqz9HedsagJwuZV1ur7", product);
//        }
//
////        userOrder = new Order("Order id", "Order name", new Timestamp(new Date().getTime()), false);
////        userOrder = new Order("Order id", "Order name", null, false);
////
////        for (int i = 0; i < 5; i++) {
////            userOrder.addNewPosition(canteenProductListTaken.get(getRandomInteger(0, canteenProductListTaken.size())));
////            userOrder.addNewPosition(fastFoodProductListTaken.get(getRandomInteger(0, fastFoodProductListTaken.size())));
////        }
//
////        convertForRecyclerView(canteenProductListTaken);
////        convertForRecyclerView(fastFoodProductListTaken);
//
////        canteenProductList.addAll(canteenProductListTaken);
////        fastFoodProductList.addAll(fastFoodProductListTaken);
//    }

//    private void convertForRecyclerView(List<Product> productList) {
//        List<String> categoryNamesList = new ArrayList<>();
//        String savedCategoryName = productList.get(0).getCategoryName();
//        categoryNamesList.add(savedCategoryName);
//
//        for (Product product : productList) {
//            if (!product.getCategoryName().equals(savedCategoryName)) {
//                savedCategoryName = product.getCategoryName();
//                categoryNamesList.add(savedCategoryName);
//            }
//        }
//
//        int index = 0;
//        savedCategoryName = categoryNamesList.get(0);
//        productList.add(0, new Product(
//                null, null, null,
//                savedCategoryName, null, 0,
//                0, 0.0f, 0, 0, false, MENU_HEADER));
//        index++;
//
//        for (int i = 1; i < productList.size(); i++) {
//            if (!productList.get(i).getCategoryName().equals(savedCategoryName)) {
//                productList.add(i, new Product(
//                        null, null, null,
//                        categoryNamesList.get(index), null, 0,
//                        0, 0.0f, 0, 0, false, MENU_HEADER));
//                savedCategoryName = categoryNamesList.get(index);
//                index++;
//            }
//        }
//    }

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
                        Log.d(TAG, "authUserWithEmailAndPassword(): Task Successful!");
                        firebaseUser = firebaseAuth.getCurrentUser();
                        if (!firebaseUser.isEmailVerified()) {
                            Fragment fragment = getSupportFragmentManager().findFragmentByTag(AUTH_REG_FRAGMENT);
                            ((AuthRegFragment) fragment).showSnackBarEmailNotVerified();
                        } else findUserInDatabase();
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
                        if (currentTimeGetterThread == null) {
                            currentTimeGetterThread = new CurrentTimeGetterThread();
                            currentTimeGetterThread.start();
                        }
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
//        fragmentTransaction.replace(R.id.mainContainer,
//                MenuFragment.newInstance(true, restaurantList.get(0).getProductList()), MENU_FRAGMENT);
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


    private void generateData() {
        List<Product> productList = new ArrayList<>();

        for (String soupName : soupNames) {
            Product product = new Product();
            product.setRestaurantId("XUqz9HedsagJwuZV1ur7");
            product.setProductsLeft(getRandomInteger(0, 10));
            product.setProductName(soupName);
            product.setPrice(getRandomInteger(50, 100));
            product.setLikesCount(getRandomInteger(0, 20));
            product.setImageURL(getRandomString(100));
            product.setDescription(getRandomString(255));
            product.setCategoryName(categoriesNames.get(0));
            productList.add(product);
        }

        for (String meatName : meatNames) {
            Product product = new Product();
            product.setRestaurantId("XUqz9HedsagJwuZV1ur7");
            product.setProductsLeft(getRandomInteger(0, 10));
            product.setProductName(meatName);
            product.setPrice(getRandomInteger(50, 100));
            product.setLikesCount(getRandomInteger(0, 20));
            product.setImageURL(getRandomString(100));
            product.setDescription(getRandomString(255));
            product.setCategoryName(categoriesNames.get(1));
            productList.add(product);
        }

        for (String drinkName : drinkNames) {
            Product product = new Product();
            product.setRestaurantId("XUqz9HedsagJwuZV1ur7");
            product.setProductsLeft(getRandomInteger(0, 10));
            product.setProductName(drinkName);
            product.setPrice(getRandomInteger(50, 100));
            product.setLikesCount(getRandomInteger(0, 20));
            product.setImageURL(getRandomString(100));
            product.setDescription(getRandomString(255));
            product.setCategoryName(categoriesNames.get(2));
            productList.add(product);
        }

        for (Product product : productList) {
            firebaseFirestore.collection(COLLECTION_PRODUCTS).add(product);
        }
    }

//    public void removeCartFragment() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(
//                R.anim.enter_from_left, R.anim.exit_to_right,
//                R.anim.enter_from_left, R.anim.exit_to_right);
//        fragmentTransaction.replace(R.id.mainContainer, MenuFragment.newInstance(
//                true, restaurantList.get(0).getProductList()), MENU_FRAGMENT);
//        fragmentTransaction.commit();
//
//        setBottomNavigationViewToZeroPosition();
//    }

    private void setBottomNavigationViewToZeroPosition() {
        previousDirection = 0;
        previousBottomNavigationTabId = R.id.menuItemRestaurants;
        bottomNavigationView.setOnNavigationItemSelectedListener(null);
        bottomNavigationView.setSelectedItemId(R.id.menuItemRestaurants);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }


    private void replaceToRestaurantsFragment(List<Restaurant> restaurantList) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, RestaurantsFragment.newInstance(restaurantList));
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getRestraintsFromFireStore() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection(COLLECTION_RESTAURANTS).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurantList = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Restaurant restaurant = queryDocumentSnapshot.toObject(Restaurant.class);
                            restaurant.setRestaurantId(queryDocumentSnapshot.getId());
                            restaurantList.add(restaurant);
                        }
                        progressBar.setVisibility(View.GONE);
                        replaceToRestaurantsFragment(restaurantList);
                    } else {
                        Log.d(TAG, "getRestraintsFromFireStore(): Failed!");
                    }
                });
    }


    private List<Product> getConvertedProductListForRecyclerView(List<Product> productList, boolean isMenu) {
        for (Product product : productList) {
            product.setRating(((float) product.getLikesCount()) / ((float) productList.size()));
            product.setViewType(MainActivity.MENU_PRODUCT_INACTIVE);
        }

        Collections.sort(productList, Product.PRODUCT_COMPARATOR);
        if (isMenu) convertForRecyclerView(productList);

        return productList;
    }

    private void convertForRecyclerView(List<Product> productList) {
        List<String> categoryNamesList = new ArrayList<>();

        for (Product product : productList) {
            String savedCategoryName = product.getCategoryName();
            if (!categoryNamesList.contains(savedCategoryName)) {
                categoryNamesList.add(savedCategoryName);
            }
        }

        int index = 0;
        String savedCategoryName = categoryNamesList.get(0);
        Product firstHeader = new Product();
        firstHeader.setCategoryName(savedCategoryName);
        firstHeader.setViewType(MainActivity.MENU_HEADER);
        productList.add(0, firstHeader);
        index++;

        for (int i = 1; i < productList.size(); i++) {
            if (!productList.get(i).getCategoryName().equals(savedCategoryName)) {
                savedCategoryName = categoryNamesList.get(index);
                Product categoryName = new Product();
                categoryName.setCategoryName(savedCategoryName);
                categoryName.setViewType(MainActivity.MENU_HEADER);
                productList.add(i, categoryName);
                index++;
            }
        }
    }

    private void replaceToMenuFragment(boolean isMenu, List<Product> productList) {
        List<Product> result = getConvertedProductListForRecyclerView(productList, isMenu);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContainer, MenuFragment.newInstance(
                isMenu, result));
        if (isMenu) fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void getFavoriteProductsFromFireStore() {
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection(COLLECTION_PRODUCTS)
                .whereArrayContains("likedUserIds", firebaseUser.getUid())
                .whereGreaterThanOrEqualTo("productsLeft", 1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Product product = queryDocumentSnapshot.toObject(Product.class);
                            product.setProductId(queryDocumentSnapshot.getId());
                            product.setLiked(true);
                            productList.add(product);
                        }
                        progressBar.setVisibility(View.GONE);
                        replaceToMenuFragment(false, productList);
                        Log.d(TAG, "getFavoriteProductsFromFireStore(): Success!");
                    } else {
                        Log.d(TAG, "getFavoriteProductsFromFireStore(): Failed!");
                    }
                });
    }

    public void getProductsFromFireStore(Restaurant restaurant) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection(COLLECTION_PRODUCTS)
                .whereEqualTo("restaurantId", restaurant.getRestaurantId())
                .whereGreaterThanOrEqualTo("productsLeft", 1).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Product> productList = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Product product = queryDocumentSnapshot.toObject(Product.class);
                            product.setProductId(queryDocumentSnapshot.getId());
                            productList.add(product);
                        }
                        progressBar.setVisibility(View.GONE);
                        replaceToMenuFragment(true, productList);
                        Log.d(TAG, "getProductsFromFireStore(): Success!");
                    } else {
                        Log.d(TAG, "getProductsFromFireStore(): Failed!");
                    }
                });
    }

    public void markProductAsLiked(Product product, boolean isLiked) {
        firebaseFirestore.collection(COLLECTION_PRODUCTS).document(product.getProductId())
                .update("likedUserIds", isLiked ?
                        FieldValue.arrayUnion(firebaseUser.getUid()) :
                        FieldValue.arrayRemove(firebaseUser.getUid()))
                .addOnCompleteListener(task -> {
                    String message = isLiked ? getResources().getString(R.string.like_add) :
                            getResources().getString(R.string.like_remove);
                    Snackbar.make(parentLayout, message,
                            BaseTransientBottomBar.LENGTH_SHORT).show();
                });
    }

//    private void readRestaurant_badWayForHack() {
//        firebaseFirestore.collection(COLLECTION_RESTAURANTS).document(CANTEEN_DOCUMENT_ID).get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        Restaurant restaurant = task.getResult().toObject(Restaurant.class);
//                        restaurant.setRestaurantId(task.getResult().getId());
//                        restaurantList.add(restaurant);
//
//                        firebaseFirestore.collection(COLLECTION_RESTAURANTS).document(restaurant.getRestaurantId())
//                                .collection(COLLECTION_PRODUCTS)
//                                .whereEqualTo("onMenuWeekdays." + currentWeekday, true)
//                                .whereGreaterThan("count", 0)
//                                .get()
//                                .addOnCompleteListener(task1 -> {
//                                    if (task1.isSuccessful()) {
//                                        List<Product> productList = new ArrayList<>();
//                                        for (QueryDocumentSnapshot document : task1.getResult()) {
//                                            Product product = document.toObject(Product.class);
//                                            product.setProductId(document.getId());
//                                            productList.add(product);
//                                        }
//                                        restaurantList.get(0).setProductList(productList);
//
//                                        firebaseFirestore.collection(COLLECTION_RESTAURANTS).document(CAFE_DOCUMENT_ID).get()
//                                                .addOnCompleteListener(task11 -> {
//                                                    if (task11.isSuccessful()) {
//                                                        Restaurant restaurant1 = task11.getResult().toObject(Restaurant.class);
//                                                        restaurant1.setRestaurantId(task11.getResult().getId());
//                                                        restaurantList.add(restaurant1);
//
//                                                        firebaseFirestore.collection(COLLECTION_RESTAURANTS).document(restaurant.getRestaurantId())
//                                                                .collection(COLLECTION_PRODUCTS)
//                                                                .whereEqualTo("onMenuWeekdays." + currentWeekday, true)
//                                                                .whereGreaterThan("count", 0)
//                                                                .get()
//                                                                .addOnCompleteListener(task111 -> {
//                                                                    List<Product> productList1 = new ArrayList<>();
//                                                                    for (QueryDocumentSnapshot document : task111.getResult()) {
//                                                                        Product product = document.toObject(Product.class);
//                                                                        product.setProductId(document.getId());
//                                                                        productList1.add(product);
//                                                                    }
//                                                                    restaurantList.get(1).setProductList(productList1);
//                                                                    setFragmentMenu();
//                                                                });
//                                                    }
//                                                });
//                                    }
//                                });
//                    }
//                });
//    }

//    private void readAllRestaurants() {
//        firebaseFirestore.collection(COLLECTION_RESTAURANTS).get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        restaurantList = new ArrayList<>();
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Restaurant restaurant = document.toObject(Restaurant.class);
//                            restaurant.setRestaurantId(document.getId());
//                            readAndWriteTodayRestaurantMenu(restaurant);
//                            restaurantList.add(restaurant);
//                        }
//                    }
//                });
//    }

//    private void readAndWriteTodayRestaurantMenu(Restaurant restaurant) {
//        Log.d("LOG_MESSAGE", currentWeekday);
//        firebaseFirestore.collection(COLLECTION_RESTAURANTS).document(restaurant.getRestaurantId())
//                .collection(COLLECTION_PRODUCTS)
//                .whereEqualTo("onMenuWeekdays." + currentWeekday, true)
//                .whereGreaterThan("count", 0)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<Product> productList = new ArrayList<>();
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Product product = document.toObject(Product.class);
//                            product.setProductId(document.getId());
//                            productList.add(product);
//                            Log.d("LOG_MESSAGE", product.toString());
//                        }
//                        restaurant.setProductList(productList);
//                    }
//                });
//    }

//    private void readLikedProducts(Restaurant restaurant) {
//        firebaseFirestore.collection(COLLECTION_RESTAURANTS).document(restaurant.getRestaurantId())
//                .collection(COLLECTION_PRODUCTS)
//                .whereArrayContains(currentWeekday, true)
//                .whereGreaterThan("count", 0)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        List<Product> productList = new ArrayList<>();
//
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Product product = document.toObject(Product.class);
//                            product.setProductId(document.getId());
//                            productList.add(product);
//                        }
//                    }
//                });
//    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id != previousBottomNavigationTabId) {
            int currentDirection = 0;
            if (id == R.id.menuItemRestaurants) {
                getRestraintsFromFireStore();
//            } else if (id == R.id.menuItemOrders) {
//                currentDirection = 1;
//
            } else if (id == R.id.menuItemFavoriteProducts) {
                getFavoriteProductsFromFireStore();
            }
//            else if (id == R.id.menuItemCart) {
//                currentDirection = 3;
//                fragment = new CartFragment();
//            }
//
//            if (previousDirection < currentDirection) {
//                fragmentTransaction.setCustomAnimations(
//                        R.anim.enter_from_right, R.anim.exit_to_left,
//                        R.anim.enter_from_right, R.anim.exit_to_left);
//            } else {
//                fragmentTransaction.setCustomAnimations(
//                        R.anim.enter_from_left, R.anim.exit_to_right,
//                        R.anim.enter_from_left, R.anim.exit_to_right);
//            }
//
//            if (id == R.id.menuItemRestaurants || id == R.id.menuItemCafe) {
//                fragmentTransaction.replace(R.id.mainContainer, fragment, MENU_FRAGMENT);
//            } else fragmentTransaction.replace(R.id.mainContainer, fragment);
//            fragmentTransaction.commit();
//
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