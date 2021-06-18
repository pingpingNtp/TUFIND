package com.example.tufind.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tufind.MainActivity;
import com.example.tufind.R;
import com.example.tufind.UploadActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    TextView txtpass,txtemail,txtname,txtcheckpass;
    Button logout;
    ImageButton home,hamBtn;
    private boolean valid = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);
        txtname = findViewById(R.id.nametext);
        txtemail = findViewById(R.id.emailtext);
        txtpass = findViewById(R.id.passtext);
        txtcheckpass = findViewById(R.id.checkpasstext);
        logout = findViewById(R.id.buttonrofile);


        home = findViewById(R.id.btphome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid){

                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful() && task != null) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.getString("isAdmin") != null) {
                                        //user is admin
                                        startActivity(new Intent(ProfileActivity.this, UploadActivity.class));
                                    }
                                    if (documentSnapshot.getString("isUser") != null) {
                                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));

                                    }

                                } else {
                                    startActivity(new Intent(ProfileActivity.this, LoginUiActivity.class));
                                }
                            }
                        });
   //                 }
                }

            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot =task.getResult();
                    if(documentSnapshot.exists() && documentSnapshot != null){
                        String username = documentSnapshot.getString("FullName");
                        String email = documentSnapshot.getString("UserEmail");
                        String password = documentSnapshot.getString("PassWord");
                        String checkpassword = documentSnapshot.getString("Confirmpassword");

                        txtname.setText("Username :  "+username);
                        txtemail.setText("E-mail :  "+email);
                        txtpass.setText("Password :  "+password);
                        txtcheckpass.setText("CheckPassword :  "+checkpassword);
                    }
                }else{
                }
            }
        });
    }
}
