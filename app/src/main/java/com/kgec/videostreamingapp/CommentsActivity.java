package com.kgec.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kgec.videostreamingapp.model.Comments;
import com.kgec.videostreamingapp.model.CommentsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView comments_list;
    private EditText comment_text;
    private ImageButton send_btn;
    private DatabaseReference CommentsRef,UsersRef;
    private FirebaseAuth auth;
    private String get_comment_id,get_fullname,current_userid;
    String savecurrentDate,saveCurrentTime;

    private List<Comments>list;
    private CommentsAdapter commentsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        get_comment_id=getIntent().getExtras().get("visit_video_id").toString();
//        get_fullname=getIntent().getExtras().get("visit_video_fullname").toString();


        comment_text=findViewById(R.id.Write_comments);
        send_btn=findViewById(R.id.post_comment_button);
        comments_list=findViewById(R.id.comments_list);
        comments_list.setLayoutManager(new LinearLayoutManager(this));
        comments_list.setHasFixedSize(true);

        auth=FirebaseAuth.getInstance();
        current_userid= auth.getCurrentUser().getUid();
        CommentsRef= FirebaseDatabase.getInstance().getReference().child("Videos").child(get_comment_id).child("Comments");
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Video Users").child(current_userid);


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UsersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            String fullname=dataSnapshot.child("fullname").getValue().toString();

                            String comments=comment_text.getText().toString();

                                AddComments(comments,fullname);
                                comment_text.setText(" ");

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        list=new ArrayList<>();

        DisplayComment();





    }

    private void DisplayComment() {

        CommentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                list.clear();

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    Comments comments=snapshot.getValue(Comments.class);

                    list.add(comments);
                }

                commentsAdapter=new CommentsAdapter(CommentsActivity.this,list);
                comments_list.setAdapter(commentsAdapter);



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void AddComments(String comments,String fullname) {

        if (!TextUtils.isEmpty(comments)) {


            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            savecurrentDate = dateFormat.format(calForDate.getTime());


            Calendar calForTime = Calendar.getInstance();
           // SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = timeFormat.format(calForTime.getTime());

            HashMap<String, Object> map = new HashMap<>();
            map.put("date", savecurrentDate);
            map.put("time", saveCurrentTime);
            map.put("fullname", fullname);
            map.put("comment", comments);
            map.put("uid", current_userid);

            String postRefKey = current_userid + " " + savecurrentDate + " " + saveCurrentTime;
            CommentsRef.child(postRefKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(CommentsActivity.this, "Successfull.......", Toast.LENGTH_SHORT).show();
                    } else {
                        String e = task.getException().getMessage();
                        Toast.makeText(CommentsActivity.this, "Failed.....     " + e, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else {

            Toast.makeText(this, "Write your comment first", Toast.LENGTH_SHORT).show();
        }




    }
}