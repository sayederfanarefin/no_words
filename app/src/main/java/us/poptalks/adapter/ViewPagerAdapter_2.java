package us.poptalks.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import us.poptalks.fragment.FriendQRFragment;
import us.poptalks.fragment.InviteFriendFragment;
import us.poptalks.fragment.SearchFriendFragment;


/**
 * Created by piashsarker on 7/10/17.
 */

public class ViewPagerAdapter_2 extends FragmentStatePagerAdapter {

    public ViewPagerAdapter_2(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new InviteFriendFragment();
        } else if (position == 1) {
            return new FriendQRFragment();
        } else {
            return new SearchFriendFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}