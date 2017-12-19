package us.poptalks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import us.poptalks.R;

import us.poptalks.model.Message;
import us.poptalks.model.users;
import us.poptalks.ui.FirstScreen;


/**
 * Created by schmaedech on 30/06/17.
 */
public class SearchListFragment extends Fragment {
    public static final int RC_SIGN_IN = 1;
    private static final String TAG = "MessageFragment";
    private RecyclerView recentChatRecyclerview;
    private ArrayList<Message> recentMessageArrayList;
    private LinearLayoutManager linearLayoutManager;

    private ListView mChatListView;
    private FirebaseListAdapter mChatAdapter;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;
    private String mUsername;
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;

    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mFriendsDatabaseReference;
    private FirebaseUser currentUser;
    private FirebaseDatabase mFirebaseDatabase;

    public SearchListFragment() {

    }

ListView listView_id;
    private View view_;

    EditText search_bar;

    LinearLayout floating_new_message;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_ = inflater.inflate(R.layout.fragment_search_list, container, false);

        findViews(view_);
        return view_;
    }

    private void findViews(View view) {
      //  recentChatRecyclerview = view.findViewById(R.id.recent_chat_recyclerview);
        search_bar = view.findViewById(R.id.id_search);
        listView_id = view.findViewById(R.id.firend_search_list_id);
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

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
              //      onInitialize(user, view);

                } else {
                    Intent intent = new Intent(getActivity(), FirstScreen.class);
                    getActivity().startActivity(intent);

                }
            }
        };

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/users");//FirebaseDatabase.getInstance().getReference().child(values.dbUserLocation).getDatabase().getReference();

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");


        if (mFirebaseAuth != null)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        super.onPause();
    }

    private void search( CharSequence text ){ //}, FirebaseUser user, View view) {
        Query query = mUserDatabaseReference.orderByChild("uid").startAt(text.toString()).endAt("~");
        FirebaseListAdapter <users> firebase_list_adapter_users = new FirebaseListAdapter<users>(
                getActivity(),
                users.class,
                R.layout.friend_item,
                query
        ) {
            @Override
            protected void populateView(View v, final users model, int position) {
             //   CardView message_container_card_view = v.findViewById(R.id.message_container_card_view);

                if(model.getUid().equals(currentUser.getUid())){
                    v.setVisibility(View.GONE);
                   // message_container_card_view.setElevation(0);
                }else {
                    TextView invite_friend_user_name = v.findViewById(R.id.invite_friend_user_name);
                    TextView invite_friend_user_id = v.findViewById(R.id.invite_friend_user_id);
                    invite_friend_user_id.setText(model.getUid());
                    invite_friend_user_name.setText(model.getUsername());
                    ImageView invite_friend_user_image = v.findViewById(R.id.invite_friend_user_image);
                    if (model.getProfilePicLocation() != null && model.getProfilePicLocation() != "") {
                        Glide.with(getActivity().getApplicationContext())
                                .load(model.getProfilePicLocation())
                                .bitmapTransform(new CropCircleTransformation(getActivity().getApplicationContext()))
                                .into(invite_friend_user_image);
                    }

                    final Button b = v.findViewById(R.id.addFriend);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            b.setVisibility(View.GONE);
                            addFriend(model);

                        }
                    });
                }
            }
        };

        listView_id.setAdapter(firebase_list_adapter_users);

    }

    private void addFriend(final users u){

        //  Query queryRef = mUserDatabaseReference.orderByChild("uid").equalTo(currentUser.getUid());

        mFriendsDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/friends");//FirebaseDatabase.getInstance().getReference().child(values.dbUserLocation).getDatabase().getReference();



        mFriendsDatabaseReference.child(currentUser.getUid()).child(u.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                mUserDatabaseReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        users u2 = dataSnapshot.getValue(users.class);
                        mFriendsDatabaseReference.child(u.getUid()).child(u2.getUid()).setValue(u2).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity().getApplicationContext(), "Added!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//                HashMap<String, String> result1 = new HashMap<>();
//              //  result1.put( "user", currentUser.getUid());
//                result1.put("status", "new");
//                result1.put("user_name", currentUser.get);
//
//                mFriendsDatabaseReference.child(u.getUid()).child(currentUser.getUid()).setValue(result1).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Toast.makeText(getActivity().getApplicationContext(), "Added!", Toast.LENGTH_SHORT).show();
//                    }
//                });

            }
        });

    }
}
