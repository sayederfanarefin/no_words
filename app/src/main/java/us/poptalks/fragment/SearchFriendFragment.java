package us.poptalks.fragment;

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

import java.lang.reflect.Field;

import us.poptalks.R;


/**
 * Created by piashsarker on 7/10/17.
 */

/**
 * Created by schmaedech on 30/06/17.
 */
public class SearchFriendFragment extends Fragment {

    LinearLayout buttonSearchId, buttonSearchPhone;

    public SearchFriendFragment() {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend_search, container, false);
        findViews(view);
        SearchListFragment messageFragment = new SearchListFragment();
        fragmentTransaction(messageFragment);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonSearchId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchListFragment groupFragment = new SearchListFragment();
                fragmentTransaction(groupFragment);
                buttonSearchPhone.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                buttonSearchId.setBackgroundColor(getResources().getColor(R.color.colorAccentPrimary));
            }
        });
        buttonSearchPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchListPhoneFragment messageFragment = new SearchListPhoneFragment();
                fragmentTransaction(messageFragment);
                buttonSearchId.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                buttonSearchPhone.setBackgroundColor(getResources().getColor(R.color.colorAccentPrimary));

            }
        });
    }
    private void fragmentTransaction(Fragment groupFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.search_frame, groupFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void findViews(View view) {
        buttonSearchId = view.findViewById(R.id.button_search_id);
        buttonSearchPhone = view.findViewById(R.id.button_search_phone);
    }
}
