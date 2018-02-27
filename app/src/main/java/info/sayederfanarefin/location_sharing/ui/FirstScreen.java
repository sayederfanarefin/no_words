package info.sayederfanarefin.location_sharing.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.authentication.CreateProfile;
import info.sayederfanarefin.location_sharing.authentication.LoginPhone;

public class FirstScreen extends AppCompatActivity {

    Button sign_in, sign_up;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        sign_in = (Button) findViewById(R.id.button_login_2);
        sign_up = (Button) findViewById(R.id.button_sign_up_2);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstScreen.this, LoginPhone.class);
                startActivity(intent);
                finish();
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstScreen.this, CreateProfile.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
