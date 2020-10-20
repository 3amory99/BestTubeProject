package com.androidproject.besttube.vip.users;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference searchReference;


    private Toolbar toolbar;
    private CircleImageView currentUserImage;
    private RecyclerView allUsersRecycler;
    UsersAdapter usersAdapter;
    List<User> usersList;
    private ImageView noUserIcon;
    private TextView noUserTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        toolbar = findViewById(R.id.all_users_toolBar);
        setSupportActionBar(toolbar);
        initiation();
        getAllUsers();
        databaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {

                } else {
                    String image = "" + snapshot.child("image").getValue();
                    try {
                        Picasso.get().load(image).placeholder(R.drawable.user).into(currentUserImage);
                    } catch (Exception e) {
                        currentUserImage.setImageResource(R.drawable.user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void initiation() {
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        searchReference = FirebaseDatabase.getInstance().getReference("users");
        allUsersRecycler = findViewById(R.id.users_recycler);
        allUsersRecycler.setHasFixedSize(true);
        allUsersRecycler.setLayoutManager(new LinearLayoutManager(this));
        usersList = new ArrayList<>();
        currentUserImage = findViewById(R.id.user_activity_image_profile);
        usersAdapter = new UsersAdapter(UsersActivity.this, usersList);
        noUserIcon = findViewById(R.id.user_not_found_icon);
        noUserTxt = findViewById(R.id.user_not_found_txt);


    }

    private void getAllUsers() {
        currentUser = mAuth.getCurrentUser();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(UsersActivity.this, "No users", Toast.LENGTH_SHORT).show();
                    noUserIcon.setVisibility(View.VISIBLE);
                    noUserTxt.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User users = snapshot.getValue(User.class);
                        if (users != null) {
                            if (!users.getUserUid().equals(currentUser.getUid())) {
                                usersList.add(users);
                            }
                        }
                    }

                    usersAdapter = new UsersAdapter(getApplicationContext(), usersList);
                    usersAdapter.notifyDataSetChanged();
                    allUsersRecycler.setAdapter(usersAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("SearchResult", "" + databaseError.getMessage());
//                Toast.makeText(SearchActivity.this, ""+getResources().getString(R.string.no_videos_founded), Toast.LENGTH_SHORT).show();
                noUserIcon.setVisibility(View.VISIBLE);
                noUserTxt.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_search, menu);
        MenuItem item = menu.findItem(R.id.users_search_item);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchInsideActivity(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchInsideActivity(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void searchInsideActivity(String searchText) {
        String updatedResult = searchText.toLowerCase().trim();
        Query query = searchReference.orderByChild("search").startAt(updatedResult).endAt(updatedResult + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                if (!snapshot.hasChildren()) {
                    noUserIcon.setVisibility(View.VISIBLE);
                    noUserTxt.setVisibility(View.VISIBLE);
                } else {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            if (!user.getUserUid().equals(currentUser.getUid())) {
                                usersList.add(user);
                            }
                        }
                    }
                }
                noUserIcon.setVisibility(View.GONE);
                noUserTxt.setVisibility(View.GONE);
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchResult", "" + error.getMessage());
//                Toast.makeText(SearchActivity.this, ""+getResources().getString(R.string.no_videos_founded), Toast.LENGTH_SHORT).show();
                noUserIcon.setVisibility(View.VISIBLE);
                noUserTxt.setVisibility(View.VISIBLE);
            }
        });


    }
}