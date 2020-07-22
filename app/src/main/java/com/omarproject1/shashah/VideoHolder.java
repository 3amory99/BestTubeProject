package com.omarproject1.shashah;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.DatabaseReference;

public class VideoHolder extends RecyclerView.ViewHolder {


    public SimpleExoPlayer player;
    public PlayerView playerView;
    ImageButton likeButton;
    public ImageButton downloadBtn;
    ImageButton shareBtn;
    TextView likesDisplay;
    int likes;
    DatabaseReference likeReference;
    LinearLayout expandLayout;
    public CardView cardView;
    public TextView description;


    public VideoHolder(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(view,getAdapterPosition());
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickListener.onItemLongClick(view, getAdapterPosition());
                return true;
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downClickListener.onItemClick(view,getAdapterPosition());
            }
        });




    }



    public void setExoplayer (Context context, String name, String videoDescription, String videoHashTag, String videoUrl){

        downloadBtn =itemView.findViewById(R.id.download_video);

        TextView title = itemView.findViewById(R.id.title_main_activity);
        description = itemView.findViewById(R.id.description_main_activity);
        TextView hashTag = itemView.findViewById(R.id.video_hashTag_main_activity);
        playerView = itemView.findViewById(R.id.video_exoplayer_item);
        expandLayout = itemView.findViewById(R.id.expandable_view);
        cardView = itemView.findViewById(R.id.card_view);

        title.setText(name);
        description.setText(videoDescription);
        hashTag.setText(videoHashTag);

        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(context).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            player = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(context);
            Uri video = Uri.parse(videoUrl);
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video,httpDataSourceFactory,extractorsFactory,null,null);
            playerView.setPlayer(player);
            player.prepare(mediaSource);

//            Auto play
//            player.setPlayWhenReady(true);


         }catch (Exception e){

            Log.e("MyViewHolder","exoplayer error"+e.toString());

        }


    }

    public boolean isPlaying(){
        return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
    }
    public void setUpVideoActions(final String videoKey){
        downloadBtn =itemView.findViewById(R.id.download_video);
    }




    private VideoHolder.VideoClickListener clickListener;
    public interface VideoClickListener{
        void onItemClick(View view, int position);
        void onItemLongClick(View view,int position);

    }
    public void setOnClickListener(VideoHolder.VideoClickListener videoClickListener){
        clickListener = videoClickListener;
    }


    private VideoHolder.DownClickListener downClickListener;
    public interface DownClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnClickListener(VideoHolder.DownClickListener downClickListener){
        downClickListener = downClickListener;
    }
}
