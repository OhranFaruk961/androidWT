package com.example.faruk.wt_travel_agency;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //region Private Variables
    private Toolbar mainToolbar;
    private FirebaseAuth auth;
    private FloatingActionButton addBtn;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private RecyclerView tourListView;
    private List<Tour> tourList;
    private FirebaseFirestore firebaseFirestore;
    private TourRecyclerAdapter tourRecyclerAdapter;

//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.zatvori, R.string.otvori);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mainToolbar = (Toolbar) findViewById(R.id.navigation_actionbar);
        mNavigationView = findViewById(R.id.navigation_view);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("WT Travel Agency");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        auth = FirebaseAuth.getInstance();
        tourListView = findViewById(R.id.tour_list);
        tourList = new ArrayList<>();
        tourRecyclerAdapter = new TourRecyclerAdapter(tourList);
        tourListView.setLayoutManager(new LinearLayoutManager(this));
        tourListView.setAdapter(tourRecyclerAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Tours").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){

                        Tour tour = doc.getDocument().toObject(Tour.class);
                        tourList.add(tour);
                        tourRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mainToolbar = (Toolbar) findViewById(R.id.navigation_actionbar);
        setSupportActionBar(mainToolbar);
       // getSupportActionBar().setTitle("WT");
       // if (currentUser == null) {
         //   sendToLogin();
    //}
    }

   // @Override
   // public boolean onCreateOptionsMenu(Menu menu) {
     //   getMenuInflater().inflate(R.menu.menu,menu);
      //  return true;
    //}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       if(mToggle.onOptionsItemSelected(item)){

           return  true;
       }

       return  super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawers();

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
