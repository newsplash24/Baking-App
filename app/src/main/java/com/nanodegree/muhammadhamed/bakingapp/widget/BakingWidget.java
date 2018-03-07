package com.nanodegree.muhammadhamed.bakingapp.widget;

/**
 * Created by Mohamed on 3/7/2018.
 */

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.nanodegree.muhammadhamed.bakingapp.R;
import com.nanodegree.muhammadhamed.bakingapp.models.Ingredient;
import com.nanodegree.muhammadhamed.bakingapp.services.BakingWidgetService;

import java.util.List;


public class BakingWidget extends AppWidgetProvider {


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, BakingWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_widget);

        views.setRemoteAdapter(R.id.widgetListView, intent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void refreshWidget(Context context, List<Ingredient> ingredients, String recipeName, AppWidgetManager appWidgetManager, int[] appWidgetIds) {


        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.refreshed_widget);

            String ingTxt = "";
            int i = 0;
            for (Ingredient ingredient : ingredients) {
                ingTxt += +(++i) + "- \"" + ingredient.getQuantity() + " " +
                        ingredient.getMeasure() + " \" of " +
                        ingredient.getIngredient() + " \n\n";
            }

            views.setTextViewText(R.id.titleTv, recipeName + " Ingredients");

            views.setTextViewText(R.id.contentTv, ingTxt);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }


}

