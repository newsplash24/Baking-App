package com.nanodegree.muhammadhamed.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;


public class RecipesHomeActivityDetailActivity extends AppCompatActivity implements RecipesHomeActivityDetailFragment.NextStepPerformer {

    private int stepIndx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipeshomeactivity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            stepIndx = getIntent().getIntExtra(RecipesHomeActivityDetailFragment.ARG_ITEM_ID, 0);
            arguments.putInt(RecipesHomeActivityDetailFragment.ARG_ITEM_ID, stepIndx );
            RecipesHomeActivityDetailFragment fragment = new RecipesHomeActivityDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipeshomeactivity_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            NavUtils.navigateUpTo(this, new Intent(this, RecipesHomeActivityListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void nextStepClicked() {
        Bundle arguments = new Bundle();
        arguments.putInt(RecipesHomeActivityDetailFragment.ARG_ITEM_ID, ++stepIndx );
        RecipesHomeActivityDetailFragment fragment = new RecipesHomeActivityDetailFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recipeshomeactivity_detail_container, fragment)
                .commit();
    }
}
