package info.sayederfanarefin.location_sharing.pop_remastered.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import info.sayederfanarefin.location_sharing.R;
import info.sayederfanarefin.location_sharing.model.post;
import info.sayederfanarefin.location_sharing.model.timeline_header;
import info.sayederfanarefin.location_sharing.model.timeline_post;
import info.sayederfanarefin.location_sharing.model.users;

/**
 * Created by erfan on 9/14/17.
 */

public class TimelineFirebaseRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<post> postsList;
    private users current_user;
    private Context context;

    public TimelineFirebaseRecycler(List<post> postsList, users current_user, Context context) {
        this.postsList = postsList;
        this.current_user = current_user;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        //Log.v("---xx-",  " inside getItemViewType, postiion->" + String.valueOf(position) +" length->" +  String.valueOf(postsList.size()) );
        if(postsList.size()-1 == position){
            return 1;
        }else{
            return 0;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.timeline_post_layout, parent, false);
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
                timeline_post viewHolder_timeline_post = (timeline_post) holder;

                post post = postsList.get(position);

                viewHolder_timeline_post.setPost_profile_image(current_user.getProfilePicLocation());
                viewHolder_timeline_post.setPost_user_name(post.getUserName());
                viewHolder_timeline_post.setPost_time(post.getPost_time());
                viewHolder_timeline_post.setUserId(current_user.getUid());
                viewHolder_timeline_post.setUserName(current_user.getUsername());
                viewHolder_timeline_post.setPostIdUserId(post.getUser(), post.getPost_id(), context);
                viewHolder_timeline_post.setPost_type(post.getPost_type());

                if (post.getPost_type().equals("image")) {
                    viewHolder_timeline_post.setPost_Image(post.getImage_link(), context);
                } else if (post.getPost_type().equals("link")) {
                    viewHolder_timeline_post.setPost_link(post.getLink(), context);
                }
                viewHolder_timeline_post.setPost_status(post.getStatus_text());

                break;

            case 1:
                timeline_header viewHolder_timeline_header = (timeline_header)holder;

                break;
        }

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

}
