package com.example.faruk.wt_travel_agency;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class UserNameActivity extends AppCompatActivity {


    private ImageView setupImage;

    private EditText userName;
    private Button  saveBtn;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private  FirebaseFirestore db;
    private  String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);


        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        db = FirebaseFirestore.getInstance();

        setupImage=findViewById(R.id.setup_image);

        userName = findViewById(R.id.username);

        saveBtn = findViewById(R.id.save_btn);

        user_id = firebaseAuth.getCurrentUser().getUid();

        db.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){


                    if(task.getResult().exists()) { // ako si vec logiran ispise ti username

                        userName.setText(task.getResult().getString("name"));

                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(UserNameActivity.this,"Greška na serveru",Toast.LENGTH_LONG).show();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = userName.getText().toString();

                if(!TextUtils.isEmpty(user_name)) {
                       user_id  = firebaseAuth.getCurrentUser().getUid();

                    Map<String,String> userMap = new HashMap<>();

                    userMap.put("name",user_name);
                    //ovde slika ?

                  db.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {

                          if(task.isSuccessful()){

                              Toast.makeText(UserNameActivity.this,"Korisnicko ime snimljeno",Toast.LENGTH_LONG).show();
                              Intent mainIntent = new Intent(UserNameActivity.this,MainActivity.class);
                              startActivity(mainIntent);
                              finish();

                          } else {
                              String error = task.getException().getMessage();
                              Toast.makeText(UserNameActivity.this,"Greška na serveru",Toast.LENGTH_LONG).show();
                          }


                      }
                  });



                }


            }
        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(ContextCompat.checkSelfPermission(UserNameActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(UserNameActivity.this,"Nemate permisije",Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(UserNameActivity.this, new String[]  {Manifest.permission.READ_EXTERNAL_STORAGE },1);
                    }else  {
                        Toast.makeText(UserNameActivity.this,"Vec imate permisije",Toast.LENGTH_LONG).show();
                    }

                }




            }
        });

    }

}
