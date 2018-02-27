package info.sayederfanarefin.location_sharing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
//import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import info.sayederfanarefin.location_sharing.Timleline_showComments;
import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.Timleline_showlikes;
import info.sayederfanarefin.location_sharing.model.Like;
import info.sayederfanarefin.location_sharing.model.comments;
import info.sayederfanarefin.location_sharing.model.post;
import info.sayederfanarefin.location_sharing.model.users;
import info.sayederfanarefin.location_sharing.utils.database;

public class ViewImageActivity extends AppCompatActivity {
    //private Toolbar mToolBar;

    private ImageView imageView;
    private FirebaseDatabase rootDb;


    DatabaseReference postRef;
    DatabaseReference commentRef;
    DatabaseReference likeRef;
    DatabaseReference loggedInUserRef;

    ValueEventListener postRefValueEventListener;
    ValueEventListener likeRefValueEventListener;
    ValueEventListener commentRefValueEventListener;
//    ValueEventListener loggedInUserRefValueEventListener;

    private TextView likers_display;
    private TextView comment_display;
    private TextView post_image_status;
    private EditText comment_box;

//    private SeekBar musicprogress;
    private ProgressBar progressBar_view_image;

    private LinearLayout bottom_stuff;

    private ImageButton comment_button;

    String image_url = "";
    String image_audio_url = "";
    String user_id ;//= b.getString("user_id","");
    String post_id ;//= b.getString("post_id","");

    private boolean isPostType = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Bundle b = getIntent().getExtras();


        likers_display = (TextView) findViewById(R.id.likers_display);
        comment_display = (TextView) findViewById(R.id.comment_display);
        post_image_status = (TextView) findViewById(R.id.post_image_status);

        imageView = (ImageView) findViewById(R.id.main_image);
//        play = (ImageButton) findViewById(R.id.play);
//        pause = (ImageButton) findViewById(R.id.pause);
//        play.setVisibility(View.GONE);
//        pause.setVisibility(View.GONE);

        progressBar_view_image = (ProgressBar) findViewById(R.id.progressBar_view_image);
//        musicprogress = (SeekBar) findViewById(R.id.musicprogress);
        progressBar_view_image.setVisibility(View.VISIBLE);

        bottom_stuff = (LinearLayout) findViewById(R.id.bottom_stuff);

        String image_type = b.getString("image_type",""); //url or post


        if(image_type.equals("post")){
             user_id = b.getString("user_id","");
             post_id = b.getString("post_id","");

            isPostType = true;

            rootDb = database.getDatabase();
            postRef = rootDb.getReference()
                    .child("users")
                    .child(user_id)
                    .child("posts")
                    .child(post_id);

            commentRef = postRef.child("comment");
            likeRef = postRef.child("like");

             postRefValueEventListener = postRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    post p = dataSnapshot.getValue(post.class);
                    if(p.getPost_type().equals("image")){
                        image_url = p.getImage_link();
                      //  image_audio_url = p.getImage_audio_link();
                        setImage(image_url);
                        //setAudio(image_audio_url);
                    }
                    post_image_status.setText(p.getStatus_text());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            final List<String> likes = new ArrayList<>();
            likeRefValueEventListener = likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String tobedisplayedforlikers = "";
                    Boolean selfFlag = false;

                    for (DataSnapshot likeSnapshot: dataSnapshot.getChildren()) {
                        Like like = likeSnapshot.getValue(Like.class);
                        likes.add(like.getUserName());
                        if(like.getUserId().equals(user_id)){
                            tobedisplayedforlikers = tobedisplayedforlikers+ "You";
                            selfFlag = true;
                        }
                    }

                    if(!selfFlag){
                        if(likes.size() > 0){
                            tobedisplayedforlikers = tobedisplayedforlikers + likes.get(0);
                        }
                    }
                    if(likes.size() > 1){
                        tobedisplayedforlikers = tobedisplayedforlikers + " and " + String.valueOf(likes.size()-1) + " others";
                    }
                    likers_display.setText(tobedisplayedforlikers);
                    likers_display.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ViewImageActivity.this, Timleline_showlikes.class);
                            intent.putExtra("user_id", user_id);
                            intent.putExtra("post_id", post_id);
                            startActivity(intent);
                        }
                    });

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            //comment display
           commentRefValueEventListener = commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int count_comment = 0;
                    for (DataSnapshot likeSnapshot: dataSnapshot.getChildren()) {
                        count_comment++;
                    }
                    if(count_comment >1){
                        comment_display.setText(String.valueOf(count_comment) + " comments");
                    }else if (count_comment == 1){
                        comment_display.setText(String.valueOf(count_comment) + " comment");
                    }else{
                        comment_display.setText("No comments");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            bottom_stuff.setVisibility(View.VISIBLE);
        }else if (image_type.equals("url")){

            isPostType = false;
            image_url = b.getString("url", "");
            setImage(image_url);
            bottom_stuff.setVisibility(View.GONE);
        }
        comment_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewImageActivity.this, Timleline_showComments.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("post_id", post_id);
                startActivity(intent);
            }
        });
        comment_box = (EditText) findViewById(R.id.post_comment_editBox);
        comment_button = (ImageButton) findViewById(R.id.post_comment_button);
        comment_button.setOnClickListener(new View.OnClickListener() {
            boolean flag_one_post = true;
            @Override
            public void onClick(View view) {
                if(comment_box.getText().toString().equals("")){
                        Toast.makeText(ViewImageActivity.this, "Please enter a comment!", Toast.LENGTH_SHORT).show();

                }else{

                    final String loggedInUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    loggedInUserRef = rootDb.getReference()
                            .child("users")
                            .child(loggedInUserId)
                            ;

                   // loggedInUserRefValueEventListener =

                            loggedInUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                                                Toast.makeText(ViewImageActivity.this, "Comment successful!", Toast.LENGTH_SHORT).show();
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


                        Intent intent = new Intent(ViewImageActivity.this, POstIMageComment.class);
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

//    private ImageButton play;
//    private ImageButton pause;

    private void setImage(String url){
        Glide.with(imageView.getContext())
                .load(url)
                .fitCenter()
                //.centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar_view_image.setVisibility(View.GONE);
//                        play.setVisibility(View.GONE);
//                        pause.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar_view_image.setVisibility(View.GONE);
//                        play.setVisibility(View.VISIBLE);
//                        pause.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(imageView);
    }

//    private void setAudio(String aud_url){
//
//        if(aud_url == null || aud_url.equals("")){
//            Log.v("--xx--", "gne called");
//            try {
//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        play.setVisibility(View.GONE);
//                        pause.setVisibility(View.GONE);
//                        musicprogress.setProgress(0);
//
//                    }
//                });
//                //Thread.sleep(300);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }else{
//            AudioWife.getInstance()
//                    .init(ViewImageActivity.this, Uri.parse(aud_url))
//                    .setPlayView(play)
//                    .setPauseView(pause)
//                    .setSeekBar(musicprogress);
//            play.setVisibility(View.GONE);
//            pause.setVisibility(View.VISIBLE);
//            AudioWife.getInstance().play();
//            play.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AudioWife.getInstance().play();
//                    play.setVisibility(View.GONE);
//                    pause.setVisibility(View.VISIBLE);
//                }
//            });
//            pause.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AudioWife.getInstance().pause();
//                    pause.setVisibility(View.GONE);
//                    play.setVisibility(View.VISIBLE);
//                }
//            });
//        }
//
//    }
//

    @Override
    public void onPause() {
        super.onPause();
     //   AudioWife.getInstance().pause();
        //AudioWife.getInstance().release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(isPostType){
            postRef.removeEventListener(postRefValueEventListener);
            commentRef.removeEventListener(commentRefValueEventListener);
            likeRef.removeEventListener(likeRefValueEventListener);
        }

        // loggedInUserRef.removeEventListener(loggedInUserRefValueEventListener);

//        AudioWife.getInstance().pause();
//        AudioWife.getInstance().release();
    }
}
