package info.sayederfanarefin.location_sharing.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraListener;
import com.wonderkiln.camerakit.CameraView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.skyfishjy.library.RippleBackground;

import info.sayederfanarefin.location_sharing.MainActivity;
import info.sayederfanarefin.location_sharing.model.Like;
import info.sayederfanarefin.location_sharing.model.users;
import info.sayederfanarefin.location_sharing.utils.database;
import info.sayederfanarefin.location_sharing.utils.doRequest;
import info.sayederfanarefin.location_sharing.R;

public class ReactActivity extends AppCompatActivity {

    String emotion = "happiness";

    ImageButton manual_emotion_disgust;

    ImageButton manual_emotion_anger;

    ImageButton manual_emotion_fear;

    ImageButton manual_emotion_surprise;

    ImageButton manual_emotion_sad;

    ImageButton manual_emotion_happiness;
    ImageButton set_emotion;


    DatabaseReference postRef;

    private DatabaseReference userRef;

    private EmotionServiceClient client;
    CameraView cameraView;
    ImageButton re_capture_emotion;
    ImageButton capture;

    ImageView preview_666;
    TextView status_text_emotion;

    ImageView emotion_after_detection;

    RippleBackground emoji_animation_background;
    private FirebaseDatabase rootDb;
    LinearLayout status_layout, manual_emotion;

    String visitor_user_id;
    String visitor_user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_react);

        Bundle b = getIntent().getExtras();
        String post_id = b.getString("post_id", "0");
        String user_id = b.getString("user_id", "0");

        rootDb = database.getDatabase();
        postRef = rootDb.getReference().child("users").child(user_id).child("posts").child(post_id);

        visitor_user_id =  FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRef = rootDb.getReference().child("users").child(visitor_user_id);


        manual_emotion_disgust = (ImageButton) findViewById(R.id.manual_emotion_disgust);

         manual_emotion_anger= (ImageButton) findViewById(R.id.manual_emotion_anger);

         manual_emotion_fear= (ImageButton) findViewById(R.id.manual_emotion_fear);

         manual_emotion_surprise= (ImageButton) findViewById(R.id.manual_emotion_surprise);

         manual_emotion_sad= (ImageButton) findViewById(R.id.manual_emotion_sad);

         manual_emotion_happiness= (ImageButton) findViewById(R.id.manual_emotion_happiness);
        set_emotion= (ImageButton) findViewById(R.id.set_emotion);


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users visiting_user_user_object = dataSnapshot.getValue(users.class);
                visitor_user_name = visiting_user_user_object.getUsername();
                set_emotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatabaseReference pushRefLike = postRef.child("like").push();
                        pushRefLike.getKey();
                        Like like = new Like();
                        like.setUserId(visitor_user_id);
                        like.setUserName(visitor_user_name);
                        like.setLikeType(emotion);
                        pushRefLike.setValue(like).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                goBack();
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (client == null) {
            client = new EmotionServiceRestClient("b855f4690116467e85162db631c6799a");
        }
        cameraView = (CameraView) findViewById(R.id.camera);
        capture = (ImageButton) findViewById(R.id.capture_emotion);
        re_capture_emotion = (ImageButton) findViewById(R.id.re_capture_emotion);

        status_text_emotion = (TextView) findViewById(R.id.snap_emotion_status);
        preview_666 = (ImageView) findViewById(R.id.preview_666);

        emoji_animation_background =(RippleBackground)findViewById(R.id.emoji_animation_background);
        emotion_after_detection =(ImageView)findViewById(R.id.emotion_after_detection);

        status_layout =(LinearLayout) findViewById(R.id.status_layout);
        manual_emotion =(LinearLayout) findViewById(R.id.manual_emotion);

        cameraView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);
        cameraView.setMethod(CameraKit.Constants.METHOD_STANDARD);
        cameraView.setZoom(CameraKit.Constants.ZOOM_PINCH);
        cameraView.setPermissions(CameraKit.Constants.PERMISSIONS_STRICT);
        cameraView.setJpegQuality(100);
        cameraView.setFacing(CameraKit.Constants.FACING_FRONT);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.captureImage();
            }
        });

        manual_emotion_disgust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheUltimateEmotion("disgust");
            }});
        manual_emotion_happiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheUltimateEmotion("happpiness");
            }});
        manual_emotion_anger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheUltimateEmotion("anger");
            }});
        manual_emotion_sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheUltimateEmotion("sad");
            }});
        manual_emotion_surprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheUltimateEmotion("surprise");
            }});
        manual_emotion_fear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTheUltimateEmotion("fear");
            }});

        re_capture_emotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset everything
                cameraView.stop();
                cameraView.start();
                capture.setVisibility(View.VISIBLE);
                re_capture_emotion.setVisibility(View.GONE);
                status_layout.setVisibility(View.VISIBLE);
                manual_emotion.setVisibility(View.GONE);
                emoji_animation_background.setVisibility(View.GONE);
                emoji_animation_background.stopRippleAnimation();
                status_text_emotion.setText("Let POP detect your reaction by taking a snap!");
                set_emotion.setVisibility(View.GONE);
            }
        });

        cameraView.setCameraListener(new CameraListener() {

            @Override
            public void onCameraOpened() {
                super.onCameraOpened();
            }

            @Override
            public void onCameraClosed() {
                super.onCameraClosed();
            }

            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);
                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                result = scaleBitmap(result);
                preview_666.setVisibility(View.VISIBLE);
                preview_666.setImageBitmap(result);

                capture.setVisibility(View.GONE);
                re_capture_emotion.setVisibility(View.VISIBLE);

                status_text_emotion.setText("processing..");
                try {

                    Handler myHandler = new Handler() {

                        @Override
                        public void handleMessage(android.os.Message msg) {

                            String emotion = msg.getData().getString("emotion");
                            Log.v("-x-", "---" + emotion);
                            status_text_emotion.setText("Done");

                            setTheUltimateEmotion(emotion);
                            ReactActivity.this.emotion = emotion;

                            re_capture_emotion.setVisibility(View.VISIBLE);
                            set_emotion.setVisibility(View.VISIBLE);

                            Animation fadeOut = new AlphaAnimation(1, 0);
                            fadeOut.setInterpolator(new AccelerateInterpolator());
                            fadeOut.setDuration(1000);

                            fadeOut.setAnimationListener(new Animation.AnimationListener()
                            {
                                public void onAnimationEnd(Animation animation)
                                {
                                    status_layout.setVisibility(View.GONE);
                                    preview_666.setVisibility(View.GONE);

                                }
                                public void onAnimationRepeat(Animation animation) {}
                                public void onAnimationStart(Animation animation) {}
                            });

                            Animation fadeIn = new AlphaAnimation(0,1);
                            fadeIn.setInterpolator(new AccelerateInterpolator());
                            fadeIn.setDuration(1000);

                            fadeIn.setAnimationListener(new Animation.AnimationListener()
                            {
                                public void onAnimationEnd(Animation animation)
                                {
                                    manual_emotion.setVisibility(View.VISIBLE);

                                }
                                public void onAnimationRepeat(Animation animation) {}
                                public void onAnimationStart(Animation animation) {}
                            });

                            status_layout.startAnimation(fadeOut);
                            preview_666.startAnimation(fadeOut);
                            capture.setAnimation(fadeOut);

                            re_capture_emotion.setAnimation(fadeIn);
                            manual_emotion.setAnimation(fadeIn);
                            emoji_animation_background.setVisibility(View.VISIBLE);
                            emoji_animation_background.startRippleAnimation();

                        }
                    };

                    new doRequest(result, client, myHandler).execute();
                } catch (Exception e) {
                    Log.v("-x-", "---" + e.getLocalizedMessage());
                }

            }

        });
    }

private void setTheUltimateEmotion(String emotion){
    if(emotion.equals("happpiness")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_happiness);
    }else if(emotion.equals("disgust")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_disgust);
    }else if(emotion.equals("anger")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_anger);
    }else if(emotion.equals("fear")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_fear);
    }else if(emotion.equals("sadness")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_sad);
    }else if(emotion.equals("surprise")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_surprise);
    }else if(emotion.equals("contempt")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_contempt);
    }else if(emotion.equals("neutral")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_neutral);
    }else if(emotion.equals("no emotion")){
        emotion_after_detection.setImageResource(R.mipmap.emotion_happiness);
    }else {
        emotion_after_detection.setImageResource(R.mipmap.emotion_neutral);
    }

}

    private void goBack(){
        Intent intent = new Intent(ReactActivity.this, MainActivity.class);
        intent.putExtra("frag", "timeline");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_dp, R.anim.stay);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        emoji_animation_background.setVisibility(View.GONE);
        emoji_animation_background.stopRippleAnimation();
        super.onPause();
    }
    private Bitmap scaleBitmap(Bitmap bm) {

        int maxWidth = 512;
        int maxHeight = 512;

        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / maxWidth;
            width = maxWidth;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / maxHeight;
            height = maxHeight;
            width = (int)(width / ratio);
        } else {
            // square
            height = maxHeight;
            width = maxWidth;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

}
