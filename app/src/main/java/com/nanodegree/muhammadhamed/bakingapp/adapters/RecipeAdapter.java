package com.nanodegree.muhammadhamed.bakingapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.muhammadhamed.bakingapp.R;
import com.nanodegree.muhammadhamed.bakingapp.models.Recipe;
import com.nanodegree.muhammadhamed.bakingapp.models.Step;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Mohamed on 3/2/2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeCardViewHolder>{

    private DialogDismisser dialogDismisser;
    private RecipeItemClickhandler recipeItemClickhandler;
    private Context mContext;
    private Boolean toastShown;
    private List<Recipe> recipes;

    public interface DialogDismisser {
        void dismissDialog();
    }

    public interface RecipeItemClickhandler {
        void onRecipeClicked(View view, int position);
    }



    public RecipeAdapter (Context context){
        dialogDismisser = (DialogDismisser) context;
        recipeItemClickhandler = (RecipeItemClickhandler) context;
        toastShown = false;
        mContext = context;
    }

    @Override
    public RecipeCardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_card, viewGroup, false);
        RecipeCardViewHolder item = new RecipeCardViewHolder(v);
        return item;
    }

    @Override
    public void onBindViewHolder(RecipeCardViewHolder holder, int position) {

        if (recipes == null) {
            holder.recipeImage.setImageResource(R.mipmap.ic_launcher);
            return;
        }

        if (!haveNetworkConnection()) {
            holder.recipeImage.setImageResource(R.mipmap.ic_launcher);
        } else {

                String url = "";
                Recipe recipe = recipes.get(position);

            Step step = recipe.getSteps().get(recipe.getSteps().size()-1);

            if (recipe.getImage() != null && !recipe.getImage().isEmpty()) {
                url = recipe.getImage();
            }

            else if (!step.getVideoURL().isEmpty()) {
                url = step.getVideoURL();
            } else if (!step.getThumbnailURL().isEmpty()){
                url = step.getThumbnailURL();
            }

            if(url.isEmpty()) holder.recipeImage.setImageResource(R.mipmap.ic_launcher);
            else {

                if (url.endsWith(".mp4")) {
                    try {
                        holder.recipeImage.setImageBitmap(retriveVideoFrameFromVideo(url));
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
                else {

                    loadImageOnline(mContext, holder.recipeImage, url);
                }


            }
            holder.title.setText(recipe.getName());
            holder.serving.setText("Servings: "+recipe.getServings());


            Log.v("RECIPE_URL", url);
        }

    }


    @Override
    public int getItemCount() {
        if (recipes == null) {
            return 10;
        } else {
            return recipes.size();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public  class RecipeCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipeCard)
        CardView cardView;
        @BindView(R.id.recipeTitle)
        TextView title;
        @BindView(R.id.recipeServing)
        TextView serving;
        ImageView recipeImage;
        RecipeCardViewHolder(View itemView) {
            super(itemView);
            recipeImage = (ImageView)itemView.findViewById(R.id.imageView);
            recipeImage.setScaleType(ImageView.ScaleType.FIT_XY);
            recipeImage.setAdjustViewBounds(true);
            title = itemView.findViewById(R.id.recipeTitle);
            serving = itemView.findViewById(R.id.recipeServing);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recipeItemClickhandler.onRecipeClicked(view, getPosition());
        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }


    private void loadImageOnline(final Context context, final ImageView img, final String url ){

        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.thumb)
                .into(img, new Callback() {
                    @Override
                    public void onSuccess() {
                        if(dialogDismisser != null) {
                            dialogDismisser.dismissDialog();

                        }
                    }

                    @Override
                    public void onError() {
                        Log.v("Picasso","Could not fetch image");

                        if(!toastShown)
                            Toast.makeText(mContext, "Cannot fetch posters. No internet connection!", Toast.LENGTH_LONG).show();
                        toastShown = true;
                    }
                });

    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
