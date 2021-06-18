package com.example.tufind.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tufind.MainActivity;
import com.example.tufind.R;
import com.example.tufind.UploadActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    TextInputLayout inputname,inputemail,inputpassword,inputcheckpassword;
    TextInputEditText fullName,email,password,checkpassword;
    CheckBox admin,user;
    Button registerBtn,goToLogin;
    float v=0;

    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    //checkbox
    CheckBox isTeacherBox,isStudentBox;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.activity_register,container,false);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        fullName = root.findViewById(R.id.registerName);
        email = root.findViewById(R.id.registerEmail);
        password = root.findViewById(R.id.registerPassword);
        checkpassword = root.findViewById(R.id.registerCheckpassword);
        admin = root.findViewById(R.id.isTeacher);
        user = root.findViewById(R.id.isStudent);
        registerBtn = root.findViewById(R.id.registerBtn);
//        goToLogin = root.findViewById(R.id.gotoLogin);
        isTeacherBox = root.findViewById(R.id.isTeacher);
        isStudentBox = root.findViewById(R.id.isStudent);


        inputemail = root.findViewById(R.id.input_email);
        inputname = root.findViewById(R.id.input_username);
        inputpassword = root.findViewById(R.id.input_password);
        inputcheckpassword = root.findViewById(R.id.input_checkpassword);


        //checkbox logic
        isStudentBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    isTeacherBox.setChecked(false);
                }
            }
        });

        isTeacherBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked()){
                    isStudentBox.setChecked(false);
                }
            }
        });

        //เมื่อกดสมัครสมาชิก
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmInput(fullName);
                confirmInput(email);
                confirmInput(password);
                confirmInput(checkpassword);

                // checkbox
                if(!(isTeacherBox.isChecked() || isStudentBox.isChecked())){
                    Toast.makeText(getActivity(),"Select The Account Type",Toast.LENGTH_SHORT).show();
                    return;
                }
                String ppassword = password.getText().toString();
                String CCheckPassword = checkpassword.getText().toString();
                if(!ppassword.equals(CCheckPassword)) {
                    inputpassword.setError("not match");
                    //Set Message that password is not equal
                }else
                    if(valid){
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(getActivity(),"Account Created",Toast.LENGTH_SHORT).show();
                            DocumentReference df = fstore.collection("Users").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("FullName",fullName.getText().toString());
                            userInfo.put("UserEmail",email.getText().toString());
                            userInfo.put("PassWord",password.getText().toString());
                            userInfo.put("Confirmpassword",checkpassword.getText().toString());
// specify if user is admin
                            //checkbox
                            if (isTeacherBox.isChecked()){
                                userInfo.put("isAdmin","1");
                                Log.e("Log", "Error on saving");
                            }
                            if (isStudentBox.isChecked()){
                                userInfo.put("isUser","1");
                            }

                            df.set(userInfo);
                            if(isTeacherBox.isChecked()){
                                startActivity(new Intent(getActivity(), UploadActivity.class));
                                getActivity().finish();
                                Log.e("Log", "Error on saving");
                            }
                            if(isStudentBox.isChecked())
                            {
                                startActivity(new Intent(getActivity(), MainActivity.class));
                                getActivity().finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (email.getText().toString().trim().matches(email.getText().toString())) {
                                inputemail.setError("have email");
//                                Toast.makeText(getActivity(),"Invalid email address",Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getActivity(),"Failed to Create Account",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        return root;
    }
////เช็คกล่ิงข้อความ
//    public boolean checkField(TextInputEditText textField){
//        if(textField.getText().toString().isEmpty()){
//            textField.setError("Error");
//            valid = false;
//        }else {
//            valid = true;
//        }
//
//        return valid;
//    }
    private boolean validateEmail(){
    String emailinput = inputemail.getEditText().getText().toString().trim();
       if(emailinput.isEmpty()){
           inputemail.setError("Field can't be empty");
           return false;
       } else if (!email.getText().toString().matches(emailPattern)) {
           inputemail.setError("Invalid email address");
           return false;
       }else {
           inputemail.setError(null);
           inputemail.setErrorEnabled(false);
            return true;
       }
    }
    private boolean validateName(){
        String nameinput = inputname.getEditText().getText().toString().trim();
        if(nameinput.isEmpty()){
            inputname.setError("Field can't be empty");
            return false;
        } else if(nameinput.length()>15) {
            inputname.setError("Username too long");
            return false;
        }else{
            inputname.setError(null);
            return true;
        }
    }
    private boolean validatepassword(){
        String passwordinput = inputpassword.getEditText().getText().toString().trim();
        if(passwordinput.isEmpty()){
            inputpassword.setError("Field can't be empty");
            return false;
        } else{
                inputpassword.setError(null);
                return true;
            }
        }
    private boolean validatecheckpassword(){
        String checkpasswordinput = inputcheckpassword.getEditText().getText().toString().trim();
        if(checkpasswordinput.isEmpty()){
            inputcheckpassword.setError("Field can't be empty");
            return false;
        } else{
            inputcheckpassword.setError(null);
            return true;
        }
    }
    public void confirmInput(View v){
        if(!validateEmail() | !validateName() |!validatepassword() | !validatecheckpassword()){
            return;
        }
        String input = "Email "+inputemail.getEditText().getText().toString();
        input += "\n";
        input += "Username" + inputname.getEditText().getText().toString();
        input += "\n";
        input += "Password" + inputpassword.getEditText().getText().toString();
        input += "\n";
        input += "CheckPassword" + inputcheckpassword.getEditText().getText().toString();

    }
}
