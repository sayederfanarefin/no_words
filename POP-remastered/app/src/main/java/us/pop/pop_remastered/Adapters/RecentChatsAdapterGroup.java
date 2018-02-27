package info.sayederfanarefin.location_sharing.pop_remastered.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import info.sayederfanarefin.location_sharing.ChatBox_;
import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.model.ChatHeaderGroup;

/**
 * Created by erfanarefin on 27/07/2017.
 */

public class RecentChatsAdapterGroup extends ArrayAdapter<ChatHeaderGroup> {
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;

    public RecentChatsAdapterGroup(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        final ChatHeaderGroup currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.row_recent_chat, parent, false);
        }

        row.setTag(currentItem);
        final TextView txt_profile_name = (TextView) row.findViewById(R.id.txt_profile_name);
        final TextView txt_last_message = (TextView) row.findViewById(R.id.txt_last_message);
        final TextView txt_date_time = (TextView) row.findViewById(R.id.txt_date_time);
        final TextView seen = (TextView) row.findViewById(R.id.seen);

       // final CardView the_row = (CardView) row.findViewById(R.id.row_recent_chat_container_card_view);


        final ImageView message_profile_image = (ImageView) row.findViewById(R.id.message_profile_image);

        txt_last_message.setText(currentItem.getLastMessage());
        txt_profile_name.setText(currentItem.getChatName());
        txt_date_time.setText(currentItem.getTime());
        //seen.setText(currentItem.getSeen());

        Glide.with(message_profile_image.getContext())
                .load(currentItem.getImageLocation().get(0))
                .bitmapTransform(new CropCircleTransformation(mContext))
                .centerCrop()
                .override(256, 256)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(message_profile_image);


        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext.getApplicationContext(), ChatBox_.class);
                i.putExtra("chatID", currentItem.getChatId());
                i.putExtra("chatName", currentItem.getChatName());
                mContext.startActivity(i);
            }
        });

        return row;
    }
}