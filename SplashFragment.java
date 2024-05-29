package com.example.buzzchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashFragment extends Fragment {

    private View splashScreenView;

    private FirebaseAuth mAuth;     private DatabaseReference rootRef,userRef;
    private String currentUserId; private Boolean isCurrentUserHaveProfile = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        splashScreenView = inflater.inflate(R.layout.fragment_splash, container, false);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
            Log.d("Splash Screen", "Authenticated user ID: " + currentUserId);
        } else {
            Log.e("Splash Screen", "No authenticated user found");
        }
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            if (dataSnapshot.getKey().equals(currentUserId)){
                                isCurrentUserHaveProfile = true;
                                break;
                            }
                        }
                        if (currentUserId != null && !isCurrentUserHaveProfile) {
                            Log.d("Splash Screen", "run: current user exists and has no profile " + currentUserId);
                            MakeProfileFragment makeProfileScreen = new MakeProfileFragment();
                            ((MainActivity) requireActivity()).replaceFragment(makeProfileScreen,0);
                        } else if (currentUserId != null && isCurrentUserHaveProfile) {
                            Log.d("Splash Screen", "run: current user exists and has profile " + currentUserId);
//                            MainFragment mainFragment = new MainFragment();
//                            ((MainActivity) requireActivity()).replaceFragment(mainFragment);
                            Intent intent = new Intent(getActivity(),DashboardActivity.class);
                            startActivity(intent);
                            requireActivity().finish(); // Finish the current activity to avoid going back to splash screen
                        } else {
                            Log.d("Splash Screen", "run: current user does not exist and has no profile " + currentUserId);
                            SignInFragment signinScreen = new SignInFragment();
                            ((MainActivity) requireActivity()).replaceFragment(signinScreen,0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Splash Screen", "Database error: " + error.getMessage());
                        Log.e("Splash Screen", "Database error: " + error.getMessage());
                        SignInFragment signinScreen = new SignInFragment();
                        ((MainActivity) requireActivity()).replaceFragment(signinScreen,0);
                    }
                });
            }
        }, 2000);

        return splashScreenView;
    }
}