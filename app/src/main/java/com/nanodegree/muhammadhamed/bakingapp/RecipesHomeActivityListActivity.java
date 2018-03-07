package com.nanodegree.muhammadhamed.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.nanodegree.muhammadhamed.bakingapp.dummy.DummyContent;
import com.nanodegree.muhammadhamed.bakingapp.models.Recipe;
import com.nanodegree.muhammadhamed.bakingapp.models.Step;
import com.nanodegree.muhammadhamed.bakingapp.models.StepsViewModel;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecipesHomeActivityListActivity extends AppCompatActivity {


    public static final String CLICKED_RECIPE_INDEX = "clicked_recipe";
    private boolean mTwoPane;
    private List<Step> steps;


    @OnClick
    public void ingredientsClicked(View v) {

        if (mTwoPane) {
            Bundle arguments = new Bundle();

            IngredientsFragment fragment = new IngredientsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipeshomeactivity_detail_container, fragment)
                    .commit();
        } else {
            Intent i = new Intent(this, IngredientsActivity.class);
            startActivity(i);

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipeshomeactivity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);;
        toolbar.setTitle(StepsViewModel.instance.getRecipeName());
        setSupportActionBar(toolbar);
        if (getActionBar() != null) {
            getActionBar().setTitle(StepsViewModel.instance.getRecipeName());
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        ButterKnife.bind(this);

        steps = StepsViewModel.instance.getSteps();


        View recyclerView = findViewById(R.id.recipeshomeactivity_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.recipeshomeactivity_detail_container) != null) {

            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(steps));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Step> steps;

        public SimpleItemRecyclerViewAdapter(List<Step> steps) {
            this.steps = steps;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recipeshomeactivity_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mStep.setText( "Step "+(position+1)+": "+ steps.get(position).getShortDescription());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putInt(RecipesHomeActivityDetailFragment.ARG_ITEM_ID, position);
//                        Toast.makeText(RecipesHomeActivityListActivity.this, "Position is: "+position, Toast.LENGTH_SHORT).show();
                        RecipesHomeActivityDetailFragment fragment = new RecipesHomeActivityDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.recipeshomeactivity_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, RecipesHomeActivityDetailActivity.class);
                        intent.putExtra(RecipesHomeActivityDetailFragment.ARG_ITEM_ID, position);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return steps.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mStep;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mStep = (TextView) view.findViewById(R.id.stepTv);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mStep.getText() + "'";
            }
        }
    }
}
