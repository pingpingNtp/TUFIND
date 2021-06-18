package com.example.tufind.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tufind.ChartActivity;
import com.example.tufind.HistoryActivity;
import com.example.tufind.Login.LoginUiActivity;
import com.example.tufind.Login.ProfileActivity;
import com.example.tufind.MainActivity;
import com.example.tufind.R;
import com.example.tufind.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class CardsDetailsActivity extends AppCompatActivity {

    private ImageView img,back;
    private TextView placeName,detail,distance;

    private String name,imageuri,detailname,distancename;


    private ImageButton hamBtn,profile;
    private   String text="";
    private  boolean valid = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards_detail);
        getSupportActionBar().hide();
        text = getIntent().getStringExtra("response");

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
                                    if (documentSnapshot.getString("isTeacher") != null) {
                                        //user is admin
                                        showMenu1(v);
                                    }
                                    if (documentSnapshot.getString("isStudent") != null) {
                                        showMenu(v);

                                    }
                                }
                            }
                        });
                    }
                }
            }
        });


        Intent i = getIntent();

        name = i.getStringExtra("name");
        imageuri = i.getStringExtra("image");
        detailname = i.getStringExtra("detail");
        distancename = i.getStringExtra("distance");


        placeName = findViewById(R.id.placeName);
        img = findViewById(R.id.big_image);
        detail = findViewById(R.id.Alldetail);
        distance = findViewById(R.id.textdistance);


        placeName.setText(name);
        detail.setText(detailname);
        distance.setText(distancename);


        Picasso.get().load(imageuri).into(img);





        ///login
        profile= (ImageButton)findViewById(R.id.btprofile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///////***************////////////////
                Log.d("iscomming", String.valueOf(valid));

                if(valid){
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                    }else{
                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&&task!=null){
                                    DocumentSnapshot documentSnapshot =task.getResult();
                                    if(documentSnapshot.exists() && documentSnapshot != null){
                                        startActivity(new Intent(CardsDetailsActivity.this, ProfileActivity.class));
                                    }else{
                                        startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                                    }
                                }else{
                                    startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                                }
                            }
                        });
                    }
                }
            }
        });

    }


    //**menu***///
    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(CardsDetailsActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_userogin, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(CardsDetailsActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.history){
                    Log.e("MainActivity", "history");
                    if(valid){
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(CardsDetailsActivity.this, HistoryActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
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
        PopupMenu popupMenu = new PopupMenu(CardsDetailsActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(CardsDetailsActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.statistic){
                    Log.e("MainActivity", "statistic");

                    if(valid){
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(CardsDetailsActivity.this, ChartActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                                    }
                                }
                            });
                        }
                    }

                }
                if(item.getItemId() == R.id.upload){

                    if(valid){
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(CardsDetailsActivity.this, UploadActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(CardsDetailsActivity.this, LoginUiActivity.class));
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
