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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText email,password,username;
    private FirebaseAuth mAuth;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.sign_email);
        password = findViewById(R.id.sign_password);
        username = findViewById(R.id.sign_nickname);

        mAuth = FirebaseAuth.getInstance();




    }

    public void signUp(View view)
    {
        String txt_username = username.getText().toString();
        String txt_email = email.getText().toString();
        String txt_password = password.getText().toString();

        if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
            Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else if (txt_password.length() < 6 ){
            Toast.makeText(RegisterActivity.this, "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
        } else {
            register(txt_username, txt_email, txt_password);
        }
    }




    private void register(final String username, String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {

                            FirebaseUser user =mAuth.getCurrentUser();
                            assert user != null;
                            String userID = user.getUid();


                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                            HashMap<String,String> hashMap = new HashMap<>();
                            hashMap.put("id",userID);
                            hashMap.put("username",username);
                            hashMap.put("imageURL","default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        Intent chatIntent= new Intent(RegisterActivity.this,ChatActivity.class);
                                        chatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(chatIntent);
                                        finish();
                                    }
                                }
                            });

                        }
                        else
                        {
                            Toast.makeText(RegisterActivity.this,"Error",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


}