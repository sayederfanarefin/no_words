package us.poptalks.authentication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import us.poptalks.MainActivity;
import us.poptalks.R;
import us.poptalks.ui.NewStatusActivity;
import us.poptalks.ui.ProfileActivity;
import us.poptalks.utils.values;

public class InputName extends AppCompatActivity {
    FirebaseUser user;
    private StorageReference mStorageRef;
    private static final int GALLERY_INTENT = 2;
    //databses
    DatabaseReference rootRef;
    DatabaseReference usersRef;

    ImageButton takeSnap;
    EditText create_profile_name;
    private FirebaseAuth mFirebaseAuth;
    ImageView inputNameProPic;
    private ProgressDialog mProgress;

    CheckBox sync_contact;
SharedPreferences sharedPreferences;



    Button getStarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_name);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        usersRef = rootRef.child(values.dbUserLocation+ "/" + user.getUid());
        takeSnap = (ImageButton) findViewById(R.id.imageButton_takesnap);

        mProgress = new ProgressDialog(this);

        takeSnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.showContextMenu();
            }
        });
        sharedPreferences = getSharedPreferences("sync_contacts", Context.MODE_PRIVATE);

        showPermissionWriteExternalStorage();
        showPermissionCamera();

        registerForContextMenu(takeSnap);

        create_profile_name = (EditText) findViewById(R.id.create_profile_name);
        getStarted = (Button) findViewById(R.id.button_create_user_getStarted);
        inputNameProPic = (ImageView) findViewById(R.id.profilePicture_66);

        sync_contact = (CheckBox) findViewById(R.id.sync_contact);
        sync_contact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("sync_contacts", true);
                    editor.commit();
                }else{
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("sync_contacts", false);
                    editor.commit();
                }
            }
        });

        mStorageRef = FirebaseStorage.getInstance().getReference();

        usersRef.child(values.dbUserUserName).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                create_profile_name.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        usersRef.child(values.dbUserUserPhoto).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Glide.with(inputNameProPic.getContext())
                        .load(dataSnapshot.getValue(String.class))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.mipmap.user)
                        .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                        .into(inputNameProPic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        create_profile_name.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);




        create_profile_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){

                    view.setBackgroundResource( R.drawable.edittexrroundedcorner_focused);
                }
                else{
                    view.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);
                }
            }
        });


        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                usersRef.child(values.dbUserUserName).setValue( create_profile_name.getText().toString()).addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Bundle b = getIntent().getExtras();
                                usersRef.child(values.dbUserUserPhone).setValue( b.getString("phone")).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                                usersRef.child(values.dbUserUid).setValue(user.getUid()).addOnCompleteListener(
                                                        new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Intent intent = new Intent(InputName.this, MainActivity.class);
                                                                startActivity(intent);
                                                                finish();

                                                            }
                                                        }
                                                );

                                            }
                                        }
                                );
                            }
                        });

                    }
                });
            }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.choose_photo_source_context_menu_input_name, menu);
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


                Intent intent2 = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                Context context = InputName.this;
                File imagePath = new File(context.getFilesDir(), "public");
                if (!imagePath.exists()) imagePath.mkdirs();
                String name =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
                destination = new File(imagePath, name+"tmp.jpg");

                this.imagePath = destination.getAbsolutePath();

                Uri imageUri = FileProvider.getUriForFile(context, "us.poptalks.provider", destination);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent2, REQUEST_IMAGE);


//                // your second action code
//
//              //  // Log.v("===x", "camera called");
//                String name =   dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
//                destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
//             //   // Log.v("===uri cam", destination.getAbsolutePath());
//                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
//                startActivityForResult(intent2, REQUEST_IMAGE);

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


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String permissions[],
            int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_PHONE_STATE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(InputName.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InputName.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(InputName.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InputName.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
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
            UploadTask uploadTask = riversRef.putFile(uri);
    //        // Log.v("=====excep", "uploading...");
// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    //// Log.v("=====excep", exception.getLocalizedMessage());
                    //// Log.v("=====excep", exception.getMessage());
                    Toast.makeText(InputName.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
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
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) { mProgress.dismiss();
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
        usersRef.child(values.dbUserUserPhoto).setValue(imageLocation).addOnCompleteListener(
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Glide.with(inputNameProPic.getContext())
                                    .load(imageLocation)
                                    .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(inputNameProPic);
                        }
                    }
            );

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
