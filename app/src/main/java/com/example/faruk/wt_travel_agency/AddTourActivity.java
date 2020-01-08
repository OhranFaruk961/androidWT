package com.example.faruk.wt_travel_agency;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddTourActivity extends AppCompatActivity {


    //region Private Variables
    private EditText add_destination;

    private EditText add_price;

    private EditText add_departure;

    private EditText add_return;

    private Button add_SaveBtn;

    private ImageView add_tour_img;

   // private ProgressBar addTour_progress;

    private StorageReference storageReference;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseAuth firebaseAuth;

    private  String current_user_id;

    //endregion

    //region Logic

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tour);

        //region Initialization

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        current_user_id = firebaseAuth.getUid();

        add_destination = findViewById(R.id.add_destination);

        add_price = findViewById(R.id.add_price);

        add_departure = findViewById(R.id.add_departure);

        add_return = findViewById(R.id.add_return);

        add_SaveBtn = findViewById(R.id.add_SaveBtn);

        add_tour_img = findViewById(R.id.add_tour_img);

       // addTour_progress = findViewById(R.id.addTour_Progress);




        //endregion


        add_tour_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        add_SaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // addTour_progress.setVisibility(View.VISIBLE);

                SaveTour(add_destination.getText().toString(),add_price.getText().toString(),add_departure.getText().toString(),add_return.getText().toString(),current_user_id);
            }
        });
    }
    //endregion

    //region Helper methods

    private void SaveTour(String destination,String price,String departureDate,String returnDate, String current_user_id ) {

        Map<String, Object> tourMap = new HashMap<>();
        tourMap.put("destination",destination);
        tourMap.put("price",price);
        tourMap.put("departureDate",departureDate);
        tourMap.put("returnDate",returnDate);
        tourMap.put("user_id",current_user_id);

        firebaseFirestore.collection("Tours").add(tourMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){
                    Toast.makeText(AddTourActivity.this,"Putovanje uspješno dodano",Toast.LENGTH_LONG).show();
                    Intent mainPage = new Intent(AddTourActivity.this,MainActivity.class);
                    startActivity(mainPage);
                    finish();//ovo onemogucava back button ?
                }else {

                }
                // addTour_progress.setVisibility(View.INVISIBLE);
            }
        });
    }
//endregion
}
