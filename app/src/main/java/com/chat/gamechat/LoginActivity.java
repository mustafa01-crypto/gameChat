package com.chat.gamechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        mAuth = FirebaseAuth.getInstance();
    }


    public void signIn(View view)
    {
        String mail_txt = email.getText().toString();
        String pass_txt = password.getText().toString();


            mAuth.signInWithEmailAndPassword(mail_txt,pass_txt)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            if (task.isSuccessful())
                            {
                                Intent chatIntent= new Intent(LoginActivity.this,ChatActivity.class);
                                startActivity(chatIntent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

}