package com.chat.gamechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
        {
            Intent chatIntent = new Intent(StartActivity.this,ChatActivity.class);
            startActivity(chatIntent);
            finish();
        }
    }

    public void signToRegister(View view)
    {
        Intent loginIntent = new Intent(StartActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    public void signToLogin(View view)
    {

        Intent registerIntent = new Intent(StartActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }
}