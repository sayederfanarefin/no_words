package us.poptalks.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.wonderkiln.camerakit.CameraView;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import us.poptalks.R;
import us.poptalks.Timleline_showComments;
import us.poptalks.Timleline_showlikes;
import us.poptalks.ui.NewStatusActivity;
import us.poptalks.ui.POstIMageComment;
import us.poptalks.ui.ReactActivity;
import us.poptalks.ui.ViewImageActivity;
import us.poptalks.utils.CommonStaticMethods;
import us.poptalks.utils.database;
import us.poptalks.utils.doRequest;

/**
 * Created by erfanarefin on 30/08/2017.
 */

public class timeline_post extends RecyclerView.ViewHolder {
    private final TextView post_user_name;
    private final TextView post_time;
    private final TextView post_status;
    private final TextView preview_link_666;
    private final TextView preview_link_666_desc;
    private final TextView likers_display;
    private final TextView comment_display;
    private final ImageView post_image;
    private final ImageView post_profile_image;
    private final ImageView post_profile_image_empty;
    private final ImageView image_link_preview;
    private final EditText comment_box;
    private final LinearLayout link_display_layout, comment_box_hidden;
    private final RelativeLayout comment_button_layout_post_timeline, react_button_layout_post_timeline;
    private final LinearLayout link_display_clickable_layout;
    private final ProgressBar linkUploadProgress;
    private final ProgressBar profilePictureProgress;
    private final ImageButton like_button;

    CameraView cameraView;
    ImageButton capture;
    TextView status_text_emotion;

    private final RelativeLayout image_preview_black_bg;//, camera_view_snap_for_emotion;

private final RecyclerView view_timeline_self;

    private final ProgressBar progressBar_post_image;



    public String getPost_type() {
        return post_type;
    }

    public void setPost_type(String post_type) {
        this.post_type = post_type;

        if (post_type.equals("status")){
            link_display_layout.setVisibility(View.GONE);
            post_image.setVisibility(View.GONE);
            image_preview_black_bg.setVisibility(View.GONE);

        }
    }

    private String post_type;


    private final ImageButton comment_button;
    TextCrawler textCrawler;
    private FirebaseDatabase rootDb;

    DatabaseReference postRef;

    String user_id_visiting_user;
    String user_name;
    String post_id;
    String post_user_id;


    public timeline_post(View itemView) {
        super(itemView);
        post_user_name = (TextView) itemView.findViewById(R.id.post_user_name);
        post_time = (TextView) itemView.findViewById(R.id.post_time);
        post_status = (TextView) itemView.findViewById(R.id.post_status);
        preview_link_666 = (TextView) itemView.findViewById(R.id.preview_link_666);
        preview_link_666_desc = (TextView) itemView.findViewById(R.id.preview_link_666_desc);
        likers_display = (TextView) itemView.findViewById(R.id.likers_display);
        comment_display = (TextView) itemView.findViewById(R.id.comment_display);
        post_image = (ImageView) itemView.findViewById(R.id.post_image);
        image_link_preview = (ImageView) itemView.findViewById(R.id.image_link_preview);
        post_profile_image = (ImageView) itemView.findViewById(R.id.post_profile_image);


        post_profile_image_empty = (ImageView) itemView.findViewById(R.id.post_profile_image_empty);


        comment_box = (EditText) itemView.findViewById(R.id.post_comment_editBox);
        link_display_layout = itemView.findViewById(R.id.link_display_layout);
        linkUploadProgress = itemView.findViewById(R.id.linkUploadProgress);
        profilePictureProgress = itemView.findViewById(R.id.progressBarTimelineProfilePic);


        link_display_clickable_layout = itemView.findViewById(R.id.link_display_clickable_layout);
        //header_recycler_view = itemView.findViewById(R.id.header_recycler_view);
        progressBar_post_image = itemView.findViewById(R.id.progressBar_post_image);

        like_button = itemView.findViewById(R.id.like_button);
        comment_button = itemView.findViewById(R.id.post_comment_button);

        image_preview_black_bg = itemView.findViewById(R.id.image_preview_black_bg);
        image_preview_black_bg.setVisibility(View.GONE);

        comment_box_hidden = itemView.findViewById(R.id.comment_box_hidden);


        comment_button_layout_post_timeline = itemView.findViewById(R.id.comment_button_layout_post_timeline);
        react_button_layout_post_timeline = itemView.findViewById(R.id.react_button_layout_post_timeline);


        if (client == null) {
            client = new EmotionServiceRestClient("b855f4690116467e85162db631c6799a");
        }

        view_timeline_self = itemView.findViewById(R.id.view_timeline_self);

        comment_button_layout_post_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment_box_hidden.getVisibility() == View.VISIBLE){
                    comment_box_hidden.setVisibility(View.GONE);
                }else{
                    comment_box_hidden.setVisibility(View.VISIBLE);
                }


            }
        });


        rootDb = database.getDatabase();
        textCrawler = new TextCrawler();

    }
    private EmotionServiceClient client;
    private String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
    }


    public void setHeader(final Context context, final String user_pro_pic_url){



    }
    public void setUserId(String uid){
        user_id_visiting_user = uid;
    }
    public void setUserName(String name){
        user_name = name;
    }

    public void setPostIdUserId(String user_s_post, final String post_id, final Context context){

        this.post_id = post_id;
        post_user_id = user_s_post;

        postRef = rootDb.getReference().child("users").child(user_s_post).child("posts").child(post_id);

        //like count display
        final List<String> likes = new ArrayList<>();
        final List<String> likes_uid = new ArrayList<>();

        rootDb.getReference()
                .child("users")
                .child(user_s_post)
                .child("posts")
                .child(post_id)
                .child("like")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String tobedisplayedforlikers = "";
                Boolean selfFlag = false;

                for (DataSnapshot likeSnapshot: dataSnapshot.getChildren()) {
                    Like like = likeSnapshot.getValue(Like.class);
                    likes.add(like.getUserName());
                    likes_uid.add(like.getUserId());
                    if(like.getUserId().equals(user_id_visiting_user)){
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
                        Intent intent = new Intent(context, Timleline_showlikes.class);
                        intent.putExtra("user_id", post_user_id);
                        intent.putExtra("post_id", post_id);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_dp, R.anim.stay);
                        ((Activity) context).finish();
                    }
                });


                //if someone likes a post
                like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if(!likes_uid.contains(user_id_visiting_user)){
//                            DatabaseReference pushRefLike = postRef.child("like").push();
//                            pushRefLike.getKey();
//                            Like like = new Like();
//                            like.setUserId(user_id_visiting_user);
//                            like.setUserName(user_name);
//                            like.setLikeType("smile");
//                            pushRefLike.setValue(like).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                }
//                            });
//                        }else{
//                            // TODO on Like button press, remove like if like already exists
//                            //remove like
//                        }
                    }
                });

                comment_box.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                       // final int DRAWABLE_LEFT = 0;
                       // final int DRAWABLE_TOP = 1;
                        final int DRAWABLE_RIGHT = 2;
                      //  final int DRAWABLE_BOTTOM = 3;

                        if(event.getAction() == MotionEvent.ACTION_UP) {
                            if(event.getRawX() >= (comment_box.getRight() - comment_box.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {


                                Intent intent = new Intent(context, POstIMageComment.class);
                                intent.putExtra("post_id", post_id);
                                intent.putExtra("user_id", post_user_id);
                                context.startActivity(intent);

                                ((Activity)context).overridePendingTransition(R.anim.slide_dp, R.anim.stay);
                                ((Activity) context).finish();

                                return true;
                            }
                        }
                        return false;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //comments display count
        rootDb.getReference()
                .child("users")
                .child(user_s_post)
                .child("posts")
                .child(post_id)
                .child("comment")
                .addValueEventListener(new ValueEventListener() {
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

        comment_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Timleline_showComments.class);
                intent.putExtra("user_id", post_user_id);
                intent.putExtra("post_id", post_id);
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_dp, R.anim.stay);
                ((Activity) context).finish();

            }
        });

        react_button_layout_post_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ReactActivity.class);
                intent.putExtra("user_id", post_user_id);
                intent.putExtra("post_id", post_id);
                context.startActivity(intent);
               // Animation slide_up_animation = AnimationUtils.loadAnimation(context, R.anim.slide_dp);
                ((Activity)context).overridePendingTransition(R.anim.slide_dp, R.anim.stay);
                ((Activity) context).finish();

            }
        });

        comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment_box.getText().toString().equals("")){


                    if (context instanceof Activity) {

                        Toast.makeText(((Activity) context), "Please enter a comment!", Toast.LENGTH_SHORT);
                    }



                }else{
                    String comment = comment_box.getText().toString();
                    DatabaseReference pushRefComment = postRef.child("comment").push();
                    pushRefComment.getKey();
                    comments c = new comments();
                    c.setComment_user_id(user_id_visiting_user);
                    c.setComment_type("text");
                    c.setComment_user_name(user_name);
                    c.setComment(comment);
                    c.setComment_time(dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss"));

                    pushRefComment.setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText((Activity)context, "Comment successful!", Toast.LENGTH_SHORT);
                        }
                    });

                }
            }
        });


    }

    public void setPost_link(final String url, final Context context){
        link_display_layout.setVisibility(View.VISIBLE);
        post_image.setVisibility(View.GONE);
        image_preview_black_bg.setVisibility(View.GONE);

        LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
            @Override
            public void onPre() {
                // Any work that needs to be done before generating the preview. Usually inflate
                // your custom preview layout here.

                Random rand2 = new Random();
                int xx = rand2.nextInt(50) +5;

                linkUploadProgress.setVisibility(View.VISIBLE);
                linkUploadProgress.setProgress(xx);

            }

            @Override
            public void onPos(SourceContent sourceContent, boolean b) {

                preview_link_666_desc.setText(sourceContent.getDescription());
                preview_link_666.setText(sourceContent.getTitle());
                linkUploadProgress.setProgress(75);
                try {
                    Glide.with(image_link_preview.getContext())
                            .load(sourceContent.getImages().get(0))
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(image_link_preview);
                }catch (Exception e){

                }

                linkUploadProgress.setProgress(100);
                linkUploadProgress.setVisibility(View.GONE);

            }
        };
        textCrawler.makePreview( linkPreviewCallback, url);
        link_display_clickable_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });
    }


    public  void setPost_profile_image(String url){
        Glide.with(post_profile_image.getContext())
                .load(url)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        profilePictureProgress.setVisibility(View.GONE);
                        post_profile_image_empty.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        profilePictureProgress.setVisibility(View.GONE);
                        post_profile_image_empty.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(post_profile_image);
    }

    public void setPost_user_name(String name) {
        post_user_name.setText(name);
    }

    public void setPost_time(String message) {
        post_time.setText(CommonStaticMethods.convertFromFirebaseStringDate(message, "at"));//+ " at " + timestamp.toString());
    }
    public void setPost_status(String message) {
        post_status.setText(message);
    }

    public void setPost_Image(String url, final Context context){
        post_image.setVisibility(View.VISIBLE);
        image_preview_black_bg.setVisibility(View.VISIBLE);
        link_display_layout.setVisibility(View.GONE);

        progressBar_post_image.setVisibility(View.VISIBLE);
        Glide.with(post_image.getContext())
                .load(url)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        progressBar_post_image.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar_post_image.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(post_image);

        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra("user_id", post_user_id);
                intent.putExtra("post_id", post_id);
                intent.putExtra("image_type", "post");
                context.startActivity(intent);
            }
        });
    }


}
