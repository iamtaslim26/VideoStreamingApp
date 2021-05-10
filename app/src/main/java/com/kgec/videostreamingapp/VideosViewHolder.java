package com.kgec.videostreamingapp;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideosViewHolder extends RecyclerView.ViewHolder {

    TextView upload_date,upload_time,Description,view_fullname,no_of_likes_btn;
    SimpleExoPlayerView simpleExoPlayerView;
    SimpleExoPlayer simpleExoPlayer;
    ImageButton like_btn,comment_btn;

    DatabaseReference LikeRef;
    String current_user_id;
    int LikesCount=0;



    public VideosViewHolder(@NonNull View itemView) {
        super(itemView);

            upload_date=itemView.findViewById(R.id.post_date);
            upload_time=itemView.findViewById(R.id.post_Time);
            Description=itemView.findViewById(R.id.post_description);
            simpleExoPlayerView=itemView.findViewById(R.id.exoplayer);
            view_fullname=itemView.findViewById(R.id.username);

            comment_btn=itemView.findViewById(R.id.comment_button);
            like_btn=itemView.findViewById(R.id.like_post_button);
            no_of_likes_btn=itemView.findViewById(R.id.display_no_of_likes);

         LikeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
         current_user_id= FirebaseAuth.getInstance().getCurrentUser().getUid();




    }

    void prepareExoplayer(Application application,String videotitle,String videoURL){

        try {

            Description.setText(videotitle);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            simpleExoPlayer =(SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(application,trackSelector);

            Uri videoURI = Uri.parse(videoURL);

            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            simpleExoPlayerView.setPlayer(simpleExoPlayer);
            simpleExoPlayer.prepare(mediaSource);
            simpleExoPlayer.setPlayWhenReady(false);


        }catch (Exception e){

            Log.e("Explayer Crashed", "Error : " + e.toString());
        }
    }

    public void CheckLikeStatus(String get_video_id) {

        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(get_video_id).hasChild(current_user_id)){

                    LikesCount= (int) dataSnapshot.child(get_video_id).getChildrenCount();
                    like_btn.setImageResource(R.drawable.like);
                    no_of_likes_btn.setText(Integer.toString(LikesCount)+" Likes");
                }
                else {
                    LikesCount= (int) dataSnapshot.child(get_video_id).getChildrenCount();
                    like_btn.setImageResource(R.drawable.dislike);
                    no_of_likes_btn.setText(Integer.toString(LikesCount)+" Likes");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
