package info.sayederfanarefin.location_sharing.ui;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import info.sayederfanarefin.location_sharing.MainActivity;
import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.adapter.ViewPagerAdapter_2;

public class AddFriendActivity extends AppCompatActivity {
    private int[] pageIcon = {R.drawable.ic_invite_friends, R.drawable.ic_qr,  R.drawable.ic_search_friends};

    private TabLayout tabLayout;
    private ViewPager viewPager;
    TextView toolbarText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addfriend);
        setSupportActionBar(toolbar);

        //check for errors-----------------------------------------------------------------------------------------------------------------------toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // finish();
                goBack();
            }
        });
        //---------------------------------------------------------------------------------------------------------------------------------------toolbar

        tabLayout = (TabLayout) findViewById(R.id.tab_layout_add_friend);
        for (int i = 0; i < pageIcon.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setIcon(pageIcon[i]));
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapter_2 pagerAdapter = new ViewPagerAdapter_2(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        toolbarText = (TextView) findViewById(R.id.toolbar_title_add_friend);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                switch (tab.getPosition()) {
                    case 0:
                        toolbarText.setText("Invite");
                        break;
                    case 1:
                        toolbarText.setText("QR Code");
                        break;
                    case 2:
                        toolbarText.setText("Search");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(0);
        toolbarText.setText("Invite");
    }

    public void onResume(){
        super.onResume();

        Bundle b = getIntent().getExtras();
        if(b != null){
            String frag = b.getString("frag", "0");
            switch(frag){

                case "qr_reader":
                    viewPager.setCurrentItem(1);
                    break;

                case "search":
                    viewPager.setCurrentItem(2);
                    break;


                default:
                    viewPager.setCurrentItem(2);
                    break;
            }
        }
    }
    private void goBack(){
        Intent intent = new Intent(AddFriendActivity.this, MainActivity.class);
        intent.putExtra("frag", "friends");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
