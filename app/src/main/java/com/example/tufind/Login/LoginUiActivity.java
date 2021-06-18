package com.example.tufind.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.tufind.MainActivity;
import com.example.tufind.R;
import com.example.tufind.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginUiActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    FloatingActionButton google;
    float v=0;

    private ImageButton home;
    private  boolean valid = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);
        getSupportActionBar().hide();


        viewPager = (ViewPager) findViewById(R.id.view_page);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        home = findViewById(R.id.btphome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid){

                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&&task!=null){
                                    DocumentSnapshot documentSnapshot =task.getResult();
                                    if(documentSnapshot.getString("isAdmin")!= null){
                                        //user is admin
                                        startActivity(new Intent(LoginUiActivity.this, UploadActivity.class));
                                    }
                                    if(documentSnapshot.getString("isUser")!= null){
                                        startActivity(new Intent(LoginUiActivity.this, MainActivity.class));

                                    }
                                }else{
                                    startActivity(new Intent(LoginUiActivity.this, LoginUiActivity.class));
                                }
                            }
                        });
                    }

            }
        });


        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#499982"));
        tabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));

        tabLayout.setTranslationY(300);

        tabLayout.setAlpha(v);

        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(800).start();
    }

    private void setupViewPager(ViewPager viewPager) {
        LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new RegisterFragment(), "Signup");
        viewPager.setAdapter(adapter);
    }
}
