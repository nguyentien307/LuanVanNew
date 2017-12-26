package com.example.tiennguyen.luanvannew.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.WriteData;
import com.example.tiennguyen.luanvannew.dialogs.AlertDialogClearHistory;
import com.example.tiennguyen.luanvannew.fragments.SearchFm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import static android.R.attr.fragment;
import static android.support.v7.widget.RecyclerView.Adapter;
import static android.support.v7.widget.RecyclerView.OnClickListener;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by Quyen Hua on 11/11/2017.
 */

public class HistoryAdapter extends Adapter<HistoryAdapter.HistoryViewHolder> {

    ArrayList<String> arrSongs;
    private Context ctx;
    private Activity activity;

    public HistoryAdapter(ArrayList<String> arrSongs, Context ctx, Activity activity) {
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
        holder.tvHistoryName.setText(arrSongs.get(position));
    }

    @Override
    public int getItemCount() {
        return arrSongs.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class HistoryViewHolder extends ViewHolder implements OnClickListener {
        TextView tvHistoryName;
        LinearLayout llClearItem;

        HistoryViewHolder(View itemView) {
            super(itemView);
//            LinearLayout llHistory = (LinearLayout) itemView.findViewById(R.id.llHistory);
            tvHistoryName = (TextView) itemView.findViewById(R.id.tvHistory);
            llClearItem = (LinearLayout) itemView.findViewById(R.id.llClearItem);
            itemView.setOnClickListener(this);
            llClearItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.llClearItem:
                    clearItem(getAdapterPosition());
                    break;
                default:
                    EditText edSearchView = (EditText) activity.findViewById(R.id.edSearch);
                    edSearchView.setText(tvHistoryName.getText(), TextView.BufferType.EDITABLE);
                    break;
            }
        }

        public void clearItem(int position) {
            AlertDialogClearHistory alertDialogClearHistory = new AlertDialogClearHistory(new AlertDialogClearHistory.ConfirmClear() {
                @Override
                public void confirmClear(int position) {
                    arrSongs.remove(position);
                    String listHistory = TextUtils.join("\n", arrSongs);
                    saveData(listHistory, activity.MODE_PRIVATE);
                    Fragment searchFm = new SearchFm();
                    ((FragmentActivity)ctx).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, searchFm)
                            .commit();
                }
            });
            alertDialogClearHistory.showConfirmClearDialog(
                    ctx,
                    arrSongs.get(position),
                    ctx.getResources().getString(R.string.clear_message),
                    position);
        }

        private void saveData(String data, final int mode) {
            WriteData writeData = new WriteData(new WriteData.GetFileOutputStream() {
                @Override
                public FileOutputStream getFileOutputStream() {
                    FileOutputStream fos = null;
                    try {
                        fos = activity.openFileOutput(Constants.SUGGESTION_FILE, mode);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return fos;
                }
            });
            writeData.saveData(data);
        }
    }
}
