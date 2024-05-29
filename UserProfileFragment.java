package com.example.buzzchat;

import android.app.Dialog;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    private View userProfileScreen;
    private ImageView user_profile_back_btn;     private AppCompatButton chat_req_dialog_accept_btn,chat_req_dialog_cancel_btn,remove_contact_dialog_remove_btn,remove_contact_dialog_cancel_btn;
    private CircleImageView user_profile_image,user_profile_msg_btn,user_profile_call_btn,user_profile_add_contact_btn;
    private TextView user_profile_add_contact_text,user_profile_msg_contact_text, user_profile_call_contact_text;
    private TextView user_profile_name,user_profile_about,user_profile_nickname,user_profile_DOB,user_profile_gender,
            user_profile_country,user_profile_contact_number,user_profile_address;

    private FirebaseAuth mAuth;     private DatabaseReference rootRef,requestRef,contactRef;      private String currentUserId;
    private String currentState = "new";        private String userId; // here **userId** refer to contact user id

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userProfileScreen = inflater.inflate(R.layout.fragment_user_profile, container, false);

        FieldsInitializations();
        fetchUserInformations();

        user_profile_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return userProfileScreen;
    }
    private void FieldsInitializations() {

        user_profile_back_btn = userProfileScreen.findViewById(R.id.user_profile_back_btn);
        user_profile_image = userProfileScreen.findViewById(R.id.user_profile_image);
        user_profile_msg_btn = userProfileScreen.findViewById(R.id.user_profile_msg_btn);
        user_profile_add_contact_btn = userProfileScreen.findViewById(R.id.user_profile_add_contact_btn);
        user_profile_call_btn = userProfileScreen.findViewById(R.id.user_profile_call_btn);
        user_profile_add_contact_text = userProfileScreen.findViewById(R.id.user_profile_add_contact_text);
        user_profile_msg_contact_text = userProfileScreen.findViewById(R.id.user_profile_msg_contact_text);
        user_profile_call_contact_text = userProfileScreen.findViewById(R.id.user_profile_call_contact_text);
        user_profile_name = userProfileScreen.findViewById(R.id.user_profile_name);
        user_profile_about = userProfileScreen.findViewById(R.id.user_profile_about);
        user_profile_nickname = userProfileScreen.findViewById(R.id.user_profile_nickname);
        user_profile_DOB = userProfileScreen.findViewById(R.id.user_profile_DOB);
        user_profile_gender = userProfileScreen.findViewById(R.id.user_profile_gender);
        user_profile_country = userProfileScreen.findViewById(R.id.user_profile_country);
        user_profile_contact_number = userProfileScreen.findViewById(R.id.user_profile_contact_number);
        user_profile_address = userProfileScreen.findViewById(R.id.user_profile_address);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            currentUserId = mAuth.getCurrentUser().getUid();
        }
        rootRef = FirebaseDatabase.getInstance().getReference();
        requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        contactRef = FirebaseDatabase.getInstance().getReference().child("Contact");
    }
    private void fetchUserInformations() {

        Bundle args = getArguments();

        if (args!=null){
            userId = args.getString("userId");

            ManageProfileView();

            rootRef.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
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
    private void ManageProfileView() {

        Log.d("MYProfileFrag", "ManageProfileView: ...");
        requestRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(userId)) {
                    String request_type = snapshot.child(userId).child("request_type").getValue().toString();

                    if (request_type.equals("sent")) {
                        currentState = "request_sent";

                        user_profile_add_contact_btn.setImageResource(R.drawable.remove_contact);
                        user_profile_add_contact_text.setText("Cancel");
                    } else if (request_type.equals("received")) {

                        currentState = "request_received";

                        user_profile_add_contact_btn.setImageResource(R.drawable.friend);
                        user_profile_add_contact_text.setText("You Got Friend Request");
                    }
                } else {
                    contactRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.hasChild(userId)) {
                                currentState = "friend";
                                user_profile_add_contact_btn.setImageResource(R.drawable.dost);
                                user_profile_add_contact_text.setText("Friend");
                                user_profile_msg_btn.setVisibility(View.VISIBLE);
                                user_profile_msg_contact_text.setVisibility(View.VISIBLE);
                                user_profile_call_btn.setVisibility(View.VISIBLE);
                                user_profile_call_contact_text.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        user_profile_add_contact_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState.equals("new")){
                    SendChatRequest();
                } else if (currentState.equals("request_sent")) {
                    CancelChatRequest();
                } else if (currentState.equals("request_received")) {

                    LayoutInflater inflater = LayoutInflater.from(requireContext());
                    View requestDialogBoxView = inflater.inflate(R.layout.chat_request_dialog_box,null);
                    Dialog requestDialogBox = new Dialog(requireContext());
                    requestDialogBox.setContentView(requestDialogBoxView);

                    requestDialogBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    requestDialogBox.setCancelable(true);
                    requestDialogBox.show();

                    chat_req_dialog_cancel_btn = requestDialogBoxView.findViewById(R.id.chat_req_dialog_cancel_btn);
                    chat_req_dialog_accept_btn = requestDialogBoxView.findViewById(R.id.chat_req_dialog_accept_btn);

                    chat_req_dialog_accept_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AcceptRequest();
                            requestDialogBox.dismiss();
                        }
                    });
                    chat_req_dialog_cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CancelRequest();
                            requestDialogBox.dismiss();
                        }
                    });
                } else if (currentState.equals("friend")) {

                    LayoutInflater inflater = LayoutInflater.from(requireContext());
                    View removeContactDialogBoxView = inflater.inflate(R.layout.remove_contact_dialog_box,null);
                    Dialog removeContactDialogBox = new Dialog(requireContext());
                    removeContactDialogBox.setContentView(removeContactDialogBoxView);

                    removeContactDialogBox.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    removeContactDialogBox.setCancelable(true);
                    removeContactDialogBox.show();

                    remove_contact_dialog_cancel_btn = removeContactDialogBoxView.findViewById(R.id.remove_contact_dialog_cancel_btn);
                    remove_contact_dialog_remove_btn = removeContactDialogBoxView.findViewById(R.id.remove_contact_dialog_remove_btn);

                    remove_contact_dialog_remove_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RemoveContact();
                            removeContactDialogBox.dismiss();
                        }
                    });
                    remove_contact_dialog_cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeContactDialogBox.dismiss();
                        }
                    });
                }
            }
        });

    }
    private void insertData( DataSnapshot snapshot) {
        if (snapshot.hasChild("userProfile") && snapshot.hasChild("name")){
            Log.d("MyProfileFrag", "onDataChange: userprofile and name");
            Glide.with(getContext()).load(snapshot.child("userProfile").getValue().toString()).placeholder(R.drawable.my_profile).into(user_profile_image);
            user_profile_name.setText(snapshot.child("name").getValue().toString());
            user_profile_about.setText(snapshot.child("about").getValue().toString());
            user_profile_nickname.setText(snapshot.child("nickname").getValue().toString());
            user_profile_DOB.setText(snapshot.child("dob").getValue().toString());
            user_profile_gender.setText(snapshot.child("gender").getValue().toString());
            user_profile_country.setText(snapshot.child("country").getValue().toString());
            user_profile_contact_number.setText(snapshot.child("contactNumber").getValue().toString());
            user_profile_address.setText(snapshot.child("address").getValue().toString());
        }
        else if (!snapshot.hasChild("userProfile") && snapshot.hasChild("name")){
            Log.d("MyProfileFrag", "onDataChange: not userprofile but name");
            user_profile_name.setText(snapshot.child("name").getValue().toString());
            user_profile_about.setText(snapshot.child("about").getValue().toString());
            user_profile_nickname.setText(snapshot.child("nickname").getValue().toString());
            user_profile_DOB.setText(snapshot.child("dob").getValue().toString());
            user_profile_gender.setText(snapshot.child("gender").getValue().toString());
            user_profile_country.setText(snapshot.child("country").getValue().toString());
            user_profile_contact_number.setText(snapshot.child("contactNumber").getValue().toString());
            user_profile_address.setText(snapshot.child("address").getValue().toString());
        }
        else if (snapshot.hasChild("userProfile") && !snapshot.hasChild("name")){
            Log.d("MyProfileFrag", "onDataChange: userprofile but not name");
            Glide.with(getContext()).load(snapshot.child("userProfile").getValue().toString()).placeholder(R.drawable.my_profile).into(user_profile_image);
        }
    }
    private void SendChatRequest() {

        requestRef.child(currentUserId).child(userId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    requestRef.child(userId).child(currentUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                currentState = "request_sent";
                                user_profile_add_contact_btn.setImageResource(R.drawable.remove_contact);
                                user_profile_add_contact_text.setText("Cancel");
//                                my_profile_cancel_btn.setVisibility(View.VISIBLE);
//                                my_profile_add_contact_btn.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }
    private void CancelChatRequest() {

        requestRef.child(currentUserId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    requestRef.child(userId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            currentState = "new";
                            user_profile_add_contact_btn.setImageResource(R.drawable.add_contact);
                            user_profile_add_contact_text.setText("Add");
                        }
                    });
                }
            }
        });
    }

    private void AcceptRequest() {

        contactRef.child(currentUserId).child(userId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    contactRef.child(userId).child(currentUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                requestRef.child(currentUserId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            requestRef.child(userId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    currentState = "friend";
                                                    user_profile_add_contact_btn.setImageResource(R.drawable.dost);
                                                    user_profile_add_contact_text.setText("Friend");
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
    private void CancelRequest() {
        CancelChatRequest();
    }
    private void RemoveContact() {

        contactRef.child(currentUserId).child(userId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    contactRef.child(userId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                currentState = "new";
                                user_profile_add_contact_btn.setImageResource(R.drawable.add_contact);
                                user_profile_add_contact_text.setText("Add");
                                user_profile_msg_btn.setVisibility(View.GONE);                 user_profile_msg_contact_text.setVisibility(View.GONE);
                                user_profile_call_btn.setVisibility(View.GONE);                user_profile_call_contact_text.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }
}