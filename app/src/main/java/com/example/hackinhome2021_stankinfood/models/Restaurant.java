package com.example.hackinhome2021_stankinfood.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.sql.Timestamp;

public class Restaurant implements Parcelable {
    @Exclude
    private String restaurantId;
    private String name;
    private String address;
    private Timestamp openingHours;
    private Timestamp closingHours;

    public Restaurant() {
    }

    protected Restaurant(Parcel in) {
        restaurantId = in.readString();
        name = in.readString();
        address = in.readString();
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

    public Timestamp getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(Timestamp openingHours) {
        this.openingHours = openingHours;
    }

    public Timestamp getClosingHours() {
        return closingHours;
    }

    public void setClosingHours(Timestamp closingHours) {
        this.closingHours = closingHours;
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
    }

    @Exclude
    @Override
    public String toString() {
        return "Restaurant {" + ",\n" +
                "restaurantId = '" + restaurantId + '\'' + ",\n" +
                "name = '" + name + '\'' + ",\n" +
                "address = '" + address + '\'' + ",\n" +
                "openingHours = " + openingHours + ",\n" +
                "closingHours = " + closingHours + ",\n" +
                '}' + "\n";
    }
}
