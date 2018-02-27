package info.sayederfanarefin.location_sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.model.comments;
import info.sayederfanarefin.location_sharing.model.timeline_post_comment;
import info.sayederfanarefin.location_sharing.model.users;
import info.sayederfanarefin.location_sharing.ui.POstIMageComment;
import info.sayederfanarefin.location_sharing.utils.CommonStaticMethods;
import info.sayederfanarefin.location_sharing.utils.database;

public class Timleline_showComments extends AppCompatActivity {
    private Toolbar mToolBar;

    DatabaseReference postRef;

    private EditText comment_box;

    private ImageButton comment_button;


    private FirebaseDatabase rootDb;
    private RecyclerView likes_view_recycler;

    FirebaseRecyclerAdapter likes_view_adapter;

    String user_id;
    String  post_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_show_comments);


        mToolBar = (Toolbar) findViewById(R.id.toolbar_settings);
        mToolBar.setTitle("Comments");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();

                Intent intent = new Intent(Timleline_showComments.this, MainActivity.class);
                intent.putExtra("frag", "timeline");
                startActivity(intent);
            }
        });

        Bundle b = getIntent().getExtras();
         user_id = b.getString("user_id","");
         post_id = b.getString("post_id","");

        likes_view_recycler = (RecyclerView) findViewById(R.id.view_comments);
        likes_view_recycler.setLayoutManager(new LinearLayoutManager(this));


        rootDb = database.getDatabase();
        likesRef = rootDb.getReference()
                .child("users")
                .child(user_id)
                .child("posts")
                .child(post_id)
                .child("comment");


        likes_view_adapter = new FirebaseRecyclerAdapter<comments, timeline_post_comment>(
                comments.class,
                R.layout.timeline_post_comment_layout,
                timeline_post_comment.class,
                likesRef) {
            @Override
            public void populateViewHolder(timeline_post_comment holder, comments c, int position) {

                holder.setComment_user_name(c.getComment_user_name());
                holder.setUser_id(c.getComment_user_id());

                holder.setComment_time(CommonStaticMethods.convertFromFirebaseStringDate(c.getComment_time(), "at"));

                if(c.getComment_type().equals("image")){
                    holder.setComment_image(c.getComment_image_url());
                }else{
                    holder.setComment(c.getComment());
                }
            }
        };



        likes_view_recycler.setAdapter(likes_view_adapter);




        comment_box = (EditText) findViewById(R.id.post_comment_editBox);
        comment_button = (ImageButton) findViewById(R.id.post_comment_button);

        comment_button.setOnClickListener(new View.OnClickListener() {

            boolean flag_one_post = true;
            @Override
            public void onClick(View view) {
                if(comment_box.getText().toString().equals("")){

                    Toast.makeText(Timleline_showComments.this, "Please enter a comment!", Toast.LENGTH_SHORT).show();


                }else{

                    postRef = rootDb.getReference()
                            .child("users")
                            .child(user_id)
                            .child("posts")
                            .child(post_id);


                    final String loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    rootDb.getReference()
                            .child("users")
                            .child(loggedInUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    users current_user = dataSnapshot.getValue(users.class);

                                    if(flag_one_post){
                                        String comment = comment_box.getText().toString();
                                        DatabaseReference pushRefComment = postRef.child("comment").push();
                                        pushRefComment.getKey();
                                        comments c = new comments();
                                        c.setComment_user_id(loggedInUserId);
                                        c.setComment_type("text");
                                        c.setComment_user_name(current_user.getUsername());
                                        c.setComment(comment);
                                        c.setComment_time(dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss"));

                                        pushRefComment.setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Timleline_showComments.this, "Comment successful!", Toast.LENGTH_SHORT).show();
                                                comment_box.setText("");
                                                flag_one_post = true;

                                            }
                                        });

                                        flag_one_post = false;
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                }
            }
        });

        comment_box.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (comment_box.getRight() - comment_box.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {


                        Intent intent = new Intent(Timleline_showComments.this, POstIMageComment.class);
                        intent.putExtra("post_id", post_id);
                        intent.putExtra("user_id", user_id);
                        startActivity(intent);


                        return true;
                    }
                }
                return false;
            }
        });


    }
    private String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }
    DatabaseReference likesRef;
}
