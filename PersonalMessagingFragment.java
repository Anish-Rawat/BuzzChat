package com.example.buzzchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalMessagingFragment extends Fragment {

    private View PersonalMessageScreenView ;        private ProgressBar personal_msg_screen_progress_bar;
    private ImageView personal_msg_screen_back_btn,personal_msg_screen_send_btn;     private CircleImageView personal_msg_screen_contact_profile;
    private TextView personal_msg_screen_contact_name,personal_msg_screen_contact_state;      private EditText personal_msg_screen_edit_text;
    private RecyclerView personal_msg_screen_recycler_view;
    private ArrayList<MessageInfoDataModel> messageList = new ArrayList<>();        private PersonalMessagingAdapter personalMessagingAdapter;
    private FirebaseAuth mAuth;     private DatabaseReference rootRef,chatRef,userRef;      private String currentUserId,userId,currentDate,currentTime,currentUserProfile,userProfile;

    @Override
    public void onResume() {
        super.onResume();

        View bottomNavigation = requireActivity().findViewById(R.id.main_fragment_bottom_navigation);
        bottomNavigation.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        View bottomNavigation = requireActivity().findViewById(R.id.main_fragment_bottom_navigation);
        bottomNavigation.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PersonalMessageScreenView = inflater.inflate(R.layout.fragment_personal_messaging, container, false);

        FieldsInitializations();
        personal_msg_screen_progress_bar.setVisibility(View.VISIBLE);

        FetchMsgReceiverInfo();

        personalMessagingAdapter = new PersonalMessagingAdapter(messageList,getContext(),currentUserProfile,userProfile);
        personal_msg_screen_recycler_view.setAdapter(personalMessagingAdapter);
        personal_msg_screen_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        personalMessagingAdapter.notifyDataSetChanged();

        FetchCurrentUserProfile();
        FetchPreviousMessages();

        personal_msg_screen_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = personal_msg_screen_edit_text.getText().toString();

                if (message.isEmpty()){
                    Toast.makeText(requireContext(), "Enter message first...", Toast.LENGTH_SHORT).show();
                }
                else {
                    SendMessage(message);
                    personal_msg_screen_edit_text.setText("");
                }
            }
        });

        personal_msg_screen_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return PersonalMessageScreenView;
    }
    private void FieldsInitializations() {

        personal_msg_screen_back_btn = PersonalMessageScreenView.findViewById(R.id.personal_msg_screen_back_btn);
        personal_msg_screen_edit_text = PersonalMessageScreenView.findViewById(R.id.personal_msg_screen_edit_text);
        personal_msg_screen_send_btn = PersonalMessageScreenView.findViewById(R.id.personal_msg_screen_send_btn);
        personal_msg_screen_contact_profile = PersonalMessageScreenView.findViewById(R.id.personal_msg_screen_contact_profile);
        personal_msg_screen_contact_name = PersonalMessageScreenView.findViewById(R.id.personal_msg_screen_contact_name);
        personal_msg_screen_contact_state = PersonalMessageScreenView.findViewById(R.id.personal_msg_screen_contact_state);
        personal_msg_screen_recycler_view = PersonalMessageScreenView.findViewById(R.id.personal_msg_screen_recycler_view);
        personal_msg_screen_progress_bar = PersonalMessageScreenView.findViewById(R.id.personal_msg_screen_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        chatRef = rootRef.child("Chat");
        userRef = rootRef.child("Users");
    }
    private void FetchMsgReceiverInfo() {
        Bundle args = getArguments();

        if (args != null){
            String identify = args.getString("Identify");
            if (identify.equals("MyContactToPersonalMessage")){
                userId = args.getString("userId");
                personal_msg_screen_contact_name.setText(args.getString("name"));
                userProfile = args.getString("userProfile");
                userRef.child(userId).child("userState").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("state")){
                            if (snapshot.child("state").getValue().toString().equals("offline")){
                                personal_msg_screen_contact_state.setText(snapshot.child("state").getValue().toString()+ ", "+snapshot.child("onlineTiming").getValue().toString()+", "+snapshot.child("onlineDate").getValue().toString());
                            }
                            else {
                                personal_msg_screen_contact_state.setText(snapshot.child("state").getValue().toString());
                            }
                        }
                        else {
                            Log.d("Personal Msg frag", "Online State not updated");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Log.d("Personal Msg frag", "message := "+error.getMessage().toString());
                    }
                });
                if (isAdded() && getContext()!=null){
                    Glide.with(requireContext()).load(args.getString("userProfile")).placeholder(R.drawable.my_profile).into(personal_msg_screen_contact_profile);
                }
            }
        }
    }

    private void FetchCurrentUserProfile() {

        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.hasChild("userProfile")){

                        currentUserProfile = snapshot.child("userProfile").getValue().toString();
                    }
                    else {
                        Log.d("PersonalMessagingFragment", "current user having no profile image...."+snapshot.getKey());
                    }
                }
                else {
                    Log.d("PersonalMessagingFragment", "snapshot is not exist "+snapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("PersonalMessagingFragment", "RequestCancelled");
            }
        });
    }
    private void SendMessage(String message) {

        String Text = "text";
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        String uniqueMessageKey = chatRef.child(currentUserId).child(userId).push().getKey();

        MessageInfoDataModel messageInfo = new MessageInfoDataModel(currentUserId,userId,message,currentDate,currentTime,"text");

        chatRef.child(currentUserId).child(userId).child(uniqueMessageKey).setValue(messageInfo);
        chatRef.child(userId).child(currentUserId).child(uniqueMessageKey).setValue(messageInfo);

        messageList.add(messageInfo);
        personalMessagingAdapter.notifyDataSetChanged();
    }

    private void FetchPreviousMessages() {

        chatRef.child(currentUserId).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messageList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    messageList.add(new MessageInfoDataModel(dataSnapshot.child("senderId").getValue().toString(),dataSnapshot.child("receiverId").getValue().toString(),
                            dataSnapshot.child("message").getValue().toString(),dataSnapshot.child("date").getValue().toString(),
                            dataSnapshot.child("time").getValue().toString(),dataSnapshot.child("type").getValue().toString()));
                }
                personal_msg_screen_progress_bar.setVisibility(View.GONE);
                personalMessagingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}