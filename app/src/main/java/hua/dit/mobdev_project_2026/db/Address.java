package hua.dit.mobdev_project_2026.db;

import androidx.room.ColumnInfo;

public class Address {

    private String city;

    private String state;

    private String street;

    private int postCode;

    public Address(String city, String state, String street, int postCode) {
        this.city = city;
        this.state = state;
        this.street = street;
        this.postCode = postCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPostCode() {
        return postCode;
    }

    public void setPostCode(int postCode) {
        this.postCode = postCode;
    }

}
