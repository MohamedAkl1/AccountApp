package com.akl.android.accountapp;

/**
 * Created by Mohamed Akl on 6/20/2018.
 */
public class User {
    private String name;
    private String number;
    private String address;
    private String email;

    User(String name, String number, String address, String email){
        this.name = name;
        this.number = number;
        this.address = address;
        this.email = email;
    }

    public User(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
