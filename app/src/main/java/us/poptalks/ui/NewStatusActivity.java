package us.poptalks.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import us.poptalks.MainActivity;
import us.poptalks.R;
import us.poptalks.model.post;
import us.poptalks.model.users;
import us.poptalks.utils.database;
import us.poptalks.utils.values;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

public class NewStatusActivity extends AppCompatActivity {
    private DatabaseReference rootRef;
    private FirebaseDatabase rootDb;

    private Button post_button, add_audio_button;
    private LinearLayout camera, photo, link, image_preview_layout, link_preview_layout, preview_box_link_entry;

    private EditText status_edit_text, link_edit_text;

    private TextView link_preview_title, link_preview_des;

    boolean flag_image = false;
    boolean flag_link = false;

    ImageView preview_image, post_pro_pic, preview_image_image_status;

    ProgressBar upload_progress, link_progress;

    TextCrawler textCrawler ;//= new TextCrawler();
    private FirebaseUser current_user_firebase;
    private users current_user;

    private CardView preview_box_link;


    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mPostDatabaseReference;

    private String imagePath;
    private String imagePathUrl="";

    private boolean inFinished;

    private StorageReference mStorageRef;
    private boolean audio_added_flag = false;
    private final int REQUEST_PERMISSION_CAMERA=2;
    private final int REQUEST_PERMISSION_GALLERY=1;
    private final int CAMERA_REQUEST_CODE=5000;
    private static final int GALLERY_INTENT = 22;
private ValueEventListener mUserDatabaseReferenceValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_notification);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("New Status");

        inFinished = false;

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();

                backToMainActivity();
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Bundle b= getIntent().getExtras();
        String type_of_status = b.getString("type");

        image_preview_layout = (LinearLayout) findViewById(R.id.image_preview_layout);
        link_preview_layout = (LinearLayout) findViewById(R.id.link_preview_layout);
        preview_box_link_entry = (LinearLayout) findViewById(R.id.preview_box_link_entry);

        preview_box_link = (CardView) findViewById(R.id.preview_box_link);

        mProgress = new ProgressDialog(this);

        image_preview_layout.setVisibility(View.GONE);
        link_preview_layout.setVisibility(View.GONE);
        preview_box_link_entry.setVisibility(View.GONE);

        status_edit_text = (EditText) findViewById(R.id.status_editText);
        link_edit_text = (EditText) findViewById(R.id.edit_text_link);

        link_preview_title = (TextView) findViewById(R.id.preview_link_666);
        link_preview_des = (TextView) findViewById(R.id.preview_link_666_desc);


        preview_image = (ImageView) findViewById(R.id.image_link_preview);
        preview_image_image_status = (ImageView) findViewById(R.id.add_status_image_preview);
        post_pro_pic = (ImageView) findViewById(R.id.post_pro_pic);


        upload_progress = (ProgressBar) findViewById(R.id.imageUploadProgress);
        link_progress = (ProgressBar) findViewById(R.id.linkUploadProgress);

        link_progress.setProgress(0);
        upload_progress.setVisibility(View.GONE);

        textCrawler = new TextCrawler();

        preview_box_link.setVisibility(View.GONE);

        if(type_of_status.equals("text")){

        }else if(type_of_status.equals("camera")){
            onCameraClicked();

        }else if(type_of_status.equals("link")){
            onLinkClicked();

        }else if(type_of_status.equals("gallery")){
            onGalleryClicked();
        }else{

        }

        post_button = (Button) findViewById(R.id.post_button);
        post_button.setClickable(false);

        camera = (LinearLayout) findViewById(R.id.new_status_camera);
        photo = (LinearLayout) findViewById(R.id.new_status_photo);
        link = (LinearLayout) findViewById(R.id.new_status_link);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCameraClicked();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGalleryClicked();
            }
        });
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLinkClicked();
            }
        });


        current_user_firebase = FirebaseAuth.getInstance().getCurrentUser();

        rootDb = database.getDatabase();

        rootRef = rootDb.getReference();

        mUserDatabaseReference = rootRef.child(values.dbUserLocation).child(current_user_firebase.getUid());
        mPostDatabaseReference = mUserDatabaseReference.child("posts");//.child(current_user_firebase.getUid());


        mUserDatabaseReferenceValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current_user = dataSnapshot.getValue(users.class);
                post_button.setClickable(true);

                if(!inFinished){
                    Glide.with(post_pro_pic.getContext())
                            .load(current_user.getProfilePicLocation())
                            .centerCrop().
                            diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(post_pro_pic);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mUserDatabaseReference.addValueEventListener(mUserDatabaseReferenceValueEventListener);


        link_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        // Any work that needs to be done before generating the preview. Usually inflate
                        // your custom preview layout here.

                        Random rand2 = new Random();
                        int xx = rand2.nextInt(50) +5;

                        link_progress.setVisibility(View.VISIBLE);
                        link_progress.setProgress(xx);

                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean b) {

                        link_preview_des.setText(sourceContent.getDescription());
                        link_preview_title.setText(sourceContent.getTitle());
                        link_progress.setProgress(75);
                        try {
                            if(!inFinished) {
                                Glide.with(preview_image.getContext())
                                        .load(sourceContent.getImages().get(0))
                                        .centerCrop().
                                        diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                        .into(preview_image);
                            }
                        }catch (Exception e){

                        }

                        link_progress.setProgress(100);
                        link_progress.setVisibility(View.GONE);

                        preview_box_link.setVisibility(View.VISIBLE);
                    }
                };

                String url = editable.toString();

                if(!URLUtil.isNetworkUrl(url)){
                    url = "http://"+url;
                }

                textCrawler.makePreview( linkPreviewCallback, url);

            }
        });
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                post newpost = new post();

                newpost.setStatus_text(status_edit_text.getText().toString());
                newpost.setPost_type("status");
                newpost.setUser(current_user.getUid());
                newpost.setUserName(current_user.getUsername());

                //post time ---------------------------------------------------------------------------may be an issue
                newpost.setPost_time(dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss"));//DateFormat.getDateTimeInstance().format(new Date()));


                DatabaseReference pushRef = mPostDatabaseReference.push();
                newpost.setPost_id(pushRef.getKey());

                if(flag_link){
                    newpost.setPost_type("link");
                    String url = link_edit_text.getText().toString();

                    if(!url.equals("")){
                        newpost.setLink(url);
                        pushRef.setValue(newpost).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                             //   finish();

                                backToMainActivity();
                            }
                        });

                    }else{
                        Toast.makeText(NewStatusActivity.this, "Please enter a url", Toast.LENGTH_SHORT);
                    }



                }else if(flag_image && !imagePathUrl.equals("")){
                    newpost.setPost_type("image");

                    newpost.setImage_link(imagePathUrl);

                    pushRef.setValue(newpost).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           // finish();
                            backToMainActivity();
                        }
                    });

                }else{
                    pushRef.setValue(newpost).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           // finish();
                            backToMainActivity();
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textCrawler.cancel();
    }

    private void backToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("frag", "timeline");
        startActivity(intent);
        finish();
    }

    private void onCameraClicked(){
        showPermissionWriteExternalStorage();
        showPermissionCamera();
        flag_image = true;
        flag_link = false;
        image_preview_layout.setVisibility(View.VISIBLE);
        preview_box_link_entry.setVisibility(View.GONE);
        link_preview_layout.setVisibility(View.GONE);


        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        Context context = NewStatusActivity.this;
        File imagePath = new File(context.getFilesDir(), "public");
        if (!imagePath.exists()) imagePath.mkdirs();
        String name =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
        destination = new File(imagePath, name+"tmp.jpg");

        this.imagePath = destination.getAbsolutePath();
        Uri imageUri = FileProvider.getUriForFile(context, "us.poptalks.provider", destination);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    File destination;

    private void onLinkClicked(){
        flag_link = true;
        flag_image = false;
        image_preview_layout.setVisibility(View.GONE);
        preview_box_link_entry.setVisibility(View.VISIBLE);
        link_preview_layout.setVisibility(View.VISIBLE);
    }
    private void onGalleryClicked(){
        showPermissionWriteExternalStorage();
        showPermissionCamera();
        flag_image = true;
        flag_link = false;
        image_preview_layout.setVisibility(View.VISIBLE);
        preview_box_link_entry.setVisibility(View.GONE);
        link_preview_layout.setVisibility(View.GONE);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    private String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
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

    private void showPermissionWriteExternalStorage() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_GALLERY);
            } else {
                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_PERMISSION_GALLERY);
                //requestPermission(perms, REQUEST_PERMISSION_PHONE_STATE);
            }
        } else {
            //  Toast.makeText(ProfileActivity.this, "Permission (already) Granted! extstr", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_GALLERY:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(NewStatusActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewStatusActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(NewStatusActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewStatusActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
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

    private ProgressDialog mProgress;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, requestCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgress.setMessage("Uploading...");
            mProgress.show();
            Uri uri = data.getData();

            StorageReference riversRef = mStorageRef.child("Photos/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
            UploadTask  uploadTask = riversRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Toast.makeText(NewStatusActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imagePathUrl = downloadUrl.toString();
                    upload_progress.setVisibility(View.VISIBLE);

                    if(!inFinished){
                    Glide.with(preview_image_image_status.getContext())
                            .load(imagePathUrl)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    upload_progress.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    upload_progress.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(preview_image_image_status);
                    }
                    post_button.setClickable(true);
                    mProgress.dismiss();

                }
            });

        }

//        if (requestCode == 666 && resultCode == RESULT_OK) {
//            mProgress.setMessage("Uploading...");
//            mProgress.show();
//            Uri uri = data.getData();
//
//            StorageReference riversRef = mStorageRef.child("Photos_Audio/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
//            UploadTask  uploadTask = riversRef.putFile(uri);
//            uploadTask.addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    Toast.makeText(NewStatusActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
//                    mProgress.dismiss();
//                }
//            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                    imageAudioPathUrl = downloadUrl.toString();
//                    audio_added_flag = true;
//                    post_button.setClickable(true);
//                    mProgress.dismiss();
//                }
//            });
//
//        }
        if( requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK ){
            try {
                mProgress.setMessage("Uploading...");
                post_button.setClickable(false);
                mProgress.show();

                Uri uri = Uri.parse(imagePath);
                InputStream stream = new FileInputStream(imagePath);
                StorageReference riversRef = mStorageRef.child("Photos/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
                UploadTask  uploadTask = riversRef.putStream(stream);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(NewStatusActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        imagePathUrl = downloadUrl.toString();
                        upload_progress.setVisibility(View.VISIBLE);

                        if(!inFinished){
                        Glide.with(preview_image_image_status.getContext())
                                .load(imagePathUrl)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .listener(new RequestListener<String, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        upload_progress.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        upload_progress.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(preview_image_image_status);
                        }
                       post_button.setClickable(true);
                        mProgress.dismiss();
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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


    @Override
    public void onPause(){
        super.onPause();
        mUserDatabaseReference.removeEventListener(mUserDatabaseReferenceValueEventListener);
        inFinished = true;
    }

    @Override
    public void onResume(){
        super.onResume();
        mUserDatabaseReference.addValueEventListener(mUserDatabaseReferenceValueEventListener);
        inFinished = false;
    }
}
