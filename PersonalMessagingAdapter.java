package com.example.buzzchat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalMessagingAdapter extends RecyclerView.Adapter<PersonalMessagingAdapter.PersonalMessagingViewHolder> {

    private ArrayList<MessageInfoDataModel> messageList = new ArrayList<>();
    private Context context;
    private FirebaseAuth mAuth;     private String currentUserId,currentUserProfile,userProfile;        // here userProfile is profileimage of receiver
    private DatabaseReference rootRef,userRef;
    public PersonalMessagingAdapter(ArrayList<MessageInfoDataModel> messageList, Context context, String currentUserProfile, String userProfile) {
        this.messageList = messageList;
        this.context = context;
        this.currentUserProfile = currentUserProfile;
        this.userProfile = userProfile;
    }

    @NonNull
    @Override
    public PersonalMessagingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_messaging_item_card,parent,false);
        PersonalMessagingViewHolder holder = new PersonalMessagingViewHolder(itemView);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalMessagingViewHolder holder, int position) {

        MessageInfoDataModel obj = messageList.get(position);
        currentUserId = mAuth.getCurrentUser().getUid();

        if (obj.getType().equals("text")){

            if (currentUserId.equals(obj.getSenderId())){

                Log.d("PersonalMsgAdapter", "onBindViewHolder: "+currentUserProfile);
                holder.message_receiver_layout.setVisibility(View.GONE);
                holder.message_receiver_profile.setVisibility(View.GONE);
                holder.message_sender_layout.setVisibility(View.VISIBLE);
                holder.message_sender_profile.setVisibility(View.VISIBLE);

                holder.message_sender_msg_text.setText(obj.getMessage());
                holder.message_sender_msg_time.setText(obj.getTime());
                Glide.with(context).load(currentUserProfile).placeholder(R.drawable.my_profile).into(holder.message_sender_profile);
            }
            else {

                Log.d("Personal Msg Adapter", "onBindViewHolder: "+obj.getMessage());
                holder.message_receiver_layout.setVisibility(View.VISIBLE);
                holder.message_receiver_profile.setVisibility(View.VISIBLE);
                holder.message_sender_layout.setVisibility(View.GONE);
                holder.message_sender_profile.setVisibility(View.GONE);

                holder.message_receiver_msg_text.setText(obj.getMessage());
                holder.message_receiver_msg_time.setText(obj.getTime());
                Glide.with(context).load(userProfile).placeholder(R.drawable.my_profile).into(holder.message_receiver_profile);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class PersonalMessagingViewHolder extends RecyclerView.ViewHolder{

        private View message_receiver_layout,message_sender_layout;
        private CircleImageView message_receiver_profile,message_sender_profile;
        private TextView message_receiver_msg_text,message_sender_msg_text,message_receiver_msg_time,message_sender_msg_time;
        public PersonalMessagingViewHolder(@NonNull View itemView) {
            super(itemView);

            message_receiver_layout = itemView.findViewById(R.id.message_receiver_layout);
            message_sender_layout = itemView.findViewById(R.id.message_sender_layout);

            message_receiver_profile = itemView.findViewById(R.id.message_receiver_profile);
            message_sender_profile = itemView.findViewById(R.id.message_sender_profile);
            message_receiver_msg_text = itemView.findViewById(R.id.message_receiver_msg_text);
            message_sender_msg_text = itemView.findViewById(R.id.message_sender_msg_text);
            message_receiver_msg_time = itemView.findViewById(R.id.message_receiver_msg_time);
            message_sender_msg_time = itemView.findViewById(R.id.message_sender_msg_time);
        }
    }
}
