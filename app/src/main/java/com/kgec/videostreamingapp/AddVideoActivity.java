package com.kgec.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddVideoActivity extends AppCompatActivity {

    private VideoView videoView;
    private Button browse_btn,upload_btn;
    private MediaController mediaController;
    private Uri videoUri;
    private DatabaseReference VideosRef,UsersRef;
    private StorageReference storageReference;
    private String current_user_id,video_title;
    private FirebaseAuth mAuth;
    private EditText title_video;
    private String savecurrentDate,savecurrentTime,PostRefKey;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        videoView=findViewById(R.id.play_video);
        browse_btn=findViewById(R.id.browse_video_btn1);
        upload_btn=findViewById(R.id.upload_video_btn1);
       title_video=findViewById(R.id.title_video);

        mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();


        storageReference= FirebaseStorage.getInstance().getReference();
        VideosRef= FirebaseDatabase.getInstance().getReference().child("Videos");
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Video Users");

        loadingbar=new ProgressDialog(this);





        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 video_title=title_video.getText().toString();
                if (TextUtils.isEmpty(video_title)){

                    Toast.makeText(AddVideoActivity.this, "Enter the title first", Toast.LENGTH_SHORT).show();
                }
                else {
                    UploadVideos(video_title);
                }


            }
        });



        browse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent=new Intent();

                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent,100);

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                permissionToken.continuePermissionRequest();

                            }
                        }).check();

            }
        });

    }

    public String getExtension(){

        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(getContentResolver().getType(videoUri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==100 && resultCode==RESULT_OK && data!=null){

            videoUri=data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    private void UploadVideos(String video_title) {

        loadingbar.setMessage("Uploading. .. . ");
        loadingbar.show();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd,yyyy");
        savecurrentDate = dateFormat.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        savecurrentTime = timeFormat.format(calForTime.getTime());


        PostRefKey = current_user_id + " " + savecurrentDate + " " + savecurrentTime;

        StorageReference filepath = storageReference.child("My Videos/" + System.currentTimeMillis() + "." + getExtension());
        filepath.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){

                    String downloadUrl=task.getResult().getDownloadUrl().toString();
                    Toast.makeText(AddVideoActivity.this, "Video is uploaded in to storage. . .. ", Toast.LENGTH_SHORT).show();
                    SaveUserintoDatabase(downloadUrl);
                }

            }
        });
    }

    private void SaveUserintoDatabase(String downloadUrl) {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    String fullname=dataSnapshot.child("fullname").getValue().toString();
                    HashMap<String,Object>map=new HashMap<>();
                    map.put("title",video_title);
                    map.put("date",savecurrentDate);
                    map.put("time",savecurrentTime);
                    map.put("fullname",fullname);
                    map.put("title",video_title);
                    map.put("uid",current_user_id);
                    map.put("url",downloadUrl);

                    VideosRef.child(PostRefKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(AddVideoActivity.this, "Video Uploaded.....", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),DashboardActivity.class));

                            }
                            else {

                                String e=task.getException().getMessage();
                                Toast.makeText(AddVideoActivity.this, "Failed....    "+e, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}



