package com.example.budgetfoodapp.Api.RecipeApi;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetfoodapp.R;
import com.example.budgetfoodapp.RecipeApiActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class RecipePuppy extends RecipeApi {

    public RecipePuppy(Context current) {
        context = current;
    }

    @Override
    public void getRecipeFromApi(String keywords, String includedIngredients, String excludedIngredients) {
        // Initialize variables
        recipes = new ArrayList<Recipe>();
        request = Volley.newRequestQueue(context);
        //Url to receive recipes
        String url = context.getResources().getString(R.string.recipe_puppy_api);
        //Construct url
        url = constructUrl(url, keywords, includedIngredients, excludedIngredients);
        Log.d("TEST", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonRecipe = jsonArray.getJSONObject(i);
                        Recipe recipe = new Recipe();
                        recipe.title = jsonRecipe.getString("title");
                        recipe.imagePath = jsonRecipe.getString("thumbnail");
                        recipe.href = jsonRecipe.getString("href");
                        String ingredients = jsonRecipe.getString("ingredients");
                        recipe.ingredients = new ArrayList<String>(Arrays.asList(ingredients.split("\\s*,\\s*")));
                        recipes.add(recipe);
                    }

                    // Bad code workaround for async methode
                    RecipeApiActivity recipeActivity = (RecipeApiActivity) context;
                    recipeActivity.updateRecipes(recipes);
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        this.request.add(request);
    }

    private String prepareExcludesForUrl(String excludes, int includeLength){
        String result = "";

        if(excludes.length() > 0) {//If there are ingredients to exclude
            //Remove all spaces
            excludes = excludes.replaceAll("\\s+", "");
            if(includeLength > 0)   //If there are ingredients to include, seperate them by ,
            {
                result += ",";
            }
            //Add - before ingredients to exclude them
            result += "-";
            //Add - after ,
            Log.d("TEST", "Before: "+result+excludes);
            excludes = excludes.replaceAll(",", ",-");
        }
        //Add to url
        result += excludes;
        Log.d("TEST", "After: "+result);
        return result;
    }

    private String constructUrl(String url, String keywords, String includedIngredients, String excludedIngredients){
        //If there are any ingredient preferences, add the pre-fix to the url
        if(includedIngredients.length()+excludedIngredients.length() > 0){
            url += context.getResources().getString(R.string.ingredient);
        }

        try {
            //Remove all spaces
            includedIngredients = includedIngredients.replaceAll("\\s+", "");
            //Add to url
            url += includedIngredients;
        }
        catch(Exception e){
            Log.d("ERROR", "Can't add ingredients to url");
        }

        url += prepareExcludesForUrl(excludedIngredients, includedIngredients.length());

        //Add recipe to url
        if(includedIngredients.length()+excludedIngredients.length()>0){
            url += "&";
        }
        url += context.getResources().getString(R.string.recipe_keyword);

        //Replace Spaces with _
        keywords.replaceAll(" ", "_");
        //Add keywords to url
        url+= keywords;
        return url;
    }
}
