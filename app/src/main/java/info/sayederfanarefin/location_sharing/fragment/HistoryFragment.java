package info.sayederfanarefin.location_sharing.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import info.sayederfanarefin.location_sharing.adapter.TimelineFirebaseRecycler;
import info.sayederfanarefin.location_sharing.utils.values;
import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.model.post;
import info.sayederfanarefin.location_sharing.model.users;
import info.sayederfanarefin.location_sharing.ui.NewStatusActivity;
import info.sayederfanarefin.location_sharing.utils.database;

import static android.app.Activity.RESULT_OK;


public class HistoryFragment extends Fragment {

    private users current_user;
    SharedPreferences sharedPref ;//= getActivity().getPreferences(Context.MODE_PRIVATE);
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;

    private DatabaseReference rootRef;
    private FirebaseDatabase rootDb;

    private SeekBar musicprogress;
    private String timeline_audio_url = "";
    Button button ;//= (Button)
    Boolean flag_first = true;
    private ValueEventListener mUserDatabaseReferenceValueEventListener;
    private ValueEventListener timeLineRefValueEventListener;

    private LinearLayoutManager linearLayoutManager;

    public HistoryFragment() {

    }
    private Toolbar mToolBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v =  inflater.inflate(R.layout.fragment_history, container, false);
        mFirebaseDatabase = database.getDatabase();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        findAndPopulateViews(v);
        init();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void findAndPopulateViews(View view){


        //mToolBar = (Toolbar) findViewById(R.id.toolbar);
        final AppCompatActivity act = (AppCompatActivity) getActivity();
        if (act.getSupportActionBar() != null) {
            Toolbar toolbar = (Toolbar) act.findViewById(R.id.toolbar);


        }


        timeline_self = (RecyclerView) view.findViewById(R.id.view_timeline_self);

        linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        empty_view = view.findViewById(R.id.empty_view);
        musicprogress = view.findViewById(R.id.musicprogress);

        mProgress = new ProgressDialog(getActivity());

        button = view.findViewById(R.id.post_button_empty);
    }

    int x = 0;


    int count = 0;

    private void init(){
        current_user_firebase = FirebaseAuth.getInstance().getCurrentUser();

        rootDb = database.getDatabase();

        rootRef = rootDb.getReference();

        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        mUserDatabaseReference = rootRef.child(values.dbUserLocation).child(current_user_firebase.getUid());

        mUserDatabaseReferenceValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current_user = dataSnapshot.getValue(users.class);

                timeline_audio_url = current_user.getTimeLineAudio();

                timeLineRef = rootRef.child(values.dbUserLocation).child(current_user_firebase.getUid()).child("posts");

                final List<post> postsList= new ArrayList<post>();

                timeLineRefValueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count = 0;
                        for (DataSnapshot imageSnapshot : dataSnapshot.getChildren()) {
                            postsList.add(imageSnapshot.getValue(post.class));
                            count ++;
                        }

                        post header_dummy_post = new post();
                        header_dummy_post.setPost_type("header");
                        postsList.add(header_dummy_post);

                        if(postsList.size()  > 0){
                            timeLineAdapter = new TimelineFirebaseRecycler(postsList,current_user,getContext());
                            empty_view.setVisibility(View.GONE);
                            timeline_self.setVisibility(View.VISIBLE);
                            timeline_self.setLayoutManager(linearLayoutManager);
                            timeline_self.setItemAnimator(new DefaultItemAnimator());
                            timeline_self.setAdapter(timeLineAdapter);
                        }else{
                        empty_view.setVisibility(View.VISIBLE);
                        timeline_self.setVisibility(View.GONE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), NewStatusActivity.class);
                                intent.putExtra("type", "text");
                                getActivity().startActivity(intent);
                            }
                        });
                    }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                };
                timeLineRef.addValueEventListener(timeLineRefValueEventListener);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mUserDatabaseReference.addValueEventListener(mUserDatabaseReferenceValueEventListener);



    }

    private FirebaseUser current_user_firebase;
    private RecyclerView timeline_self;
    DatabaseReference timeLineRef;

    private ImageButton play_button;
    private ImageButton pause_button;

    RelativeLayout empty_view;
    TimelineFirebaseRecycler timeLineAdapter;



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_fragement_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_timeline_music:
                Intent intent_upload = new Intent();
                intent_upload.setType("audio/*");
                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent_upload,666);

                return true;

            default:
                return true; //super.onOptionsItemSelected(item);
        }
    }


    private StorageReference mStorageRef;
    private ProgressDialog mProgress;

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, requestCode, data);

        Log.v("----x----", "history activity results called");

        if (requestCode == 666 && resultCode == RESULT_OK) {

            Log.v("----x----", "history inside if results called");

            mProgress.setMessage("Uploading...");
            mProgress.show();
            Uri uri = data.getData();

            StorageReference riversRef = mStorageRef.child("Timeline_Audio/"+ uri.getLastPathSegment()); //.child(uniqueId + "/profile_pic");//mStorage.child(imageLocationId);
            UploadTask uploadTask = riversRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getActivity(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    final String imageAudioPathUrl = downloadUrl.toString();

                    mUserDatabaseReference.child("timeLineAudio").setValue(imageAudioPathUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getActivity(), "Timeline music added!", Toast.LENGTH_SHORT).show();
                                    mProgress.dismiss();
                                    timeline_audio_url = imageAudioPathUrl;
                                }
                            });

                            task.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                                    mProgress.dismiss();
                                }
                            });
                        }
                    });

                   // audio_added_flag = true;
                   // post_button.setClickable(true);
                    mProgress.dismiss();
                }
            });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("=x=", "History Frag Attached");

        init();
        int scroll_pos = sharedPref.getInt("saved_pos", -1);
        if(scroll_pos != -1 && linearLayoutManager != null){
            linearLayoutManager.scrollToPosition(scroll_pos+1);
            Log.v("-x--", "saved "+String.valueOf(scroll_pos+1));
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        int currentVisiblePosition = 0;
        currentVisiblePosition = ((LinearLayoutManager)timeline_self.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("saved_pos",currentVisiblePosition);
        editor.commit();
        Log.v("-x--", "saving ... "+String.valueOf(currentVisiblePosition));


        Log.v("=x=", "Info frag Detached");
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            //  throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // throw new RuntimeException(e);
        }

        mUserDatabaseReference.removeEventListener(mUserDatabaseReferenceValueEventListener);
        timeLineRef.removeEventListener(timeLineRefValueEventListener);
    }
}
