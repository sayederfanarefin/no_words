package info.sayederfanarefin.location_sharing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import info.sayederfanarefin.location_sharing.R;


public class My_phone extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_phone);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("My Number");

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle b = getIntent().getExtras();
        final String uid = b.getString("uid");
        final String phone_number = b.getString("phone");


        Button button = (Button) findViewById(R.id.change_phone_number);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(My_phone.this, My_phone_change.class);
                intent.putExtra("uid", uid);
                intent.putExtra("phone", phone_number);
                startActivity(intent);
            }
        });
    }

}
