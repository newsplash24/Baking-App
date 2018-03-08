package com.nanodegree.muhammadhamed.bakingapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nanodegree.muhammadhamed.bakingapp.dummy.DummyContent;
import com.nanodegree.muhammadhamed.bakingapp.models.Step;
import com.nanodegree.muhammadhamed.bakingapp.models.StepsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesHomeActivityDetailFragment extends Fragment implements View.OnClickListener {

    private SimpleExoPlayer mExoPlayer;
//    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    @BindView(R.id.noVideoTxt)
    TextView noVideoTv;
    @BindView(R.id.playerView)
    SimpleExoPlayerView player;
    @BindView(R.id.descriptionTv)
    TextView descriptionTv;
    @BindView(R.id.nextStepBtn)
    Button nextStepBtn;
    @BindView(R.id.noThumbTv)
    TextView noThumbTv;
    @BindView(R.id.thumbIv)
    ImageView thumbIv;
    private static final String TAG = RecipesHomeActivityDetailFragment.class.getSimpleName();
    public static final String ARG_ITEM_ID = "item_id";
    private Step step;
    private int stepIndx;
    private NextStepPerformer nextStepPerformer;
    private ProgressDialog pDialog;
    private long currentPlaybak;
    private MediaSource mediaSource;


    public interface NextStepPerformer {
        void nextStepClicked();
    }


    public RecipesHomeActivityDetailFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getContext() instanceof RecipesHomeActivityDetailActivity) {
            nextStepPerformer = (NextStepPerformer) getContext();
        }

        if (savedInstanceState != null) {
            currentPlaybak = savedInstanceState.getLong("playback_time");
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            stepIndx = getArguments().getInt(ARG_ITEM_ID);
            step = StepsViewModel.instance.getSteps().get(stepIndx);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(step.getShortDescription());

            }
        }

        initializeMediaSession();

        if (step != null) {
            if (step.getVideoURL() != null || !step.getVideoURL().isEmpty())
            initializePlayer(Uri.parse(step.getVideoURL()));
        }


        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading .....");


    }

    @Override
    public void onResume() {
        super.onResume();

        if (mExoPlayer != null) {
            if (mediaSource != null) {
                mExoPlayer.prepare(mediaSource);
            }

            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipeshomeactivity_detail, container, false);

        ButterKnife.bind(this, rootView);

            if (stepIndx >= StepsViewModel.instance.getSteps().size() - 1) {
                nextStepBtn.setVisibility(View.GONE);
            }
            String videoURL = "";
            if (step != null) {
                videoURL = step.getVideoURL();
                if (!step.getDescription().isEmpty()) {
                    descriptionTv.setText(step.getDescription());
                }
            }



            if (step.getThumbnailURL() != null && !step.getThumbnailURL().isEmpty()) {
                final String thumbUrl = step.getThumbnailURL();
                pDialog.setMessage("Loading thumbnail....");
                pDialog.show();
                if (step.getThumbnailURL().endsWith(".mp4")) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                retriveVideoFrameFromVideo(thumbUrl);


                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }).start();

                    noThumbTv.setVisibility(View.GONE);
                    pDialog.dismiss();

                } else {
                    pDialog.show();
                    noThumbTv.setVisibility(View.GONE);
                    loadImageOnline(getContext(), thumbIv, thumbUrl);
                }
            } else {
                thumbIv.setVisibility(View.GONE);
            }



            if (videoURL == null || videoURL.isEmpty()) {
                player.setVisibility(View.GONE);
            } else {
                noVideoTv.setVisibility(View.GONE);

            }

            if (getContext() instanceof RecipesHomeActivityDetailActivity) {
                nextStepBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextStepPerformer.nextStepClicked();
                    }
                });
            } else {
                nextStepBtn.setVisibility(View.GONE);
            }


        player.setPlayer(mExoPlayer);

        return rootView;
    }


    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("playback_time", currentPlaybak <0? 0 : currentPlaybak);
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);


            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "RecipeStepPlayer");
            mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            currentPlaybak = mExoPlayer.getCurrentPosition();
            mExoPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (player.getVisibility() == View.VISIBLE) {
            releasePlayer();
            mMediaSession.setActive(false);
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }



    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }


    private void retriveVideoFrameFromVideo(String videoPath) throws Throwable
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

        final Bitmap retrivedBitmap = bitmap;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();
                if (retrivedBitmap != null) {
                    thumbIv.setImageBitmap(retrivedBitmap);
                }

            }
        });



    }


    private void loadImageOnline(final Context context, final ImageView img, final String url ){

        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.thumb)
                .into(img, new Callback() {
                    @Override
                    public void onSuccess() {
                            pDialog.dismiss();

                    }

                    @Override
                    public void onError() {
                        Log.v("Picasso","Could not fetch image");

                            Toast.makeText(getContext(), "Cannot fetch posters. No internet connection!", Toast.LENGTH_LONG).show();

                    }
                });

    }

}
