package com.example.buzzchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.fragment.app.Fragment;

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
import com.google.firebase.database.DatabaseReference;

public class SignUpFragment extends Fragment {
    private View signupScreenView;  private AppCompatButton signup_screen_btn;      private TextView signup_screen_signin_btn;
    private EditText signup_screen_user_name,signup_screen_user_email,signup_screen_user_password,signup_screen_user_password_confirmation;
    private FirebaseAuth mAuth;     private DatabaseReference rootRef;      private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        signupScreenView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        FieldsInitializations();

        signup_screen_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewUser();
            }
        });

        signup_screen_signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment signinScreen = new SignInFragment();
                ((MainActivity) requireActivity()).replaceFragment(signinScreen,0);
            }
        });

        return signupScreenView;
    }
    private void FieldsInitializations() {

        mAuth = FirebaseAuth.getInstance();

        signup_screen_user_name = signupScreenView.findViewById(R.id.signup_screen_user_name);
        signup_screen_user_email = signupScreenView.findViewById(R.id.signup_screen_user_email);
        signup_screen_user_password = signupScreenView.findViewById(R.id.signup_screen_user_password);
        signup_screen_user_password_confirmation = signupScreenView.findViewById(R.id.signup_screen_user_password_confirmation);
        signup_screen_btn = signupScreenView.findViewById(R.id.signup_screen_btn);
        signup_screen_signin_btn = signupScreenView.findViewById(R.id.signup_screen_signin_btn);
    }
    private void CreateNewUser() {

        String userName = signup_screen_user_name.getText().toString();
        String userEmail = signup_screen_user_email.getText().toString();
        String userPassword = signup_screen_user_password.getText().toString();
        String userConfirmationPassword = signup_screen_user_password_confirmation.getText().toString();

        if(userName.isEmpty() && userEmail.isEmpty() && userPassword.isEmpty() && userConfirmationPassword.isEmpty()){
            Toast.makeText(requireContext(), "Please fill the fields...", Toast.LENGTH_SHORT).show();
        } else if (!userEmail.endsWith("@gmail.com")) {
            Toast.makeText(requireContext(), "Please enter correct email with @gmail.com...", Toast.LENGTH_SHORT).show();
        } else if (!userPassword.equals(userConfirmationPassword)) {
            Toast.makeText(requireContext(), "Password mismatched", Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(userEmail,userConfirmationPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

//                        SendUserToHomePage();
                        SendUserToMakeProfilePage();
                        Toast.makeText(requireContext(), "Account is created sucessfully", Toast.LENGTH_SHORT).show();

                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("BuzzChatPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn",true);
                        editor.apply();

//                        SendUserToEditPage();
                    }
                    else {
                        signup_screen_user_name.setText("");    signup_screen_user_email.setText("");
                        signup_screen_user_password.setText("");    signup_screen_user_password_confirmation.setText("");
                        Toast.makeText(requireContext(), "Account is not created sucessfully "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
//    private void SendUserToHomePage() {
//
//        MainFragment mainScreen = new MainFragment();
//        ((MainActivity) requireActivity()).replaceFragment(mainScreen);
//    }
    private void SendUserToMakeProfilePage() {

        MakeProfileFragment makeProfileScreen = new MakeProfileFragment();
//        editProfileFragment.PageRequest("SignUpPage");
        ((MainActivity) requireActivity()).replaceFragment(makeProfileScreen,0);
    }
}