package com.example.hackinhome2021_stankinfood.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Filterable {

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    @Override
    public Filter getFilter() {
        return null;
    }

    private static class ViewHolderMenuHeader extends RecyclerView.ViewHolder {

        public ViewHolderMenuHeader(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolderMenuProduct extends RecyclerView.ViewHolder {

        public ViewHolderMenuProduct(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolderOrderProduct extends RecyclerView.ViewHolder {

        public ViewHolderOrderProduct(@NonNull View itemView) {
            super(itemView);
        }
    }
}
