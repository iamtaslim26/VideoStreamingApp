package com.kgec.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditActivity extends AppCompatActivity {

    private String get_video_id,currentUserId;
    private VideoView edit_video_view;
    private String get_video_url,Description;
    private TextView edit_description;
    private Button edit_btn,delete_btn;
    private FirebaseAuth mAuth;

    private DatabaseReference VideosRef;
    VideosViewHolder videosViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        get_video_id= getIntent().getExtras().get("visit_video_id").toString();
//        get_video_url= getIntent().getExtras().get("visit_video_url").toString();

     //   edit_video_view=findViewById(R.id.edit_videoview);
        edit_description=findViewById(R.id.edit_video_description);
        edit_btn=findViewById(R.id.edit_video_post_btn);
        delete_btn=findViewById(R.id.delete_video_post_btn);

        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        VideosRef= FirebaseDatabase.getInstance().getReference().child("Videos");


        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Description=edit_description.getText().toString();

                EditVideo(Description);
            }
        });

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DeleteVideo();


            }
        });

        VideosRef.child(get_video_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String url=dataSnapshot.child("url").getValue().toString();
                    String uid=dataSnapshot.child("uid").getValue().toString();
                    String description=dataSnapshot.child("title").getValue().toString();


                    if (uid.equals(currentUserId)){

                        edit_btn.setVisibility(View.VISIBLE);
                        delete_btn.setVisibility(View.VISIBLE);

                    }

                    edit_description.setText(description);

//                    videosViewHolder.prepareExoplayer(getApplication(),description,get_video_url);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void EditVideo(String description) {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Edit Post");
        final EditText inputFiled=new EditText(this);
        inputFiled.setText(Description);
        builder.setView(inputFiled);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                VideosRef.child(get_video_id).child("title").setValue(inputFiled.getText().toString());
                Toast.makeText(EditActivity.this, "Updated Succesfully. . . . ", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });

        Dialog dialog=builder.create();
        dialog.show();


    }

    private void DeleteVideo() {

        VideosRef.child(get_video_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(EditActivity.this, "Video Deleted. . . . ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                }
                else {

                    String e=task.getException().getMessage();
                    Toast.makeText(EditActivity.this, "Failed....       "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}