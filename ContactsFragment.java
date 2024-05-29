package com.example.buzzchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class ContactsFragment extends Fragment implements MyContactItemAdapter.MyContactItemClickListener{

    private View ContactScreenView;     private RecyclerView contact_screen_recycler_view;      private MyContactItemAdapter myContactItemAdapter;
    private ProgressBar contact_screen_progress_bar;

    private FirebaseAuth mAuth;     private DatabaseReference rootRef,contactRef,userRef;       private String currentUserId,userId;
    private ArrayList<UserInfoDataModel> myContactList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ContactScreenView = inflater.inflate(R.layout.fragment_contacts, container, false);

        FieldsInitializations();

        contact_screen_progress_bar.setVisibility(View.VISIBLE);

        FetchMyContact();

        myContactItemAdapter = new MyContactItemAdapter(myContactList,requireContext(),this);
        contact_screen_recycler_view.setAdapter(myContactItemAdapter);
        contact_screen_recycler_view.setLayoutManager(new LinearLayoutManager(requireContext()));

        return ContactScreenView;
    }
    private void FieldsInitializations() {

        contact_screen_recycler_view = ContactScreenView.findViewById(R.id.contact_screen_recycler_view);
        contact_screen_progress_bar = ContactScreenView.findViewById(R.id.contact_screen_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        contactRef = rootRef.child("Contact");
        userRef = rootRef.child("Users");
    }
    private void FetchMyContact() {

        contactRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myContactList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if (dataSnapshot.child("Contact").getValue(String.class).equals("Saved")){

                        userId = dataSnapshot.getKey();
                        FetchMyContactData(userId);
                    }
                }
                contact_screen_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void FetchMyContactData(String userId) {

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("userProfile")){
                    myContactList.add(new UserInfoDataModel(snapshot.child("userProfile").getValue().toString(),
                            snapshot.child("userId").getValue().toString(),snapshot.child("name").getValue().toString(),
                            snapshot.child("nickname").getValue().toString(),snapshot.child("about").getValue().toString(),
                            snapshot.child("dob").getValue().toString(),snapshot.child("gender").getValue().toString(),
                            snapshot.child("country").getValue().toString(),snapshot.child("contactNumber").getValue().toString(),
                            snapshot.child("address").getValue().toString()));
                }
                else {
                    myContactList.add(new UserInfoDataModel(snapshot.child("userId").getValue().toString(),snapshot.child("name").getValue().toString(),
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
