package com.omarproject1.shashah.video;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.model.VideoItem;

import java.util.ArrayList;
import java.util.Objects;

public class VideoRecyclerView extends RecyclerView implements PlaybackPreparer {

    private static final String TAG = "RecyclerViewVideoPlayer";

    private Context context;

    @Override
    public void preparePlayback() {
        videoPlayer.retry();
    }

    private enum VolumeState {ON, OFF}

    private ArrayList<VideoItem> videoItemList = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private int playPosition = -1;
    private boolean isVideoViewAdded;

    // Component of recyclerView ui
    private ImageView volumeControl, playBtn;
    private ProgressBar progressBar;
    private View viewHolderParent;
    private FrameLayout frameLayout;
    public PlayerView playerView;
    public SimpleExoPlayer videoPlayer;

    // controlling playback state
    private VolumeState volumeState;


    public VideoRecyclerView(@NonNull Context context) {
        super(context);
        initiation(context);
    }

    public VideoRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initiation(context);

    }


    private void initiation(Context context) {
        this.context = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        playerView = new PlayerView(this.context);
//        to make video fill screen
//        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

//        we Create the simple player Exoplayer
        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        // Binding the player into view.
        playerView.setUseController(false);
        playerView.setPlayer(videoPlayer);
        videoPlayer.setPlayWhenReady(true);
        playerView.setPlaybackPreparer(this::preparePlayback);
        setVolumeControl(VolumeState.ON);



        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onScrollStateChanged: called.");
////                    if (thumbnail != null) { // show -the old thumbnail
////                        thumbnail.setVisibility(VISIBLE);
//                    }
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true);
                    } else {
                        playVideo(false);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }
            }
        });

        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                switch (playbackState) {

                    case Player.STATE_BUFFERING:
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if (progressBar != null) {
                            playerView.setVisibility(INVISIBLE);
                            progressBar.setVisibility(VISIBLE);
                        }
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        videoPlayer.seekTo(0);
//                        videoPlayer.stop();
//                        playBtn.setVisibility(INVISIBLE);
                        break;
                    case Player.STATE_IDLE:

                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        if (progressBar != null) {
                            progressBar.setVisibility(GONE);
                            playerView.setVisibility(VISIBLE);
                        }
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {


            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

    }

    private void addVideoView() {
        frameLayout.addView(playerView);
        isVideoViewAdded = true;
        playerView.requestFocus();
        playerView.setVisibility(VISIBLE);
        playerView.setAlpha(1);
//        thumbnail.setVisibility(GONE);
    }

    public void releasePlayer() {

        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
        viewHolderParent = null;
    }

    private void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(playerView);
            playPosition = -1;
            playerView.setVisibility(INVISIBLE);
//            thumbnail.setVisibility(VISIBLE);
        }
    }

    private void playVideo(boolean isEndOfList) {
        int targetPosition ;
        if (!isEndOfList) {
            int startPosition = ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }
            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return;
            }
            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

                targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            } else {
                targetPosition = startPosition;
            }
        } else {
            targetPosition = videoItemList.size() - 1;
        }
        Log.d(TAG, "playVideo: target position: " + targetPosition);

        // video is already playing so return
        if (targetPosition == playPosition) {
            return;
        }
        playerView.setVisibility(INVISIBLE);
        removeVideoView(playerView);
        int currentPosition = targetPosition - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();

        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }
        VideoPlayerViewHolder holder = (VideoPlayerViewHolder) child.getTag();

        if (holder == null) {
            playPosition = -1;
            return;
        }

//        thumbnail = holder.thumbnail;
        progressBar = holder.progressBar;
        volumeControl = holder.volumeControl;
        playBtn = holder.playBtn;

        viewHolderParent = holder.itemView;

        frameLayout = holder.itemView.findViewById(R.id.media_container);

        playerView.setPlayer(videoPlayer);

        volumeControl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVolume();
            }
        });

        viewHolderParent.setOnClickListener(videoVClickListener);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "Shashah"));
        String mediaUrl = videoItemList.get(targetPosition).getVideoUrl();
        if (mediaUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mediaUrl));
            videoPlayer.prepare(videoSource);
            videoPlayer.setPlayWhenReady(true);
        }

    }

    private OnClickListener videoVClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            togglePlaying();
        }
    };

    private void togglePlaying() {

        if (videoPlayer.getPlayWhenReady()) {
            playBtn.setVisibility(VISIBLE);
            playBtn.animate();
            playBtn.setAlpha(1f);

            playBtn.animate()
                    .alpha(0f)
                    .setDuration(600).setStartDelay(1000);
            pausePlayer();
        } else {

            playBtn.setVisibility(INVISIBLE);
            startPlayer();
        }

    }

    private void toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);

            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);

            }
        }

    }

    private int getVisibleVideoSurfaceHeight(int startPosition) {

        int at = startPosition - ((LinearLayoutManager) Objects.requireNonNull(getLayoutManager())).findFirstVisibleItemPosition();
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }

    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            //
//            playBtn.setVisibility(INVISIBLE);
            viewHolderParent.setOnClickListener(null);
        }

    }

    private void setVolumeControl(VolumeState on) {
        volumeState = on;
        if (on == VolumeState.OFF) {
            videoPlayer.setVolume(0f);
            animateVolumeControl();
        } else if (on == VolumeState.ON) {
            videoPlayer.setVolume(1f);
            animateVolumeControl();
        }
    }

    private void animateVolumeControl() {

        if (volumeControl != null) {
            volumeControl.bringToFront();
            if (volumeState == VolumeState.OFF) {
                volumeControl.setImageResource(R.drawable.ic_volume_up);
            } else if (volumeState == VolumeState.ON) {
                volumeControl.setImageResource(R.drawable.ic_volume_down);

            }
            volumeControl.animate().cancel();

//            volumeControl.setAlpha(1f);
//
//            volumeControl.animate()
//                    .alpha(0f)
//                    .setDuration(600).setStartDelay(1000);
        }
    }

    public void setMediaItems(ArrayList<VideoItem> videoItemList) {
        this.videoItemList = videoItemList;
    }

    public void pausePlayer() {
        videoPlayer.setPlayWhenReady(false);
        videoPlayer.getPlaybackState();
    }

    public void startPlayer() {
        videoPlayer.setPlayWhenReady(true);
        videoPlayer.getPlaybackState();
    }
}
