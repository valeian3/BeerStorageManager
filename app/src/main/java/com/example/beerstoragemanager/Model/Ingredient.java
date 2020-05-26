package com.example.beerstoragemanager.Model;

public class Ingredient {

    private String ingredientId;
    private String Name;
    private int Amount;

    public Ingredient(){ }

    public Ingredient(String id, String name, int amount){
        ingredientId = id;
        Name = name;
        Amount = amount;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}