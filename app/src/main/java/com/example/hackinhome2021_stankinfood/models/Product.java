package com.example.hackinhome2021_stankinfood.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.Exclude;

import java.util.Comparator;

public class Product implements Parcelable {
    @Exclude
    private String productId;
    private String imageURL;
    private String categoryName;
    private String productName;
    private String description;
    private int productsLeft;
    @Exclude
    private int countForOrder;
    private int price;
    private int likesCount;
    private boolean isLiked;
    @Exclude
    private int viewType;

    public Product() {
    }

    public Product(String productId, String imageURL, String categoryName, String productName,
                   String description, int productsLeft, int countForOrder, int price,
                   int likesCount, boolean isLiked, int viewType) {
        this.productId = productId;
        this.imageURL = imageURL;
        this.categoryName = categoryName;
        this.productName = productName;
        this.description = description;
        this.productsLeft = productsLeft;
        this.countForOrder = countForOrder;
        this.price = price;
        this.likesCount = likesCount;
        this.isLiked = isLiked;
        this.viewType = viewType;
    }

    protected Product(Parcel in) {
        productId = in.readString();
        imageURL = in.readString();
        categoryName = in.readString();
        productName = in.readString();
        description = in.readString();
        productsLeft = in.readInt();
        countForOrder = in.readInt();
        price = in.readInt();
        likesCount = in.readInt();
        isLiked = in.readByte() != 0;
        viewType = in.readInt();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProductsLeft() {
        return productsLeft;
    }

    public void setProductsLeft(int productsLeft) {
        this.productsLeft = productsLeft;
    }

    public int getCountForOrder() {
        return countForOrder;
    }

    public void setCountForOrder(int countForOrder) {
        this.countForOrder = countForOrder;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(imageURL);
        dest.writeString(categoryName);
        dest.writeString(productName);
        dest.writeString(description);
        dest.writeInt(productsLeft);
        dest.writeInt(countForOrder);
        dest.writeInt(price);
        dest.writeInt(likesCount);
        dest.writeByte((byte) (isLiked ? 1 : 0));
        dest.writeInt(viewType);
    }

    public static final Comparator<Product> PRODUCT_COMPARATOR = (product_1, product_2) -> {
        if (product_1.getCategoryName().compareTo(product_2.getCategoryName()) < 0) {
            return -1;
        } else if (product_1.getCategoryName().compareTo(product_2.getCategoryName()) == 0) {
            if (product_1.getLikesCount() > product_2.getLikesCount()) {
                return -1;
            } else if (product_1.getLikesCount() == product_2.getLikesCount()) {
                return Integer.compare(product_1.getProductName().compareTo(product_2.getProductName()), 0);
            } else return 1;
        } else return 1;
    };

    @Override
    public String toString() {
        return "\nProduct {" + "\n" +
                "\tproductId = '" + productId + '\'' + ",\n" +
                "\timageURL = '" + imageURL + '\'' + ",\n" +
                "\tcategoryName = '" + categoryName + '\'' + ",\n" +
                "\tproductName = '" + productName + '\'' + ",\n" +
                "\tdescription = '" + description + '\'' + ",\n" +
                "\tproductsLeft = " + productsLeft + ",\n" +
                "\tcountForOrder = " + countForOrder + ",\n" +
                "\tprice = " + price + ",\n" +
                "\tlikesCount = " + likesCount + ",\n" +
                "\tisLiked = " + isLiked + ",\n" +
                "\tviewType = " + viewType + "\n" +
                '}' + "\n";
    }
}
