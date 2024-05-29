package com.example.buzzchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SignInFragment extends Fragment {
    private View signinScreenView;  private EditText signin_screen_user_email,signin_screen_user_password;  private TextView signin_screen_forgot_password,signin_screen_signup_btn;
    private AppCompatButton signin_screen_btn;
    private FirebaseAuth mAuth;
//    private DatabaseReference rootRef;      private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        signinScreenView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        FieldsInitializations();

        signin_screen_signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpFragment signupScreen = new SignUpFragment();
                ((MainActivity) requireActivity()).replaceFragment(signupScreen,1);
            }
        });

        signin_screen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        return signinScreenView;
    }
    private void FieldsInitializations() {
        signin_screen_user_email = signinScreenView.findViewById(R.id.signin_screen_user_email);
        signin_screen_user_password = signinScreenView.findViewById(R.id.signin_screen_user_password);
        signin_screen_forgot_password = signinScreenView.findViewById(R.id.signin_screen_forgot_password);
        signin_screen_btn = signinScreenView.findViewById(R.id.signin_screen_btn);
        signin_screen_signup_btn = signinScreenView.findViewById(R.id.signin_screen_signup_btn);

        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser()!=null){
//            currentUserId = mAuth.getCurrentUser().getUid();
//        }
//        rootRef = FirebaseDatabase.getInstance().getReference();
    }
    private void AllowUserToLogin() {

        String userEmail = signin_screen_user_email.getText().toString();
        String userPassword = signin_screen_user_password.getText().toString();

        if (userEmail.isEmpty()) {
            signin_screen_user_email.setError("Email is required");
            signin_screen_user_email.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            signin_screen_user_email.setError("Please enter a valid email");
            signin_screen_user_email.requestFocus();
            return;
        }

        if (userPassword.isEmpty()) {
            signin_screen_user_password.setError("Password is required");
            signin_screen_user_password.requestFocus();
            return;
        }

        if (userPassword.length() < 6) {
            signin_screen_user_password.setError("Password should be at least 6 characters long");
            signin_screen_user_password.requestFocus();
            return;

        }
        mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    SendUserToHomePage();
                    Toast.makeText(requireContext(), "Welcome Back", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("BuzzChatPref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn",true);
                    editor.apply();
                }
                else {
                    signin_screen_user_email.setText("");   signin_screen_user_password.setText("");
                    Toast.makeText(requireContext(), "Invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void SendUserToHomePage() {

//        MainFragment mainScreen = new MainFragment();
//        ((MainActivity) requireActivity()).replaceFragment(mainScreen);
        Intent intent = new Intent(getActivity(),DashboardActivity.class);
        startActivity(intent);
    }
}