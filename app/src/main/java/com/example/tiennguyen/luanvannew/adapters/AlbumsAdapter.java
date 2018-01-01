package com.example.tiennguyen.luanvannew.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.fragments.AlbumSongsFm;
import com.example.tiennguyen.luanvannew.models.AlbumItem;
import com.example.tiennguyen.luanvannew.models.PersonItem;

import java.util.ArrayList;

/**
 * Created by TIENNGUYEN on 11/13/2017.
 */

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<AlbumItem> arrAlbums;
    private String style;

    public AlbumsAdapter(Context context, ArrayList<AlbumItem> arrAlbums, String style) {
        this.context = context;
        this.arrAlbums = arrAlbums;
        this.style = style;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvAlbumName, tvArtistName, tvViews;
        public ImageView ivBgCover, overflow;
        public CardView albumCard;

        public ViewHolder(View view) {
            super(view);
            if(style == Constants.VERTICAL_ALBUMS_LIST) {
                albumCard = (CardView) view.findViewById(R.id.album_card);
                tvAlbumName = (TextView) view.findViewById(R.id.tvTitle);
                tvArtistName = (TextView) view.findViewById(R.id.tvSingers);
                tvViews = (TextView) view.findViewById(R.id.tvViews);
                ivBgCover = (ImageView) view.findViewById(R.id.ivBgCover);
                overflow = (ImageView) view.findViewById(R.id.overflow);

                ivBgCover.setOnClickListener(this);
                albumCard.setOnClickListener(this);
            }
            else {
                tvAlbumName = (TextView) view.findViewById(R.id.tv_album_name);
                tvArtistName = (TextView) view.findViewById(R.id.tv_artist_name);
                ivBgCover = (ImageView) view.findViewById(R.id.iv_bg_cover);
                tvViews = (TextView) view.findViewById(R.id.tv_views);

                view.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            LinearLayout secondScreen  = (LinearLayout) ((AppCompatActivity)context).findViewById(R.id.fragment_container_second);
            secondScreen.setVisibility(View.VISIBLE);
            AlbumSongsFm fragment = AlbumSongsFm.newInstance(arrAlbums.get(getAdapterPosition()));
            ((AppCompatActivity)context).getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container_second, fragment)
                    .commit();
        }
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(style == Constants.VERTICAL_ALBUMS_LIST) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.album_item_vertical, parent, false);
        }
        else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.album_item_horizontal, parent, false);
        }

        context = parent.getContext();
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        AlbumItem albumItem = arrAlbums.get(position);
        holder.tvAlbumName.setText(StringUtils.newText(albumItem.getName(), 35));
        ArrayList<PersonItem> singers =  albumItem.getSingers();
//        String singerName = "";
//        for (int singerIndex = 0; singerIndex < singers.size(); singerIndex++) {
//            if (singerIndex == singers.size() - 1) {
//                singerName += singers.get(singerIndex).getName();
//            } else {
//                singerName += singers.get(singerIndex).getName() + ", ";
//            }
//        }
        holder.tvArtistName.setText(StringUtils.newText(StringUtils.getArtists(singers), 35));

        holder.tvViews.setText("Views: "+albumItem.getViews());

        // loading album cover using Glide library
        Glide.with(context).load(albumItem.getLinkImg())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.item_up)
                .error(R.drawable.item_up)
                .into(holder.ivBgCover);

        if(style == Constants.VERTICAL_ALBUMS_LIST) {
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.overflow, position);
                }
            });
        }
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, final int position) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

//                switch (item.getItemId()) {
//                    case R.id.action_play_all:
//                        Intent intent = new Intent(mContext, PlayingMusic.class);
//                        mContext.startActivity(intent);
//                        //Toast.makeText(mContext, "Play all", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.action_detail:
//                        //Toast.makeText(mContext, "Detail", Toast.LENGTH_SHORT).show();
//                        AlbumSongsFm fragment = AlbumSongsFm.newInstance(arrAlbums.get(position));
//                        ((AppCompatActivity)mContext).getSupportFragmentManager()
//                                .beginTransaction()
//                                .addToBackStack(null)
//                                .replace(R.id.content_frame, fragment)
//                                .commit();
//                        return true;
//                    default:
//                }
                return false;
            }
        });
        popup.show();
    }


    @Override
    public int getItemCount() {
        return arrAlbums.size();
    }


    public static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    public int dpToPx(int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
