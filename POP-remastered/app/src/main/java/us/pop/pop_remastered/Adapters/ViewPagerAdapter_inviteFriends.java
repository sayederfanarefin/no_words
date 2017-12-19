package us.pop.pop_remastered.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import us.poptalks.fragment.InviteFriendsContactsFragment;
import us.poptalks.fragment.InviteFriendsEmailFragment;


/**
 * Created by piashsarker on 7/10/17.
 */

public class ViewPagerAdapter_inviteFriends extends FragmentPagerAdapter {

    public ViewPagerAdapter_inviteFriends(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new InviteFriendsContactsFragment();
        }else {
            return new InviteFriendsEmailFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}