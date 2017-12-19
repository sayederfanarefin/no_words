package us.poptalks.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.TimeZone;

import us.poptalks.MainActivity;
import us.poptalks.R;
import us.poptalks.model.comments;
import us.poptalks.model.users;
import us.poptalks.utils.database;

public class POstIMageComment extends AppCompatActivity {
    private Toolbar mToolBar;

    private ImageView preview_image_image_status;

    private ProgressBar progressBar_post_comment_image;
    private FirebaseDatabase rootDb;

    private Button post_button;

    private LinearLayout gallery_selector, camera_selector;

    private final int REQUEST_PERMISSION_CAMERA=2;
    private final int REQUEST_PERMISSION_GALLERY=1;

    DatabaseReference commentRef;
    private StorageReference mStorageRef;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image_comment);


        mToolBar = (Toolbar) findViewById(R.id.toolbar_settings);
        mToolBar.setTitle("Image Comment");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        rootDb = database.getDatabase();

        mProgress = new ProgressDialog(this);

        Bundle b = getIntent().getExtras();
        final String post_id = b.getString("post_id");
        final String user_id = b.getString("user_id");

        post_button = (Button) findViewById(R.id.post_button);
        post_button.setClickable(false);

        gallery_selector = (LinearLayout) findViewById(R.id.button_top_gallery);
        camera_selector = (LinearLayout) findViewById(R.id.button_top_camera);

        progressBar_post_comment_image = (ProgressBar) findViewById(R.id.progressBar_post_comment_image);
        progressBar_post_comment_image.setVisibility(View.GONE);

        gallery_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGalleryClicked();
            }
        });
        camera_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCameraClicked();
            }
        });


        preview_image_image_status = (ImageView) findViewById(R.id.add_status_image_preview);

        commentRef = rootDb.getReference()
                .child("users")
                .child(user_id)
                .child("posts")
                .child(post_id)
                .child("comment");


        mStorageRef = FirebaseStorage.getInstance().getReference();

        post_button.setOnClickListener(new View.OnClickListener() {

            boolean flag_one_post = true;
            @Override
            public void onClick(View view) {



                rootDb.getReference()
                        .child("users")
                        .child(user_id)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                users current_user = dataSnapshot.getValue(users.class);

                                if(flag_one_post){
                                    DatabaseReference pushRefComment = commentRef.push();
                                    pushRefComment.getKey();
                                    comments c = new comments();
                                    c.setComment_user_id(user_id);
                                    c.setComment_type("image");
                                    c.setComment_user_name(current_user.getUsername());
                                    c.setComment_image_url(imagePathUrl);
                                    c.setComment_time(dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss"));

                                    pushRefComment.setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(POstIMageComment.this, "Comment successful!", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(POstIMageComment.this, MainActivity.class);
                                            intent.putExtra("frag", "timeline");
                                            startActivity(intent);
                                            finish();
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
        });




    }

    private String imagePathUrl="";
    private static final int GALLERY_INTENT = 22;
    private final int CAMERA_REQUEST_CODE=5000;

    private ProgressDialog mProgress;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, requestCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            mProgress.setMessage("Uploading...");
            mProgress.show();
            Uri uri = data.getData();

            StorageReference riversRef = mStorageRef.child("Photos/"+ uri.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(uri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    Toast.makeText(POstIMageComment.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imagePathUrl = downloadUrl.toString();
                    Glide.with(preview_image_image_status.getContext())
                            .load(imagePathUrl)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar_post_comment_image.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar_post_comment_image.setVisibility(View.GONE);
                            return false;
                        }
                    })
                            .into(preview_image_image_status);
                    post_button.setClickable(true);
                    mProgress.dismiss();
//                    upload_progress.setVisibility(View.GONE);

                }
            });

        }

        if( requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK ){
            try {
                mProgress.setMessage("Uploading...");
                post_button.setClickable(false);
                mProgress.show();

                Uri uri = Uri.parse(imagePath);
                InputStream stream = new FileInputStream(imagePath);
                StorageReference riversRef = mStorageRef.child("Photos/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
                UploadTask  uploadTask = riversRef.putStream(stream);
                //  upload_progress.setVisibility(View.VISIBLE);

//                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        long prgress_perce = (taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) * 100;
//
//                        //Log.v("===---", String.valueOf(taskSnapshot.getBytesTransferred())+" . "+String.valueOf(taskSnapshot.getTotalByteCount()) + " . " + String.valueOf(prgress_perce));
//
//                        Integer i = (int) (long) prgress_perce;
//                        upload_progress.setProgress(i);
//                        //Log.v("===---", String.valueOf(i));
//
//
//                    }
//                })

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(POstIMageComment.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
//                        upload_progress.setVisibility(View.GONE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        imagePathUrl = downloadUrl.toString();
                        mProgress.dismiss();
                        progressBar_post_comment_image.setVisibility(View.VISIBLE);

                        Glide.with(preview_image_image_status.getContext())
                                .load(imagePathUrl)
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar_post_comment_image.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar_post_comment_image.setVisibility(View.GONE);
                                return false;
                            }
                        })
                                .into(preview_image_image_status);
                        post_button.setClickable(true);

//                        upload_progress.setVisibility(View.GONE);
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    private void onCameraClicked(){
        showPermissionWriteExternalStorage();
        showPermissionCamera();

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        Context context = POstIMageComment.this;
        File imagePath = new File(context.getFilesDir(), "public");
        if (!imagePath.exists()) imagePath.mkdirs();
        String name =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
        destination = new File(imagePath, name+"tmp.jpg");
        Uri imageUri = FileProvider.getUriForFile(context, "us.poptalks.provider", destination);

        this.imagePath = destination.getAbsolutePath();

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }
    private void onGalleryClicked(){
        showPermissionWriteExternalStorage();
        showPermissionCamera();


        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }
    File destination;



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
                    Toast.makeText(POstIMageComment.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(POstIMageComment.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(POstIMageComment.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(POstIMageComment.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
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

    }

    }
