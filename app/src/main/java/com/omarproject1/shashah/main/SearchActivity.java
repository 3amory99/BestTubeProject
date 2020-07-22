package com.omarproject1.shashah.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.VideoHolder;
import com.omarproject1.shashah.model.VideoItem;
import com.omarproject1.shashah.video.VerticalSpacingItemDecorator;
import com.omarproject1.shashah.video.VideoPlayerViewHolder;
import com.omarproject1.shashah.video.VideoRecyclerView;
import com.omarproject1.shashah.video.VideoRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference searchReference;
    private VideoRecyclerView searchRecyclerView;
    private VideoPlayerViewHolder myViewHolder;
    private VideoRecyclerViewAdapter adapter;
    private ArrayList<VideoItem> itemArrayList;
    SnapHelper snapHelper;

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

        searchRecyclerView = findViewById(R.id.recycler_view_search);
        searchRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(layoutManager);
        searchReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(10);
        searchRecyclerView.addItemDecoration(itemDecorator);
        myViewHolder = new VideoPlayerViewHolder(getWindow().getDecorView().getRootView());
        itemArrayList = new ArrayList<>();
        snapHelper = new PagerSnapHelper();
        playerIcon = findViewById(R.id.video_player_icon);
        playerTxt = findViewById(R.id.video_player_text);
    }


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
        Query query = searchReference.orderByChild("search").startAt(updatedResult).endAt(oldResult + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {
                    playerIcon.setVisibility(View.VISIBLE);
                    playerTxt.setVisibility(View.VISIBLE);
//                    Toast.makeText(SearchActivity.this, ""+getResources().getString(R.string.no_videos_founded), Toast.LENGTH_SHORT).show();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoItem videoItem = dataSnapshot.getValue(VideoItem.class);
                    itemArrayList.add(videoItem);
                }
                playerIcon.setVisibility(View.GONE);
                playerTxt.setVisibility(View.GONE);
                adapter = new VideoRecyclerViewAdapter(SearchActivity.this, itemArrayList);
                searchRecyclerView.setMediaItems(itemArrayList);
                adapter.notifyDataSetChanged();
                searchRecyclerView.setAdapter(adapter);
                snapHelper.attachToRecyclerView(searchRecyclerView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SearchResult",""+error.getMessage());
//                Toast.makeText(SearchActivity.this, ""+getResources().getString(R.string.no_videos_founded), Toast.LENGTH_SHORT).show();
                playerIcon.setVisibility(View.VISIBLE);
                playerTxt.setVisibility(View.VISIBLE);
            }
        });

    }
}