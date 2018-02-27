package info.sayederfanarefin.location_sharing.pop_remastered.Utils;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by erfan on 10/2/17.
 */

public class StaticAuthChecker {

    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener mAuthStateListener;

    public static void check(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                firebaseAuth.getCurrentUser();
            }
        };
    }

}
