package com.example.buzzchat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileFragment extends Fragment {

    private View myProfileScreen;
    private ImageView my_profile_back_btn,my_profile_edit_btn;      private AppCompatButton my_profile_signout_btn;     private AppCompatButton chat_req_dialog_accept_btn,chat_req_dialog_cancel_btn,remove_contact_dialog_remove_btn,remove_contact_dialog_cancel_btn;
    private CircleImageView my_profile_image,my_profile_msg_btn,my_profile_call_btn,my_profile_add_contact_btn;
    private TextView my_profile_add_contact_text,my_profile_msg_contact_text, my_profile_call_contact_text;
    private TextView my_profile_name,my_profile_about,my_profile_nickname,my_profile_DOB,my_profile_gender,
            my_profile_country,my_profile_contact_number,my_profile_address;

    private FirebaseAuth mAuth;     private DatabaseReference rootRef,requestRef,contactRef;      private String currentUserId;
    private String userId; // here **userId** refer to contact user id

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myProfileScreen = inflater.inflate(R.layout.fragment_my_profile, container, false);

        FieldsInitializations();

        fetchUserInformations();

        my_profile_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToEditProfilePage();
            }
        });

        my_profile_signout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSignout();
            }
        });

        return myProfileScreen;
    }
    private void FieldsInitializations() {

        my_profile_back_btn = myProfileScreen.findViewById(R.id.my_profile_back_btn);
        my_profile_edit_btn = myProfileScreen.findViewById(R.id.my_profile_edit_btn);

        my_profile_signout_btn = myProfileScreen.findViewById(R.id.my_profile_signout_btn);

        my_profile_image = myProfileScreen.findViewById(R.id.my_profile_image);
        my_profile_msg_btn = myProfileScreen.findViewById(R.id.my_profile_msg_btn);
        my_profile_call_btn = myProfileScreen.findViewById(R.id.my_profile_call_btn);
        my_profile_add_contact_btn = myProfileScreen.findViewById(R.id.my_profile_add_contact_btn);


        my_profile_add_contact_text = myProfileScreen.findViewById(R.id.my_profile_add_contact_text);
        my_profile_msg_contact_text = myProfileScreen.findViewById(R.id.my_profile_msg_contact_text);
        my_profile_call_contact_text = myProfileScreen.findViewById(R.id.my_profile_call_contact_text);


        my_profile_name = myProfileScreen.findViewById(R.id.my_profile_name);
        my_profile_about = myProfileScreen.findViewById(R.id.my_profile_about);
        my_profile_nickname = myProfileScreen.findViewById(R.id.my_profile_nickname);
        my_profile_DOB = myProfileScreen.findViewById(R.id.my_profile_DOB);
        my_profile_gender = myProfileScreen.findViewById(R.id.my_profile_gender);
        my_profile_country = myProfileScreen.findViewById(R.id.my_profile_country);
        my_profile_contact_number = myProfileScreen.findViewById(R.id.my_profile_contact_number);
        my_profile_address = myProfileScreen.findViewById(R.id.my_profile_address);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            currentUserId = mAuth.getCurrentUser().getUid();
        }
        Log.d("MyProfile", "FieldsInitializations: "+mAuth.getCurrentUser());
        rootRef = FirebaseDatabase.getInstance().getReference();
        requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contact");
    }
    private void fetchUserInformations() {
        my_profile_add_contact_btn.setVisibility(View.GONE);
        my_profile_add_contact_text.setVisibility(View.GONE);

        if (currentUserId!=null){
            rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){
                        insertData(snapshot);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
    private void insertData( DataSnapshot snapshot) {
        if (snapshot.hasChild("userProfile") && snapshot.hasChild("name")){
            Log.d("MyProfileFrag", "onDataChange: userprofile and name");
            if (isAdded() && getActivity()!=null){
                Glide.with(getContext()).load(snapshot.child("userProfile").getValue().toString()).placeholder(R.drawable.my_profile).into(my_profile_image);
            }
            my_profile_name.setText(snapshot.child("name").getValue().toString());
            my_profile_about.setText(snapshot.child("about").getValue().toString());
            my_profile_nickname.setText(snapshot.child("nickname").getValue().toString());
            my_profile_DOB.setText(snapshot.child("dob").getValue().toString());
            my_profile_gender.setText(snapshot.child("gender").getValue().toString());
            my_profile_country.setText(snapshot.child("country").getValue().toString());
            my_profile_contact_number.setText(snapshot.child("contactNumber").getValue().toString());
            my_profile_address.setText(snapshot.child("address").getValue().toString());
        }
        else if (!snapshot.hasChild("userProfile") && snapshot.hasChild("name")){
            Log.d("MyProfileFrag", "onDataChange: not userprofile but name");
            my_profile_name.setText(snapshot.child("name").getValue().toString());
            my_profile_about.setText(snapshot.child("about").getValue().toString());
            my_profile_nickname.setText(snapshot.child("nickname").getValue().toString());
            my_profile_DOB.setText(snapshot.child("dob").getValue().toString());
            my_profile_gender.setText(snapshot.child("gender").getValue().toString());
            my_profile_country.setText(snapshot.child("country").getValue().toString());
            my_profile_contact_number.setText(snapshot.child("contactNumber").getValue().toString());
            my_profile_address.setText(snapshot.child("address").getValue().toString());
        }
        else if (snapshot.hasChild("userProfile") && !snapshot.hasChild("name")){
            Log.d("MyProfileFrag", "onDataChange: userprofile but not name");
            Glide.with(getContext()).load(snapshot.child("userProfile").getValue().toString()).placeholder(R.drawable.my_profile).into(my_profile_image);
        }
    }

    private void SendUserToEditProfilePage() {
        EditProfileFragment editProfileScreen = new EditProfileFragment();

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_frame_layout,editProfileScreen).addToBackStack(null).commit();
    }
    private void UserSignout() {
        mAuth.signOut();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("BuzzChatPref" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn",false);
        editor.apply();

        UpdateUserOnlineStatus();

//        SignInFragment signInFragment = new SignInFragment();
//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.main_activity_frame_layout, signInFragment)
//                .commit();
        // Start MainActivity with sign out action
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("action", "signout");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
    private void UpdateUserOnlineStatus() {

        String currentDate , currentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        UserInfoDataModel userInfoDataModel = new UserInfoDataModel();
        userInfoDataModel.setOnlineDate(currentDate);
        userInfoDataModel.setOnlineTiming(currentTime);
        userInfoDataModel.setState("offline");

        rootRef.child("Users").child(currentUserId).child("userState").setValue(userInfoDataModel);
    }
}