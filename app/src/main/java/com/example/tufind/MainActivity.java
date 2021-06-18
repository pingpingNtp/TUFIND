package com.example.tufind;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tufind.Login.LoginUiActivity;
import com.example.tufind.Login.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private DatabaseReference dbRef;
    private DatabaseReference dbRef_tmp;
    private ImageButton hamBtn,profile;
    private String imageUri="";
    private Uri imgUri;
    public static final int MY_DEFAULT_TIMEOUT = 1200000;
    private ProgressBar progressBar;
    String result="";
    Uri outputFileUri;
    boolean valid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        dbRef = FirebaseDatabase.getInstance().getReference().child("result_finding");
        dbRef_tmp = FirebaseDatabase.getInstance().getReference().child("image_tmp");
        progressBar = (ProgressBar) findViewById(R.id.my_progressBar1);
        progressBar.setVisibility(View.GONE);
        hamBtn = (ImageButton) findViewById(R.id.hamBtn);
        hamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid){
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        showMenu(v);
                    }else{
                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&&task!=null) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.getString("isAdmin") != null) {
                                        //user is admin
                                        showMenu1(v);
                                    }
                                    if (documentSnapshot.getString("isUser") != null) {
                                        showMenu(v);

                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        imageView = (ImageView) findViewById(R.id.main_pic);
        final ImageButton cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        final ImageButton galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
        final ImageButton findBtn = (ImageButton) findViewById(R.id.findBtn);

        //ask location permission ##################################################
        try{
            if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }
        catch (Exception e){ }
        //ask location permission ##################################################

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask camera permission ##################################################
                try{
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                        requestPermissions(new String[] {Manifest.permission.CAMERA}, 1);
                    }
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        ContentValues values = new ContentValues(1);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                        outputFileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                        startActivityForResult(intent, 0);
                    }
                }
                catch (Exception e){ }
                //ask camera permission ##################################################
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ask gallery permission ##################################################
                try{
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
                    }
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select photo from"), 1);
                }
                catch (Exception e){ }
//                ask gallery permission ##################################################

            }
        });

        findBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(!isConnected()){
                        showCustomDialog();
                    }
                    progressBar.setVisibility(View.VISIBLE);
//                    Log.e("MainActivity", "test uri: " + String.valueOf(imgUri));
                    uploadImage(imgUri);
//                    img_processing();
//                    deleteImage(imageUri);
                } catch (Exception e) {
                    Log.e("Log", "Error on saving file");
                }
            }
        });

        ///login
        profile= (ImageButton)findViewById(R.id.btprofile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //               startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                ///////***************////////////////
                Log.d("iscomming", String.valueOf(valid));

                if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                    }else{
                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&&task!=null){
                                    DocumentSnapshot documentSnapshot =task.getResult();
                                    if(documentSnapshot.exists() && documentSnapshot != null){
                                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
//                                startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                    }else{
                                        startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                    }
                                }else{
                                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //camera
        if (requestCode == 0) {
            try {
                Uri selectedImage = outputFileUri;
                ContentResolver cr = getContentResolver();
                getContentResolver().notifyChange(selectedImage, null);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);
                    int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
                    Matrix matrix = new Matrix();
                    matrix.setRotate(90); //90 degrees
                    Bitmap bOutput = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    imageView.setImageBitmap(bOutput);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imgUri = outputFileUri;
//                    Log.e("MainActivity", "test uri: " +String.valueOf(outputFileUri));
//                    imgPath.setText(outputFileUri.getPath());
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to Take a picture", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("filename", "Error from Camera Activity");
            }
        }
        //gallery
        if (requestCode == 1) {
            try {
                Uri uri = data.getData();
                imgUri = data.getData();
                //start convert image to bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to Select a photo", Toast.LENGTH_SHORT).show();
                Log.e("Log", "Error on saving file");
            }
        }
    }

    public void updateTable(String place){
        try {
            LocalDate myObj = LocalDate.now();
            historyTable htr = new historyTable("user1",place,String.valueOf(myObj));
            dbRef.push().setValue(htr);
//            Toast.makeText(this, "Add Success", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void uploadImage(Uri ImageData) {
        if (ImageData != null) {
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageData);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();

                final StorageReference ImageFolder  = FirebaseStorage.getInstance().getReference().child("ImageFind");
                final StorageReference imageName = ImageFolder.child("image/"+ImageData.getLastPathSegment());
                imageName.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageUri = String.valueOf(uri);
                                tableTmp tmp = new tableTmp(imageUri,"tmp");
                                dbRef_tmp.push().setValue(tmp);
                                Log.e("MainActivity", "upload success");
                                Log.e("MainActivity", "imageUri: " + String.valueOf(imageUri));
                                img_processing();
                            }
                        });
                    }
                });
            }
            catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
//            img_processing();
        }
    }

    public void deleteImage(String imageUri){
        //delete from storage
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.e("MainActivity", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e("MainActivity", "onFailure: did not delete file");
            }
        });
        //delete from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("image_tmp").orderByChild("title").equalTo("tmp");
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                    Log.e("MainActivity", "delete success");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MainActivity", "onCancelled", databaseError.toException());
            }
        });
        progressBar.setVisibility(View.GONE);

    }

    public void img_processing(){
        String URL="https://toppnnn.pythonanywhere.com/text/";
        StringRequest request = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("MainActivity", response);
                result = response;
                updateTable(response);
                deleteImage(imageUri);
                Intent intent2 = new Intent(MainActivity.this, ResultActivity.class);
                intent2.putExtra("response",result);
                intent2.putExtra("imageUri", String.valueOf(imgUri));
                startActivity(intent2);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("MainActivity", "error: " + error);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), "Failed to Find a Building", Toast.LENGTH_SHORT).show();

            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue =  Volley.newRequestQueue(MainActivity.this);
        queue.add(request);

    }

    //check network connection ############################################
    private boolean isConnected() {
        ConnectivityManager connectivityManager = ( ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())){
            return true;
        }
        else{
            return false;
        }
    }
    private void showCustomDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Please connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent((Settings.ACTION_WIFI_SETTINGS)));
                    }
                })
                .setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    //check network connection ############################################

    class tableTmp{
        String uri;
        String title;
        public tableTmp(String uri, String title){
            this.uri=uri;
            this.title=title;
        }
        public void setUri(String uri){
            this.uri=uri;
        }
        public String getUri(){
            return uri;
        }
        public void setTitle(String title){
            this.title=title;
        }
        public String getTitle(){
            return title;
        }
    }

    class historyTable {
        String user;
        String place;
        String date;
        public historyTable(String user, String place, String date){
            this.user = user;
            this.place = place;
            this.date = date;
        }
        public historyTable(){

        }
        public void setUser(String user) {
            this.user = user;
        }
        public void setPlace(String place) {
            this.place = place;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public String getUser() {
            return user;
        }
        public String getPlace() {
            return place;
        }
        public String getDate() {
            return date;
        }
    }

    //////ต้องเพิ่มเงื่อนไข
    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_userogin, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.history){
                    Log.e("MainActivity", "history");
//                    itemStr="history";
//                    Intent intent1 = new Intent(ResultActivity.this, HistoryActivity.class);
//                    startActivity(intent1);
                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(MainActivity.this, HistoryActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                    }
                                }
                            });
                        }
                    }

                }

                return true;
            }
        });
        popupMenu.show();
    }
    //////ต้องเพิ่มเงื่อนไข
    private void showMenu1(View v){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.statistic){
                    Log.e("MainActivity", "statistic");
//                    itemStr="history";
//                    Intent intent1 = new Intent(ResultActivity.this, HistoryActivity.class);
//                    startActivity(intent1);
                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(MainActivity.this, ChartActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                    }
                                }
                            });
                        }
                    }

                }
                if(item.getItemId() == R.id.upload){
//                    startActivity(new Intent(UploadActivity.this, UploadActivity.class));
                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(MainActivity.this, UploadActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                    }
                                }
                            });
                        }
                    }
                }



                return true;
            }
        });
        popupMenu.show();
    }
}