package com.example.faruk.wt_travel_agency;

import android.annotation.SuppressLint;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    private ReservationRecyclerAdapter reservationRecyclerAdapter;
    private FirebaseUser currentUser;
    private TourAdminRecyclerAdapter tourAdminRecyclerAdapter;
    private TextView usernameNavDrawer;
    private ImageView profileImageNavDrawer;
    private boolean isAdmin;

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
        usernameNavDrawer = mNavigationView.getHeaderView(0).findViewById(R.id.profile_username_nav);
        profileImageNavDrawer = mNavigationView.getHeaderView(0).findViewById(R.id.profile_image_nav);
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
        reservationList = new ArrayList<Reservation>();
        reservationRecyclerAdapter = new ReservationRecyclerAdapter(this,reservationList);
        tourListView.setLayoutManager(new LinearLayoutManager(this));
        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //endregion
        if (currentUser != null) {
            firebaseFirestore.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.isSuccessful() || !task.getResult().exists()) return;

                    isAdmin = task.getResult().getBoolean("admin");
                    String username = task.getResult().getString("name");
                    String imageProfile = task.getResult().getString("image");

                    usernameNavDrawer.setText(username);
                    Glide.with(MainActivity.this).load(imageProfile).into(profileImageNavDrawer);

                    if (isAdmin)
                        openAdmin();
                    else {
                        openUser();
                        addBtn.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }


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

            case R.id.reservation:
                sendToReservations();
                return  true;

            case R.id.home_screen:
                sendToTourScreen();
                return true;

            default:
                return  false;
        }
    }

    private void sendToTourScreen() {
        tourListView.setAdapter(tourRecyclerAdapter);
        tourList.clear();
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

    private void sendToReservations() {

        reservationList.clear();
        tourListView.setAdapter(reservationRecyclerAdapter);
        Query collectionReference = null;
        if (isAdmin)
                collectionReference = firebaseFirestore.collection("Reservations");
        else
            collectionReference = firebaseFirestore.collection("Reservations").whereEqualTo("user_id", currentUser.getUid());

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (e != null) {
                    e.printStackTrace();
                    return;
                }

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED){

                        Reservation reservation = doc.getDocument().toObject(Reservation.class).withId(doc.getDocument().getId());
                        reservationList.add(reservation);
                        reservationRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


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
