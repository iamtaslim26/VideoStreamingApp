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

public class LoginActivity extends AppCompatActivity {

    private EditText email_text,password_text;
    private Button login_btn;
    private TextView register_link;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_text=findViewById(R.id.et_login_email);
        password_text=findViewById(R.id.et_login_password);
        login_btn=findViewById(R.id.login_btn);
        register_link=findViewById(R.id.create_link);

        loadingbar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();

        register_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email=email_text.getText().toString();
                String password=password_text.getText().toString();

                LoginAccount(email,password);

            }
        });
    }

    private void LoginAccount(String email, String password) {

        loadingbar.setMessage("Athenticating. . . . . ");
        loadingbar.show();;
        if (TextUtils.isEmpty(email)){

            Toast.makeText(this, "Enter your email. ... ", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else if (TextUtils.isEmpty(password)){

            Toast.makeText(this, "Enter your password. .. . ", Toast.LENGTH_SHORT).show();
            loadingbar.dismiss();
        }
        else {
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        SendUserToDashboardActivity();
                        Toast.makeText(LoginActivity.this, "Logged in succesfully. . .. . . . .", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    }
                    else {

                        String e=task.getException().getMessage();
                        loadingbar.dismiss();

                        Toast.makeText(LoginActivity.this, "Failed. ..     "+e, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }

    private void SendUserToDashboardActivity() {


        Intent intent=new Intent(getApplicationContext(),DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser!=null){

            SendUserToDashboardActivity();
        }
    }
}