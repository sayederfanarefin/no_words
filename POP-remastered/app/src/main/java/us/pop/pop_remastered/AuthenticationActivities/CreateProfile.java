package info.sayederfanarefin.location_sharing.pop_remastered.AuthenticationActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import butterknife.BindView;
import info.sayederfanarefin.location_sharing.pop_remastered.R;

public class CreateProfile extends AppCompatActivity {

    @BindView(R.id.create_profile_login) TextView create_profile_login;
    @BindView(R.id.button_create_user_send) Button button_create_user_send;
    @BindView(R.id.create_profile_phone) EditText phone;

    CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authentication_activities_create_profile2);

        create_profile_login.setText(Html.fromHtml("<u>Login</u>"));
        create_profile_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateProfile.this, LoginPhone.class);
                startActivity(intent);
                finish();
            }
        });

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

        button_create_user_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(  phone.getText().toString()!= "" && phone.getText().toString().length() > 2){
                    Intent intent = new Intent(CreateProfile.this, CreateProfileSmsSend.class);
                    intent.putExtra("area_code", "");
                    intent.putExtra("phone_number", ccp.getFullNumberWithPlus());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter phone number.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
