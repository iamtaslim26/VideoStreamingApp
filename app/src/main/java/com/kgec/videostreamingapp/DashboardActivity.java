package com.kgec.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kgec.videostreamingapp.model.Videos;

public class DashboardActivity extends AppCompatActivity {
    private FloatingActionButton fb_btn;
    private Toolbar mToolbar;
    private RecyclerView video_list;
    private DatabaseReference VideosRef,LikesRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    Boolean LikeChecker=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();


        fb_btn = findViewById(R.id.floating_btn);
        mToolbar = findViewById(R.id.dashBoard_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Videos");

        video_list = findViewById(R.id.video_list);
        video_list.setLayoutManager(new LinearLayoutManager(this));
        video_list.setHasFixedSize(true);

        currentUserId=FirebaseAuth.getInstance().getCurrentUser().getUid();
        VideosRef = FirebaseDatabase.getInstance().getReference().child("Videos");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), AddVideoActivity.class));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Videos> options = new FirebaseRecyclerOptions.Builder<Videos>()
                .setQuery(VideosRef, Videos.class)
                .build();


        FirebaseRecyclerAdapter<Videos, VideosViewHolder> adapter = new FirebaseRecyclerAdapter<Videos, VideosViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull VideosViewHolder holder, int position, @NonNull Videos model) {

                holder.upload_time.setText(model.getTime());
                holder.Description.setText(model.getTitle());
                holder.upload_date.setText(model.getDate());
                holder.view_fullname.setText(model.getFullname());

                holder.prepareExoplayer(getApplication(), model.getTitle(), model.getUrl());

                String get_video_id=getRef(position).getKey();
              //  String get_fullname=getRef(position).getKey();


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent video_intent=new Intent(getApplicationContext(),EditActivity.class);
                        video_intent.putExtra("visit_video_id",get_video_id);
                        //video_intent.putExtra("visit_video_url",model.getUrl());
                        startActivity(video_intent);

                    }
                });

                holder.comment_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent comment_intent=new Intent(getApplicationContext(),CommentsActivity.class);
                        comment_intent.putExtra("visit_video_id",get_video_id);
                        //comment_intent.putExtra("visit_video_fullname",get_fullname);
                        startActivity(comment_intent);


                    }
                });

                holder.like_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LikeChecker=true;

                        LikesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (LikeChecker=true){

                                    if (dataSnapshot.child(get_video_id).hasChild(currentUserId)){

                                        LikesRef.child(get_video_id).child(currentUserId).removeValue();
                                        LikeChecker=false;
                                    }
                                    else {

                                        LikesRef.child(get_video_id).child(currentUserId).setValue(true);
                                        LikeChecker=false;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });



                holder.CheckLikeStatus(get_video_id);

            }

            @NonNull
            @Override
            public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_videos_layout, parent, false);
                VideosViewHolder videosViewHolder = new VideosViewHolder(view);
                return videosViewHolder;
            }
        };

        video_list.setAdapter(adapter);
        adapter.startListening();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.settings_page){

            startActivity(new Intent(getApplicationContext(),SettingsActivity.class));

        }
        else if (item.getItemId()==R.id.logout_options){

            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        }
        return true;
    }
}