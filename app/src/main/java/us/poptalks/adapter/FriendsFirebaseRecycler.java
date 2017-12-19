package us.poptalks.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import us.poptalks.R;
import us.poptalks.model.friends;
import us.poptalks.model.friends_recycler_view;
import us.poptalks.model.post;
import us.poptalks.model.timeline_header;
import us.poptalks.model.timeline_post;

/**
 * Created by erfan on 9/14/17.
 */

public class FriendsFirebaseRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<friends> friendsList;
    private Context context;

    public FriendsFirebaseRecycler(List<friends> friendsList, Context context) {
        this.friendsList = friendsList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        //Log.v("---xx-",  " inside getItemViewType, postiion->" + String.valueOf(position) +" length->" +  String.valueOf(friendsList.size()) );
        if(friendsList.size()-1 == position){
            return 1;
        }else{
            return 0;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_item_without_buttons, parent, false);
                return new timeline_post(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timeline_header_layout, parent, false);
                return new timeline_header(itemView, current_user, context);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:
                friends_recycler_view viewHolder_friends = (friends_recycler_view) holder;

                friends friends = friendsList.get(position);

                break;

            case 1:
                timeline_header viewHolder_timeline_header = (timeline_header)holder;

                break;
        }

    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

}
