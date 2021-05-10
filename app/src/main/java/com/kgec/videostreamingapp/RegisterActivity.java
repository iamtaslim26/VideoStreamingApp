package com.kgec.videostreamingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_text,password_text,username_text;
    private Button register_btn;
    private TextView login_link;

    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_text=findViewById(R.id.et_email);
        password_text=findViewById(R.id.et_password);
        register_btn=findViewById(R.id.register_btn);
        username_text=findViewById(R.id.et_username);
        login_link=findViewById(R.id.login_link);

        mAuth=FirebaseAuth.getInstance();
        loadingbar=new ProgressDialog(this);

        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=email_text.getText().toString();
                String password=password_text.getText().toString();
                String username=username_text.getText().toString();

                CreateAccount(email,password,username);

            }
        });
    }

    private void CreateAccount(String email, String password,String username) {

        loadingbar.setMessage("Authenticating. . . .  ");
        loadingbar.show();

        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Enter your email. ... ", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Enter your password. .. . ", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else {


            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        SendUserToSettingsActivity();

                        SaveUserInfo(username);

                        Toast.makeText(RegisterActivity.this, "Account Created. . . .", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                    else {

                        String e=task.getException().getMessage();
                        loadingbar.dismiss();

                        Toast.makeText(RegisterActivity.this, "Failed. ..     "+e, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    private void SaveUserInfo(String username) {

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        String current_user_id=firebaseUser.getUid();
        DatabaseReference UsersRef= FirebaseDatabase.getInstance().getReference().child("Video Users").child(current_user_id);

        HashMap<String,Object>map=new HashMap<>();
        map.put("username",username);
        map.put("uid",current_user_id);

        UsersRef.updateChildren(map);

    }

    private void SendUserToSettingsActivity() {

        Intent intent=new Intent(getApplicationContext(),SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}