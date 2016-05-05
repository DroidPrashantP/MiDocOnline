package com.midoconline.app.beans;

/**
 * Created by sandeep on 15/10/15.
 */
public class SignUpBean {
    String name;
    String city;
    String email;
    String country;
    String mobilenumber;
    String birthday;
    String password;
    String specialize;
    String gender;
    String surname;
    String lincence;

    public SignUpBean(String name,String surname, String city, String email, String country, String mobilenumber, String password, String specialize,String gender,String birthday, String lincence) {
        this.name = name;
        this.surname =surname;
        this.city = city;
        this.email = email;
        this.country = country;
        this.mobilenumber = mobilenumber;
        this.password = password;
        this.specialize = specialize;
        this.gender = gender;
        this.birthday = birthday;
        this.lincence = lincence;
    }

    public String getName() {
        return name;
    }

    public String getcity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public String getcountry() {
        return country;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getPassword() {
        return password;
    }
    public String getSpecialize() {
        return specialize;
    }

    public String getGender() {
        return gender;
    }

    public String getSurname() {
        return surname;
    }
    public String getLincence() {
        return lincence;
    }
}
