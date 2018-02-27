package info.sayederfanarefin.location_sharing.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import info.sayederfanarefin.location_sharing.R;


/**
 * Created by piashsarker on 7/10/17.
 */
/**
 * Created by schmaedech on 30/06/17.
 */
public class InfoFragment extends Fragment {
    public InfoFragment() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("=x=", "Info frag Detached");
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
        return inflater.inflate(R.layout.fragment_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v("=x=", "Info Frag Attached");
    }
}
