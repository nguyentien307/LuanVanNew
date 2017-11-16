package com.example.tiennguyen.luanvannew.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.interfaces.ItemClickListener;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.utils.Constants;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TIENNGUYEN on 11/14/2017.
 */

public class CustomAdapter extends ArrayAdapter<PersonItem>{

    private ArrayList<PersonItem> arrPerson;
    private Context context;
    private SparseBooleanArray expandState = new SparseBooleanArray();

    public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PersonItem> objects) {
        super(context, resource, objects);

        this.context = context;
        this.arrPerson = new ArrayList<PersonItem>(objects);
        for(int i = 0; i < arrPerson.size(); i++){
            expandState.append(i, false);
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        final ViewHolder viewHolder;
        if(itemView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.info_content_item, null);
            viewHolder = new ViewHolder();

            viewHolder.rlTitle = (RelativeLayout) itemView.findViewById(R.id.rl_title);
            viewHolder.tvPersonTitle = (TextView) itemView.findViewById(R.id.tv_person_title);
            viewHolder.rlButton = (RelativeLayout) itemView.findViewById(R.id.rl_button);
            viewHolder.button = (RelativeLayout) itemView.findViewById(R.id.button);

            //expand
            viewHolder.expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);
            viewHolder.ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            viewHolder.tvName = (TextView) itemView.findViewById(R.id.tv_name);
            viewHolder.tvViews = (TextView) itemView.findViewById(R.id.tv_views);
            viewHolder.tvLink = (TextView) itemView.findViewById(R.id.tv_link);
            viewHolder.tvDetail = (TextView) itemView.findViewById(R.id.tv_detail);

            itemView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PersonItem item = arrPerson.get(position);

        viewHolder.tvPersonTitle.setText(item.getName());

        viewHolder.expandableLayout.setInRecyclerView(true);
        viewHolder.expandableLayout.setExpanded(expandState.get(position));
        viewHolder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {

            @Override
            public void onPreOpen() {
                changeRotate(viewHolder.button,0f,180f).start();
                expandState.put(position,true);
            }

            @Override
            public void onPreClose() {
                changeRotate(viewHolder.button,180f,0f).start();
                expandState.put(position,false);
            }
        });
        viewHolder.button.setRotation(expandState.get(position)?180f:0f);
        viewHolder.rlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.expandableLayout.toggle();
            }
        });

        viewHolder.expandableLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.expandableLayout.toggle();
            }
        });

        Glide.with(context).load(R.drawable.avatar)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.item_up)
                .error(R.drawable.item_up)
                .into(viewHolder.ivAvatar);
        viewHolder.tvName.setText(item.getName());
        viewHolder.tvViews.setText("Views: "+ item.getViews());
        viewHolder.tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "xem bai hat", Toast.LENGTH_SHORT).show();
            }
        });
        viewHolder.tvDetail.setText(Constants.ARTIST_INFO);

        return itemView;
    }


    static class ViewHolder{
        RelativeLayout rlTitle, rlButton, button;
        TextView tvPersonTitle, tvName, tvViews, tvLink, tvDetail;
        ImageView ivAvatar;
        ExpandableLinearLayout expandableLayout;
        ItemClickListener itemClickListener;
    }

    private ObjectAnimator changeRotate(RelativeLayout button, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "rotation", from, to );
        animator.setDuration(400);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }
}
