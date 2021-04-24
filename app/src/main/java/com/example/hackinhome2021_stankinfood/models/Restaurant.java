package com.example.hackinhome2021_stankinfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Restaurant implements Parcelable {
    @Exclude
    private String restaurantId;
    private String name;
    private String address;
    private Date openingHours;
    private Date closingHours;
    @Exclude
    private List<Order> orderList;
    @Exclude
    private List<Product> productList;

    public Restaurant() {
    }

    public Restaurant(String restaurantId, String name, String address, Date openingHours, Date closingHours) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.address = address;
        this.openingHours = openingHours;
        this.closingHours = closingHours;
    }

    protected Restaurant(Parcel in) {
        restaurantId = in.readString();
        name = in.readString();
        address = in.readString();
        orderList = in.createTypedArrayList(Order.CREATOR);
        productList = in.createTypedArrayList(Product.CREATOR);
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Date openingHours) {
        this.openingHours = openingHours;
    }

    public Date getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(Date closingHours) {
        this.closingHours = closingHours;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(restaurantId);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeTypedList(orderList);
        dest.writeTypedList(productList);
    }

    @Exclude
    @Override
    public String toString() {
        return "\nRestaurant {" + ",\n" +
                "\trestaurantId = '" + restaurantId + '\'' + ",\n" +
                "\tname = '" + name + '\'' + ",\n" +
                "\taddress = '" + address + '\'' + ",\n" +
                "\topeningHours = " + openingHours + ",\n" +
                "\tclosingHours = " + closingHours + ",\n" +
                '}' + "\n";
    }
}
