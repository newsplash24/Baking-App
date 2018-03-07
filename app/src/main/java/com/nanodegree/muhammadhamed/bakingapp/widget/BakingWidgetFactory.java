package com.nanodegree.muhammadhamed.bakingapp.widget;

/**
 * Created by Mohamed on 3/7/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.nanodegree.muhammadhamed.bakingapp.R;
import com.nanodegree.muhammadhamed.bakingapp.models.Ingredient;
import com.nanodegree.muhammadhamed.bakingapp.models.Recipe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

public class BakingWidgetFactory implements RemoteViewsService.RemoteViewsFactory {


    private Recipe recipe;
    private List<Ingredient> ingredientList;

    private Context mContext;

    public BakingWidgetFactory(Context applicationContext, Intent intent) {

        mContext = applicationContext;


    }

    @Override
    public void onCreate() {

        FileInputStream fis;
        try {
            fis = mContext.openFileInput("steps_list");
            ObjectInputStream ois = new ObjectInputStream(fis);
            recipe = (Recipe) ois.readObject();
            ingredientList = recipe.getIngredients();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDataSetChanged() {


    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {

        if (ingredientList != null) {
            return ingredientList.size();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int position) {


        String ingTxt = "";
        int i = 0;
        for (Ingredient ingredient : ingredientList) {
            ingTxt += +(++i) + "- \"" + ingredient.getQuantity() + " " +
                    ingredient.getMeasure() + " \" of " +
                    ingredient.getIngredient() + " \n";
        }


        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.titleTv, recipe.getName());
        rv.setTextViewText(R.id.contentTv, ingTxt);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}

