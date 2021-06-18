package com.example.tufind.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tufind.HistoryActivity;
import com.example.tufind.MainActivity;
import com.example.tufind.R;
import com.example.tufind.UploadActivity;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginFragment extends Fragment {
    EditText email,password;
    Button loginBtn,gotoRegister;
    HintRequest h,t;
    float v=0;
    TextView name;

    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = (ViewGroup) inflater.inflate(R.layout.activity_login,container,false);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        email = root.findViewById(R.id.loginEmail);
        password = root.findViewById(R.id.loginPassword);
        loginBtn = root.findViewById(R.id.loginBtn);


        email.setTranslationX(800);
        password.setTranslationX(800);
        loginBtn.setTranslationX(800);


        email.setAlpha(v);
        password.setAlpha(v);
        loginBtn.setAlpha(v);


        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        loginBtn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();




        ////เมื่อกดล็อกอิน ล็อกอินsuccess
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(email);
                checkField(password);
                Log.d("TAG", "onClick: "+email.getText().toString());

                if(valid){
                    fAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
//                            Toast.makeText(LoginFragment.this,"Loggedin Successfully.",Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(),"Loggedin Successfully.",Toast.LENGTH_SHORT).show();
                            checkUserAccessLevel(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return root;

    }
    ////check edittext
    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
///checkเป็นีหำพฟรือแอดมิน
    private void checkUserAccessLevel(String uid) {
        DocumentReference df = fstore.collection("Users").document(uid);
        //data from the document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess: "+ documentSnapshot.getData());
                // access level
                if(documentSnapshot.getString("isAdmin")!= null){
                    //user is admin
                    startActivity(new Intent(getActivity(), UploadActivity.class));
                    getActivity().finish();
                }
                if(documentSnapshot.getString("isUser")!= null){
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();

                }
            }
        });
    }
   //ดึงข้อมูลไปยังหน้าที่เป็นสมาชิก
    @Override
    public void onStart() {
        super.onStart();
// email signin
        String emailinput = email.getText().toString().trim();
        if(emailinput.isEmpty()){

        }else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("isAdmin") != null) {
                        startActivity(new Intent(getActivity(), UploadActivity.class));
                        getActivity().finish();
                    }
                    if (documentSnapshot.getString("isUser") != null) {
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getActivity(), LoginUiActivity.class));
                    getActivity().finish();
                }
            });
        }
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_userogin, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
                if(item.getItemId() == R.id.history){
                    Log.e("MainActivity", "history");
//                    itemStr="history";
                    Intent intent1 = new Intent(getActivity(), HistoryActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });
        popupMenu.show();
    }

}
