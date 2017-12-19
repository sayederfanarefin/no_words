package us.poptalks.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import us.poptalks.MainActivity;
import us.poptalks.NewChatActivity;
import us.poptalks.R;

public class Settings extends AppCompatActivity {
    private Toolbar mToolBar;

    @BindView(R.id.settings_edit_profile) LinearLayout settings_edit_profile;
    @BindView(R.id.settings_about) LinearLayout settings_about;
    @BindView(R.id.settings_chats_and_calls) LinearLayout settings_chats_and_calls;
    @BindView(R.id.settings_friends) LinearLayout settings_friends;
    @BindView(R.id.settings_my_account) LinearLayout settings_my_account;
    @BindView(R.id.settings_timeline) LinearLayout settings_timeline;
    @BindView(R.id.settings_notifications) LinearLayout settings_notifications;
    @BindView(R.id.settings_photos) LinearLayout settings_photos;
    @BindView(R.id.settings_privacy) LinearLayout settings_privacy;
    @BindView(R.id.settings_help) LinearLayout settings_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        Intent intent = new Intent(Settings.this, MainActivity.class);
        intent.putExtra("frag", "messages");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
