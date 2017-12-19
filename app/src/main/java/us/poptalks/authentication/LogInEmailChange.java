package us.poptalks.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import us.poptalks.MainActivity;
import us.poptalks.R;
import us.poptalks.model.users;
import us.poptalks.utils.values;

public class LogInEmailChange extends AppCompatActivity {
EditText user_name,user_password;
    Button sign_in, no_email;
    TextView forgot_password, Create_new_account;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        root = FirebaseDatabase.getInstance();
        sign_in = (Button) findViewById(R.id.button_login);

        user_name = (EditText) findViewById(R.id.login_email);
        user_name.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);
//        user_password = (EditText) findViewById(R.id.login_pass);
//        user_password.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);

//        no_email = (Button) findViewById(R.id.button_no_registered_email);
//        no_email.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LogInEmail.this, CreateProfile.class);
//                startActivity(intent);
//                finish();
//            }
//        });

        user_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        usersDb = root
                .getReference().child(values.dbUserLocation
                        + "/" + user.getUid());


        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_me = dataSnapshot.getValue(users.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = user_name.getText().toString();
               // String pass = user_password.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }else{

                    usersDb.child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Email added! Please add a password for email log in.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });
        Create_new_account = (TextView) findViewById(R.id.create_new);
//        Create_new_account.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LogInEmailChange.this, CreateProfile.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }
    users user_me;
    private FirebaseDatabase root;
    private DatabaseReference usersDb;
}
