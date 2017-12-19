package us.poptalks.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import us.poptalks.EditMood;
import us.poptalks.R;
import us.poptalks.model.users;
import us.poptalks.ui.ProfileActivity;
import us.poptalks.utils.values;

public class PasswordSet extends AppCompatActivity {

    users user_me;
    private FirebaseDatabase root;
    private DatabaseReference usersDb;
    boolean flag = false;

    EditText user_new_pass, user_new_pass_confirm;
    Button send;
    FirebaseAuth auth;
    AuthCredential credential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_set);

        root = FirebaseDatabase.getInstance();

//        FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usersDb = root
                .getReference().child(values.dbUserLocation
                        + "/" + user.getUid());

        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_me = dataSnapshot.getValue(users.class);
                flag = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        user_new_pass = (EditText) findViewById(R.id.forgot_password_email);
        user_new_pass_confirm = (EditText) findViewById(R.id.forgot_password_email_2);

        user_new_pass.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);
        user_new_pass_confirm.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);

        user_new_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        user_new_pass_confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        auth = FirebaseAuth.getInstance();

        send = (Button) findViewById(R.id.button_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pass = user_new_pass.getText().toString();

                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(user_new_pass.getText().toString().equals(user_new_pass_confirm.getText().toString())){

                        String emai1 = user_me.getEmail();

                        credential = EmailAuthProvider.getCredential(emai1, user_new_pass.getText().toString());

                        //login for renewing creds

                        Intent intent = new Intent(PasswordSet.this, ReLoginPhone.class);
                        intent.putExtra("activity", "PasswordSet");
                        startActivityForResult(intent,1);


                    }else{
                        Toast.makeText(getApplicationContext(), "Passwords do not match! Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }


//                auth.sendPasswordResetEmail(email)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(getApplicationContext(), "Email sent!", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(getApplicationContext(), "Something went wrong! May be you have entered wrong email", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   //     // Log.v("=x=", "calleed");
        String resulto=data.getStringExtra("result");

        if (requestCode == 1) {

         //   // Log.v("=x=", "activity result request code, result code: "+String.valueOf(resultCode)+" data: "+data.getStringExtra("result"));

            if(resultCode == Activity.RESULT_OK){

 //               // Log.v("=x=", "activity result ok");

                String result=data.getStringExtra("result");
                if(result.equals("done")){


//                    // Log.v("=x=", "result done");

                    FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                            .addOnCompleteListener(PasswordSet.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

//                                    // Log.v("=x=", "on complete");


                                    if (task.isSuccessful()) {

                                        FirebaseUser user = task.getResult().getUser();
                                        Toast.makeText(PasswordSet.this, "Done!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();

                                    } else {

 //                                       // Log.v("=x=", task.getException().toString());
                                        Toast.makeText(PasswordSet.this, "Something went wrong! Please try again later.",
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                }
            }else{
//                // Log.v("=x=", "else intent data 1");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
 //               // Log.v("=x=", "RESULT_CANCELED: there's no result");
            }
        }else{
 //           // Log.v("=x=", "else intent data 1");
        }
    }
}
