package com.example.faruk.wt_travel_agency;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //region Private Variables
    private Toolbar mainToolbar;
    private  FirebaseAuth auth;
    private FloatingActionButton addBtn;

//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        addBtn = findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             Intent newTourIntent = new  Intent(MainActivity.this,AddTourActivity.class);
            startActivity(newTourIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("WT");
        if (currentUser == null) {
            sendToLogin();
    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case  R.id.log_out:
                logOut();
                return  true;

            case R.id.account_settings:
                sendToAccountSettings();
                return  true;

                default:
                    return  false;
        }
    }

    //region Helper methods
    private void logOut() {
        auth.signOut();
        sendToLogin();
    }
    private void sendToAccountSettings() {
        Intent settingsIntent = new Intent(MainActivity.this,UserNameActivity.class);
        startActivity(settingsIntent);
    }
    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class); //prebacuje nas na login
        startActivity(loginIntent);
    }
    //endregion

}
