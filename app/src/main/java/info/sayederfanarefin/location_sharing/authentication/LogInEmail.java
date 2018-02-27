package info.sayederfanarefin.location_sharing.authentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import info.sayederfanarefin.location_sharing.MainActivity;
import info.sayederfanarefin.location_sharing.R;

public class LogInEmail extends AppCompatActivity {
EditText user_name,user_password;
    Button sign_in, no_email;
    TextView forgot_password, Create_new_account;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        sign_in = (Button) findViewById(R.id.button_login);

        user_name = (EditText) findViewById(R.id.login_email);
        user_name.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);
        user_password = (EditText) findViewById(R.id.login_pass);
        user_password.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);

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

        user_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

//        forgot_password = (TextView) findViewById(R.id.forgot_pass);
//        forgot_password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LogInEmail.this, ForgotPassword.class);
//                startActivity(intent);
//            }
//        });

        auth = FirebaseAuth.getInstance();

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = user_name.getText().toString();
                String pass = user_password.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //authenticate user
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(LogInEmail.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
//                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error

                                        Toast.makeText(LogInEmail.this, "Authentication failed!", Toast.LENGTH_LONG).show();

                                } else {
                                    Intent intent = new Intent(LogInEmail.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
        Create_new_account = (TextView) findViewById(R.id.create_new);
        Create_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInEmail.this, CreateProfile.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
