package com.example.tiennguyen.luanvannew.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.fragments.ArtistMusicFm;
import com.example.tiennguyen.luanvannew.interfaces.ItemClickListener;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.services.GetPage;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/6/2017.
 */
public class InformationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ArrayList<PersonItem> arrPerson;
    private Context context;
    private SparseBooleanArray expandState = new SparseBooleanArray();

    public InformationsAdapter(ArrayList<PersonItem> arrPerson, Context context){
        this.arrPerson = arrPerson;
        this.context = context;
        for(int i = 0; i < arrPerson.size(); i++){
            expandState.append(i, false);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        RelativeLayout rlTitle, rlButton, button;
        TextView tvPersonTitle, tvName, tvLink, tvDetail;
        ImageView ivAvatar;
        ExpandableLinearLayout expandableLayout;
        ItemClickListener itemClickListener, itemChildClick;


        public ViewHolder(View itemView) {
            super(itemView);
            //title
            rlTitle = (RelativeLayout) itemView.findViewById(R.id.rl_title);
            tvPersonTitle = (TextView) itemView.findViewById(R.id.tv_person_title);
            rlButton = (RelativeLayout) itemView.findViewById(R.id.rl_button);
            button = (RelativeLayout) itemView.findViewById(R.id.button);

            //expand
            expandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.expandableLayout);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
           // tvViews = (TextView) itemView.findViewById(R.id.tv_views);
            tvLink = (TextView) itemView.findViewById(R.id.tv_link);
            tvDetail = (TextView) itemView.findViewById(R.id.tv_detail);

            tvLink.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        public void setItemChildClickListener(ItemClickListener itemChildClick){
            this.itemChildClick = itemChildClick;
        }


        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tv_link){
                itemChildClick.onClick(v, getAdapterPosition(), false);
            }else {
                itemClickListener.onClick(v, getAdapterPosition(), false);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.info_content_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder)holder;
        PersonItem item = arrPerson.get(position);

        viewHolder.setIsRecyclable(false);

        viewHolder.tvPersonTitle.setText(item.getName());

        viewHolder.expandableLayout.setInRecyclerView(true);
        viewHolder.expandableLayout.setExpanded(expandState.get(position));
        viewHolder.expandableLayout.setListener(new ExpandableLayoutListenerAdapter() {

            @Override
            public void onPreOpen() {
                changeRotate(viewHolder.button, 0f, 180f).start();
                expandState.put(position, true);
            }

            @Override
            public void onPreClose() {
                changeRotate(viewHolder.button, 180f, 0f).start();
                expandState.put(position, false);
            }
        });
        viewHolder.button.setRotation(expandState.get(position) ? 180f : 0f);
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
        if(item.getLink() != "") {

            GetPage getArtist = new GetPage(context);
            getArtist.setDataDownloadListener(new GetPage.DataDownloadListener() {
                @Override
                public void dataDownloadedSuccessfully(Document data) {
                    String basicInfo = "";
                    String detailInfo = "";
                    String songsLink = "";
                    String albumsLink = "";
                    String imgLink = "";

                    imgLink = data.select("div.singer-avatar img").attr("src");
                    Elements pTags = data.select("div.singer-left-avatar p");
                    for (int i = 0; i < pTags.size(); i++) {
                        Element pTag = pTags.get(i);
                        if (i == pTags.size() - 1) {
                            basicInfo += pTag.text();
                        } else {
                            basicInfo += pTag.text() + "\n";
                        }
                    }
                    Elements details = data.select("div.singer_profile_content div p");
                    for (int i = 0; i < details.size(); i++) {
                        Element pTag = details.get(i);
                        if (i == pTags.size() - 1) {
                            detailInfo += pTag.text();
                        } else {
                            detailInfo += pTag.text() + "\n";
                        }
                    }

                    Elements links = data.select("div.singer-top-menu div div ul li");
                    if (links.size() != 0) {
                        songsLink = links.get(1).select("a").attr("href");
                        albumsLink = links.get(2).select("a").attr("href");
                    }
                    else{
                        Elements searchLinks = data.select("ul.search_control_select li");
                        songsLink = searchLinks.get(1).select("a").attr("href");
                        albumsLink = searchLinks.get(2).select("a").attr("href");
                    }



                    Glide.with(context).load(imgLink)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.item_up)
                            .error(R.drawable.item_up)
                            .into(viewHolder.ivAvatar);
                    viewHolder.tvName.setText(basicInfo);
                    //viewHolder.tvViews.setText("Views: "+ item.getViews());

                    viewHolder.tvDetail.setText(detailInfo);


                    final String finalSongsLink = songsLink;
                    final String finalAlbumsLink = albumsLink;
                    viewHolder.setItemChildClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            Fragment fragment = ArtistMusicFm.newInstance(arrPerson.get(position), finalSongsLink, finalAlbumsLink);
                            transaction(R.id.fragment_container_second, fragment);
                        }
                    });
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                        }
                    });
                }

                @Override
                public void dataDownloadFailed() {

                }
            });
            getArtist.execute(item.getLink());
        }
        else {

        }


    }



    private ObjectAnimator changeRotate(RelativeLayout button, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(button, "rotation", from, to );
        animator.setDuration(400);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    @Override
    public int getItemCount() {
        return arrPerson.size();
    }

    public void transaction(int idLayout, Fragment fragment){
        ((AppCompatActivity)context).getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(idLayout, fragment)
                .commit();
    }


}