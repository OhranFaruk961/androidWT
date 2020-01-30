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
    private List<Reservation> reservationList;
    private FirebaseFirestore firebaseFirestore;
    private TourRecyclerAdapter tourRecyclerAdapter;
    private FirebaseUser currentUser;
    private TourAdminRecyclerAdapter tourAdminRecyclerAdapter;

//endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Inicijalizacija
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.zatvori, R.string.otvori);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        mainToolbar = (Toolbar) findViewById(R.id.navigation_actionbar);
        addBtn = findViewById(R.id.add_tour);
        mNavigationView = findViewById(R.id.navigation_view);
        setSupportActionBar(mainToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("WT Travel Agency");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        auth = FirebaseAuth.getInstance();
        tourListView = findViewById(R.id.tour_list);
        tourList = new ArrayList<>();
        tourRecyclerAdapter = new TourRecyclerAdapter(this, tourList);
        tourAdminRecyclerAdapter = new TourAdminRecyclerAdapter(this,tourList);
        tourListView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

       if(currentUser != null){
        String userEmail = currentUser.getEmail().toString();
        //endregion

        if(userEmail.equals("admin@mejl.com")){

            openAdmin();
        }
        else {

            openUser();
        }
       }
       else
           openUser();

        mNavigationView.setNavigationItemSelectedListener(this);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToAddNewTour();
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        mainToolbar = (Toolbar) findViewById(R.id.navigation_actionbar);
        setSupportActionBar(mainToolbar);
       // getSupportActionBar().setTitle("WT");
       if (currentUser == null) {
           sendToLogin();
    }
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
        Intent settingsIntent = new Intent(MainActivity.this,RegistracijaActivity.class);
        startActivity(settingsIntent);
    }
    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class); //prebacuje nas na login
        startActivity(loginIntent);
    }

    private void sendToAddNewTour() {
        Intent addTourIntent = new Intent(MainActivity.this, AddTourActivity.class); //prebacuje nas na add tour
        startActivity(addTourIntent);
    }

    private void openUser() {

        tourListView.setAdapter(tourRecyclerAdapter);
        firebaseFirestore.collection("Tours").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){

                        Tour tour = doc.getDocument().toObject(Tour.class);
                        tourList.add(tour);
                        tourRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    private void openAdmin() {

        tourListView.setAdapter(tourAdminRecyclerAdapter);
        firebaseFirestore.collection("Tours").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){

                        Tour tour = doc.getDocument().toObject(Tour.class);
                        tourList.add(tour);
                        tourAdminRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    //endregion

}
