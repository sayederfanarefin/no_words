package info.sayederfanarefin.location_sharing.model;

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

import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.utils.database;

/**
 * Created by erfanarefin on 30/08/2017.
 */

public class timeline_post_like extends RecyclerView.ViewHolder {
    private final TextView post_user_name;

    private final ImageView post_image;

    private FirebaseDatabase rootDb;

    DatabaseReference postRef;


    public timeline_post_like(View itemView) {
        super(itemView);
        post_user_name = (TextView) itemView.findViewById(R.id.post_like_user_name);

        post_image = (ImageView) itemView.findViewById(R.id.post_like_profile_image);
        rootDb = database.getDatabase();
    }

    public  void setUser_id(String user_id){

        rootDb.getReference().child("users").child(user_id).child("profilePicLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Glide.with(post_image.getContext())
                        .load(url)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(post_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void setPost_user_name(String name) {
        post_user_name.setText(name);
    }

}
