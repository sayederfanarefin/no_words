package us.poptalks.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import us.poptalks.R;
import us.poptalks.utils.database;

/**
 * Created by erfanarefin on 30/08/2017.
 */

public class timeline_post_comment extends RecyclerView.ViewHolder {
    private final TextView comment_user_name;
    private final TextView comment;
    private final TextView comment_time;

    private final ImageView comment_user_image;
    private final ImageView comment_image;

    private FirebaseDatabase rootDb;

    DatabaseReference postRef;


    public timeline_post_comment(View itemView) {
        super(itemView);
        comment_user_name = (TextView) itemView.findViewById(R.id.post_comment_user_name);
        comment = (TextView) itemView.findViewById(R.id.comment_text);
        comment_time = (TextView) itemView.findViewById(R.id.post_comment_time);

        comment_user_image = (ImageView) itemView.findViewById(R.id.post_comment_profile_image);
        comment_image = (ImageView) itemView.findViewById(R.id.comment_image);
        rootDb = database.getDatabase();
    }

    public  void setUser_id(String user_id){

        rootDb.getReference().child("users").child(user_id).child("profilePicLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Glide.with(comment_user_image.getContext())
                        .load(url)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(comment_user_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void setComment_user_name(String name) {
        comment_user_name.setText(name);
    }
    public void setComment(String name) {
        comment.setText(name);
        comment.setVisibility(View.VISIBLE);
        comment_image.setVisibility(View.GONE);
    }
    public void setComment_time(String name) {
        comment_time.setText(name);

    }

    public void setComment_image(String url){
        comment.setVisibility(View.GONE);
        comment_image.setVisibility(View.VISIBLE);
        Glide.with(comment_image.getContext())
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(comment_image);

    }

}
