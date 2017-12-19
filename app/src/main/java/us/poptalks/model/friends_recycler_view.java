package us.poptalks.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
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
import com.flurgle.camerakit.CameraView;
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
import us.poptalks.ui.POstIMageComment;
import us.poptalks.ui.ReactActivity;
import us.poptalks.ui.ViewImageActivity;
import us.poptalks.utils.CommonStaticMethods;
import us.poptalks.utils.database;

/**
 * Created by erfanarefin on 30/08/2017.
 */

public class friends_recycler_view extends RecyclerView.ViewHolder {
    final TextView name;
    final TextView mood;
    final TextView phone;
    final ImageView invite_friend_user_image;


    public friends_recycler_view(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.friend_list_without_button_item_name);
        mood = itemView.findViewById(R.id.friend_list_without_button_item_status);
        phone = itemView.findViewById(R.id.friend_list_without_button_item_user_id);
        invite_friend_user_image = itemView.findViewById(R.id.message_profile_image_without_button);
    }

}
