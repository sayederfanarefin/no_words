package info.sayederfanarefin.location_sharing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
//import com.flurgle.camerakit.CameraView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.wang.avi.AVLoadingIndicatorView;
//import com.mindorks.paracamera.Camera;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import info.sayederfanarefin.location_sharing.model.Message_2;
import info.sayederfanarefin.location_sharing.model.users;
import info.sayederfanarefin.location_sharing.utils.Constants;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import info.sayederfanarefin.location_sharing.R;

public class ChatBox_ extends AppCompatActivity {

    private String chatId;
    private static final String TAG = "ChatMessagesActivity";
    private static final int GALLERY_INTENT = 2;
    //CameraView camera;
    private ProgressBar chatsendprogress;
    private String messageId;
    private EditText mMessageField;
    private ImageButton mSendButton;
    private String chatName;
    private ListView mMessageList;
    private Toolbar mToolBar;
    private FirebaseDatabase mFirebaseDatabase;

    private DatabaseReference mMessageDatabaseReference;
    private DatabaseReference mMessageDatabaseReferenceWithMessageId;


    private DatabaseReference mMessagesDatabaseReference;
    private DatabaseReference mUsersDatabaseReference;
    private DatabaseReference mTypeIndicatorDatabaseReference;

    private ChildEventListener mMessageDatabaseReferenceChildEventListener;
    private ValueEventListener mMessageDatabaseReferenceValueEventListener;
    private ValueEventListener mTypeIndicatorDatabaseReferenceValueEventListener;

    private FirebaseListAdapter<Message_2> mMessageListAdapter;
    private FirebaseAuth mFirebaseAuth;
    private ImageButton mphotoPickerButton;
    private ImageButton mcameraPickerButton;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private ImageButton mrecordVoiceButton;
    private TextView mRecordLable;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    private ValueEventListener mValueEventListener;
    private EditText type_in_box;

    private View type_indicator_layout;
    private RelativeLayout type_indicator_relative_layout;

    private String myName;
    private String myProPic;
    private String myUid;

    private String[] permissions = {
            "android.permission.RECORD_AUDIO",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.CAMERA"
    };

    //for emotion detection purposes
    private Bitmap mBitmap;
    private Boolean busy = false;
    private String current_message_id_for_emotion_detection;
    private AVLoadingIndicatorView avi;
    private EmotionServiceClient client;

    //Camera theCamera;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private String[] emotions_ = {
            "anger",
            "contempt",
            "disgust",
            "fear",
            "happiness",
            "neutral",
            "sadness",
            "surprise"
    };
    private String[] emotions_images_ = {
            "R.mipmap.emotions_anger",//
            "R.mipmap.emotions_contempt",//
            "R.mipmap.emotions_disgust",//
            "R.mipmap.emotions_fear",//
            "R.mipmap.emotions_happiness",//
            "R.mipmap.emotions_neutral",//
            "R.mipmap.emotions_sad",//
            "R.mipmap.emotions_surprise"//
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box_);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);
        initializeScreen();

        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }


        if(chatName.equals("")){
            mToolBar.setTitle("New Chat!");
        }else{
            mToolBar.setTitle(chatName);
        }


        showMessages();

        showTypeIndicator(false);

    }

    private boolean amITyping = false;
    //RelativeLayout.LayoutParams type_indicator_params;

    private void notifyUser(Message_2 new_message){
        if(!isInFront){
            playNotification();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(new_message.getSender_name());
            mBuilder.setContentText(new_message.getContent());
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(666, mBuilder.build());
        }
    }
    private void playNotification(){
   //     // Log.v("====--", "play sound");
        try {
            Uri path = Uri.parse("android.resource://"+getPackageName()+"/raw/pop3.mp3");
             RingtoneManager.setActualDefaultRingtoneUri(
                    getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                    path);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), path);
            r.play();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showTypeIndicator(boolean state){
        if(state){
            type_indicator_relative_layout.setVisibility(View.VISIBLE);
        }else{
            type_indicator_relative_layout.setVisibility(View.GONE);
        }

    }

    private void setProgressSendMessage(int progress, boolean state){
        if(state){
            chatsendprogress.setVisibility(View.VISIBLE);
            chatsendprogress.setProgress(progress);
        }else{
            chatsendprogress.setProgress(0);
            chatsendprogress.setVisibility(View.GONE);
        }
    }
    private void initializeScreen() {
        mMessageList = (ListView) findViewById(R.id.messageListView);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mMessageField = (EditText) findViewById(R.id.messageToSend);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);

        mphotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mcameraPickerButton = (ImageButton) findViewById(R.id.cameraPickerButton);
        mphotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        mcameraPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                Context context = ChatBox_.this;
                File imagePath2 = new File(context.getFilesDir(), "public");
                if (!imagePath2.exists()) imagePath2.mkdirs();
                String name =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
                destination = new File(imagePath2, name+"tmp.jpg");
                imagePath = destination.getAbsolutePath();
                Uri imageUri = FileProvider.getUriForFile(context, "info.sayederfanarefin.location_sharing.provider", destination);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_IMAGE);

            }
        });

        chatsendprogress = (ProgressBar) findViewById(R.id.chatsendprogress);
        chatsendprogress.setVisibility(View.GONE);

        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        type_indicator_layout = li.inflate(R.layout.type_indicator,null);
        type_indicator_relative_layout = type_indicator_layout.findViewById(R.id.type_indiator);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mMessageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                amITyping = true;
                typeIndicator(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mMessageField.getText().toString().equals("")){
                    amITyping = false;
                    typeIndicator(false);
                }
            }
        });
        //getting chatId and chat name from previous activity
        Bundle b = getIntent().getExtras();
        chatId = b.getString("chatID");
        chatName = b.getString("chatName", "");
        //// Log.v("====baby", chatName);
        //------------------------------------------getting chatId and chat name from previous activity

        //initializing firebasecomponenets
        myUid = mFirebaseAuth.getCurrentUser().getUid();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_LOCATION);
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child(Constants.MESSAGE_LOCATION);
        mMessageDatabaseReference = mMessagesDatabaseReference.child(chatId);


        mMessageDatabaseReferenceChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
              //  playNotification();
                takeUserEmotionAndSend(dataSnapshot);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        mMessageDatabaseReference.addChildEventListener(mMessageDatabaseReferenceChildEventListener);
        //----------------------------------initializing firebasecomponenets

        //initializing ui elements
        mToolBar.setTitle(chatName);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        //------------------------------------initializing ui elements


        //send message button listener
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mMessageField.getText().toString().equals("")){
                    sendMessage(view);
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter a message to send!",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //getting my/sender information and setting onchange listener for realtime db
        mUsersDatabaseReference.child(myUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        populateStuff(snapshot);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
        mUsersDatabaseReference.child(myUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        populateStuff(snapshot);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });

        //--------------------getting my/sender information and setting onchange listener for realtime db


        //typeIndicator = (TextView) findViewById(R.id.messageTextViewright_type_indicator);

        mTypeIndicatorDatabaseReference = mMessagesDatabaseReference.child("typingIndicator").child(chatId).child("typing");

        mTypeIndicatorDatabaseReferenceValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String typ = dataSnapshot.getValue(String.class);
                if(typ !=null && typ.equals("true") && !amITyping){
                    showTypeIndicator(true);
                    // Toast.makeText(getApplicationContext(),"Some one is typing...",Toast.LENGTH_SHORT).show();
                }else{
                    showTypeIndicator(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mTypeIndicatorDatabaseReference.addValueEventListener(mTypeIndicatorDatabaseReferenceValueEventListener);

    }

    @Override
    public void onBackPressed()
    {
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
        goBack();
    }


    private void populateStuff(DataSnapshot snapshot){
        users uu = snapshot.getValue(users.class);
        myProPic = uu.getProfilePicLocation();
        myName = uu.getUsername();
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //this sends the text message

    public void sendMessage(View view) {

        mSendButton.setClickable(false);

        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();



        String messageString = mMessageField.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);

        Random rand2 = new Random();
        int xx = rand2.nextInt(25) +5;

        setProgressSendMessage(xx, true);

        Message_2 message = new Message_2();
        message.setSender_name(myName);
        message.setSender_uid(myUid);
        message.setContent(messageString);
        message.setTimestamp(timestamp);
        message.setType("text");
        message.setSender_image_location(myProPic);
        message.setId(pushKey);

        //Create HashMap for Pushing
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String, Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);

        Random rand = new Random();
        int i = rand.nextInt(50) +30;
        setProgressSendMessage(i, true);


        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {


                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            setProgressSendMessage(100, true);
                        }
                        if(task.isSuccessful()){
                            mMessageField.setText("");
                        }else{
                            Toast.makeText(ChatBox_.this, "Please try again.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        setProgressSendMessage(0, false);
                        mSendButton.setClickable(true);

                      //  updateLastActive(chatId);

                    }

                });
    }

    //send image message
    public void sendMessage( String url) {

        mSendButton.setClickable(false);

        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();



        String messageString = mMessageField.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);

        Random rand2 = new Random();
        int xx = rand2.nextInt(25) +5;

        setProgressSendMessage(xx, true);

        Message_2 message = new Message_2();
        message.setSender_name(myName);
        message.setSender_uid(myUid);
        message.setContent(url);
        message.setTimestamp(timestamp);
        message.setType("image");
        message.setSender_image_location(myProPic);
        message.setId(pushKey);

        //Create HashMap for Pushing
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String, Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObj);

        Random rand = new Random();
        int i = rand.nextInt(50) +30;
        setProgressSendMessage(i, true);


        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {


                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            setProgressSendMessage(100, true);
                        }
                        if(task.isSuccessful()){
                            mMessageField.setText("");
                        }else{
                            Toast.makeText(ChatBox_.this, "Please try again.", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        setProgressSendMessage(0, false);
                        mSendButton.setClickable(true);

                        //  updateLastActive(chatId);

                    }

                });
    }

    private Message_2 message_for_emotion_detection;
    //private boolean new_message_tobe_snapped =false;

    private List<Message_2> newMessages = new  ArrayList<Message_2>();

    private void takeUserEmotionAndSend(DataSnapshot dataSnapshot){

       // // Log.v("==emotion", "got a new child");
        Message_2 new_message = dataSnapshot.getValue(Message_2.class);

        if(!new_message.getSender_uid().equals(myUid)){
            notifyUser(new_message);
        }

        if(!new_message.getSender_uid().equals(myUid) && !new_message.getMessage_seen().equals("seen")){


            newMessages.add(new_message);
         //   // Log.v("==emotion", "got a new child -> new message to be snapped"+new_message.getSender_uid());

            if(!busy){
                busy = true;

                snap();
            }

        }else{
        }

    }

    private void showMessages() {

        Query query = mMessageDatabaseReference.limitToLast(100);

        mMessageListAdapter = new FirebaseListAdapter<Message_2>(this, Message_2.class, R.layout.message_item, query) {
            @Override
            protected void populateView(final View view, final Message_2 message, final int position) {

                TextView messageTextViewright = (TextView) view.findViewById(R.id.messageTextViewright);
                final TextView senderTextViewright = (TextView) view.findViewById(R.id.senderTextViewright);
                final TextView timeTextViewright = (TextView) view.findViewById(R.id.timeRight);
                final ImageView rightMessagePic = (ImageView) view.findViewById(R.id.rightMessagePic);
                final ImageView emojiRight = (ImageView) view.findViewById(R.id.emojiRight);
                LinearLayout individMessageLayoutRight = (LinearLayout) view.findViewById(R.id.individMessageLayoutRight);

                TextView messageTextViewleft = (TextView) view.findViewById(R.id.messageTextViewleft);
                final TextView senderTextViewleft = (TextView) view.findViewById(R.id.senderTextViewleft);
                final TextView timeTextViewleft = (TextView) view.findViewById(R.id.timeLeft);
                final ImageView leftMessagePic = (ImageView) view.findViewById(R.id.leftMessagePic);
                final ImageView emojiLeft = (ImageView) view.findViewById(R.id.emojiLeft);
                LinearLayout individMessageLayoutLeft = (LinearLayout) view.findViewById(R.id.individMessageLayoutLeft);


                final ImageView message_image_right = (ImageView) view.findViewById(R.id.imageMessageright);
                final ImageView message_image_left = (ImageView) view.findViewById(R.id.imageMessageleft);


                message_image_right.setVisibility(View.GONE);
                message_image_left.setVisibility(View.GONE);


                //self message right allign
                String mSender = message.getSender_uid();

                  if (mSender.equals(myUid)) {

                      messageTextViewleft.setVisibility(View.GONE);
                      senderTextViewleft.setVisibility(View.GONE);
                      leftMessagePic.setVisibility(View.GONE);
                      emojiLeft.setVisibility(View.GONE);
                      individMessageLayoutLeft.setVisibility(View.GONE);
                      timeTextViewleft.setVisibility(View.GONE);

                      messageTextViewright.setVisibility(View.VISIBLE);
                      senderTextViewright.setVisibility(View.VISIBLE);
                      rightMessagePic.setVisibility(View.VISIBLE);
                      emojiRight.setVisibility(View.VISIBLE);
                      individMessageLayoutRight.setVisibility(View.VISIBLE);
                      timeTextViewright.setVisibility(View.VISIBLE);



                     // messageTextViewright.setText(message.getContent());
                      timeTextViewright.setText(message.getTimestamp());

                //      // Log.v("=====", "message type: "+message.getType() +" message: "+ message.getContent());
                      if(message.getType().equals("image")){
                          messageTextViewright.setVisibility(View.GONE);
                          message_image_right.setVisibility(View.VISIBLE);

                          Glide.with(message_image_right.getContext())
                                  .load(message.getContent())
                                  .fitCenter()
                                  .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                  .into(message_image_right);

                      }else if(message.getType().equals("text")){
                          messageTextViewright.setVisibility(View.VISIBLE);
                          messageTextViewright.setText(message.getContent());
                      }

                    //profile image back to here
                    mUsersDatabaseReference.child(mSender).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            users userInfo = dataSnapshot.getValue(users.class);

                            if (userInfo != null && userInfo.getProfilePicLocation() != null) {

                                senderTextViewright.setText(userInfo.getUsername());

                                Glide.with(rightMessagePic.getContext())
                                        .load(userInfo.getProfilePicLocation())
                                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                        .centerCrop()
                                        .override(256, 256)
                                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                        .into(rightMessagePic);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                      Glide.with(emojiRight.getContext())
                              .load(get_emption_location(message.getRecipient_mood()))
                              .asGif()
                              .into(emojiRight);

                        if(message.getRecipient_mood() !=null && !message.getRecipient_mood().equals("")){
                           // // Log.v("==emo", message.getRecipient_mood());
                        }else{
                          //  // Log.v("==emo", "null message.getRecipient_mood()");
                        }

                      mMessageDatabaseReferenceValueEventListener = new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                              Message_2 tempo = dataSnapshot.getValue(Message_2.class);
                              String emo = tempo.getRecipient_mood();
                              if(emo !=null && !emo.equals("")){
                       //           // Log.v("==emo", emo);
                              }else{
                         //         // Log.v("==emo", "null emo");
                              }
                              Glide.with(emojiRight.getContext())
                                      .load(get_emption_location(emo))
                                      .asGif()
                                      .into(emojiRight);
                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {

                          }
                      };

                      mMessageDatabaseReferenceWithMessageId = mMessageDatabaseReference.child(message.getId());

                      mMessageDatabaseReferenceWithMessageId.addValueEventListener(mMessageDatabaseReferenceValueEventListener);

                      individMessageLayoutRight.setBackgroundResource(R.drawable.roundedmessagescolored);


                } else if (mSender.equals("System")) {

                } else {


//                      messageTextViewleft.setVisibility(View.VISIBLE);
                      senderTextViewleft.setVisibility(View.VISIBLE);
                      leftMessagePic.setVisibility(View.VISIBLE);
                      emojiLeft.setVisibility(View.VISIBLE);
                      individMessageLayoutLeft.setVisibility(View.VISIBLE);
                      timeTextViewleft.setVisibility(View.VISIBLE);

                      messageTextViewright.setVisibility(View.GONE);
                      senderTextViewright.setVisibility(View.GONE);
                      rightMessagePic.setVisibility(View.GONE);
                      emojiRight.setVisibility(View.GONE);
                      individMessageLayoutRight.setVisibility(View.GONE);
                      timeTextViewright.setVisibility(View.GONE);

                      timeTextViewleft.setText(message.getTimestamp());

                      if(message.getType().equals("image")){
                          messageTextViewleft.setVisibility(View.GONE);
                          message_image_left.setVisibility(View.VISIBLE);

                          Glide.with(message_image_left.getContext())
                                  .load(message.getContent())
                                  .fitCenter()
                                  .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                  .into(message_image_left);

                      }else if(message.getType().equals("text")){
                          messageTextViewleft.setVisibility(View.VISIBLE);
                          messageTextViewleft.setText(message.getContent());
                      }



                      //profile image back to here
                      mUsersDatabaseReference.child(mSender).addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                              users userInfo = dataSnapshot.getValue(users.class);

                              if (userInfo != null && userInfo.getProfilePicLocation() != null) {

                                  senderTextViewleft.setText(userInfo.getUsername());



                                  ImageView iv = type_indicator_layout.findViewById(R.id.leftMessagePic);
                                  Glide.with(iv.getContext())
                                          .load(userInfo.getProfilePicLocation())
                                          .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                          .centerCrop()
                                          .override(256, 256)
                                          .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                          .into(iv);

                                  TextView tv = type_indicator_layout.findViewById(R.id.senderTextViewleft);
                                   tv.setText(userInfo.getUsername());

                                  Glide.with(leftMessagePic.getContext())
                                          .load(userInfo.getProfilePicLocation())
                                          .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                          .centerCrop()
                                          .override(256, 256)
                                          .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                          .into(leftMessagePic);
                              }
                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {

                          }
                      });


                      Glide.with(emojiLeft.getContext())
                              .load(get_emption_location(message.getRecipient_mood()))
                              .asGif()
                              .into(emojiLeft);

                      if(message.getRecipient_mood() !=null && !message.getRecipient_mood().equals("")){
                      //    // Log.v("==emo", message.getRecipient_mood());
                      }else{
                      //    // Log.v("==emo", "null message.getRecipient_mood()");
                      }
                      mMessageDatabaseReferenceWithMessageId = mMessageDatabaseReference.child(message.getId());

                      mMessageDatabaseReferenceValueEventListener = new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                              Message_2 tempo = dataSnapshot.getValue(Message_2.class);
                              String emo = tempo.getRecipient_mood();
                              if(emo !=null && !emo.equals("")){
                                  //           // Log.v("==emo", emo);
                              }else{
                                  //         // Log.v("==emo", "null emo");
                              }
                              Glide.with(emojiRight.getContext())
                                      .load(get_emption_location(emo))
                                      .asGif()
                                      .into(emojiRight);
                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {

                          }
                      };
                      
                      mMessageDatabaseReferenceWithMessageId.addValueEventListener(mMessageDatabaseReferenceValueEventListener);

                      individMessageLayoutRight.setBackgroundResource(R.drawable.roundedmessagescolored);
                }
            }

        };
        mMessageList.setAdapter(mMessageListAdapter);
        mMessageList.addFooterView(type_indicator_layout);
        mMessageList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


    }

    private void typeIndicator (boolean abc){

        if(abc){
            mTypeIndicatorDatabaseReference.setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }else{
            mTypeIndicatorDatabaseReference.setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
        }


    }

    public void doRecognize() {

        try {

           // // Log.v("==emotion", "call to do recognize");
            new doRequest(false).execute();

        } catch (Exception e) {
            Log.v("----qa check", e.toString());
          //  // Log.v("==emotion exception",  e.toString());
        }

    }


    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        //Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();

        if(mBitmap == null){
         //    // Log.v("=====emo", "bitmap null");
        }else{
         //   // Log.v("=====emo", "bitmap not null");
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // // Log.v("===emo", "1");
        long startTime = System.currentTimeMillis();
        // // Log.v("===emo", "2");
        List<RecognizeResult> result = null;
        // // Log.v("===emo", "3");



        result = this.client.recognizeImage(inputStream);


        //  // Log.v("===emo", "4");
        String json = gson.toJson(result);
        //  Log.d("result", json);
        //  Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));

        return result;
    }

private int get_emption_location(String emo){

    int emption_image = R.mipmap.emotion_neutral;

    if(emo != null){

        if(emo.equals(emotions_[0])){
            // Log.v("===emo", "R.mipmap.emotion_anger");
            emption_image = R.mipmap.emotion_anger;
        }else if (emo.equals(emotions_[1])){

            // Log.v("===emo", "R.mipmap.emotion_contempt");
            emption_image =R.mipmap.emotion_contempt;
        }else if (emo.equals(emotions_[2])){

            // Log.v("===emo", "R.mipmap.emotion_disgust");
            emption_image = R.mipmap.emotion_disgust;
        }else if (emo.equals(emotions_[3])){

            // Log.v("===emo", "R.mipmap.emotion_fear");
            emption_image = R.mipmap.emotion_fear;
        }else if (emo.equals(emotions_[4])){
            // Log.v("===emo", "R.mipmap.emotion_happiness");
            emption_image = R.mipmap.emotion_happiness;
        }else if (emo.equals(emotions_[5])){

            // Log.v("===emo", "R.mipmap.emotion_neutral");
            emption_image = R.mipmap.emotion_neutral;
        }else if (emo.equals(emotions_[6])){

            // Log.v("===emo", "R.mipmap.emotion_sad");
            emption_image = R.mipmap.emotion_sad;
        }else if (emo.equals(emotions_[7])){


            // Log.v("===emo", "R.mipmap.emotion_surprise");
            emption_image =R.mipmap.emotion_surprise;
        }else{

            // Log.v("===emo", "R.mipmap.emotion_neutral");
            emption_image = R.mipmap.emotion_neutral;
        }
    }else{
        // Log.v("===emo", "null");
    }

    return emption_image;
}

private void goBack(){
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra("frag", "timeline");
    startActivity(intent);
    finish();

}

    private int getMaxScoreIndex (double[] emotion_scores) {

        double highest = 0;
        int index = 0;

        for (int i =0; i < emotion_scores.length ; i ++){
            if(highest > emotion_scores[i]){
                highest = emotion_scores[i];
                index = i;
            }
        }

        Log.v("----qa check","printing results");
        for (int x =0 ; x< emotion_scores.length; x++){
            Log.v("----qa chec", String.valueOf(emotion_scores[x]) + " index: " + String.valueOf(x));
        }
        Log.v("----qa chec score: ", String.valueOf(highest));
        Log.v("----qa chec index: ", String.valueOf(index));

        return index;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    private List<RecognizeResult> processWithFaceRectangles() throws EmotionServiceException, com.microsoft.projectoxford.face.rest.ClientException, IOException {
        Log.d("emotion", "Do emotion detection with known face rectangles");
        Gson gson = new Gson();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long timeMark = System.currentTimeMillis();
        Log.d("emotion", "Start face detection using Face API");
        FaceRectangle[] faceRectangles = null;
        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
        Face faces[] = faceClient.detect(inputStream, false, false, null);
        Log.d("emotion", String.format("Face detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));

        if (faces != null) {
            faceRectangles = new FaceRectangle[faces.length];

            for (int i = 0; i < faceRectangles.length; i++) {
                // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
            }
        }

        List<RecognizeResult> result = null;
        if (faceRectangles != null) {
            inputStream.reset();

            timeMark = System.currentTimeMillis();
            Log.d("emotion", "Start emotion detection using Emotion API");

            result = this.client.recognizeImage(inputStream, faceRectangles);

            String json = gson.toJson(result);
            Log.d("result", json);

            Log.d("emotion", String.format("Emotion detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));

        }
        return result;
    }

    private String emotion = "nutral";

    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {


        private Exception e = null;
        private boolean useFaceRectangles = false;

        public doRequest(boolean useFaceRectangles) {
            this.useFaceRectangles = useFaceRectangles;
            Log.v("----qa check", "doRequest constructor");
        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            if (this.useFaceRectangles == false) {
                try {
                    return processWithAutoFaceDetection();
                } catch (Exception e) {
                    this.e = e;
                }
            } else {
                try {
                    return processWithFaceRectangles();
                } catch (Exception e) {
                    this.e = e;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RecognizeResult> result) {
            super.onPostExecute(result);
            // Log.v("==emotion", "on post execute");

            if (this.useFaceRectangles == false) {
                // Log.v("==emotion",  "\n\nRecognizing emotions with auto-detected face rectangles...\n");
            } else {
                // Log.v("==emotion",  "\n\nRecognizing emotions with existing face rectangles from Face API...\n");
            }
            if (e != null) {
                // Log.v("==emotion",  e.getMessage());
                Log.v("----qa check", e.toString());
                this.e = null;
            } else {
                if (result.size() == 0) {

                    // Log.v("==emotion", "No emotion detected result =0");
                    //handler.sendEmptyMessage(0);
                    Log.v("----qa check", "result 0");
                    emotion = "none";
                } else {

                    Log.v("----qa check", "result paise");
                    Bitmap bitmapCopy = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas faceCanvas = new Canvas(bitmapCopy);
                    faceCanvas.drawBitmap(mBitmap, 0, 0, null);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);

                    for (RecognizeResult r : result) {

                        double[] emotions_value = {
                                r.scores.anger,
                                r.scores.contempt,
                                r.scores.disgust,
                                r.scores.fear,
                                r.scores.happiness,
                                r.scores.neutral,
                                r.scores.sadness,
                                r.scores.surprise

                        };

                        String[] emotions = {
                                "anger",
                                "contempt",
                                "disgust",
                                "fear",
                                "happiness",
                                "neutral",
                                "sadness",
                                "surprise"
                        };

                        int index = getMaxScoreIndex(emotions_value);
                        emotion = emotions[index];

                    }

                }

            }
          //  message.setRecipient_mood(emotion);
            //now update this child

            // Log.v("==change emotions ", emotion);
            busy = false;

            updateChildren();
        }
    }

    private void updateChildren(){

        //Create HashMap for updating
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();

        for(int cou =0; cou < newMessages.size(); cou++){

            Message_2 temp = newMessages.get(cou);
            temp.setRecipient_mood(emotion);
            temp.setMessage_seen("seen");
            HashMap<String, Object> messageObj = (HashMap<String, Object>) new ObjectMapper().convertValue(temp, Map.class);

            messageItemMap.put("/" + temp.getId(), messageObj);


        }

        mMessageDatabaseReference.updateChildren(messageItemMap)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Log.v("==emotion", "updated childs------------ ");
                    }
                });

    }



    @TargetApi(23)
    private void snap(){
          Log.v("=====camera", "called snap");

        int hasWriteContactsPermission = checkSelfPermission(permissions[2]);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {permissions[2]},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }

        /** picture call back */
        Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera)
            {
                mBitmap = rotateBitmap(BitmapFactory.decodeByteArray(data, 0, data.length),270);
                doRecognize();
            }
        };






        SurfaceView surface = new SurfaceView(this);
        Camera camera = Camera.open(1);
        try {
            camera.setPreviewDisplay(surface.getHolder());
            SurfaceTexture st = new SurfaceTexture(MODE_PRIVATE);
            camera.setPreviewTexture(st);
            camera.startPreview();
            camera.takePicture(null,null,jpegCallback);

        } catch (Exception e) {
             Log.v("----qa check", e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.chatbox_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                } else {
                    // Permission Denied
                    Toast.makeText(ChatBox_.this, "Camera Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        mTypeIndicatorDatabaseReference.removeEventListener(mTypeIndicatorDatabaseReferenceValueEventListener);

        mMessageDatabaseReference.removeEventListener(mMessageDatabaseReferenceChildEventListener);

        mMessageDatabaseReferenceWithMessageId.removeEventListener(mMessageDatabaseReferenceValueEventListener);

        mUsersDatabaseReference.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mMessageList.setAdapter(null);
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);

        return df.format(date);
    }
    private static final int REQUEST_IMAGE = 100;
    File destination;
    String imagePath;

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }
    private StorageReference mStorageRef;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgress.setMessage("Uploading...");
            mProgress.show();
            Uri uri = data.getData();

            StorageReference riversRef = mStorageRef.child("Photos/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
            UploadTask uploadTask = riversRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                     Toast.makeText(ChatBox_.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                    // Log.v("=====excep", exception.getLocalizedMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    sendMessage(downloadUrl.toString());
                    mProgress.dismiss();
                }
            });

        }

        if( requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK ){
            mProgress.setMessage("Uploading...");
            mProgress.show();
            Uri uri = Uri.parse(imagePath);
            InputStream stream = null;
            try {
                stream = new FileInputStream(imagePath);



            StorageReference riversRef = mStorageRef.child("Photos/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
                UploadTask  uploadTask = riversRef.putStream(stream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                          Toast.makeText(ChatBox_.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                        // Log.v("=====excep", exception.getLocalizedMessage());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        sendMessage(downloadUrl.toString());
                        mProgress.dismiss();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }
    private boolean isInFront;

    @Override
    public void onResume() {
        super.onResume();
        isInFront = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub

        outState.putString("photopath", imagePath);


        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("photopath")) {
                imagePath = savedInstanceState.getString("photopath");
            }
        }

        super.onRestoreInstanceState(savedInstanceState);
    }
}
