package com.chat.gamechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    EditText messageText;
    RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<String> chatMessages = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private AdView mAdView;
    ScrollView scrollView ;
    Toolbar mToolbar;


    private static final String ONESIGNAL_APP_ID ="5471d2ad-520f-4072-ad6d-bd00e02ec14e";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //ca-app-pub-6313549492070266/8717981128 banner add
        //ca-app-pub-3940256099942544/6300978111 test add

        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        messageText = findViewById(R.id.input_message);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Game Chat");

        scrollView = findViewById(R.id.scrollView);
        scrollView.fullScroll(View.FOCUS_DOWN);

        AlertDialog.Builder builder =new AlertDialog.Builder(ChatActivity.this,R.style.MyAlertDialogStyle);
        builder.setTitle("Game Chat");

        RecyclerView.LayoutManager recyclerViewManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(chatMessages);
        recyclerView.setAdapter(recyclerViewAdapter);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        //userid = getIntent().getStringExtra("username");
        getData();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();



        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String userId, final String registrationId) {
                System.out.println("userID: " + userId);

                UUID uuid = UUID.randomUUID();
                String uuidString = uuid.toString();

                DatabaseReference newReference = database.getReference("playerIDs");
                newReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ArrayList<String> playerIDsFromServer = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            HashMap<String,String> hashMap =(HashMap<String,String>) ds.getValue();

                            String currentPlayerID = hashMap.get("playerID");

                            playerIDsFromServer.add(currentPlayerID);
                        }
                        if (!playerIDsFromServer.contains(userId))
                        {
                            databaseReference.child("playerIDs").child(uuidString).child("playerID").setValue(userId);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.options_menu_sign_out)
        {
            mAuth.signOut();
            Intent mainIntent = new Intent(getApplicationContext(), StartActivity.class);
            startActivity(mainIntent);
        }
        else if (item.getItemId()== R.id.options_menu_profile)
        {
            Intent proIntent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(proIntent);
        }
        return super.onOptionsItemSelected(item);
    }
    public void sendMessage(View view)
    {


        scrollView.fullScroll(View.FOCUS_DOWN);
        String messageToSend =  messageText.getText().toString();
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail().toString();



        databaseReference.child("Chats").child(uuidString).child("user message").setValue(messageToSend);
        databaseReference.child("Chats").child(uuidString).child("user email").setValue(userEmail);
        databaseReference.child("Chats").child(uuidString).child("user message time").setValue(ServerValue.TIMESTAMP);


        messageText.setText("");

        getData();

        //OneSignal

        DatabaseReference newReference = database.getReference("playerIDs");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    HashMap<String,String> hashMap = (HashMap<String, String>) ds.getValue();

                    String playerID = hashMap.get("playerID");

                    try {
                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+messageToSend+"'}, 'include_player_ids': ['" + playerID + "']}"), null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getData()
    {
        scrollView.fullScroll(View.FOCUS_DOWN);
        DatabaseReference newReference = database.getReference("Chats");

        Query query = newReference.orderByChild("user message time");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatMessages.clear();


                for (DataSnapshot ds: snapshot.getChildren())
                {
                    HashMap<String,String> hashMap = (HashMap<String, String>) ds.getValue();
                    String userEmail = hashMap.get("user email");
                    String userMessage = hashMap.get("user message");


                    chatMessages.add( userEmail  + " ==> " + userMessage);

                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        });

    }


}