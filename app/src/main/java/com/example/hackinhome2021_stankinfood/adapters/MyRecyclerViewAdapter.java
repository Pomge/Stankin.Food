package com.example.hackinhome2021_stankinfood.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hackinhome2021_stankinfood.R;

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
        private final TextView textViewName;

        public ViewHolderMenuHeader(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }

    private static class ViewHolderMenuProduct extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageButton imageButtonLiked;
        private final ImageView imageViewPicture;
        private final TextView textViewName;
        private final RatingBar ratingBar;
        private final TextView textViewDescription;
        private final Button buttonPrice;
        private final ImageButton imageButtonMinus;
        private final TextView textViewCount;
        private final ImageButton imageButtonPlus;

        public ViewHolderMenuProduct(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imageButtonLiked = itemView.findViewById(R.id.imageButtonLiked);
            imageViewPicture = itemView.findViewById(R.id.imageViewPicture);
            textViewName = itemView.findViewById(R.id.textViewName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            buttonPrice = itemView.findViewById(R.id.buttonPrice);
            imageButtonMinus = itemView.findViewById(R.id.imageButtonMinus);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            imageButtonPlus = itemView.findViewById(R.id.imageButtonPlus);
        }
    }

    private static class ViewHolderOrderProduct extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView imageViewPicture;
        private final TextView textViewName;
        private final TextView textViewTotalPrice;
        private final TextView textViewRealTotalPrice;
        private final ImageButton imageButtonMinus;
        private final TextView textViewCount;
        private final ImageButton imageButtonPlus;

        public ViewHolderOrderProduct(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imageViewPicture = itemView.findViewById(R.id.imageViewPicture);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
            textViewRealTotalPrice = itemView.findViewById(R.id.textViewRealTotalPrice);
            imageButtonMinus = itemView.findViewById(R.id.imageButtonMinus);
            textViewCount = itemView.findViewById(R.id.textViewCount);
            imageButtonPlus = itemView.findViewById(R.id.imageButtonPlus);
        }
    }
}
