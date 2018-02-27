package info.sayederfanarefin.location_sharing.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import info.sayederfanarefin.location_sharing.authentication.LogInEmailChange;
import info.sayederfanarefin.location_sharing.authentication.PasswordSet;
import info.sayederfanarefin.location_sharing.model.users;
import info.sayederfanarefin.location_sharing.utils.values;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import info.sayederfanarefin.location_sharing.EditMood;
import info.sayederfanarefin.location_sharing.R;

/**
 * Created by schmaedech on 30/06/17.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT = 2;
    private Toolbar mToolBar;
    private ImageButton mphotoPickerButton, nameEdit, coverPhoto, moodEdit;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private FirebaseAuth mFirebaseAuth;
    private ImageView profileImage, coverImage;
    private FirebaseDatabase root;
    private DatabaseReference usersDb;
    private Context mView;
    private StorageReference mStorageRef;
    private String currentUserId="";
    private String phone_number = "";

    private int proCoverPic = 0;
    TextView mTitle;

    RelativeLayout phone, email, id_, password, qr, time_line_relative_layout, friends;
    TextView phone_t, email_t, id_t, name_t, mood_t;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        mView = ProfileActivity.this;

        mStorageRef = FirebaseStorage.getInstance().getReference();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

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
                    Toast.makeText(ProfileActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ProfileActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProfileActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
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

    private void initializeScreen() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance();
        mProgress = new ProgressDialog(this);
        usersDb = root
                .getReference().child(values.dbUserLocation
                        + "/" + mFirebaseAuth.getCurrentUser().getUid());

        currentUserId =  mFirebaseAuth.getCurrentUser().getUid();
        mToolBar = (Toolbar) findViewById(R.id.toolbar);
       mToolBar.setTitle("");

        mTitle = (TextView) mToolBar.findViewById(R.id.toolbar_title);
        mTitle.setText("My Profile");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        phone = (RelativeLayout) findViewById(R.id.editphone);
        email = (RelativeLayout) findViewById(R.id.editemail);
        password = (RelativeLayout) findViewById(R.id.editpassword);
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

        mphotoPickerButton = (ImageButton) findViewById(R.id.imageButton);
        coverPhoto = (ImageButton) findViewById(R.id.floating_change_cover);
       // nameEdit = (ImageButton) findViewById(R.id.name_edit);
        moodEdit = (ImageButton) findViewById(R.id.status_edit);


        showPermissionWriteExternalStorage();
        showPermissionCamera();

        registerForContextMenu(mphotoPickerButton);
        registerForContextMenu(coverPhoto);

        mphotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proCoverPic = 0;
                view.showContextMenu();
            }
        });
        coverPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(ProfileActivity.this, "change cover", Toast.LENGTH_SHORT).show();
                proCoverPic = 1;
                view.showContextMenu();
            }
        });

//        nameEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ProfileActivity.this, FirstScreen.class);
//                startActivity(intent);
//            }
//        });

        moodEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditMood.class);
                startActivity(intent);
            }
        });


        time_line_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("frag", "timeline");
                startActivity(intent);
            }
        });


        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.putExtra("frag", "friends");
                startActivity(intent);
            }
        });

        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, My_phone.class);
                intent.putExtra("uid", currentUserId);
                intent.putExtra("phone", phone_number);
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, LogInEmailChange.class);
                startActivity(intent);
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, PasswordSet.class);
                startActivity(intent);
            }
        });

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, My_QR.class);
                intent.putExtra("uid", currentUserId);
                startActivity(intent);
            }
        });

        id_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, My_id.class);
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

    private void setUserInfo(users user){
        if (user.getProfilePicLocation() != null) {

            Glide.with(profileImage.getContext())
                    .load(user.getProfilePicLocation())
                    .centerCrop().
                    diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                    .into(profileImage);


//            StorageReference storageRef = FirebaseStorage.getInstance()
//                    .getReference().child(user.getProfilePicLocation());
//
//            Glide.with(mView)
//                    .using(new FirebaseImageLoader())
//                    .load(storageRef)
//                    .bitmapTransform(new CropCircleTransformation(mView))
//                    .into(profileImage);
        }

        if (user.getCoverPicLocation() != null) {


            Glide.with(coverImage.getContext())
                    .load(user.getCoverPicLocation())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .into(coverImage);


//            StorageReference storageRef2 = FirebaseStorage.getInstance()
//                    .getReference().child(user.getCoverPicLocation());
//
//            Glide.with(mView)
//                    .using(new FirebaseImageLoader())
//                    .load(storageRef2)
//                    .bitmapTransform(new CropCircleTransformation(mView))
//                    .into(coverImage);
        }


//        if(!user.getEmail().equals("")){
//            email_t.setText(user.getEmail());
//        }else{
//            email_t.setText("No email provided");
//            email_t.setTypeface(null, Typeface.ITALIC);
//
//        }

        phone_t.setText(user.getPhone());
        phone_number= user.getPhone();
        email_t.setText(user.getEmail());
        id_t.setText(user.getuser_custom_id());
        name_t.setText(user.getUsername());
        if(user.getMood() != ""){mood_t.setText(user.getMood());}else{ mood_t.setText("Feeling Happy!"); }
    }

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
//                String name =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
//                destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
//                // Log.v("===uri cam", destination.getAbsolutePath());
//                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
//                startActivityForResult(intent2, REQUEST_IMAGE);

                Intent intent3 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                Context context = ProfileActivity.this;
                File imagePath = new File(context.getFilesDir(), "public");
                if (!imagePath.exists()) imagePath.mkdirs();
                String name2 =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
                destination = new File(imagePath, name2+"tmp.jpg");
                Uri imageUri = FileProvider.getUriForFile(context, "info.sayederfanarefin.location_sharing.provider", destination);


                this.imagePath = destination.getAbsolutePath();

                intent3.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent3, REQUEST_IMAGE);


                return true;

            case R.id.view_profile_photo:


                String photo_stuff = "";
                if(proCoverPic == 0 ){
                    photo_stuff =  "profilePicLocation";
                }else{
                    photo_stuff = "coverPicLocation";
                }

                usersDb.child(photo_stuff).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Intent intenti = new Intent(ProfileActivity.this, ViewImageActivity.class);
                        intenti.putExtra("image_type", "url");
                        intenti.putExtra("url", dataSnapshot.getValue(String.class));
                        startActivity(intenti);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



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


    private void goBack(){
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.putExtra("frag", "friends");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
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
