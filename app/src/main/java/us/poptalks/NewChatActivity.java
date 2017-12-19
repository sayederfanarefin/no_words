package us.poptalks;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import us.poptalks.model.Chat;
import us.poptalks.model.users;
import us.poptalks.ui.ProfileActivityFriend;

public class NewChatActivity extends AppCompatActivity {

    EditText search_bar;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mFriendsDatabaseReference;
    private FirebaseUser currentUser;
    ListView listView_id;
    SpannableStringBuilder sb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // finish();
                goBack();
            }
        });

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               newChat(added_users);
//            }
//        });

        search_bar  = (EditText) findViewById(R.id.id_search_chat_friend);
        sb = new SpannableStringBuilder();

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/users");
        mFriendsDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/friends");
        listView_id = (ListView) findViewById(R.id.firend_search_list_id);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                R.layout.friend_item_direct_send_message,
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
                    Glide.with(getApplicationContext())
                            .load(model.getProfilePicLocation())
                            .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                            .into(invite_friend_user_image);
                }


               // final Button b = v.findViewById(R.id.addFriend);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                      //  b.setVisibility(View.INVISIBLE);
                        addToChat(model);
                        newChat(added_users);

                    }
                });

            }
        };


        listView_id.setAdapter(firebase_list_adapter_users);


    }
    public TextView createContactTextView(String text){
        //creating textview dynamically
        TextView tv = new TextView(this);
        tv.setText("  "+text+"  ");
        tv.setTextSize(getResources().getDimension(R.dimen.textsizechatbubblename));
        tv.setBackgroundResource(R.drawable.chat_name_bubble_head);
        tv.setPadding(48,48,48,48);
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_cross, 0);

        return tv;
    }

    public static Object convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(viewBmp);

    }
    private void search( CharSequence texto ){ //}, FirebaseUser user, View view) {

        String deal_with = texto.toString();
//
//        // Log.v("=====vvvimp", "---texto: "+ texto.toString());
//        // Log.v("=====vvvimp", "StringLength: "+ String.valueOf(StringLength));

        if(StringLength > 0){

            try {
                deal_with = texto.toString().substring(StringLength, search_bar.getText().toString().length());
            }catch (Exception e ){
                Toast.makeText(this, "Sorry, we couldn't find it.", Toast.LENGTH_SHORT);
            }

        }

        String[] words = deal_with.split(" ");
        StringBuilder sb = new StringBuilder();
        if (words[0].length() > 0) {
            sb.append(Character.toUpperCase(words[0].charAt(0)) + words[0].subSequence(1, words[0].length()).toString().toLowerCase());
            for (int i = 1; i < words.length; i++) {
                sb.append(" ");
                sb.append(Character.toUpperCase(words[i].charAt(0)) + words[i].subSequence(1, words[i].length()).toString().toLowerCase());
            }
        }
        deal_with =  sb.toString();
//
//        // Log.v("=====vvvimp", "deal_with: "+ deal_with);

        Query query = mFriendsDatabaseReference.child(currentUser.getUid()).orderByChild("username").startAt(deal_with).endAt("~");

        FirebaseListAdapter<users> firebase_list_adapter_users = new FirebaseListAdapter<users>(
                this,
                users.class,
                R.layout.friend_item_direct_send_message,
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
                        Glide.with(invite_friend_user_image.getContext())
                                .load(model.getProfilePicLocation())
                                .bitmapTransform(new CropCircleTransformation(getApplicationContext()))
                                .into(invite_friend_user_image);
                    }

               // final Button b = v.findViewById(R.id.addFriend);
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                       // b.setVisibility(View.INVISIBLE);
                        addToChat(model);
                        newChat(added_users);

                    }
                });

            }
        };

        listView_id.setAdapter(firebase_list_adapter_users);

    }
    String contactName;
    private void addToChat(users u){

       // String temp = search_bar.getText() + u.getUsername()+", ";
        //StringLength = temp.length();
        //search_bar.setText(temp);

        added_users.add(u.getUid());

//bubble head
        contactName = u.getUsername();

        //final Editable e = search_bar.getEditableText();

        TextView tv = createContactTextView(contactName);

        BitmapDrawable bd = (BitmapDrawable) convertViewToDrawable(tv);
        bd.setBounds(0, 0, bd.getIntrinsicWidth(),bd.getIntrinsicHeight());

        sb.append(contactName + " ");
        sb.setSpan(new ImageSpan(bd), sb.length()-(contactName.length()+1), sb.length()-1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        search_bar.setText(sb);

   //--end of bubble head
        StringLength =  sb.length();

    }

    int StringLength = 0;
    List<String> added_users = new ArrayList<String>();

    private String chatKey;
    private void newChat(final List<String> added_users1){


        added_users1.add(currentUser.getUid());


        if(added_users1.size()==2){
            Query q1 = mUserDatabaseReference
                    .child(added_users1.get(0))
                    .child("chats")
                    .child(added_users1.get(1))
                    .child("chatId");
            q1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    chatKey = dataSnapshot.getValue(String.class);
                    afterChatKeyRetrival(chatKey, added_users1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    private void openChatBox(String pushRefKey, String ChatName){

 //       // Log.v("=====xxx", pushRefKey);
        Intent i = new Intent(NewChatActivity.this, ChatBox_.class);
        i.putExtra("chatID",pushRefKey );
        i.putExtra("chatName",contactName);
        startActivity(i);
        finish();
    }

    private void afterChatKeyRetrival(final String chatKey, final List<String> added_users1){
        if(chatKey==null){
 //           // Log.v("=====xxx", "chatKeyNotFound ");
            final DatabaseReference chatDbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://pop-free-emoji-texting.firebaseio.com/chats");

            final Chat c = new Chat("", added_users1);
            final DatabaseReference pushRef = chatDbRef.push();
            c.setUid(pushRef.getKey());
            pushRef.setValue(c).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(added_users1.size()==2){

                        final HashMap<String, String> a = new HashMap<String, String>();
                        a.put("chatId",c.getUid() );

                        mUserDatabaseReference
                                .child(added_users1.get(0))
                                .child("chats")
                                .child(added_users1.get(1))
                                .setValue(a).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mUserDatabaseReference
                                        .child(added_users1.get(1))
                                        .child("chats")
                                        .child(added_users1.get(0))
                                        .setValue(a).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        openChatBox(pushRef.getKey(), c.getChatName());
                                    }
                                });
                            }
                        });
 //                       // Log.v("=====xxx", "2 users chatKeyNotFound, created new chatKey: "+ pushRef.getKey());
                    }else{
 //                       // Log.v("=====xxx", "multiple users chatKeyNotFound, created new chatKey: "+ pushRef.getKey());
                        openChatBox(pushRef.getKey(), c.getChatName());
                    }
                }
            });
        }else{
            //getting the chat name
 //           // Log.v("=====xxx", "2 users chatKeyFound, opening next Activity"+ chatKey);

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

    private void goBack(){
        Intent intent = new Intent(NewChatActivity.this, MainActivity.class);
        intent.putExtra("frag", "messages");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

}
