package com.ks.weatherforecast.share.model.userinfo;

/**
 * Created by manhhoang on 4/25/18.
 */

public class UserInfo {

    private Long id;
    private String name;
    private String userName ;
    private String email;
    private String phone;
    private String website;
    private Address address;
    private Company company;

    public static String createDB = "CREATE TABLE IF NOT EXITS UserInfo ("
            + " id INTEGER PRIMARY KEY,"
            + " name TEXT,"
            + " userName TEXT,"
            + " email TEXT,"
            + " phone TEXT,"
            +")";



    public UserInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
