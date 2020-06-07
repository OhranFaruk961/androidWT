package com.example.faruk.wt_travel_agency;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import com.bumptech.glide.Glide;
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
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import id.zelory.compressor.Compressor;

public class UserNameActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    //region Privatne varijable
    private static final int REQUEST_STORAGE = 12340;
    private static final int PICK_IMAGE = 12341;
    private static final int CAPTURE_IMAGE = 12342;

    private ImageView setupImage;

    private EditText userName;
    private Button saveBtn;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private String user_id;
    private File photoFileDoc = null;
    private Uri image_path;
    private String mImageFileLocation = "";
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);


        //region inicijalizacija
        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        db = FirebaseFirestore.getInstance();

        setupImage = findViewById(R.id.setup_image);

        userName = findViewById(R.id.username);

        saveBtn = findViewById(R.id.save_btn);

        user_id = firebaseAuth.getCurrentUser().getUid();

        //endregion


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

                    if (photoFileDoc != null) {
                        File resizeImage = null;
                        try {
                            resizeImage = new Compressor(UserNameActivity.this).compressToFile(photoFileDoc);

                            Uri imageUri = Uri.fromFile(resizeImage);
                            final StorageReference storageImageReference = storageReference.child("image/" + imageUri.getLastPathSegment() + System.currentTimeMillis());
                            UploadTask uploadTask = storageImageReference.putFile(imageUri);

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

                                        Map<String, Object> userMap = new HashMap<>();

                                        userMap.put("name", user_name);
                                        userMap.put("image", String.valueOf(downloadUri));
                                        userMap.put("admin", false);

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
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {


                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("name", user_name);
                        userMap.put("admin", false);

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
                    }
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
                        selectImage();
                    }

                } else {
                    selectImage();
                }


            }
        });

    }


    //region Helper metode
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


    //moje funkcije
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
        Intent openCamera = new Intent();
        openCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            photoFileDoc = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String authorities = getPackageName() + ".fileprovider";
        if (photoFileDoc != null) {
            image_path = FileProvider.getUriForFile(UserNameActivity.this, authorities, photoFileDoc);
        }

        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, image_path);
        startActivityForResult(openCamera, CAPTURE_IMAGE);
    }

    File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "Images " + timeStamp + "_";

        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageName, ".jpg", storageDirectory);
        mImageFileLocation = image.getAbsolutePath();

        return image;
    }

    private void reduceImageSizeDocProfile() {
        int targetImageViewWidth = setupImage.getWidth();
        int targetImageViewHeight = setupImage.getHeight();

        BitmapFactory.Options bmo = new BitmapFactory.Options();
        bmo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmo);
        int cameraImageWidth = bmo.outWidth;
        int cameraImageHeight = bmo.outHeight;

        int scaleImage = Math.max(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmo.inSampleSize = scaleImage;

        bmo.inJustDecodeBounds = false;

        Bitmap reduceBitmap = BitmapFactory.decodeFile(mImageFileLocation, bmo);
        setupImage.setImageBitmap(reduceBitmap);

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
            if (data == null) {
                Toast.makeText(UserNameActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
            } else {
                image_path = data.getData();
                assert image_path != null;
                if (image_path.toString().contains("com.google.android.apps.docs.storage")) {
                    Toast.makeText(UserNameActivity.this, "You cannot select image from Google Drive", Toast.LENGTH_LONG).show();

                } else {

                    try {
                        photoFileDoc = new File(Objects.requireNonNull(getFilePath(UserNameActivity.this, image_path)));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    Glide.with(getApplicationContext()).load(image_path).into(setupImage);

                }

            }
        } else if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            reduceImageSizeDocProfile();
        }
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    //endregion
}
