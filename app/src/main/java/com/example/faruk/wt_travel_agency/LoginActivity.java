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

public class LoginActivity extends AppCompatActivity {


    private EditText loginEmailText;
    private  EditText loginPasswordText;
    private Button loginBtn;
    private Button registrationBtn;
    private  FirebaseAuth auth;

    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        loginEmailText  = (EditText) findViewById(R.id.reg_email);

        loginPasswordText  = (EditText) findViewById(R.id.reg_password);

        loginBtn = (Button) findViewById(R.id.login);

        registrationBtn = (Button) findViewById(R.id.registration);

        loginProgress = (ProgressBar) findViewById(R.id.registration_progress);

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToRegistration();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String prijavaEmail = loginEmailText.getText().toString();
                String  prijavaLozinka = loginPasswordText.getText().toString();

                if (!TextUtils.isEmpty(prijavaEmail)&& !TextUtils.isEmpty(prijavaLozinka))
                {
                    loginProgress.setVisibility(View.VISIBLE);

                    auth.signInWithEmailAndPassword(prijavaEmail,prijavaLozinka).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                sendToMain();

                            }else    {
                           String errorMessage = task.getException().getMessage();
                           Toast.makeText(LoginActivity.this,"Error : "+ errorMessage,Toast.LENGTH_LONG     ).show();

                            }
                            loginProgress.setVisibility(View.INVISIBLE);

                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = auth.getCurrentUser();

        if(currentUser != null)
        {
            sendToMain();
        }

    }


    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class); //prebacuje nas na main ako je user logiran

        startActivity(mainIntent);
        finish();
    }

    private void sendToRegistration() {
        Intent registerIntent = new Intent(LoginActivity.this,RegistracijaActivity.class); //prebacuje nas na main ako je user logiran

        startActivity(registerIntent);
        finish();
    }

}
