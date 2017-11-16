package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.fragments.SearchFm;

import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.OnClickListener;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by Quyen Hua on 11/11/2017.
 */

public class HistoryAdapter extends Adapter<HistoryAdapter.HistoryViewHolder> {

    String[] arrSongs;
    private Context ctx;
    private Activity activity;

    public HistoryAdapter(String[] arrSongs, Context ctx, Activity activity) {
        this.arrSongs = arrSongs;
        this.ctx = ctx;
        this.activity = activity;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.history_card, parent, false);
        HistoryViewHolder historyViewHolder = new HistoryViewHolder(itemView);
        return historyViewHolder;
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        Typeface typeface = Typeface.createFromAsset(ctx.getAssets(), "UVNBuiDoi.TTF");
        holder.tvHistoryName.setTypeface(typeface);
        holder.tvHistoryName.setText(arrSongs[position]);
    }

    @Override
    public int getItemCount() {
        return arrSongs.length;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class HistoryViewHolder extends ViewHolder implements OnClickListener {
        TextView tvHistoryName;

        HistoryViewHolder(View itemView) {
            super(itemView);
//            LinearLayout llHistory = (LinearLayout) itemView.findViewById(R.id.llHistory);
            tvHistoryName = (TextView) itemView.findViewById(R.id.tvHistory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            EditText edSearchView = (EditText) activity.findViewById(R.id.edSearch);
            edSearchView.setText(tvHistoryName.getText(), TextView.BufferType.EDITABLE);
        }
    }
}
