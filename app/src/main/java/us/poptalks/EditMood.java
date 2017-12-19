package us.poptalks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.StringTokenizer;

import us.poptalks.utils.database;

public class EditMood extends AppCompatActivity {
    private Toolbar mToolBar;
    EditText mood_bar;
    TextView char_count;
    int max = 39;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mood);

        mood_bar = (EditText) findViewById(R.id.mood_edit);
        char_count = (TextView) findViewById(R.id.charCountMood);

        mToolBar = (Toolbar) findViewById(R.id.toolbar_settings);
        mToolBar.setTitle("Edit Mood");
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();


        rootDb = database.getDatabase();

        rootRef = rootDb.getReference();
        userRef = rootRef.child("users").child(user.getUid());



        mood_bar.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(max)});

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mood_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int c_count = Integer.valueOf(mood_bar.getText().toString().length());


                char_count.setText(String.valueOf(c_count)+"/" + String.valueOf(max));


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.edit_mood_settings, menu);
        return true;
    }


    //databses
    private DatabaseReference rootRef;
    private FirebaseDatabase rootDb;
    private DatabaseReference userRef;

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.edit_mood_save:

                    if(!mood_bar.getText().toString().equals("")){

                        userRef.child("mood").setValue(mood_bar.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                finish();
                            }
                        });

                    }


                    return true;
                default:
                    return true;
            }
        }

}
