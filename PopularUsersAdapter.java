package com.example.buzzchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class PopularUsersAdapter extends RecyclerView.Adapter<PopularUsersAdapter.PopularUsersViewHolder> {

    private ArrayList<UserInfoDataModel> popularUsersList = new ArrayList<>();
    private Context context;

    public PopularUsersAdapter(ArrayList<UserInfoDataModel> popularUsersList, Context context) {
        this.popularUsersList = popularUsersList;
        this.context = context;
    }

    @NonNull
    @Override
    public PopularUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_users_item_card,parent,false);
        PopularUsersViewHolder holder = new PopularUsersViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PopularUsersViewHolder holder, int position) {

        UserInfoDataModel obj = popularUsersList.get(position);

        holder.popular_user_name.setText(obj.getName());
        Glide.with(context).load(obj.getUserProfile()).placeholder(R.drawable.my_profile).into(holder.popular_user_profile);
    }

    @Override
    public int getItemCount() {
        return popularUsersList.size();
    }

    class PopularUsersViewHolder extends RecyclerView.ViewHolder{

        private ImageView popular_user_profile;     private TextView popular_user_name;
        public PopularUsersViewHolder(@NonNull View itemView) {
            super(itemView);

            popular_user_name = itemView.findViewById(R.id.popular_user_name);
            popular_user_profile = itemView.findViewById(R.id.popular_user_profile);
        }
    }
}
