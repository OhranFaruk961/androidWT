package com.example.faruk.wt_travel_agency;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistracijaActivity extends AppCompatActivity {


    private EditText reg_email_field;
    private  EditText reg_pass_field;
    private  EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_progress;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);

        auth = FirebaseAuth.getInstance();


        reg_email_field = (EditText) findViewById(R.id.reg_email);

        reg_pass_field = (EditText) findViewById(R.id.reg_password);

        reg_confirm_pass_field = (EditText) findViewById(R.id.reg_password_confirm);

        reg_btn = (Button) findViewById(R.id.reg_registration);

        reg_progress = (ProgressBar) findViewById(R.id.registration_progress);



        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = reg_email_field.getText().toString();
                String pass = reg_pass_field.getText().toString();

                String confirm_pass = reg_confirm_pass_field.getText().toString();

                if (!TextUtils.isEmpty(email)&& !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass)) {

                    if(pass.equals(confirm_pass)) {

                        reg_progress.setVisibility(View.VISIBLE);

                        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    sendToSetup();
                                }else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegistracijaActivity.this,errorMessage, Toast.LENGTH_LONG).show();
                                }
                                reg_progress.setVisibility(View.INVISIBLE);

                            }
                        });

                    } else {
                        Toast.makeText(RegistracijaActivity.this,"Lozinke se ne slažu ", Toast.LENGTH_LONG).show();

                    }
                }


            }
        });


    }

    @Override
    protected void onStart() {
            super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
        sendToMain();
        }
    }




    private void sendToMain() {

        Intent mainIntent = new Intent(RegistracijaActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();

    }

    private void sendToSetup() {

        Intent mainIntent = new Intent(RegistracijaActivity.this,UserNameActivity.class);
        startActivity(mainIntent);
        finish();


    }
}

