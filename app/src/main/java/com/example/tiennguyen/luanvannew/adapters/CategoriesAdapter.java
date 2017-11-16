package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.fragments.MusicAlbumsFm;
import com.example.tiennguyen.luanvannew.fragments.MusicSongsFm;
import com.example.tiennguyen.luanvannew.models.CategoryItem;
import com.example.tiennguyen.luanvannew.utils.Constants;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private ArrayList<CategoryItem> arrCategories;
    private Context context;
    private Activity activity;
    private String type;

    public CategoriesAdapter(Context context, Activity activity, ArrayList<CategoryItem> arrCategories, String type ){
        this.arrCategories = arrCategories;
        this.context = context;
        this.activity = activity;
        this.type = type;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvCategoryName;
        ImageView ivAvatar;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = (TextView) itemView.findViewById(R.id.tv_category_name);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            LinearLayout fullScreen  = (LinearLayout) activity.findViewById(R.id.full_screen_content);
            fullScreen.setVisibility(View.GONE);

            if(type == Constants.ALBUM_CATEGORIES) {
                Fragment fragment = MusicAlbumsFm.newInstance(arrCategories.get(getAdapterPosition()));
                transaction(R.id.music_content, fragment);
            }
            else {
                Fragment fragment = MusicSongsFm.newInstance(arrCategories.get(getAdapterPosition()));
                transaction(R.id.music_content, fragment);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        itemView = inflater.inflate(R.layout.category_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvCategoryName.setText(arrCategories.get(position).getName());
        Glide.with(context).load(arrCategories.get(position).getImage())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.item_up)
                .error(R.drawable.item_up)
                .into(holder.ivAvatar);

    }

    @Override
    public int getItemCount() {
        return arrCategories.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void transaction(int idLayout, Fragment fragment){
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                .replace(idLayout, fragment)
                .commit();
    }

}