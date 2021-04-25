package com.example.hackinhome2021_stankinfood.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackinhome2021_stankinfood.R;
import com.example.hackinhome2021_stankinfood.activities.MainActivity;
import com.example.hackinhome2021_stankinfood.adapters.OrderRecyclerViewAdapter;
import com.example.hackinhome2021_stankinfood.interfaces.OnBackPressedFragment;
import com.example.hackinhome2021_stankinfood.interfaces.OnRecyclerViewClickListener;
import com.example.hackinhome2021_stankinfood.models.Order;
import com.example.hackinhome2021_stankinfood.models.Product;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment implements
        SearchView.OnQueryTextListener,
        OnRecyclerViewClickListener,
        OnBackPressedFragment {

    private static final String ORDER_LIST = "orderList";

    private List<Order> orderList;

    private SearchView searchView;
    private RecyclerView recyclerViewMenu;
    private LinearLayoutManager linearLayoutManager;
    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;

    public OrdersFragment() {
    }

    public static OrdersFragment newInstance(List<Order> orderList) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ORDER_LIST, (ArrayList<? extends Parcelable>) orderList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderList = getArguments().getParcelableArrayList(ORDER_LIST);
        }
        if (savedInstanceState != null) {
            orderList = savedInstanceState.getParcelableArrayList(ORDER_LIST);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(ORDER_LIST, (ArrayList<? extends Parcelable>) orderList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        initSearchView(view);
        linearLayoutManager = new LinearLayoutManager(view.getContext());
        initRecyclerViewMenu(view);
        initTabLayout(view);

        return view;
    }


    private void initSearchView(View view) {
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);
    }

    private void initRecyclerViewMenu(View view) {
        recyclerViewMenu = view.findViewById(R.id.recyclerViewMenu);
        recyclerViewMenu.getItemAnimator().setChangeDuration(0);
        orderRecyclerViewAdapter = new OrderRecyclerViewAdapter(orderList, this);
        recyclerViewMenu.setLayoutManager(linearLayoutManager);
        recyclerViewMenu.setAdapter(orderRecyclerViewAdapter);
    }

    private void initTabLayout(View view) {
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.GONE);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ((MainActivity) getActivity()).hideBottomNavigationView(newText.length() > 0);
        orderRecyclerViewAdapter.getFilter().filter(newText);

        return false;
    }


    @Override
    public void onItemClick(View view, int position) {
        if (view.getId() == R.id.cardView) {
            ((MainActivity) getActivity()).replaceFragmentToOrderFragment(
                    false, orderList.get(position));
        }
    }


    @Override
    public void onDestroyView() {
        searchView.setQuery(null, true);
        super.onDestroyView();
    }


    @Override
    public boolean onBackPressed() {
        if (searchView.getQuery().length() > 0) {
            searchView.setQuery(null, true);
            searchView.clearFocus();
            recyclerViewMenu.requestFocus();
            return true;
        } else return false;
    }
}