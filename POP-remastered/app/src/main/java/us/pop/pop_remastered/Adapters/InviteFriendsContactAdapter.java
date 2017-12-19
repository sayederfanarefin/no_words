package us.pop.pop_remastered.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import us.poptalks.R;
import us.poptalks.model.invite_contacts;
import us.poptalks.utils.CheeseFilter;

/**
 * Created by erfanarefin on 27/07/2017.
 */

public class InviteFriendsContactAdapter extends ArrayAdapter<invite_contacts> implements Filterable {
    Context mContext;
    private CheeseFilter filter;

    ArrayList<invite_contacts> contacts ;//= new ArrayList<String>();
    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public InviteFriendsContactAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;

        contacts = new ArrayList<invite_contacts>();
    }

    @Override
    public void add(invite_contacts object)
    {
         super.add(object);
        contacts.add(object);
    }

    @Override
    public Filter getFilter() {
        // return a filter that filters data based on a constraint

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = contacts;
                    results.count = contacts.size();
                }
                else {
                    // We perform filtering operation
                    List<invite_contacts> ncontacts = new ArrayList<invite_contacts>();

                    for (invite_contacts p : contacts) {
                        if (p.getName().toUpperCase()
                                .contains(constraint.toString().toUpperCase()))
                            ncontacts.add(p);

                        if (p.getContact().toUpperCase()
                                .contains(constraint.toString().toUpperCase()))
                            ncontacts.add(p);
                    }

                    results.values = ncontacts;
                    results.count = ncontacts.size();
                }


                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0){
                    notifyDataSetInvalidated();
                    Log.v("-----xx", "count 0");
                }else {

                    ArrayList<invite_contacts> contacts2 = (ArrayList<invite_contacts>) results.values;
                    InviteFriendsContactAdapter.super.clear();

                    for(int i =0; i < contacts2.size(); i++){
                        InviteFriendsContactAdapter.super.add(contacts2.get(i));
                    }
                    //Log.v("-----xx", "count " +String.valueOf(contacts.size()));

                    notifyDataSetChanged();
                }
            }
        };
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final invite_contacts currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.row_invite_friend_contact, parent, false);
        }

        row.setTag(currentItem);
        final TextView name = (TextView) row.findViewById(R.id.invite_friend_name);
        final TextView contact = (TextView) row.findViewById(R.id.invite_friend_contact);
        final CheckBox checkBox = (CheckBox) row.findViewById(R.id.checkBox_send_invitation_invite_friend_contact);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    currentItem.setChecked();
                }
            }
        });
        name.setText(currentItem.getName());
        contact.setText(currentItem.getContact());

        return row;
    }
}