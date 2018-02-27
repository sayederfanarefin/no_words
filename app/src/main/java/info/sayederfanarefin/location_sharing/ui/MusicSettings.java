package info.sayederfanarefin.location_sharing.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import info.sayederfanarefin.location_sharing.MainActivity;
import info.sayederfanarefin.location_sharing.R;

public class MusicSettings extends AppCompatActivity {
    private Toolbar mToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_settings);
        mToolBar = (Toolbar) findViewById(R.id.toolbar_settings);
        mToolBar.setTitle("");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void goBack(){
        Intent intent = new Intent(MusicSettings.this, MainActivity.class);
        intent.putExtra("frag", "timeline");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
