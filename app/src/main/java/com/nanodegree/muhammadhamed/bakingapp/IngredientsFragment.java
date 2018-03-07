package com.nanodegree.muhammadhamed.bakingapp;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.muhammadhamed.bakingapp.models.Ingredient;
import com.nanodegree.muhammadhamed.bakingapp.models.StepsViewModel;
import com.nanodegree.muhammadhamed.bakingapp.widget.BakingWidget;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class IngredientsFragment extends Fragment {


    public IngredientsFragment() {
        // Required empty public constructor
    }


    @BindView(R.id.ingredientsTv)
    TextView ingredientsTv;

    @BindView(R.id.addToWidgetBtn)
    Button addtoWidgetBtn;

    List<Ingredient> ingredientList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        ButterKnife.bind(this, view);

        String ingredientsText = "";

        ingredientList = StepsViewModel.instance.getIngredients();

        int ingredintsNumber = 1;

        for (Ingredient ingredient : ingredientList) {

            ingredientsText += (ingredintsNumber++) + "- \"" + ingredient.getQuantity() + " " +
                    ingredient.getMeasure() + " \" of " + ingredient.getIngredient() + "\n\n";

        }

        ingredientsTv.setText(ingredientsText);

        addtoWidgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getContext(), BakingWidget.class));
                for (int i : appWidgetIds) {
                    Log.v("Widget_Id", ":: " + i);
                }

                BakingWidget.refreshWidget(getContext(), StepsViewModel.instance.getIngredients(),
                        StepsViewModel.instance.getRecipeName(), appWidgetManager, appWidgetIds);

                Toast.makeText(getContext(), "Ingredients added to widget successfully!", Toast.LENGTH_LONG).show();

            }
        });


        return view;
    }

}
