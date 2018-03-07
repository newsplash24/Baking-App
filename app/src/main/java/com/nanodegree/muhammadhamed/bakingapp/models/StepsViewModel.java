package com.nanodegree.muhammadhamed.bakingapp.models;

import java.util.List;

/**
 * Created by Mohamed on 3/3/2018.
 */

public class StepsViewModel {

    List<Step> steps;
    List<Ingredient> ingredients;
    String recipeName;

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public static StepsViewModel instance = new StepsViewModel();

    private StepsViewModel() {
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
