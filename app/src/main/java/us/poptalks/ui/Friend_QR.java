package us.poptalks.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import net.glxn.qrgen.android.QRCode;

import us.poptalks.R;


public class Friend_QR extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_qr);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolbar);
        mToolBar.setTitle("User QR Code");
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
        String uid = b.getString("uid");

        Bitmap myBitmap = QRCode.from(uid).withSize(512,512).bitmap();
        ImageView myImage = (ImageView) findViewById(R.id.my_qr);
        myImage.setImageBitmap(myBitmap);

        Button button = (Button) findViewById(R.id.button_scan_qr_reader);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friend_QR.this, AddFriendActivity.class);
                intent.putExtra("frag", "qr_reader");
                startActivity(intent);
            }
        });
    }

}
