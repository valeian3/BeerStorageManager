package com.example.beerstoragemanager.Model;

public class Beer {

    private String beerId;
    private String Name;
    private String AmountOfBags;

    public Beer(){ }

    public Beer(String id, String name, String amountOfBags){
        beerId = id;
        Name = name;
        AmountOfBags = amountOfBags;
    }

    public Beer(String name){
        Name = name;
    }

    public Beer(String id, String name){
        beerId = id;
        Name = name;
    }

    public String getBeerId() {
        return beerId;
    }

    public void setBeerId(String beerId) {
        this.beerId = beerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Override
    public String toString(){
        return Name;
    }

    public String getAmountOfBags() {
        return AmountOfBags;
    }

    public void setAmountOfBags(String amountOfBags) {
        AmountOfBags = amountOfBags;
    }
}
