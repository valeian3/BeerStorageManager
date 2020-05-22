package com.example.beerstoragemanager.Model;

public class Ingredient {

    private String ingredientId;
    private String Name;
    private String Amount;

    public Ingredient(){ }

    public Ingredient(String id, String name, String amount){
        ingredientId = id;
        Name = name;
        Amount = amount;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
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
