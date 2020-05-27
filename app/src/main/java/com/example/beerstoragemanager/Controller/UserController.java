package com.example.beerstoragemanager.Controller;

import com.example.beerstoragemanager.MainActivity;
import com.example.beerstoragemanager.Model.User;

public class UserController {

    private User model;
    private MainActivity view;

    public UserController(User model, MainActivity view){
        this.model = model;
        this.view = view;
    }

    public String getUserName(){
        return  model.getUsername();
    }

    public void setUserName(String name){
        model.setUsername(name);
    }

    public String getPassword(){
        return model.getPassword();
    }

    public void setPassword(String password){
        model.setPassword(password);
    }

    public String getEmail(){
        return model.getEmail();
    }

    public void setEmail(String email){
        model.setEmail(email);
    }

}
