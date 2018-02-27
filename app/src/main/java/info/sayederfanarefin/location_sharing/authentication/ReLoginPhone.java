package info.sayederfanarefin.location_sharing.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import info.sayederfanarefin.location_sharing.R;

public class ReLoginPhone extends AppCompatActivity {

    TextView create_profile_login;
    Button button_create_user_send;
    Button login_using_email;
    CountryCodePicker ccp;


    EditText phone;
    Spinner area_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_login_using_phone);

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


        button_create_user_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(  phone.getText().toString()!= "" && phone.getText().toString().length() > 2){

                    Bundle b = getIntent().getExtras();

                    Intent intent = new Intent(ReLoginPhone.this, CreateProfileSmsSend.class);
                    intent.putExtra("area_code", "");
                    intent.putExtra("phone_number", ccp.getFullNumberWithPlus());
                    intent.putExtra("login", true);
                    intent.putExtra("activity", b.getString("activity","none"));

                    startActivityForResult(intent, 1);

                    //finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please enter phone number.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // Log.v("=x=", "calleed");

        if (requestCode == 1) {

            if(resultCode == Activity.RESULT_OK){
                Intent returnIntent = new Intent(getIntent());
                returnIntent.putExtra("result","done");
                //setResult(Activity.RESULT_OK,returnIntent);

                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, returnIntent);
                }
                else {
                    getParent().setResult(Activity.RESULT_OK, returnIntent);
                }

                finish();
            }
        }
    }

}
