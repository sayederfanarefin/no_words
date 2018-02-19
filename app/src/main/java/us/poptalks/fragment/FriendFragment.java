package us.poptalks.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseListAdapter;
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
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import us.poptalks.MainActivity;
import us.poptalks.NewChatActivity;
import us.poptalks.adapter.FriendsFirebaseRecycler;
import us.poptalks.adapter.TimelineFirebaseRecycler;
import us.poptalks.model.Friend;
import us.poptalks.model.friends;
import us.poptalks.model.post;
import us.poptalks.model.users;
import us.poptalks.ui.AddFriendActivity;
import us.poptalks.ui.FirstScreen;
import us.poptalks.R;
import us.poptalks.ui.My_QR;
import us.poptalks.ui.NewStatusActivity;
import us.poptalks.ui.ProfileActivity;
import us.poptalks.ui.ProfileActivityFriend;
import us.poptalks.utils.database;
import us.poptalks.utils.values;


public class FriendFragment extends Fragment {
    private FirebaseListAdapter mFriendAdapter;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;
    private String mUsername;
    private ChildEventListener mChildEventListener;
    private ValueEventListener mValueEventListener;

    private ValueEventListener mUserDatabaseReferencecurrentUsergetUidValueEventListener;
    private RecyclerView friendsListView;
    private Button add_friend_button_empty;

    private boolean isAttached;

    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mUserDatabaseReferenceCurrentUser;
    private DatabaseReference mFriendsDatabaseReferenceCurrentUser;
    private DatabaseReference mFriendsDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;

    private RelativeLayout empty_view_friendsList, not_empty_friendsList;

    private ValueEventListener friendsListValueEventListener;

    private FirebaseUser currentUser;
    private LinearLayoutManager linearLayoutManager;

    private TextView user_name, user_custom_id, user_status;
    LinearLayout self, add_friend_floating;
    ImageView user_pro_pic_self_row;
    public FriendFragment() {

    }
View view_;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_ = inflater.inflate(R.layout.fragment_friends, container, false);
        setHasOptionsMenu(true);
        user_name = view_.findViewById(R.id.self_row__profile_name);
        user_custom_id = view_.findViewById(R.id.self_row_profile_id);
        user_status = view_.findViewById(R.id.self_row_last_message);
        user_pro_pic_self_row = view_.findViewById(R.id.self_row_profile_image);

        self = view_.findViewById(R.id.self_row);
        add_friend_floating = view_.findViewById(R.id.add_frients_floating);


        add_friend_button_empty = view_.findViewById(R.id.add_friend_button_empty);
        add_friend_button_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_friend_button();
            }
        });

        linearLayoutManager =new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        not_empty_friendsList = view_.findViewById(R.id.not_empty_friendsList);
        empty_view_friendsList = view_.findViewById(R.id.empty_view_friendsList);
        return view_;

    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mFirebaseDatabase = database.getDatabase();

        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser= mFirebaseAuth.getCurrentUser();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser= firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    onInitialize(view);

                } else {
                    Intent intent = new Intent(getActivity(), FirstScreen.class);
                    getActivity().startActivity(intent);
                }
            }
        };

        if (currentUser != null) {

            onInitialize(view);

        } else {

            Intent intent = new Intent(getActivity(), FirstScreen.class);
            getActivity().startActivity(intent);
        }

        self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivity(intent);

            }
        });
        add_friend_floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_friend_button();
            }
        });
    }


    private void add_friend_button(){
        Intent intent = new Intent(getActivity(), AddFriendActivity.class);
        getActivity().startActivity(intent);
    }
    private void onInitialize(View view) {
        mUsername = currentUser.getDisplayName();
        user_name.setText(currentUser.getDisplayName());
        mFriendsDatabaseReference = mFirebaseDatabase.getReference()
                .child(values.dbFriendsLocation);
        mUserDatabaseReference = mFirebaseDatabase.getReference()
                .child(values.dbUserLocation);
        mUserDatabaseReferencecurrentUsergetUidValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                users me = snapshot.getValue(users.class);
                if(me != null){
                    user_status.setText(me.getMood());
                    user_name.setText(me.getUsername());
                    user_custom_id.setText(me.getPhone());
                    if(isAttached){
                        Glide.with(getActivity().getApplicationContext())
                                .load(me.getProfilePicLocation())
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .bitmapTransform(new CropCircleTransformation(getActivity().getApplicationContext()))
                                .into(user_pro_pic_self_row);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mUserDatabaseReferenceCurrentUser = mUserDatabaseReference.child(currentUser.getUid());
        mUserDatabaseReferenceCurrentUser.addValueEventListener(mUserDatabaseReferencecurrentUsergetUidValueEventListener);
        friendsListView =  view.findViewById(R.id.friendsListView);
        populateListView();
        mFriendsDatabaseReference.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                populateListView();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    FriendsFirebaseRecycler friendsListAdapter;
    int count = 0;
    private void populateListView(){
        mFriendsDatabaseReferenceCurrentUser = mFriendsDatabaseReference.child(currentUser.getUid());//.orderByChild("username");

        friendsListValueEventListener = new ValueEventListener() {
            final List<friends> friendsList= new ArrayList<friends>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot imageSnapshot : dataSnapshot.getChildren()) {

                    friendsList.add(imageSnapshot.getValue(friends.class));
                    count++;
                }
                if(count > 0){

                    friends header_dummy_friends = new friends();

                    header_dummy_friends.setType_("header");
                    friendsList.add(header_dummy_friends);
                    empty_view_friendsList.setVisibility(View.GONE);
                    not_empty_friendsList.setVisibility(View.VISIBLE);


                    friendsListAdapter = new FriendsFirebaseRecycler(friendsList,getContext());

                    friendsListView.setLayoutManager(linearLayoutManager);
                    friendsListView.setItemAnimator(new DefaultItemAnimator());
                    friendsListView.setAdapter(friendsListAdapter);


                }else{
                    empty_view_friendsList.setVisibility(View.VISIBLE);
                    not_empty_friendsList.setVisibility(View.GONE);
                }


                    FirebaseListAdapter<friends> firebase_list_adapter_users = new FirebaseListAdapter<friends>(
                            getActivity(),
                            friends.class,
                            R.layout.friend_item_without_buttons,
                            query2
                    )  {
                        @Override
                        protected void populateView(View v, final friends model, int position) {
                            TextView name = v.findViewById(R.id.friend_list_without_button_item_name);
                            final TextView mood = v.findViewById(R.id.friend_list_without_button_item_status);
                            TextView phone = v.findViewById(R.id.friend_list_without_button_item_user_id);
                            final ImageView invite_friend_user_image = v.findViewById(R.id.message_profile_image_without_button);
                            name.setText(model.getUsername());
                            phone.setText(model.getPhone());
                            final String uid = model.getUid();
                            mUserDatabaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    users temp = dataSnapshot.getValue(users.class);
                                    if(temp == null || temp.getMood() == null || temp.getMood().equals("")){
                                        mood.setText("Feeling Good!");
                                    }else{
                                        mood.setText(temp.getMood());
                                    }
                                    if (temp.getProfilePicLocation() != null && !temp.getProfilePicLocation().equals("")) {

                                        if(isAttached){
                                            Glide.with(getActivity().getApplicationContext())
                                                    .load(temp.getProfilePicLocation())
                                                    .bitmapTransform(new CropCircleTransformation(getActivity().getApplicationContext()))
                                                    .centerCrop()
                                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                    .into(invite_friend_user_image);
                                        }

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });

                            v.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getActivity(), ProfileActivityFriend.class);
                                    intent.putExtra("uid", uid);
                                    startActivity(intent);
                                }
                            });

                        }
                    };
                    friendsListView.setAdapter(firebase_list_adapter_users);


                    empty_view_friendsList.setVisibility(View.GONE);
                    not_empty_friendsList.setVisibility(View.VISIBLE);


                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
            };

        mFriendsDatabaseReferenceCurrentUser.addValueEventListener(friendsListValueEventListener);


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.friends_fragement_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search_friends:
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                intent.putExtra("frag", "search");
                startActivity(intent);
                return true;
            case R.id.edit_friends:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
        Log.v("=x=", "Friends Frag Attached");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("=x=", "Friends frag Detached");
        mUserDatabaseReferenceCurrentUser.removeEventListener(mUserDatabaseReferencecurrentUsergetUidValueEventListener);
        isAttached = false;
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {

        } catch (IllegalAccessException e) {

        }
        mFriendsDatabaseReferenceCurrentUser.removeEventListener(friendsListValueEventListener);
    }
}
