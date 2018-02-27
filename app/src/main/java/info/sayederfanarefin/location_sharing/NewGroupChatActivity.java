package info.sayederfanarefin.location_sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import info.sayederfanarefin.location_sharing.model.Chat;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import mabbas007.tagsedittext.TagsEditText;
import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.model.users;

public class NewGroupChatActivity extends AppCompatActivity implements TagsEditText.TagsEditListener{

    TagsEditText search_bar;
    private String myName;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mFriendsDatabaseReference;
    private FirebaseUser currentUser;
    ListView listView_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group_box2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  finish();
                goBack();
            }
        });


        search_bar  = (TagsEditText) findViewById(R.id.id_search_chat_friend);
        search_bar.setTagsListener(this);
        search_bar.setTagsWithSpacesEnabled(true);
        search_bar.setThreshold(1);


        mUserDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/users");



        mFriendsDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/friends");
        listView_id = (ListView) findViewById(R.id.firend_search_list_id);


        ImageButton send_message_group = (ImageButton) findViewById(R.id.send_message_group);
        send_message_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(added_users.size()>=2){
                   List<String> added_users_name = search_bar.getTags();
                   added_users_name.add(myName);
                   newChat(added_users, added_users_name);
               }else{
                   Toast.makeText(NewGroupChatActivity.this, "Please select at least 2 friends to start a group chat", Toast.LENGTH_SHORT);

               }

            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUserDatabaseReference.child(currentUser.getUid()).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myName = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

        Query query2 = mFriendsDatabaseReference.child(currentUser.getUid()).orderByChild("username").limitToFirst(100);

        FirebaseListAdapter<users> firebase_list_adapter_users = new FirebaseListAdapter<users>(
                this,
                users.class,
                R.layout.friend_item,
                query2
        ) {
            @Override
            protected void populateView(View v, final users model, int position) {

                TextView invite_friend_user_name = v.findViewById(R.id.invite_friend_user_name);
                TextView invite_friend_user_id = v.findViewById(R.id.invite_friend_user_id);
                invite_friend_user_id.setText(model.getPhone());
                invite_friend_user_name.setText(model.getUsername());
                ImageView invite_friend_user_image = v.findViewById(R.id.invite_friend_user_image);
                if (model.getProfilePicLocation() != null && model.getProfilePicLocation() != "") {
                    Glide.with(invite_friend_user_image.getContext())
                            .load(model.getProfilePicLocation())
                            .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                            .into(invite_friend_user_image);
                }


                final Button b = v.findViewById(R.id.addFriend);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        b.setVisibility(View.INVISIBLE);
                        addToChat(model);

                    }
                });

            }
        };


        listView_id.setAdapter(firebase_list_adapter_users);

    }


    private void search( CharSequence texto ){ //}, FirebaseUser user, View view) {

        String deal_with = texto.toString();

//        // Log.v("=====vvvimp", "---texto: "+ texto.toString());
//        // Log.v("=====vvvimp", "StringLength: "+ String.valueOf(StringLength));

        if(StringLength > 0){

            try {
                deal_with = texto.toString().substring(StringLength, search_bar.getText().toString().length());
            }catch (Exception e ){
                Toast.makeText(this, "Sorry, we couldn't find it.", Toast.LENGTH_SHORT);
            }

        }
    //    // Log.v("=====vvvimp", "deal_with (before capt): "+ deal_with);
        String[] words = deal_with.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words.length >0 && words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        deal_with =  sb.toString();

    //    // Log.v("=====vvvimp", "deal_with: "+ deal_with);

        Query query = mFriendsDatabaseReference.child(currentUser.getUid()).orderByChild("username").startAt(deal_with).endAt("~");

        FirebaseListAdapter<users> firebase_list_adapter_users = new FirebaseListAdapter<users>(
                this,
                users.class,
                R.layout.friend_item,
                query
        ) {
            @Override
            protected void populateView(View v, final users model, int position) {

                    TextView invite_friend_user_name = v.findViewById(R.id.invite_friend_user_name);
                    TextView invite_friend_user_id = v.findViewById(R.id.invite_friend_user_id);
                    invite_friend_user_id.setText(model.getPhone());
                    invite_friend_user_name.setText(model.getUsername());
                    ImageView invite_friend_user_image = v.findViewById(R.id.invite_friend_user_image);
                    if (model.getProfilePicLocation() != null && model.getProfilePicLocation() != "") {
                        Glide.with(getApplicationContext())
                                .load(model.getProfilePicLocation())
                                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                .into(invite_friend_user_image);
                    }

                final Button b = v.findViewById(R.id.addFriend);

                if(added_users.contains(model.getUid())){
                    b.setVisibility(View.INVISIBLE);
                }else{
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            b.setVisibility(View.INVISIBLE);
                            addToChat(model);

                        }
                    });
                }


            }
        };

        listView_id.setAdapter(firebase_list_adapter_users);

    }

    @Override
    public void onTagsChanged(Collection<String> tags) {


        if(Arrays.toString(tags.toArray()).length() == 2){
            StringLength = 0;
            added_users.clear();
        }else{
            StringLength = Arrays.toString(tags.toArray()).length() - tags.size();
            List<String> temp = search_bar.getTags();

            added_users.clear();

            for(int g =0; g < temp.size(); g++){
                String key = temp.get(g);

                for(int j =0;j < added_users_obj.size(); j++){
                    if(added_users_obj.get(j).getUsername().equals(key)){
                        added_users.add(added_users_obj.get(j).getUid());
                        break;
                    }
                }
            }
        }
//
//        // Log.v("=====", "Tags changed: ");
//        // Log.v("=====", Arrays.toString(tags.toArray()));
    }

    @Override
    public void onEditingFinished() {

    }

    private void addToChat(users u){

        added_users.add(u.getUid());
        added_users_obj.add(u);
        List<String> temp = search_bar.getTags();
        String[] temp_array = new String[temp.size()+1];
        for(int g =0; g < temp.size(); g++){
            temp_array[g] = temp.get(g);
        }
        temp_array[temp.size()] = u.getUsername();
        search_bar.setTags(temp_array);

        //StringLength = Arrays.toString(temp_array).length()-1 + temp_array.length-1;

//
//        //bubble head
//        String contactName = u.getUsername();
//
//        //final Editable e = search_bar.getEditableText();
//
//        TextView tv = createContactTextView(contactName);
//
//        BitmapDrawable bd = (BitmapDrawable) convertViewToDrawable(tv);
//        bd.setBounds(0, 0, bd.getIntrinsicWidth(),bd.getIntrinsicHeight());
//
//        sb.append(contactName + " ");
//
//        final int i = sb.length()-(contactName.length()+1);
//        final int j = sb.length()-1;
//        sb.setSpan(new ImageSpan(bd), i, j ,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        sb.setSpan(new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                sb.delete(i, j+1);
//                search_bar.setText(sb);
//
//                //-- strunglenth
//                StringLength = sb.length();
//
//                //remove from users list
//
//
//            }
//        },i, j, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        search_bar.setText(sb);
//
//   //--end of bubble head
//        StringLength =  sb.length();
//
//        for(int o=0; o <added_users.size(); o++){
//            // Log.v("=====--", added_users.get(o));
//        }

    }

    int StringLength = 0;
    List<String> added_users = new ArrayList<String>();
    List<users> added_users_obj = new ArrayList<users>();

    private String chatKey;
    private void newChat(final List<String> added_users1,  List<String> added_users_name ){


            added_users1.add(currentUser.getUid());
        afterChatKeyRetrival(null, added_users1, added_users_name );

//                Query q1 = mUserDatabaseReference
//                        .child(added_users1.get(0))
//                        .child("chats")
//                        .child(added_users1.get(1))
//                        .child("chatId");
//                q1.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        chatKey = dataSnapshot.getValue(String.class);
//                        afterChatKeyRetrival(chatKey, added_users1);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });



    }

    private void openChatBox(String pushRefKey, String ChatName){

     //   // Log.v("=====xxx", pushRefKey);
        Intent i = new Intent(NewGroupChatActivity.this, ChatBox_.class);
        i.putExtra("chatID",pushRefKey );
        i.putExtra("chatName",ChatName);
        startActivity(i);
        finish();
    }

    private String group_chat_name_builder(final List<String> added_users){

        StringBuilder sbb = new StringBuilder();

        for(int i =0; i < added_users.size(); i++){
            String[] name_parts = added_users.get(i).split(" ");
            sbb.append(name_parts[0]);
            if(i!= added_users.size()-1){
                sbb.append(", ");
            }
        }
        return sbb.toString();
    }
    int confirmationFlag = 0;
    private void afterChatKeyRetrival(final String chatKey, final List<String> added_users1,  List<String> added_users_name ){
        if(chatKey==null){
        //    // Log.v("=====xxx", "chatKeyNotFound ");
            final DatabaseReference chatDbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/chats");

            final Chat c = new Chat(group_chat_name_builder(added_users_name), added_users1);
            final DatabaseReference pushRef = chatDbRef.push();
            c.setUid(pushRef.getKey());
            pushRef.setValue(c).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    final HashMap<String, String> a = new HashMap<String, String>();
                    a.put(c.getUid(),c.getUid() );


                    for(int i =0; i < added_users.size(); i++){
                        mUserDatabaseReference
                                .child(added_users1.get(i))
                                .child("groupchats")
                                .push()
                                .setValue(c.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                confirmationFlag++;
                                readyToOpenChtBox(pushRef.getKey(), c.getChatName(), confirmationFlag, added_users.size());
                            }
                        });
                    }



                }
            });
        }else{
            //getting the chat name
         //   // Log.v("=====xxx", "2 users chatKeyFound, opening next Activity"+ chatKey);

            DatabaseReference chatDbRef2 = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/chats/"+chatKey);

            Query q1 = chatDbRef2.child("chatName");
            q1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String chatName = "";
                    chatName = dataSnapshot.getValue(String.class);
                    openChatBox(chatKey, chatName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    private void readyToOpenChtBox(String pushkey, String chatname, int confirmationFlagCount, int total){

        if(total == confirmationFlagCount){
            openChatBox(pushkey, chatname);
        }

    }

    private void goBack(){
        Intent intent = new Intent(NewGroupChatActivity.this, MainActivity.class);
        intent.putExtra("frag", "messages");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
