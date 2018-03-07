package com.nanodegree.muhammadhamed.bakingapp;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nanodegree.muhammadhamed.bakingapp.adapters.RecipeAdapter;
import com.nanodegree.muhammadhamed.bakingapp.models.Recipe;
import com.nanodegree.muhammadhamed.bakingapp.models.StepsViewModel;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.DialogDismisser, RecipeAdapter.RecipeItemClickhandler {

    @BindView(R.id.recipeRV)
    RecyclerView recipeList;
    RecipeAdapter recipeAdapter;
    private ProgressDialog pDialog;
    List<Recipe> recipes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recipeList.setLayoutManager(linearLayoutManager);

        recipeAdapter = new RecipeAdapter(this);
        recipeList.setAdapter(recipeAdapter);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading .....");

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
        pDialog.show();
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
//                        Log.v("TEST","Response is: "+ response);

                        recipes = new ArrayList<>();

                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, true);


                        try {
                            recipes = mapper.readValue(response, new TypeReference<List<Recipe>>() {
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.v("TEST_LENGTH", "Length is: " + recipes.size());

                        for (Recipe recipe : recipes) {

                            Log.v("TEST_LENGTH", "Recipe name: " + recipe.getName());
                            Log.v("TEST_LENGTH", "Ingredient: " + recipe.getIngredients().get(0).getIngredient());
                        }

                        recipeList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                //At this point the layout is complete and the
                                //dimensions of recyclerView and any child views are known.
                                dismissDialog();
                                recipeList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });

                        recipeAdapter.setRecipes(recipes);
                        recipeAdapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TEST", "That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void dismissDialog() {
        if (pDialog != null)
            pDialog.dismiss();
    }

    @Override
    public void onRecipeClicked(View view, int position) {

        Intent listActivityLuancher = new Intent(this, RecipesHomeActivityListActivity.class);
        Recipe recipe = null;
        if (recipes != null) {
            recipe = recipes.get(position);

            FileOutputStream fos = null;
            try {
                fos = this.openFileOutput("steps_list", Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ObjectOutputStream oos = null;
            try {
                oos = new ObjectOutputStream(fos);
                oos.writeObject(recipe);
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        StepsViewModel.instance.setRecipeName(recipe.getName());
        StepsViewModel.instance.setSteps(recipe.getSteps());
        StepsViewModel.instance.setIngredients(recipe.getIngredients());

        startActivity(listActivityLuancher);
    }
}
