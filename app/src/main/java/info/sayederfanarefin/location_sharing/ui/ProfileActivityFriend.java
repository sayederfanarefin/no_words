package info.sayederfanarefin.location_sharing.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import info.sayederfanarefin.location_sharing.MainActivity;
import info.sayederfanarefin.location_sharing.model.post;
import info.sayederfanarefin.location_sharing.model.timeline_post;
import info.sayederfanarefin.location_sharing.utils.values;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import info.sayederfanarefin.location_sharing.ChatBox_;
import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.model.users;

/**
 * Created by schmaedech on 30/06/17.
 */
public class ProfileActivityFriend extends AppCompatActivity {
    DatabaseReference timeLineRef;
    private static final int GALLERY_INTENT = 2;
    private Toolbar mToolBar;
    private boolean hasAdapter =false;

    private TextView friend_text_view_toolbar;
    private LinearLayoutManager linearLayoutManager;

   // private ImageButton mphotoPickerButton, nameEdit, coverPhoto, moodEdit;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private FirebaseAuth mFirebaseAuth;
    private ImageView profileImage, coverImage;
    private FirebaseDatabase root;
    private DatabaseReference usersDb;
    private DatabaseReference usersChat;
    private Context mView;
    private StorageReference mStorageRef;
    private String currentUserId="";
    private String phone_number = "";

    private RecyclerView timeline_self;

    FirebaseRecyclerAdapter timeLineAdapter;

    private int proCoverPic = 0;

    RelativeLayout phone, email, id_, qr, time_line_relative_layout, friends;
    TextView phone_t, email_t, id_t, name_t, mood_t;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity_friend);
        mView = ProfileActivityFriend.this;

        Bundle b = getIntent().getExtras();
        currentUserId = b.getString("uid");

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // Log.v("===profile", currentUserId);
        initializeScreen();
       // openImageSelector();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ProfileActivityFriend.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivityFriend.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ProfileActivityFriend.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivityFriend.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }

        }
    }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, requestCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgress.setMessage("Uploading...");
            mProgress.show();
            Uri uri = data.getData();

            StorageReference riversRef = mStorageRef.child("Photos/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
            UploadTask  uploadTask = riversRef.putFile(uri);
            // Log.v("=====excep", "uploading...");
// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    //// Log.v("=====excep", exception.getLocalizedMessage());
                    //// Log.v("=====excep", exception.getMessage());
                    Toast.makeText(ProfileActivityFriend.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                    //// Log.v("=====excep", exception.);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    //// Log.v("=====down", downloadUrl.toString());
                    addImageToProfile(downloadUrl.toString());
                    mProgress.dismiss();
                }
            });

        }

        if( requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK ){
            try {
                mProgress.setMessage("Uploading...");
                mProgress.show();
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                imagePath = destination.getAbsolutePath();

                // Log.v("===zx", imagePath);
                Uri uri = Uri.parse(imagePath);

                InputStream stream = new FileInputStream(imagePath);

                StorageReference riversRef = mStorageRef.child("Photos/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
                UploadTask  uploadTask = riversRef.putStream(stream);
                // Log.v("=====excep", "uploading...");
// Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // Log.v("=====excep", exception.getLocalizedMessage());
                        // Log.v("=====excep", exception.getMessage());
                        Toast.makeText(ProfileActivityFriend.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                        //// Log.v("=====excep", exception.);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        //// Log.v("=====down", downloadUrl.toString());
                        addImageToProfile(downloadUrl.toString());
                        mProgress.dismiss();
                    }
                });

//                tvPath.setText(imagePath);
               // Bitmap bmp = BitmapFactory.decodeStream(in, null, options);


//                picture.setImageBitmap(bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

    }

    public void addImageToProfile(final String imageLocation) {
      //  final ImageView imageView = (ImageView) findViewById(R.id.profilePicture);

        if(proCoverPic == 0 ){
            usersDb
                .child(values.dbUserUserPhoto).setValue(imageLocation).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Glide.with(profileImage.getContext())
                                .load(imageLocation)
                                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(profileImage);


                    }
                }
            );
        }else{
            usersDb
                .child(values.dbUserUserCoverPhoto).setValue(imageLocation).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Glide.with(coverImage.getContext())
                                .load(imageLocation)
                                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .placeholder(R.drawable.gradient_image_view_cover_photo) // can also be a drawable
                                .error(R.mipmap.login_wallpaper)
                                .into(coverImage);

                    }
                }
            );
        }
    }
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private void initializeScreen() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance();
        mProgress = new ProgressDialog(this);
        usersDb = root
                .getReference().child(values.dbUserLocation
                        + "/" + currentUserId);


        usersChat = root
                .getReference()
                .child(values.dbUserLocation)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("chats")
                .child(currentUserId);


       // currentUserId =  mFirebaseAuth.getCurrentUser().getUid();
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        friend_text_view_toolbar = (TextView) mToolBar.findViewById(R.id.toolbar_title);


        phone = (RelativeLayout) findViewById(R.id.editphone);
        email = (RelativeLayout) findViewById(R.id.editemail);
       // password = (RelativeLayout) findViewById(R.id.editpassword);
        qr = (RelativeLayout) findViewById(R.id.editqr);
        id_ = (RelativeLayout) findViewById(R.id.editid);
        time_line_relative_layout = (RelativeLayout) findViewById(R.id.time_line_relative_layout);
        friends = (RelativeLayout) findViewById(R.id.profile_activity_friends_Layout);

        phone_t = (TextView) findViewById(R.id.phone_display);
        email_t = (TextView) findViewById(R.id.email_display);
        id_t = (TextView) findViewById(R.id.id_display);
        name_t = (TextView) findViewById(R.id.profile_name_textview);
        mood_t = (TextView) findViewById(R.id.status_textview);

        profileImage = (ImageView) findViewById(R.id.profilePicture);
        coverImage = (ImageView) findViewById(R.id.coverPic);

        timeline_self = (RecyclerView) findViewById(R.id.view_timeline_self);

        linearLayoutManager =new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        showPermissionWriteExternalStorage();
        showPermissionCamera();


        time_line_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivityFriend.this, MainActivity.class);
                intent.putExtra("frag", "timeline");
                startActivity(intent);
            }
        });


        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                usersChat.child("chatId").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String chatId = dataSnapshot.getValue(String.class);
                        // Log.v("====---", "friend chat id: "+ chatId);

                        Intent intent = new Intent(ProfileActivityFriend.this, ChatBox_.class);
                        intent.putExtra("chatID", chatId);
                        intent.putExtra("chatName", current_user_name);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivityFriend.this, Friend_QR.class);
                intent.putExtra("uid", currentUserId);
                startActivity(intent);
            }
        });




        usersDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users user = dataSnapshot.getValue(users.class);
                try {
                    setUserInfo(user);
                } catch (Exception e) {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final users current_user = dataSnapshot.getValue(users.class);
                try {
                    setUserInfo(current_user);

                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

    private final int REQUEST_PERMISSION_PHONE_STATE=1;
    private final int REQUEST_PERMISSION_CAMERA=2;

    private void showPermissionWriteExternalStorage() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_STATE);
            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_PHONE_STATE);
                //requestPermission(perms, REQUEST_PERMISSION_PHONE_STATE);
            }
        } else {
          //  Toast.makeText(ProfileActivity.this, "Permission (already) Granted! extstr", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPermissionCamera() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA);
            } else {
                requestPermission(Manifest.permission.CAMERA, REQUEST_PERMISSION_CAMERA);

            }
        } else {
          //  Toast.makeText(ProfileActivity.this, "Permission (already) Granted! camera!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setUserInfo(final users user){
        if (user.getProfilePicLocation() != null) {

            Glide.with(profileImage.getContext())
                    .load(user.getProfilePicLocation())
                    .centerCrop().
                    diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .into(profileImage);

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intenti = new Intent(ProfileActivityFriend.this, ViewImageActivity.class);
                    intenti.putExtra("image_type", "url");
                    intenti.putExtra("url", user.getProfilePicLocation());
                    startActivity(intenti);
                }
            });
        }

        if (user.getCoverPicLocation() != null) {


            Glide.with(coverImage.getContext())
                    .load(user.getCoverPicLocation())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(coverImage);

            coverImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intenti = new Intent(ProfileActivityFriend.this, ViewImageActivity.class);
                    intenti.putExtra("image_type", "url");
                    intenti.putExtra("url", user.getCoverPicLocation());
                    startActivity(intenti);
                }
            });
        }

        phone_t.setText(user.getPhone());
        phone_number= user.getPhone();
        email_t.setText(user.getEmail());
        id_t.setText(user.getUid());
        name_t.setText(user.getUsername());

        friend_text_view_toolbar.setText(user.getUsername());

        if(user.getMood() != ""){mood_t.setText(user.getMood());}else{ mood_t.setText("Feeling Happy!"); }

        current_user_name = user.getUsername();


        showTimeLine();
    }

    String current_user_name;
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.choose_photo_source_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload:
                // your first action code
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
                return true;

            case R.id.take_a_new_photo:
                // your second action code

                // Log.v("===x", "camera called");
                String name =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
                destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
                // Log.v("===uri cam", destination.getAbsolutePath());
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
                startActivityForResult(intent2, REQUEST_IMAGE);

                return true;

            case R.id.view_profile_photo:


            default:
                return super.onContextItemSelected(item);
        }
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }
    private static final int REQUEST_IMAGE = 100;
    File destination;
    String imagePath;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(hasAdapter){
            timeLineAdapter.cleanup();
        }

    }

    int count = 0;

    private void showTimeLine(){



//get the refererance to the user who is logged in
        root.getReference().child(values.dbUserLocation
                + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final users logged_in_user = dataSnapshot.getValue(users.class);
                timeLineRef = usersDb.child("posts");
                // Log.v("==--==x", "i was here "+ timeLineRef.toString());


                timeLineRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot imageSnapshot : dataSnapshot.getChildren()) {
                            count++;
                        }
                        if (count > 0){
                            hasAdapter = true;
                            timeLineAdapter = new FirebaseRecyclerAdapter<post, timeline_post>(
                                    post.class,
                                    R.layout.timeline_post_layout,
                                    timeline_post.class,
                                    timeLineRef) {
                                @Override
                                public void populateViewHolder(final timeline_post holder, final post post, int position) {

                                    root.getReference().child(values.dbUserLocation+ "/" + post.getUser()).child("profilePicLocation").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            holder.setPost_profile_image(dataSnapshot.getValue(String.class));

                                            holder.setPost_user_name(post.getUserName());
                                            holder.setPost_time(post.getPost_time());
                                            holder.setUserId(logged_in_user.getUid());
                                            holder.setUserName(logged_in_user.getUsername());
                                            holder.setPostIdUserId(post.getUser(), post.getPost_id(), ProfileActivityFriend.this);
                                            holder.setPost_type(post.getPost_type());

                                            if(post.getPost_type().equals("image")){
                                                holder.setPost_Image(post.getImage_link(), ProfileActivityFriend.this);
                                            }else if(post.getPost_type().equals("link")){
                                                holder.setPost_link(post.getLink(), ProfileActivityFriend.this);
                                            }
                                            holder.setPost_status(post.getStatus_text());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });




                                }


                            };


                            timeLineAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                                @Override
                                public void onItemRangeInserted(int positionStart, int itemCount) {
                                    super.onItemRangeInserted(positionStart, itemCount);

                                    int pollCount = timeLineAdapter.getItemCount();

                                    Log.v("==x==", "data changed"
                                            + String.valueOf(positionStart)
                                            + " "
                                            + String.valueOf(itemCount)
                                            + ", itemcount->"
                                            + String.valueOf(pollCount)

                                    );


//                        int lastVisiblePosition = mManager.findLastCompletelyVisibleItemPosition();
//
//                        // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
//                        // to the bottom of the list to show the newly added message.
//                        if (lastVisiblePosition == -1 ||
//                                (positionStart >= (pollCount - 1) && lastVisiblePosition == (positionStart - 1))) {
//                            timeline_self.scrollToPosition(positionStart);
//                        }
                                }
                            });

                            timeline_self.setLayoutManager(linearLayoutManager);
                            timeline_self.setHasFixedSize(false);
                            timeline_self.setAdapter(timeLineAdapter);
                            timeline_self.setVisibility(View.VISIBLE);
                        }else{
                            hasAdapter = false;
                            timeline_self.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void goBack(){
        Intent intent = new Intent(ProfileActivityFriend.this, MainActivity.class);
        intent.putExtra("frag", "friends");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
