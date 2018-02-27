package info.sayederfanarefin.location_sharing.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import info.sayederfanarefin.location_sharing.R;

/**
 * Created by erfanarefin on 30/08/2017.
 */

public class friends_recycler_view extends RecyclerView.ViewHolder {
    final TextView name;
    final TextView mood;
    final TextView phone;
    final ImageView invite_friend_user_image;


    public friends_recycler_view(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.friend_list_without_button_item_name);
        mood = itemView.findViewById(R.id.friend_list_without_button_item_status);
        phone = itemView.findViewById(R.id.friend_list_without_button_item_user_id);
        invite_friend_user_image = itemView.findViewById(R.id.message_profile_image_without_button);
    }

}
