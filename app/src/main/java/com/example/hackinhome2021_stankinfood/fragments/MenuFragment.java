package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.example.hackinhome2021_stankinfood.adapters.MyRecyclerViewAdapter;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment implements
        SearchView.OnQueryTextListener,
        TabLayout.OnTabSelectedListener,
        OnRecyclerViewClickListener {

    private static final String IS_MENU = "isMenu";
    private static final String PRODUCT_LIST = "productList";

    private boolean isMenu;
    private List<Product> productList;
    private List<Integer> titleIndexesList;

    private boolean isScrolled = false;

    private SearchView searchView;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewMenu;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView.SmoothScroller smoothScroller;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;

    public MenuFragment() {
    }

    public static MenuFragment newInstance(boolean isMenu, List<Product> productList) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_MENU, isMenu);
        args.putParcelableArrayList(PRODUCT_LIST, (ArrayList<? extends Parcelable>) productList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isMenu = getArguments().getBoolean(IS_MENU);
            productList = getArguments().getParcelableArrayList(PRODUCT_LIST);
        }
        if (savedInstanceState != null) {
            isMenu = savedInstanceState.getBoolean(IS_MENU);
            productList = savedInstanceState.getParcelableArrayList(PRODUCT_LIST);
        }
        setTitleIndexList();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(IS_MENU, isMenu);
        outState.putParcelableArrayList(PRODUCT_LIST, (ArrayList<? extends Parcelable>) productList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        initSearchView(view);
        initGridLayoutManager(view);
        initSmoothScroller();
        initRecyclerViewMenu(view);
        initTabLayout(view);

        return view;
    }


    private void setTitleIndexList() {
        titleIndexesList = new ArrayList<>();

        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getViewType() == MainActivity.MENU_HEADER) {
                titleIndexesList.add(i);
            }
        }
        titleIndexesList.add(productList.size());

        Log.d("LOG_MESSAGE", titleIndexesList.toString());
    }

    private void initSearchView(View view) {
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
    }

    private void initGridLayoutManager(View view) {
        gridLayoutManager = new GridLayoutManager(view.getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (productList.get(position).getViewType() == MainActivity.MENU_HEADER) {
                    return 2;
                } else return 1;
            }
        });
    }

    private void initSmoothScroller() {
        smoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
    }

    private void initRecyclerViewMenu(View view) {
        MyOnScrollListener myOnScrollListener = new MyOnScrollListener();

        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);
        recyclerViewMenu.getItemAnimator().setChangeDuration(0);
        myRecyclerViewAdapter = new MyRecyclerViewAdapter(productList, this);
        recyclerViewMenu.setLayoutManager(gridLayoutManager);
        recyclerViewMenu.setAdapter(myRecyclerViewAdapter);
        recyclerViewMenu.addOnScrollListener(myOnScrollListener);
    }

    private void initTabLayout(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);

        if (isMenu) {
            for (Product product : productList) {
                if (product.getViewType() == MainActivity.MENU_HEADER) {
                    tabLayout.addTab(tabLayout.newTab().setText(product.getProductName()));
                }
            }

            tabLayout.addOnTabSelectedListener(this);
        } else tabLayout.setVisibility(View.GONE);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() > 0) {
            tabLayout.setVisibility(View.GONE);
        } else tabLayout.setVisibility(View.VISIBLE);
        myRecyclerViewAdapter.getFilter().filter(newText);

        return false;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (!isScrolled) {
            int selectedPosition = tabLayout.getSelectedTabPosition();
            smoothScroller.setTargetPosition(titleIndexesList.get(selectedPosition));
            gridLayoutManager.startSmoothScroll(smoothScroller);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }


    private class MyOnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            isScrolled = newState != RecyclerView.SCROLL_STATE_IDLE;
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isScrolled) {
                tabLayout.removeOnTabSelectedListener(MenuFragment.this);
                int firstVisiblePosition = gridLayoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = gridLayoutManager.findLastVisibleItemPosition();

                int index = 0;
                for (int i = 0; i < titleIndexesList.size() - 1; i++) {
                    if (firstVisiblePosition < titleIndexesList.get(i + 1) &&
                            lastVisiblePosition - 3 < titleIndexesList.get(i + 1)) {
                        index = i;
                        break;
                    } else if (firstVisiblePosition < titleIndexesList.get(i) &&
                            lastVisiblePosition - 2 > titleIndexesList.get(i)) {
                        index = i;
                        break;
                    }
                }

                tabLayout.setScrollPosition(index, 0, true);
                tabLayout.selectTab(tabLayout.getTabAt(index));
                tabLayout.addOnTabSelectedListener(MenuFragment.this);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        int id = view.getId();
        Product currentProduct = productList.get(position);
        int currentCount = currentProduct.getCountForOrder();

        if (id == R.id.cardView) {
            Toast.makeText(getContext(), "[" + position + "]", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.imageButtonLiked) {
            currentProduct.setLiked(!currentProduct.isLiked());
            if (!isMenu) {
                productList.remove(currentProduct);
                myRecyclerViewAdapter.notifyItemRemoved(position);
                myRecyclerViewAdapter.notifyItemRangeChanged(position, productList.size());
            } else myRecyclerViewAdapter.notifyItemChanged(position);
        } else {
            if (id == R.id.buttonPrice) {
                currentProduct.setCountForOrder(1);
                currentProduct.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);
            } else if (id == R.id.imageButtonMinus) {
                currentProduct.setCountForOrder(currentCount - 1);
                if (currentProduct.getCountForOrder() == 0) {
                    currentProduct.setViewType(MainActivity.MENU_PRODUCT_INACTIVE);
                } else currentProduct.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);
            } else if (id == R.id.imageButtonPlus) {
                currentProduct.setCountForOrder(currentCount + 1);
                currentProduct.setViewType(MainActivity.MENU_PRODUCT_ACTIVE);
            }
            myRecyclerViewAdapter.notifyItemChanged(position);
        }
    }


    @Override
    public void onDestroyView() {
        searchView.setQuery(null, true);
        super.onDestroyView();
    }
}