package info.sayederfanarefin.location_sharing.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import info.sayederfanarefin.location_sharing.MainActivity;
import info.sayederfanarefin.location_sharing.R;

public class CreateProfileSmsSend extends AppCompatActivity {
EditText pin;
    Button verify;
    TextView resend;
    private FirebaseAuth auth;
    String area_code, phone_number, mVerificationId;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean flag_verify_button_disable = false;
    private ProgressDialog mProgress;
    private boolean login = false;

    private String redirect = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile_sms_send);


        Bundle b = getIntent().getExtras();
        area_code = b.getString("area_code");
        phone_number = b.getString("phone_number");
        login = b.getBoolean("login", false);
        redirect = b.getString("activity", "none");

        auth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone_number,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    onVerificationCallback(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w("tag", "onVerificationFailed", e);

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        // ...
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // ...
                    }

                    // Show a message and update the UI
                    // ...
                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    //Log.d("tag", "onCodeSent:" + verificationId);

                    // Save verification ID and resending token so we can use them later
                   mVerificationId = verificationId;
                   mResendToken = token;

                    // ...
                }
            });        // OnVerificationStateChangedCallbacks

        pin = (EditText) findViewById(R.id.create_profile_pin);
        verify = (Button) findViewById(R.id.button_create_user_verify);
        resend = (TextView) findViewById(R.id.no_resend_code);

        pin.setBackgroundResource( R.drawable.edittexrroundedcorner_focused);
        pin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!flag_verify_button_disable){
                    PhoneAuthCredential credential = new PhoneAuthCredential(mVerificationId, pin.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                }

            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendVerificationCode(phone_number, mResendToken);
            }
        });

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser u = auth.getCurrentUser();
                    mProgress.dismiss();
                    if(u !=null ){

                        if(login){

                            if(redirect.equals("PasswordSet")){

                                Intent returnIntent = new Intent(getIntent());
                                returnIntent.putExtra("result","done");
                                //setResult(Activity.RESULT_OK,returnIntent);

                                if (getParent() == null) {
                                    setResult(Activity.RESULT_OK, returnIntent);
                                }else {
                                    getParent().setResult(Activity.RESULT_OK, returnIntent);
                                }

                                finish();

                            }else{
                                Intent intent = new Intent(CreateProfileSmsSend.this, MainActivity.class);
                                intent.putExtra("phone", phone_number);
                                startActivity(intent);
                                finish();
                            }

                        }else{
                            Intent intent = new Intent(CreateProfileSmsSend.this, InputName.class);
                            intent.putExtra("phone", phone_number);
                            startActivity(intent);
                            finish();
                        }



                    }else{

                    }


                } else {
                    mProgress.dismiss();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(getApplicationContext(), "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
                    }
                }
                }
            });
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {

        Toast.makeText(getApplicationContext(), "Resending..", Toast.LENGTH_SHORT).show();


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            this,               // Activity (for callback binding)
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    onVerificationCallback(credential);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(), "Please try again later.", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Toast.makeText(getApplicationContext(), "Too many requests.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    mVerificationId = verificationId;
                    mResendToken = token;
                }}, token);


    }

    private void onVerificationCallback(PhoneAuthCredential credential){
        pin.setText(credential.getSmsCode());
        flag_verify_button_disable = true;
        mProgress.setMessage("Activating...");
        mProgress.show();
        signInWithPhoneAuthCredential(credential);

    }
}
