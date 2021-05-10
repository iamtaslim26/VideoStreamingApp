package com.kgec.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private EditText user_name,full_name,country_name;
    private Button save_btn;
    private ImageView profile_image;
    private StorageReference ProfileImageRef;
    private DatabaseReference SettingsRef;
    private String currentUserId;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profile_image=findViewById(R.id.set_up_image);
        user_name=findViewById(R.id.set_up_username1);
        full_name=findViewById(R.id.set_up_Full_Name1);
        country_name=findViewById(R.id.set_up_country_name1);
        save_btn=findViewById(R.id.set_up_save_button1);

        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfileImageRef= FirebaseStorage.getInstance().getReference().child("Video Profile Images");
        SettingsRef= FirebaseDatabase.getInstance().getReference().child("Video Users").child(currentUserId);


        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,101);

            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveUserInfo();
            }
        });

        SettingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("ProfileImage")&& dataSnapshot.hasChild("fullname") && dataSnapshot.hasChild("country")){

                        String FULLNAME=dataSnapshot.child("fullname").getValue().toString();

                        String COUNTRY=dataSnapshot.child("country").getValue().toString();
                        String PROFILEIMAGE=dataSnapshot.child("ProfileImage").getValue().toString();


                        full_name.setText(FULLNAME);
                        country_name.setText(COUNTRY);

                        Picasso.get().load(PROFILEIMAGE).placeholder(R.drawable.profile).into(profile_image);
                    }
                    String USERNAME=dataSnapshot.child("username").getValue().toString();
                    user_name.setText(USERNAME);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void SaveUserInfo() {

        String username=user_name.getText().toString();
        String fullname=full_name.getText().toString();
        String country=country_name.getText().toString();

        if (TextUtils.isEmpty(username)  && TextUtils.isEmpty(fullname) && TextUtils.isEmpty(country)){

            Toast.makeText(this, "Please Fill the documents. . . . . . . . ", Toast.LENGTH_SHORT).show();
        }
        else {

            HashMap<String,Object>map=new HashMap<>();
            map.put("username",username);
            map.put("fullname",fullname);
            map.put("country",country);

            SettingsRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()){

                        Toast.makeText(SettingsActivity.this, "Update Successfully . . . . .", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),DashboardActivity.class));
                    }
                    else {

                        String e=task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Failed.. .. ..      "+e, Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==101 && resultCode==RESULT_OK && data!=null){

            ImageUri=data.getData();
            profile_image.setImageURI(ImageUri);
        }

        StorageReference filepath=ProfileImageRef.child(currentUserId);
        filepath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){

                    String downloadUrl=task.getResult().getDownloadUrl().toString();
                    Toast.makeText(SettingsActivity.this, "Image is Stored in to Storage", Toast.LENGTH_SHORT).show();

                    SettingsRef.child("ProfileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                Toast.makeText(SettingsActivity.this, "Update Successfully in Database", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                String e=task.getException().getMessage();
                                Toast.makeText(SettingsActivity.this, "Failed.. .. ..      "+e, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }
                else {

                    String e=task.getException().getMessage();
                    Toast.makeText(SettingsActivity.this, "Failed.. .. ..      "+e, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}