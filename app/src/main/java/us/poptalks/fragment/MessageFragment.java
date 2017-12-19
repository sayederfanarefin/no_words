package us.poptalks.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.firebase.storage.StreamDownloadTask;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import us.poptalks.MainActivity;
import us.poptalks.NewChatActivity;
import us.poptalks.adapter.RecentChatsAdapter;
import us.poptalks.model.Chat;
import us.poptalks.model.ChatHeader;
import us.poptalks.model.Message_2;
import us.poptalks.model.users;
import us.poptalks.ui.FirstScreen;
import us.poptalks.R;

import us.poptalks.model.Message;
import us.poptalks.utils.Constants;
import us.poptalks.utils.database;
import us.poptalks.utils.values;


/**
 * Created by schmaedech on 30/06/17.
 */
public class MessageFragment extends Fragment {
    public static final int RC_SIGN_IN = 1;
    private static final String TAG = "MessageFragment";
    private RecyclerView recentChatRecyclerview;
    private ArrayList<Message> recentMessageArrayList;
    private LinearLayoutManager linearLayoutManager;

    private ListView mChatListView;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mChatDatabaseReference;
    private DatabaseReference rootChatDatabaseReference;
    private DatabaseReference messageDatabaseReference;

    DatabaseReference chatdb;

    private TextView name_new_unread;
    private TextView message_new_unread;
    private TextView message_new_unread_count;
    private ImageView pro_pic_new_unread;

    RecentChatsAdapter chatsAdapter;
    RecentChatsAdapter unreadChatsAdapter;

    List<ChatHeader> recent_chats_list;
    private String myUid;

    public MessageFragment() {

    }





    private View view_;
    private LinearLayout new_chat_row;
    LinearLayout floating_new_message;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_ = inflater.inflate(R.layout.fragment_message, container, false);

        mFirebaseDatabase = database.getDatabase();

        floating_new_message = view_.findViewById(R.id.floating_new_message);
        findViews(view_);
        return view_;
    }

    private void findViews(View view) {
        name_new_unread = view.findViewById(R.id.txt_profile_name_new_unread);
        message_new_unread = view.findViewById(R.id.txt_last_message_new_unread);
        message_new_unread_count = view.findViewById(R.id.message_count_new_unread);
        pro_pic_new_unread = view.findViewById(R.id.message_profile_image_new_unread);
        new_chat_row = view.findViewById(R.id.new_chat_row);
        //recentChatRecyclerview = view.findViewById(R.id.recent_chat_recyclerview);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        floating_new_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewChatActivity.class);
                getActivity().startActivity(intent);
            }
        });

    }






    private Map<String, Object> map_chatIds;

    private void onInitialize(FirebaseUser user, View view) {

        myUid = user.getUid();

        mChatDatabaseReference = mFirebaseDatabase.getReference()
                .child(values.dbUserLocation
                        + "/" + user.getUid() + "/"
                        + "chats");

        mChatListView =  view.findViewById(R.id.chatListView666);

        mChatDatabaseReference.orderByChild("chatId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tempMethod(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

private void tempMethod(DataSnapshot dataSnapshot){
    Iterable<DataSnapshot> children= dataSnapshot.getChildren();
    for (DataSnapshot child : children) {

        map_chatIds = (Map<String, Object>) child.getValue();
        getRecentChats();
    }
}


    private void getRecentChats (){

//list of all chat ids
        Set keys = map_chatIds.keySet();
        Iterator i = keys.iterator();
        final List<String> chatIds = new ArrayList<String>();
        while (i.hasNext()){
            String key = (String) i.next();
            String value = (String) map_chatIds.get(key);
            chatIds.add(value);
        }

        chatsAdapter = new RecentChatsAdapter(getActivity(), R.layout.row_recent_chat);
        unreadChatsAdapter = new RecentChatsAdapter(getActivity(), R.layout.row_recent_chat);



        //we have the user's chatids till now. What we need is, the person that the user wants to chat
        //and the last message, also the receivers profile picture

        for (int ii = 0; ii < chatIds.size(); ii++){
            final ChatHeader ch = new ChatHeader();
            final String chat_id = chatIds.get(ii);
            ch.setChatId(chat_id);

            rootChatDatabaseReference.child(chatIds.get(ii)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    final List<String> friends = chat.getFriends();

                    for (int o =0; o < friends.size(); o++){
                        if ( !friends.get(o).equals(myUid)){
                            try{


                            mUserDatabaseReference.child(friends.get(o)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    users temp = dataSnapshot.getValue(users.class);
                                    String name = temp.getUsername();

 //                                   // Log.v("===special case name", name);
                                    ch.setChatName(name);
                                    ch.setImageLocation(temp.getProfilePicLocation());
//                                    // Log.v("===xxx", temp.getProfilePicLocation());

                                    Query lastMessageQuery = messageDatabaseReference.child(chat_id).orderByChild("timestamp").limitToLast(1);
                                    lastMessageQuery.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Message_2 lastMessage ;

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
                                                    Log.v("=xx=", "i've got a new message "+ lastMessage.getContent() + " chat name: "+ ch.getChatName());
                                                }
                                                    ch.setTime(lastMessage.getTimestamp());

                                            }

                                            unreadChatsAdapter.add(ch);
                                            chatsAdapter.add(ch);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    messageDatabaseReference.child(chat_id).addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                            Message_2 lastMessage = dataSnapshot.getValue(Message_2.class);

                                            String cntent = "";
                                            if(lastMessage.getType() != null){
                                                if(lastMessage.getType().equals("text")){
                                                    if(lastMessage.getSender_uid().equals(myUid)){
                                                        cntent = "You: "+ lastMessage.getContent();
                                                    }else{
                                                        cntent = lastMessage.getContent();
                                                        ch.setImageLocation(lastMessage.getSender_image_location());
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

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
                            break;
                            }catch (Exception e ){

                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



        //get new messages count un read


        for (int ii = 0; ii < chatIds.size(); ii++){
            newch = new ChatHeader();
            final String chat_id = chatIds.get(ii);
            newch.setChatId(chat_id);

            rootChatDatabaseReference.child(chatIds.get(ii)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    final List<String> friends = chat.getFriends();

                    for (int o =0; o < friends.size(); o++){
                        if ( !friends.get(o).equals(myUid)){
                            try{

                                mUserDatabaseReference.child(friends.get(o)).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final users temp = dataSnapshot.getValue(users.class);
                                        final String name = temp.getUsername();

                                        //                                   // Log.v("===special case name", name);

//                                    // Log.v("===xxx", temp.getProfilePicLocation());

                                        Query lastMessageQuery = messageDatabaseReference.child(chat_id).orderByChild("message_seen").equalTo("un_seen");
                                        lastMessageQuery.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                int unseencount = 0;
                                                Message_2 lastMessage ;

                                                if(!gotwhatineeded){
                                                    gotwhatineeded = true;
                                                    new_chat_row.setVisibility(View.VISIBLE);
                                                    newch.setChatName(name);
                                                    newch.setImageLocation(temp.getProfilePicLocation());

                                                    for (DataSnapshot snap: dataSnapshot.getChildren()) {

                                                        unseencount++;

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

                                                        newch.setLastMessage(cntent);

                                                        if(lastMessage.getMessage_seen().equals("un_seen") && !lastMessage.getSender_uid().equals(myUid)){
                                                            newch.setSeen("New");
                                                            Log.v("=xx=", "i've got a new message "+ lastMessage.getContent() + " chat name: "+ newch.getChatName());
                                                        }
                                                        newch.setTime(lastMessage.getTimestamp());

                                                    }



                                                    name_new_unread.setText(newch.getChatName());
                                                    message_new_unread.setText(newch.getLastMessage());
                                                    message_new_unread_count.setText(String.valueOf(unseencount));
                                                    Log.v("=x=x",newch.getImageLocation() );
                                                    Glide.with(pro_pic_new_unread.getContext())
                                                            .load(newch.getImageLocation())

                                                            .centerCrop()
                                                            .override(256, 256)
                                                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                            .into(pro_pic_new_unread);


                                                    Log.v("=xx=", "independent unseen message count: "+ String.valueOf(unseencount));

                                                }

                                                if (unseencount ==0){
                                                    new_chat_row.setVisibility(View.GONE);
                                                }

                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        messageDatabaseReference.child(chat_id).addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                Message_2 lastMessage = dataSnapshot.getValue(Message_2.class);

                                                String cntent = "";
                                                if(lastMessage.getType() != null){
                                                    if(lastMessage.getType().equals("text")){
                                                        if(lastMessage.getSender_uid().equals(myUid)){
                                                            cntent = "You: "+ lastMessage.getContent();
                                                        }else{
                                                            cntent = lastMessage.getContent();
                                                            newch.setImageLocation(lastMessage.getSender_image_location());
                                                        }
                                                    }else{
                                                        cntent = "Multimedia";
                                                    }
                                                }else{
                                                    cntent = "Start Chatting...";
                                                }

                                                newch.setLastMessage(cntent);

                                                if(lastMessage.getMessage_seen().equals("un_seen") && !lastMessage.getSender_uid().equals(myUid)){
                                                    newch.setSeen("New");

                                                }
                                                newch.setTime(lastMessage.getTimestamp());

                                            }

                                            @Override
                                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
                                break;
                            }catch (Exception e ){

                            }
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        mChatListView.setAdapter(chatsAdapter);


    }
    private ChatHeader newch;
    private boolean gotwhatineeded = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("=x=", "Message Attached");
        if (mFirebaseAuth != null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("=x=", "Message Detached");
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

        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
