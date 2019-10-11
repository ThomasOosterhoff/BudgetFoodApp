package com.example.budgetfoodapp.Api.RecipeApi;

import android.content.Context;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

public abstract class RecipeApi {
    protected Context context;
    protected RequestQueue request;
    protected ArrayList<Recipe> recipes = new ArrayList<Recipe>();

    public abstract void getRecipeFromApi(String keywords, String includedIngredients, String excludedIngredients);

    public ArrayList<Recipe> getRecipe() {
        return recipes;
    }
}
