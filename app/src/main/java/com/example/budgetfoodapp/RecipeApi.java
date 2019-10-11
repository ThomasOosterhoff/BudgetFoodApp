package com.example.budgetfoodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecipeApi extends AppCompatActivity {

    private RequestQueue request;
    private TextView recipeView;
    private Button searchButton;
    private ImageView imageView;
    private TextInputEditText searchQuery;
    private TextInputEditText include;
    private TextInputEditText exclude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_api);

        recipeView = findViewById(R.id.recipeView);
        searchButton = findViewById(R.id.recipeSubmitButton);
        searchQuery = findViewById(R.id.recipeSearch);
        imageView = findViewById(R.id.imageView);
        include = findViewById(R.id.includedIngredients);
        exclude = findViewById(R.id.excludeIngredient);
        request = Volley.newRequestQueue(this);
    }

    private String prepareExcludesForUrl(String excludes, int includeLength){
        String result = "";

        if(excludes.length() > 0) {//If there are ingredients to exclude
            //Remove all spaces
            excludes.replaceAll("\\s+", "");
            if(includeLength > 0)   //If there are ingredients to include, seperate them by ,
            {
                result += ",";
            }
            //Add - before ingredients to exclude them
            result += "-";
            //Add - after ,
            excludes.replaceAll(",", ",-");
        }
        //Add to url
        result += excludes;
        return result;
    }
    private String constructUrl(String url, String ingredients, String excludes){
        //If there are any ingredient preferences, add the pre-fix to the url
        if(ingredients.length()+excludes.length() > 0){
            url += getResources().getString(R.string.ingredient);
        }

        try {
            //Remove all spaces
            ingredients.replaceAll("\\s+", "");
            //Add to url
            url += ingredients;
        }
        catch(Exception e){
            Log.d("ERROR", "Can't add ingredients to url");
        }

        url += prepareExcludesForUrl(excludes, include.length());

        //Add recipe to url
        if(ingredients.length()+excludes.length()>0){
            url += "&";
        }
        url += getResources().getString(R.string.recipe_keyword);
        //Replace Spaces with _
        String keywords = searchQuery.getText().toString();
        keywords.replaceAll(" ", "_");
        //Add keywords to url
        url+= keywords;
        return url;
    }

    public void showRecipe(View view){
        //Url to receive recipes
        String url = getResources().getString(R.string.recipe_api);
        //Get the prefered ingredients
        String ingredients = include.getText().toString();
        //Get excluded ingredients
        String excludes = exclude.getText().toString();
        //Construct url
        url = constructUrl(url, ingredients, excludes);

        Log.d("TEST", "url = "+url);
        JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TEST", "onResponse: started");
                try {
                    JSONArray jsonArray = response.getJSONArray("results");

                    Log.d("TEST", "\n\n\nonResponse: got results\n\n\n");
                    for (int i = 0; i < 1/*jsonArray.length()*/; i++) {
                        JSONObject recipe = jsonArray.getJSONObject(i);

                        String title = recipe.getString("title");
                        String imagePath = recipe.getString("thumbnail");
                        if (!imagePath.isEmpty()) //An image exists
                        {
                            Picasso.get().load(imagePath).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(imageView);
                        }
                        String href = recipe.getString("href");

                        Log.d("TEST", "got all data");
                        recipeView.setText(title + "\n" + href);
                    }
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
        request.add(request1);
    }
}
