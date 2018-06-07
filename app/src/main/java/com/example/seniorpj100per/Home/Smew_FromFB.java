package com.example.seniorpj100per.Home;

/**
 * Created by Smew on 22/11/2560.
 */

public class Smew_FromFB {

    private String username;

    private String email;

    private String age;

    private String gender;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "ClassPojo [username = " + username + ", email = " + email + ", age = " + age + ", gender = " + gender + "]";
    }


}