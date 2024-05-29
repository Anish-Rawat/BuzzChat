package com.example.buzzchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PersonalChatFragment extends Fragment implements MyContactItemAdapter.MyContactItemClickListener{

    private View PersonalChatScreenView;        private ProgressBar personal_chat_screen_progress_bar;
    private RecyclerView personal_chat_screen_recycler_view;        private MyContactItemAdapter myContactItemAdapter;
    private ArrayList<UserInfoDataModel> myChatWith = new ArrayList<>();
    private FirebaseAuth mAuth;     private DatabaseReference rootRef,userRef,chatRef;       private String currentUserId,userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PersonalChatScreenView = inflater.inflate(R.layout.fragment_personal_chat, container, false);

        FieldsInitializations();

        personal_chat_screen_progress_bar.setVisibility(View.VISIBLE);

        FetchMyChatWith();

        myContactItemAdapter = new MyContactItemAdapter(myChatWith,getContext(),this);
        personal_chat_screen_recycler_view.setAdapter(myContactItemAdapter);
        personal_chat_screen_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

        return PersonalChatScreenView;
    }
    private void FieldsInitializations() {

        personal_chat_screen_recycler_view = PersonalChatScreenView.findViewById(R.id.personal_chat_screen_recycler_view);
        personal_chat_screen_progress_bar = PersonalChatScreenView.findViewById(R.id.personal_chat_screen_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
        chatRef = rootRef.child("Chat");
    }
    private void FetchMyChatWith() {
        chatRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myChatWith.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                        userId = dataSnapshot.getKey();
                        FetchMyChatWithData(userId);
                }
                personal_chat_screen_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchMyChatWithData(String userId) {

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("userProfile")){
                    myChatWith.add(new UserInfoDataModel(snapshot.child("userProfile").getValue().toString(),
                            snapshot.child("userId").getValue().toString(),snapshot.child("name").getValue().toString(),
                            snapshot.child("nickname").getValue().toString(),snapshot.child("about").getValue().toString(),
                            snapshot.child("dob").getValue().toString(),snapshot.child("gender").getValue().toString(),
                            snapshot.child("country").getValue().toString(),snapshot.child("contactNumber").getValue().toString(),
                            snapshot.child("address").getValue().toString()));
                }
                else {
                    myChatWith.add(new UserInfoDataModel(snapshot.child("userId").getValue().toString(),snapshot.child("name").getValue().toString(),
                            snapshot.child("nickname").getValue().toString(),snapshot.child("about").getValue().toString(),
                            snapshot.child("dob").getValue().toString(),snapshot.child("gender").getValue().toString(),
                            snapshot.child("country").getValue().toString(),snapshot.child("contactNumber").getValue().toString(),
                            snapshot.child("address").getValue().toString()));
                }
                myContactItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void MyContactItemClick(Bundle bundle) {

        PersonalMessagingFragment personalMessageScreen = new PersonalMessagingFragment();
        personalMessageScreen.setArguments(bundle);

//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.main_fragment_frame_layout, personalMessageScreen)
//                .addToBackStack(null)
//                .commit();
        ((DashboardActivity) requireActivity()).replaceScreen(personalMessageScreen,1);
    }
}