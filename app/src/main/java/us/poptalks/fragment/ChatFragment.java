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
public class ChatFragment extends Fragment {

    LinearLayout buttonTopMessage, buttonTopGroup;

    public ChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        findViews(view);
        MessageFragment messageFragment = new MessageFragment();
        fragmentTransaction(messageFragment);

        buttonTopGroup.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        buttonTopMessage.setBackgroundColor(getResources().getColor(R.color.colorAccentPrimary));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        messages();

        buttonTopGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupFragment groupFragment = new GroupFragment();
                fragmentTransaction(groupFragment);
                buttonTopMessage.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                buttonTopGroup.setBackgroundColor(getResources().getColor(R.color.colorAccentPrimary));
            }
        });
        buttonTopMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messages();

            }
        });


    }

    private void messages(){
        MessageFragment messageFragment = new MessageFragment();
        fragmentTransaction(messageFragment);
        buttonTopGroup.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        buttonTopMessage.setBackgroundColor(getResources().getColor(R.color.colorAccentPrimary));
    }
    private void fragmentTransaction(Fragment groupFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.message_frame, groupFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void findViews(View view) {
        buttonTopGroup = view.findViewById(R.id.button_top_group);
        buttonTopMessage = view.findViewById(R.id.button_top_message);
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
}
