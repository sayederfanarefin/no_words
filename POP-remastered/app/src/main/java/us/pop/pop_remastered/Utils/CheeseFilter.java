package us.pop.pop_remastered.Utils;

import android.util.Log;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by erfanarefin on 25/08/2017.
 */

public class CheeseFilter extends Filter {

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {


        constraint = constraint.toString().toLowerCase();

        FilterResults newFilterResults = new FilterResults();

        if (constraint != null && constraint.length() > 0) {


            ArrayList<String> auxData = new ArrayList<String>();

//            for (int i = 0; i < data.size(); i++) {
//                if (data.get(i).toLowerCase().contains(constraint))
//                    auxData.add(data.get(i));
//            }

            newFilterResults.count = auxData.size();
            newFilterResults.values = auxData;
        } else {

//            newFilterResults.count = data.size();
//            newFilterResults.values = data;
        }

        return newFilterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        ArrayList<String> resultData = new ArrayList<String>();

        resultData = (ArrayList<String>) results.values;

        Log.v("-----check", "got filtered data inside cheeses filter");
//        EfficientAdapter adapter = new EfficientAdapter(context, resultData);
//        list.setAdapter(adapter);


    }

}