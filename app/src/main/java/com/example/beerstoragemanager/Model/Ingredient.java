package com.example.beerstoragemanager.Model;

public class Ingredient {

    private String ingredientId;
    private String Name;

    public Ingredient(){ }

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
