package com.omarproject1.shashah.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.VideoHolder;
import com.omarproject1.shashah.model.VideoItem;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private FirebaseRecyclerOptions<VideoItem> recyclerOptions;
    private FirebaseRecyclerAdapter<VideoItem, VideoHolder> recyclerAdapter;
    ImageView playerIcon;
    TextView playerTxt;
    String oldResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initiation();
    }

    private void initiation() {

        recyclerView = findViewById(R.id.search_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        playerIcon = findViewById(R.id.video_player_icon);
        playerTxt = findViewById(R.id.video_player_text);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (oldResult == null) {
//            searchProgressBar.setVisibility(View.INVISIBLE);
//        } else {
//            oldResult = getIntent().getStringExtra("SearchResult");
//            Query query = reference.orderByChild("search").startAt(oldResult).endAt(oldResult + "\uf8ff");
//            recyclerOptions = new FirebaseRecyclerOptions.Builder<VideoItem>()
//                    .setQuery(query, VideoItem.class)
//                    .build();
//            recyclerAdapter = new FirebaseRecyclerAdapter<VideoItem, VideoHolder>(recyclerOptions) {
//                @Override
//                protected void onBindViewHolder(@NonNull VideoHolder holder, int position, @NonNull VideoItem model) {
//                    holder.setExoplayer(getApplication(), model.getVideoTitle(), model.getVideoDescription(), model.getHashTags(), model.getVideoUrl());
//                    if (holder.description.length() < 100) {
//                        holder.showMore.setVisibility(View.GONE);
//                    }
//                    holder.showMore.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (holder.description.length() > 100) {
//                                holder.showMore.setText("Show less");
//                                TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
//                            } else {
//                            }
//
//                        }
//                    });
//                }
//
//                @NonNull
//                @Override
//                public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_main_activity, parent, false);
//                    return new VideoHolder(view);
//                }
//            };
//            recyclerAdapter.startListening();
//            recyclerView.setAdapter(recyclerAdapter);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.search_item_main);
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
        Query query = reference.orderByChild("search").startAt(updatedResult).endAt(oldResult + "\uf8ff");
        recyclerOptions = new FirebaseRecyclerOptions.Builder<VideoItem>()
                .setQuery(query, VideoItem.class)
                .build();
        recyclerAdapter = new FirebaseRecyclerAdapter<VideoItem, VideoHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull VideoHolder holder, int position, @NonNull VideoItem model) {
                holder.setExoplayer(getApplication(), model.getVideoTitle(), model.getVideoDescription(), model.getHashTags(), model.getVideoUrl());
                playerIcon.setVisibility(View.GONE);
                playerTxt.setVisibility(View.GONE);
                if (holder.description.length() < 100) {
                    holder.showMore.setVisibility(View.GONE);
                }
                holder.showMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (holder.description.length() > 100) {
                            holder.showMore.setText("Show less");
                            TransitionManager.beginDelayedTransition(holder.cardView, new AutoTransition());
                        } else {
                        }

                    }
                });
            }

            @NonNull
            @Override
            public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item_main_activity, parent, false);
                return new VideoHolder(view);
            }
        };
        recyclerAdapter.startListening();
        recyclerView.setAdapter(recyclerAdapter);


    }
}