package com.example.faruk.wt_travel_agency;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

public class AddTourActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final int REQUEST_STORAGE = 12340;
    private static final int PICK_IMAGE = 12341;
    private static final int CAPTURE_IMAGE = 12342;

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

    private File photoFileDoc = null;
    private Uri image_path;
    private String mImageFileLocation = "";

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(AddTourActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(AddTourActivity.this, "Nemate permisije", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(AddTourActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                    } else {
                        selectImage();
                    }

                } else {
                    selectImage();
                }

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

    private void SaveTour(final String destination, final String price, final String departureDate, final String returnDate, final String current_user_id ) {

        File resizeImage = null;
        try {
            resizeImage = new Compressor(AddTourActivity.this).compressToFile(photoFileDoc);

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

                        Map<String, Object> tourMap = new HashMap<>();
                        tourMap.put("destination",destination);
                        tourMap.put("price",price);
                        tourMap.put("departureDate",departureDate);
                        tourMap.put("returnDate",returnDate);
                        tourMap.put("user_id",current_user_id);
                        tourMap.put("image", downloadUri.toString());

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
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(AddTourActivity.this, "Greška na serveru", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
//endregion

    private void selectImage() {
        PopupMenu popupMenu = new PopupMenu(AddTourActivity.this, add_tour_img);
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
            image_path = FileProvider.getUriForFile(AddTourActivity.this, authorities, photoFileDoc);
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
        int targetImageViewWidth = add_tour_img.getWidth();
        int targetImageViewHeight = add_tour_img.getHeight();

        BitmapFactory.Options bmo = new BitmapFactory.Options();
        bmo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmo);
        int cameraImageWidth = bmo.outWidth;
        int cameraImageHeight = bmo.outHeight;

        int scaleImage = Math.max(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmo.inSampleSize = scaleImage;

        bmo.inJustDecodeBounds = false;

        Bitmap reduceBitmap = BitmapFactory.decodeFile(mImageFileLocation, bmo);
        add_tour_img.setImageBitmap(reduceBitmap);

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
                Toast.makeText(AddTourActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
            } else {
                image_path = data.getData();
                assert image_path != null;
                if (image_path.toString().contains("com.google.android.apps.docs.storage")) {
                    Toast.makeText(AddTourActivity.this, "You cannot select image from Google Drive", Toast.LENGTH_LONG).show();

                } else {

                    try {
                        photoFileDoc = new File(Objects.requireNonNull(getFilePath(AddTourActivity.this, image_path)));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    Glide.with(getApplicationContext()).load(image_path).into(add_tour_img);

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
}
