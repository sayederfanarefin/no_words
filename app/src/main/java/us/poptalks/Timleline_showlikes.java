package us.poptalks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import us.poptalks.model.Like;
import us.poptalks.model.post;
import us.poptalks.model.timeline_post;
import us.poptalks.model.timeline_post_like;
import us.poptalks.utils.database;

public class Timleline_showlikes extends AppCompatActivity {
    private Toolbar mToolBar;

    private FirebaseDatabase rootDb;
    private RecyclerView likes_view_recycler;

    FirebaseRecyclerAdapter likes_view_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_show_likes);


        mToolBar = (Toolbar) findViewById(R.id.toolbar_settings);
        mToolBar.setTitle("People who reacted");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Timleline_showlikes.this, MainActivity.class);
                intent.putExtra("frag", "timeline");
                startActivity(intent);
            }
        });

        Bundle b = getIntent().getExtras();
        String user_id = b.getString("user_id","");
        String post_id = b.getString("post_id","");

        likes_view_recycler = (RecyclerView) findViewById(R.id.view_likes);
        likes_view_recycler.setLayoutManager(new LinearLayoutManager(this));


        rootDb = database.getDatabase();
        likesRef = rootDb.getReference()
                .child("users")
                .child(user_id)
                .child("posts")
                .child(post_id)
                .child("like");


        likes_view_adapter = new FirebaseRecyclerAdapter<Like, timeline_post_like>(
                Like.class,
                R.layout.timeline_post_like_layout,
                timeline_post_like.class,
                likesRef) {
            @Override
            public void populateViewHolder(timeline_post_like holder, Like like, int position) {
                holder.setPost_user_name(like.getUserName());
                holder.setUser_id(like.getUserId());
            }
        };
        likes_view_recycler.setAdapter(likes_view_adapter);
    }

    DatabaseReference likesRef;
}
