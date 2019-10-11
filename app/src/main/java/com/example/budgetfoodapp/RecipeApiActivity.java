package com.example.budgetfoodapp;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.budgetfoodapp.Api.RecipeApi.Recipe;
import com.example.budgetfoodapp.Api.RecipeApi.RecipeApi;
import com.example.budgetfoodapp.Api.RecipeApi.RecipePuppy;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecipeApiActivity extends AppCompatActivity {
    private TextView recipeView;
    private Button searchButton;
    private ImageView imageView;
    private TextInputEditText searchQuery;
    private TextInputEditText include;
    private TextInputEditText exclude;
    private RecipeApi recipeApi;

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
    }

    public void showRecipe(View view){
        //Get the recipe keywords
        String keywords = searchQuery.getText().toString();
        //Get the prefered ingredients
        String ingredients = include.getText().toString();
        //Get excluded ingredients
        String excludes = exclude.getText().toString();

        recipeApi = new RecipePuppy(this);
        recipeApi.getRecipeFromApi(keywords, ingredients, excludes);
    }

    public void updateRecipes(ArrayList<Recipe> recipes){

        // If there were any recipes found
        if(recipes.size() > 0) {
            Recipe recipe = recipes.get(0);
            // Show recipe
            recipeView.setText(recipe.title + "\n" + recipe.href + "\n\n");
            for(int i = 0; i < recipe.ingredients.size(); i++){
                recipeView.append(recipe.ingredients.get(i)+ ", ");
            }
            if (!recipe.imagePath.isEmpty()) //An image exists
            {
                Picasso.get().load(recipe.imagePath).memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.NO_CACHE).into(imageView);
            }
        }
    }
}
