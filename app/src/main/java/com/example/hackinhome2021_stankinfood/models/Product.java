package com.example.hackinhome2021_stankinfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

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

    @Exclude
    @Override
    public String toString() {
        return "Product {" + "\n" +
                "productId = '" + productId + '\'' + ",\n" +
                "imageURL = '" + imageURL + '\'' + ",\n" +
                "categoryName = '" + categoryName + '\'' + ",\n" +
                "productName = '" + productName + '\'' + ",\n" +
                "description = '" + description + '\'' + ",\n" +
                "productsLeft = " + productsLeft + ",\n" +
                "countForOrder = " + countForOrder + ",\n" +
                "price = " + price + ",\n" +
                "likesCount = " + likesCount + ",\n" +
                "isLiked = " + isLiked + ",\n" +
                "viewType = " + viewType + "\n" +
                '}' + "\n";
    }
}
