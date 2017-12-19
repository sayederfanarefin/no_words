package us.poptalks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import us.poptalks.NewChatActivity;
import us.poptalks.NewGroupChatActivity;
import us.poptalks.R;

import us.poptalks.adapter.RecentChatsAdapterGroup;
import us.poptalks.model.Chat;
import us.poptalks.model.ChatHeader;
import us.poptalks.model.ChatHeaderGroup;
import us.poptalks.model.Message;
import us.poptalks.model.Message_2;
import us.poptalks.ui.FirstScreen;
import us.poptalks.utils.database;
import us.poptalks.utils.values;

/**
 * Created by piashsarker on 7/10/17.
 */
/**
 * Created by schmaedech on 30/06/17.
 */
public class GroupFragment extends Fragment {

    private TextView textViewGroupName, groupLastMessage;

    private LinearLayout button_group;

    RecentChatsAdapterGroup chatsAdapter;


    private ListView mChatListView;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mChatDatabaseReference;
    private DatabaseReference rootChatDatabaseReference;
    private DatabaseReference messageDatabaseReference;

    public GroupFragment() {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        mFirebaseDatabase = database.getDatabase();
        findViews(view);
        return view;
    }

    private void findViews(View view) {
        textViewGroupName = view.findViewById(R.id.txt_profile_name);
        groupLastMessage = view.findViewById(R.id.txt_last_message);
        //groupMessageRecentRecyclerView = view.findViewById(R.id.recent_chat_recyclerview);
        textViewGroupName.setText("Jhon, Sarah and Taylor");
        groupLastMessage.setText("It's very charming and enjoyable");
        button_group = view.findViewById(R.id.gb);
        button_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewGroupChatActivity.class);
                getActivity().startActivity(intent);
            }
        });


    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //loadDataToRecentRecylerView(loadRecentMessageData());

        messageDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child(values.dbUserLocation);
        rootChatDatabaseReference = mFirebaseDatabase.getReference().child("chats");

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onInitialize(user, view);
                } else {
                    Intent intent = new Intent(getActivity(), FirstScreen.class);
                    getActivity().startActivity(intent);
                }
            }
        };

    }


    @Override
    public void onResume() {
        super.onResume();
       // Log.e(TAG, "onResume");


        if (mFirebaseAuth != null)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        //Log.e(TAG, "onPause");
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

        super.onPause();
    }

    private Map<String, Object> map_chatIds;
    private String myUid;


    private void onInitialize(FirebaseUser user, View view) {

        myUid = user.getUid();

        mChatDatabaseReference = mFirebaseDatabase.getReference()
                .child(values.dbUserLocation
                        + "/" + user.getUid() + "/"
                        + "groupchats");

        mChatListView =  view.findViewById(R.id.chatListView666);


        chatsAdapter = new RecentChatsAdapterGroup (getActivity(), R.layout.row_recent_chat);

   //     // Log.v("==group", "before addValueEventListener");

        mChatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

      //          // Log.v("==group", "inside addValueEventListener");

                Iterable<DataSnapshot> children= dataSnapshot.getChildren();
                for (DataSnapshot child : children) {

                    final String group_chatid = (String) child.getValue();
                    final ChatHeaderGroup ch = new ChatHeaderGroup();

                    rootChatDatabaseReference.child(group_chatid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Chat c = dataSnapshot.getValue(Chat.class);
                            ch.setChatName(c.getChatName());
                            ch.setChatId(group_chatid);
                            final List<String> friends = c.getFriends();

                            final List<String> url = new ArrayList<String>();

                            for(int x=0; x < friends.size(); x++){
                                mUserDatabaseReference.child(friends.get(x)).child("profilePicLocation").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        url.add(dataSnapshot.getValue(String.class));
                                        if(checker(url, friends.size())){
                                            ch.setImageLocation(url);

//// Log.v("====--", messageDatabaseReference.child(group_chatid).getRef().toString());

                                            Query lastMessageQuery = messageDatabaseReference.child(group_chatid).orderByChild("timestamp").limitToLast(1);
                                            lastMessageQuery.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Message_2 lastMessage ;

                                                    try{

                                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {
                                                        lastMessage = snap.getValue(Message_2.class);
                                                        String cntent = "";
                                                        if(lastMessage.getType() != null){
                                                            if(lastMessage.getType().equals("text")){
                                                                if(lastMessage.getSender_uid().equals(myUid)){
                                                                    cntent = "You: "+ lastMessage.getContent();
                                                                }else{
                                                                    cntent = lastMessage.getContent();
                                                                }
                                                            }else{
                                                                cntent = "Multimedia";
                                                            }
                                                        }else{
                                                            cntent = "Start Chatting...";
                                                        }

                                                        ch.setLastMessage(cntent);

                                                        if(lastMessage.getMessage_seen().equals("un_seen") && !lastMessage.getSender_uid().equals(myUid)){
                                                            ch.setSeen("New");
                                                        }
                                                        ch.setTime(lastMessage.getTimestamp());

                                                    }
                                                    chatsAdapter.add(ch);




//
                                                    }catch (Exception e ){
 //                                                       // Log.v("====---", e.getLocalizedMessage());
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mChatListView.setAdapter(chatsAdapter);

    }

    private boolean checker(List<String> l, int x){
        if (x==l.size()){
            return true;
        }else{
            return false;
        }
    }

}
