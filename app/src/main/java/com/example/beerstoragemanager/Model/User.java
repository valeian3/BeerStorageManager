package com.example.beerstoragemanager.Model;

public class User {

    private String userId;
    private String Name;
    private String Email;
    private String Username;
    private String Password;

    public User(){ }

    public User(String id, String name, String email, String username, String password){
        userId = id;
        Name = name;
        Email = email;
        Username = username;
        Password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

}
