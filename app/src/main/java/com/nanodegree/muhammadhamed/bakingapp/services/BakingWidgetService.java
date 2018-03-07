package com.nanodegree.muhammadhamed.bakingapp.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.nanodegree.muhammadhamed.bakingapp.widget.BakingWidgetFactory;

/**
 * Created by Mohamed on 3/7/2018.
 */

public class BakingWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BakingWidgetFactory(this.getApplicationContext(), intent);
    }
}
