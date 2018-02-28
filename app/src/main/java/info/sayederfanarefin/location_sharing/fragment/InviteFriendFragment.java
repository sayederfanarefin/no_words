package info.sayederfanarefin.location_sharing.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Field;

import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.utils.Constants;


/**
 * Created by piashsarker on 7/10/17.
 */

/**
 * Created by schmaedech on 30/06/17.
 */
public class InviteFriendFragment extends Fragment {


    String user_uid;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    DatabaseReference userDatabase;

    LinearLayout buttonTopMessage, buttonTopGroup;


    public InviteFriendFragment() {

    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            Log.v("=x=", "actvty dstryd");
            // throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // throw new RuntimeException(e);
            Log.v("=x=", "actvty dstryd");
        }
    }

    View invitefriendsview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        invitefriendsview = inflater.inflate(R.layout.fragment_invite_friends, container, false);
        findViews(invitefriendsview);
        buttonTopGroup.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        buttonTopMessage.setBackgroundColor(getResources().getColor(R.color.colorAccentPrimary));


        InviteFriendsContactsFragment contactInviteFreinds = new InviteFriendsContactsFragment();
        fragmentTransaction(contactInviteFreinds);

        return invitefriendsview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
         currentUser = firebaseAuth.getCurrentUser();
        user_uid = currentUser.getUid();

         userDatabase =  FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_URL+"/"+ Constants.USERS_LOCATION);

        buttonTopGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteFriendsEmailFragment inviteFriendsEmailFragment = new InviteFriendsEmailFragment();
                fragmentTransaction(inviteFriendsEmailFragment);
                buttonTopMessage.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                buttonTopGroup.setBackgroundColor(getResources().getColor(R.color.colorAccentPrimary));
            }
        });
        buttonTopMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteFriendsContactsFragment contactInviteFreinds = new InviteFriendsContactsFragment();
                fragmentTransaction(contactInviteFreinds);
                buttonTopGroup.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                buttonTopMessage.setBackgroundColor(getResources().getColor(R.color.colorAccentPrimary));

            }
        });


    }

    private void fragmentTransaction(Fragment groupFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.invite_friend_frame, groupFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void findViews(View view) {
        buttonTopGroup = view.findViewById(R.id.button_top_group);
        buttonTopMessage = view.findViewById(R.id.button_top_message);
    }

}
