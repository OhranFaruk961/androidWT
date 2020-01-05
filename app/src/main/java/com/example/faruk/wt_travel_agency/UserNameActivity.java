package com.example.faruk.wt_travel_agency;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UserNameActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final int REQUEST_STORAGE = 12340;
    private static final int PICK_IMAGE = 12341;

    private ImageView setupImage;

    private EditText userName;
    private Button saveBtn;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private String user_id;
    private Uri image_path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);


        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        db = FirebaseFirestore.getInstance();

        setupImage = findViewById(R.id.setup_image);

        userName = findViewById(R.id.username);

        saveBtn = findViewById(R.id.save_btn);

        user_id = firebaseAuth.getCurrentUser().getUid();

        db.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {


                    if (task.getResult().exists()) { // ako si vec logiran ispise ti username

                        userName.setText(task.getResult().getString("name"));

                    }

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(UserNameActivity.this, "Greška na serveru", Toast.LENGTH_LONG).show();
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String user_name = userName.getText().toString();

                if (!TextUtils.isEmpty(user_name)) {
                    user_id = firebaseAuth.getCurrentUser().getUid();

                    Uri file = image_path;
                    final StorageReference storageImageReference = storageReference.child("image/" + file.getLastPathSegment());
                    UploadTask uploadTask = storageImageReference.putFile(file);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return storageImageReference.getDownloadUrl();
                        }
                    });
                    urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                Map<String, String> userMap = new HashMap<>();

                                userMap.put("name", user_name);
                                userMap.put("image", String.valueOf(downloadUri));

                                db.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {

                                            Toast.makeText(UserNameActivity.this, "Korisnicko ime snimljeno", Toast.LENGTH_LONG).show();
                                            Intent mainIntent = new Intent(UserNameActivity.this, MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();

                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(UserNameActivity.this, "Greška na serveru", Toast.LENGTH_LONG).show();
                                        }


                                    }
                                });
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(UserNameActivity.this, "Greška na serveru", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }


            }
        });


        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(UserNameActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(UserNameActivity.this, "Nemate permisije", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(UserNameActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                    } else {
                        Toast.makeText(UserNameActivity.this, "Vec imate permisije", Toast.LENGTH_LONG).show();
                        selectImage();
                    }

                } else {
                    selectImage();
                }


            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                }

                break;
            default:
                Toast.makeText(UserNameActivity.this, "Nemate permisije", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void selectImage() {
        PopupMenu popupMenu = new PopupMenu(UserNameActivity.this, setupImage);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.select_image_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(this); //postavljamo menu item click listener kojeg smo implementovali na početku klase
    }

    //detektujemo koji smo item kliknuli
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.capture:
                openCamera();
                return true;
            case R.id.gallery:
                openGallery();
                return true;
            default:
                return false;
        }
    }

    private void openCamera() {

    }

    private void openGallery() {
        //otvaramo gallery i u metodi onActivityResults dobijamo image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
           //dobijemo image, uzmemo cijeli path od slike (treba za upload slike na firebase storage)
           image_path = data.getData();
           setupImage.setImageURI(image_path);
       }
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
