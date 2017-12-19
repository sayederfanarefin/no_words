package us.poptalks.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import us.poptalks.R;
import us.poptalks.ui.NewStatusActivity;

/**
 * Created by erfan on 9/14/17.
 */

public class timeline_header extends RecyclerView.ViewHolder {
    private final LinearLayout link,photo,camera;
    private final EditText status;
    private final ImageView post_pro_pic;


    public timeline_header(View itemView, users current_user, final Context context) {
       super(itemView);

        post_pro_pic = itemView.findViewById(R.id.post_pro_pic);
        link = (LinearLayout) itemView.findViewById(R.id.new_status_link);
        photo = (LinearLayout) itemView.findViewById(R.id.new_status_photo);
        camera = (LinearLayout) itemView.findViewById(R.id.new_status_camera);
        status = (EditText) itemView.findViewById(R.id.status_editText);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewStatusActivity.class);
                intent.putExtra("type", "link");
                context.startActivity(intent);
                //getActivity().finish();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewStatusActivity.class);
                intent.putExtra("type", "gallery");
                context.startActivity(intent);
                //getActivity().finish();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewStatusActivity.class);
                intent.putExtra("type", "camera");
                context.startActivity(intent);
                //getActivity().finish();
            }
        });
//        status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, NewStatusActivity.class);
//                intent.putExtra("type", "text");
//                context.startActivity(intent);
//                //getActivity().finish();
//            }
//        });




        Glide.with(post_pro_pic.getContext())
                .load(current_user.getProfilePicLocation())
                .centerCrop()
                .override(256, 256)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(post_pro_pic);
    }
}
