package info.sayederfanarefin.location_sharing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.model.users;
import info.sayederfanarefin.location_sharing.ui.ProfileActivityFriend;
import info.sayederfanarefin.location_sharing.ui.QR_camera;
import info.sayederfanarefin.location_sharing.utils.values;

import static android.app.Activity.RESULT_OK;


/**
 * Created by piashsarker on 7/10/17.
 */

/**
 * Created by schmaedech on 30/06/17.
 */
public class FriendQRFragment extends Fragment {

    private FirebaseDatabase root;
    private DatabaseReference usersDb;
    private LinearLayout linerLayout;
    private TextView status_qr, searched_user_name, searched_user_phone;
    private Button scanqrbutton;
    private ImageView scan_qr_image, searched_user_image;

    public FriendQRFragment() {

    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            Log.v("=x=", "actvty dstryd");
            // throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // throw new RuntimeException(e);
            Log.v("=x=", "actvty dstryd");
        }
    }

    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_friend_qr, container, false);

        scanqrbutton = (Button) v.findViewById(R.id.button_scan_qr);
        status_qr = (TextView) v.findViewById(R.id.qr_status);
        searched_user_name = (TextView) v.findViewById(R.id.searched_friend_user_name);
        searched_user_phone = (TextView) v.findViewById(R.id.searched_friend_user_id);
        scan_qr_image = (ImageView) v.findViewById(R.id.image_qr);
        searched_user_image = (ImageView) v.findViewById(R.id.searched_qr_friend_user_image);

        linerLayout = (LinearLayout) v.findViewById(R.id.searched_user);

        setStatus("Please scan your friend's QR Code.");

        scanqrbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), QR_camera.class);
                startActivityForResult(new Intent(i),request_Code);

            }
        });

        root = FirebaseDatabase.getInstance();


        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private int request_Code = 1000;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

     //   // Log.v("=====xcv", "yay!");

        if (requestCode == request_Code) {
            if (resultCode == RESULT_OK) {
                String scanned_uid = data.getStringExtra("friend_id");
                //// Log.v("=====xcv", scanned_uid);

                search(scanned_uid);

            }
        }
    }

    public void search(String uid){

        try{
            usersDb = root.getReference().child(values.dbUserLocation  + "/" + uid);
            usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   final users user = dataSnapshot.getValue(users.class);

                    if(user != null){
                        setStatus("User found. ");

                        scan_qr_image.setVisibility(View.GONE);
                        linerLayout.setVisibility(View.VISIBLE);
                        searched_user_phone.setText(user.getPhone());
                        searched_user_name.setText(user.getUsername());
                        Glide.with(getActivity().getApplicationContext())
                                .load(user.getProfilePicLocation())
                                .centerCrop().
                                diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .bitmapTransform(new CropCircleTransformation(getActivity().getApplicationContext()))
                                .into(searched_user_image);
                        scanqrbutton.setText("Scan Another QR Code");

                        linerLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), ProfileActivityFriend.class);
                                intent.putExtra("uid", user.getUid());
                                startActivity(intent);
                            }
                        });


                    }else{
                        setStatus("User not found. Please try again!");
                    }


//                // Log.v("===qr", user.getUsername());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            setStatus("Not a valid QR Code for adding friends. Rescan the QR please.");
        }


    }

    private void setStatus(String msg){
        status_qr.setText(msg);
    }

}
