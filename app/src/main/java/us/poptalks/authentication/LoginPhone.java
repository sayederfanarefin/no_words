package us.poptalks.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import us.poptalks.R;


public class LoginPhone extends AppCompatActivity {

    TextView create_profile_login;
    Button button_create_user_send;
    Button login_using_email;
    CountryCodePicker ccp;


    EditText phone;
    Spinner area_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_using_phone);

        create_profile_login = (TextView) findViewById(R.id.create_new);
        create_profile_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPhone.this, CreateProfile.class);
                startActivity(intent);
                finish();
            }
        });
        phone = (EditText) findViewById(R.id.create_profile_phone);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);


        phone.setBackgroundResource( R.drawable.edittexrroundedcorner_gray);


        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        ccp.registerCarrierNumberEditText(phone);

        button_create_user_send = (Button) findViewById(R.id.button_login_user_send);
        login_using_email = (Button) findViewById(R.id.button_login_using_email);

        login_using_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPhone.this, LogInEmail.class);
                startActivity(intent);
                finish();
            }
        });

        button_create_user_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(  phone.getText().toString()!= "" && phone.getText().toString().length() > 2){
                    Intent intent = new Intent(LoginPhone.this, CreateProfileSmsSend.class);
                    intent.putExtra("area_code", "");
                    intent.putExtra("phone_number", ccp.getFullNumberWithPlus());
                    intent.putExtra("login", true);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter phone number.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
