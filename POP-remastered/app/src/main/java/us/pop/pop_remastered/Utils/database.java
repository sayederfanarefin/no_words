package info.sayederfanarefin.location_sharing.pop_remastered.Utils;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by erfanarefin on 28/07/2017.
 */

public class database {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
            // ...
        }

        return mDatabase;

    }

}
