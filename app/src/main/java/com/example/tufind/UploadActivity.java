package com.example.tufind;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tufind.Login.LoginFragment;
import com.example.tufind.Login.LoginUiActivity;
import com.example.tufind.Login.ProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Uri imageUri;
    private ArrayList<String> fileName_list = new ArrayList<>();
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private String dropdown="";
    private Spinner spinner;
    private DatabaseReference dbRef;
    private String imgList1="";
    private TextView img_list;
    private ProgressBar progressBar;
    private ImageButton hamBtn;
    private final int success=100;
    //***********************************************************************
    private DatabaseReference mDatabase;
    private RadioButton buildingBtn, nearBtn;
    private LinearLayout nearLinear;
    private String name="";
    private String detail="";
    private boolean valid = true;
    private ImageButton profile;
    //***********************************************************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().hide();

        nearLinear = (LinearLayout) findViewById(R.id.nearLinear);
        buildingBtn = (RadioButton) findViewById(R.id.buildingBtn);
        nearBtn = (RadioButton) findViewById(R.id.nearBtn);
        hamBtn = (ImageButton) findViewById(R.id.hamBtn);
        hamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
        dbRef = FirebaseDatabase.getInstance().getReference().child("Images_building");
        //***********************************************************************
        mDatabase = FirebaseDatabase.getInstance().getReference();  //main database
        //***********************************************************************
        spinner = (Spinner) findViewById(R.id.building_spinner);
        spinner.setOnItemSelectedListener(this);
        img_list = (TextView) findViewById(R.id.img_list);
        progressBar = (ProgressBar) findViewById(R.id.my_progressBar2);
        progressBar.setVisibility(View.GONE);

        /////*****login*****///////////
        profile = (ImageButton)findViewById(R.id.btprofile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //               startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                ///////***************////////////////
                Log.d("iscomming", String.valueOf(valid));

                if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
                    }else{
                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&&task!=null){
                                    DocumentSnapshot documentSnapshot =task.getResult();
                                    if(documentSnapshot.exists() && documentSnapshot != null){
                                        startActivity(new Intent(UploadActivity.this, ProfileActivity.class));
//                                startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                    }else{
                                        startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
                                    }
                                }else{
                                    startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
                                }
                            }
                        });
                    }
                }
            }
        });

        final Button uploadBtn = (Button) findViewById(R.id.uploadBtn);
        final Button clearBtn = (Button) findViewById(R.id.clearBtn);
        final ImageButton selectedBtn = (ImageButton) findViewById(R.id.selectedBtn);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            //edittttttttttttttttttt
            @Override
            public void onClick(View v) {
                TextInputLayout nameTxt = findViewById(R.id.nameTxt);
                TextInputLayout detailTxt = findViewById(R.id.detailTxt);
                name = nameTxt.getEditText().getText().toString();
                detail = nameTxt.getEditText().getText().toString();
                if(ImageList.isEmpty() || name=="" || detail ==""){
                    Toast.makeText(UploadActivity.this, "Please select image again \n Enter data again!", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!isConnected()){
                        showCustomDialog();
                    }
                    for(int i=0; i<ImageList.size(); i++){
                        uploadImage(ImageList.get(i),fileName_list.get(i));
                    }
                    Toast.makeText(UploadActivity.this, "Uploaded Success", Toast.LENGTH_SHORT).show();
                }
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageList.clear();
                fileName_list.clear();
                img_list.setText("");
                Log.e("MainActivity", "size: " + String.valueOf(fileName_list.size()));

            }
        });

        selectedBtn.setOnClickListener(new View.OnClickListener() {
            //edittttttttttttttttttt
            @Override
            public void onClick(View v) {
                if(buildingBtn.isChecked()){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
                }
                else{
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(Intent.createChooser(intent, "Select photo from"), 2);
                    }
                }
            }
        });
        buildingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MainActivity", "building test");
                buildingBtn.setChecked(true);
                nearBtn.setChecked(false);
                nearLinear.setVisibility(View.GONE);
            }
        });
        nearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MainActivity", "near test");
                buildingBtn.setChecked(false);
                nearBtn.setChecked(true);
                nearLinear.setVisibility(View.VISIBLE);
            }
        });
    }


    private void uploadImage(Uri ImageData, final String fileName) {
        if (ImageData != null) {
            try {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageData);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 10, baos);
                byte[] data = baos.toByteArray();

                if(data.length>350000){
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), ImageData);
                    baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 5, baos);
                    data = baos.toByteArray();
                }

                progressBar.setVisibility(View.VISIBLE);
                StorageReference ImageFolder=null;
                if(buildingBtn.isChecked()){
                    ImageFolder  = FirebaseStorage.getInstance().getReference().child("ImageFolder");
                } else{
                    ImageFolder  = FirebaseStorage.getInstance().getReference().child("ImageNearPlace");
                }
//                final StorageReference ImageFolder  = FirebaseStorage.getInstance().getReference().child("ImageFolder");
                final StorageReference imageName = ImageFolder.child("image/"+ImageData.getLastPathSegment());
                imageName.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                changeTw(fileName,success);
                                progressBar.setProgress(0);
                                progressBar.setVisibility(View.GONE);
                                if(buildingBtn.isChecked()){
                                    defineDatabase define = new defineDatabase(String.valueOf(uri),dropdown);
                                    dbRef.push().setValue(define);
                                } else{
                                    NearPlace nearPlace = new NearPlace(name, String.valueOf(uri), detail);
                                    mDatabase.child("Nearplace").child(dropdown).push().setValue(nearPlace);
                                }

                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        int progress = (int)(((float)snapshot.getBytesTransferred() * 100f) / (float)snapshot.getTotalByteCount());
                        progressBar.setProgress(progress);
                        changeTw(fileName, progress);
                        Log.e("MainActivity", "progress " + String.valueOf(progress));
                    }
                });
            }
            catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }

    }

    private String getImageFilePath(Uri uri) {
        String path = null, image_id = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            image_id = cursor.getString(0);
            image_id = image_id.substring(image_id.lastIndexOf(":") + 1);
            cursor.close();
        }
        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor!=null) {
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            cursor.close();
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int count=0;
        String imgList="";

        if(requestCode == 1 && resultCode == RESULT_OK){
            if (data.getClipData() != null) {
                count = data.getClipData().getItemCount();
                int CurrentImageSelect = 0;
                while (CurrentImageSelect < count) {
                    imageUri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                    ImageList.add(imageUri);
                    String filePath = getImageFilePath(imageUri);
                    File f = new File(filePath);
                    String fileName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/")+1);
                    fileName_list.add(fileName);
                    CurrentImageSelect++;
                }
            }
            else{
                imageUri = data.getData();
                ImageList.add(imageUri);
                String filePath = getImageFilePath(imageUri);
                File f = new File(filePath);
                String fileName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/")+1);
                fileName_list.add(fileName);
            }
            for(int i=0; i<fileName_list.size(); i++){
                imgList += fileName_list.get(i) + "\n";
            }
            img_list.setText(imgList);
        }
        if(requestCode == 2 && resultCode == RESULT_OK){
            imageUri = data.getData();
            ImageList.add(imageUri);
            String filePath = getImageFilePath(imageUri);
            File f = new File(filePath);
            String fileName = f.getAbsolutePath().substring(f.getAbsolutePath().lastIndexOf("/")+1);
            fileName_list.add(fileName);
            for(int i=0; i<fileName_list.size(); i++){
                imgList += fileName_list.get(i);
            }
            img_list.setText(imgList);
        }
    }

    public void changeTw(String fileName, int progress){
        String uploaded = getResources().getString(R.string.status_1);
        String uploading = getResources().getString(R.string.status_2);
        imgList1="";
        int index=0;
        for(int i=0; i<fileName_list.size(); i++){
            if(fileName.equalsIgnoreCase(fileName_list.get(i))){
                index = i;
            }
        }
        for(int i=0; i<fileName_list.size(); i++){
            if(i<index){
                imgList1 += fileName_list.get(i) + "\t" + uploaded + "\n";
            }
            else{
                if(progress==100){
                    imgList1 += fileName_list.get(i) + "\t" + uploaded + "\n";
                }
                else{
                    imgList1 += fileName_list.get(i) + "\t" + uploading + "\n";
                }
            }
        }
        img_list.setText(imgList1);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String value = spinner.getSelectedItem().toString();
        if(value.equalsIgnoreCase(getResources().getString(R.string.LAWS))){
            dropdown="LAWS";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.TBS))){
            dropdown="TBS";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.ECON))){
            dropdown="ECON";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.SW))){
            dropdown="SW";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.SA))){
            dropdown="SA";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.ARTS))){
            dropdown="ARTS";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.JC))){
            dropdown="JC";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.TSE))){
            dropdown="TSE";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.SIIT))){
            dropdown="SIIT";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.TDS))){
            dropdown="TDS";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.FINEART))){
            dropdown="FINEART";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.KNK))){
            dropdown="KNK";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.PYC))){
            dropdown="PYC";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.LSED))){
            dropdown="LSED";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.PYC2))){
            dropdown="PYC2";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.LITU))){
            dropdown="LITU";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.SC1))){
            dropdown="SC1";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.SC2))){
            dropdown="SC2";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.SC3))){
            dropdown="SC3";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.LC1))){
            dropdown="LC1";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.LC2))){
            dropdown="LC2";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.LC3))){
            dropdown="LC3";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.LC4))){
            dropdown="LC4";
        } else if(value.equalsIgnoreCase(getResources().getString(R.string.LC5))){
            dropdown="LC5";
        }
    }

    @Override

    public void onNothingSelected(AdapterView<?> parent) {

    }

    //////ต้องเพิ่มเงื่อนไข
    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(UploadActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(UploadActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.statistic){
                    Log.e("MainActivity", "statistic");
//                    itemStr="history";
//                    Intent intent1 = new Intent(ResultActivity.this, HistoryActivity.class);
//                    startActivity(intent1);
                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(UploadActivity.this, ChartActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
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
                            startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(UploadActivity.this, UploadActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(UploadActivity.this, LoginUiActivity.class));
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
    //***********************************************************************
    public class NearPlace {
        private String name;
        private String image;
        private String detail;
        public NearPlace(String name, String image, String detail) {
            this.name = name;
            this.image = image;
            this.detail = detail;
        }
        public void setName(String name){
            this.name = name;
        }
        public void setImage(String image){
            this.image = image;
        }
        public void setDetail(String detail){
            this.detail = detail;
        }
        public String getName() {
            return name;
        }
        public String getImage() {
            return image;
        }
        public String getDetail() {
            return detail;
        }
    }
    //***************************************************************************1111
}