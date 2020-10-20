package com.androidproject.besttube.vip.users;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.model.User;
import com.androidproject.besttube.vip.profile.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    Context context;
    List<User> usersList;

    public UsersAdapter(Context context, List<User> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_raw_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String userImage=usersList.get(position).getImage();
        String userName=usersList.get(position).getName();
        String userStatus=usersList.get(position).getAbout();

        //Set Data
        holder.userName.setText(userName);
        holder.userStatus.setText(userStatus);
        try {
            Picasso.get().load(userImage).placeholder(R.drawable.user).into(holder.userImage);
        }catch (Exception e){

        }
        final String userId=usersList.get(position).getUserUid();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,""+userName,Toast.LENGTH_SHORT).show();
                Intent goProfileIntent=new Intent(context, ProfileActivity.class);
                goProfileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                goProfileIntent.putExtra("userUidIntent",userId);

//                Pair[] pairs = new Pair[3];
//                pairs[0] =new Pair<View, String>(holder.userImage,"imageTransition");
//                pairs[1] =new Pair<View, String>(holder.userName,"nameTransition");
//                pairs[2] =new Pair<View, String>(holder.userStatus,"statusTransition");
//
//
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) holder.itemView.getContext(), pairs);
//                context.startActivity(goProfileIntent,options.toBundle());

                context.startActivity(goProfileIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView userName, userStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_image);
            userName = itemView.findViewById(R.id.user_name);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }
}
