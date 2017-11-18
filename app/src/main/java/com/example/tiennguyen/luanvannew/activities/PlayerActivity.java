package com.example.tiennguyen.luanvannew.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.PlayerAdapter;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.PlaylistItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.PlayerService;
import com.example.tiennguyen.luanvannew.utils.Constants;

import java.util.ArrayList;

import co.mobiwise.library.InteractivePlayerView;
import co.mobiwise.library.OnActionClickedListener;

public class PlayerActivity extends AppCompatActivity implements OnActionClickedListener, View.OnClickListener,
        Toolbar.OnMenuItemClickListener {

    Constants CONSTANTS = new Constants();
    public static TextView musicTitle;
    public static TextView artistName;
    private LinearLayout llBack;
    public static ImageView icNext;
    public static ImageView icPrevious;
    public static ImageView controlPlayPause;
    public static ImageView icShuffle, icReplay;
    public static ImageView icNextward, icBackward;
    public static InteractivePlayerView interactivePlayerView;

    private RecyclerView rcPlayerList;
    private LinearLayoutManager llm;
    private int index;
    private String type = "";
    private ArrayList<SongItem> arrayListSong = new ArrayList<>();

    ImageView img_bg;

    ListView lvSongs;
    public static ArrayList<SongItem> songArr;

    Intent playerService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        if (bundle != null) {
            index = bundle.getInt("index", 0);
            type = bundle.getString("type");
            arrayListSong = bundle.getParcelableArrayList("arrSong");
        }
        initViews();
        createArray();
            playerService = new Intent(this, PlayerService.class);
            playerService.putExtra("songIndex", index);
            startService(playerService);
    }

    private void createArray() {
        songArr = new ArrayList<>();
        if (type.equals(CONSTANTS.SONG_CATEGORIES)) {
            songArr = ((MyApplication) getApplication()).getArrayPlayer();
            ArrayList<PersonItem> artistArr = new ArrayList<>();
            artistArr.add(new PersonItem("Khánh Linh", "link", 1111));
//            songArr.add(new SongItem("Nếu em được lựa chọn", artistArr, "http://mp3.zing.vn/html5/song/LnJnyZGNllELNELTtbmkH"));
//            songArr.add(new SongItem("Hạnh Phúc Mong Manh", artistArr, "http://mp3.zing.vn/html5/song/ZmcntLHNXZvmWVLymyFHZmtkpkGkhBXXC"));
//            songArr.add(new SongItem("Em Không Là Duy Nhất", artistArr, "http://mp3.zing.vn/html5/song/ZHcmtLnapxhDdCXtmyFmZHykWkGkCvdac"));
//            songArr.add(new SongItem("Ta Còn Thuộc Về Nhau", artistArr, "http://mp3.zing.vn/html5/song/ZmxntLmNpJaCJHLtnyvnZmtZpLHZXbVCH"));
            if (arrayListSong.size() > 0) {
                songArr = new ArrayList<>();
                songArr.add(new SongItem(arrayListSong.get(index).getTitle(), artistArr, "http://mp3.zing.vn/html5/song/ZHcmtLnapxhDdCXtmyFmZHykWkGkCvdac"));
                index = songArr.size() - 1;
            }
        } else if (type.equals(CONSTANTS.PLAYER_COLLAPSE)) {
            songArr = ((MyApplication) getApplication()).getArrayPlayer();
        }
        else {
            for (int i = 0; i < arrayListSong.size(); i++) {
                ArrayList<PersonItem> artistArr = new ArrayList<>();
                artistArr.add(new PersonItem("Khánh Linh", "link", 1111));
                songArr.add(new SongItem(arrayListSong.get(i).getTitle(), artistArr, "http://mp3.zing.vn/html5/song/ZHcmtLnapxhDdCXtmyFmZHykWkGkCvdac"));
            }
        }
        ((MyApplication) getApplication()).setArrayPlayer(songArr);
        PlayerAdapter adapter = new PlayerAdapter(songArr, this);
        rcPlayerList.setAdapter(adapter);
        rcPlayerList.scrollToPosition(index);
    }

    private void initViews() {
        musicTitle = (TextView) findViewById(R.id.tvTitleSong);
        artistName = (TextView) findViewById(R.id.tvArtTistSong);
        llBack = (LinearLayout) findViewById(R.id.llBackFromPlayer);
        interactivePlayerView = (InteractivePlayerView) findViewById(R.id.interactivePlayerView);
        controlPlayPause = (ImageView) findViewById(R.id.control);
        icNext = (ImageView) findViewById(R.id.imgNext);
        icPrevious = (ImageView) findViewById(R.id.imgPrevious);
        icShuffle = (ImageView) findViewById(R.id.btnShuffle);
        icReplay = (ImageView) findViewById(R.id.btnReplay);
        icNextward = (ImageView) findViewById(R.id.btnNextward);
        icBackward = (ImageView) findViewById(R.id.btnBackward);

        img_bg = (ImageView) findViewById(R.id.img_bg);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bg_player);
        img_bg.startAnimation(animation);

        interactivePlayerView.setProgressLoadedColor(getResources().getColor(R.color.progressBarLightBlue));

        llBack.setOnClickListener(this);

        rcPlayerList = (RecyclerView) findViewById(R.id.rc_player_list);
        rcPlayerList.setHasFixedSize(true);
        llm = new LinearLayoutManager(getBaseContext());
        rcPlayerList.setLayoutManager(llm);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!PlayerService.mp.isPlaying()) {
            stopService(playerService);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llBackFromPlayer:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActionClicked(int i) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finish();
            default:
                break;
        }
        return false;
    }

    //Dialog xử lý tiến trình tải giữa PlayerService và MainActivity
//    private ProgressDialog pdBuff = null;
//    boolean mBufferBroadcastIsRegistered;
//    // Thiết lập BroadcastReceiver
//    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent bufferIntent) {
//            showPD(bufferIntent);
//        }
//    };

//    private void showPD(Intent bufferIntent) {
//        String bufferValue = bufferIntent.getStringExtra("buffering");
//        int bufferIntValue = Integer.parseInt(bufferValue);
//        //Nếu giá trị bufferIntValue bằng 1 thì cho dialog chạy
//        //Nếu giá trị bufferIntValue bằng 2 thì cho dismiss dialog
//        switch (bufferIntValue) {
//            case 0:
//                if (pdBuff != null) {
//                    pdBuff.dismiss();
//                }
//                break;
//            case 1:
//                BufferDialogue();
//                break;
//        }
//    }
//
//    private void BufferDialogue() {
//        pdBuff = ProgressDialog.show(this, "Vui lòng chờ...", "Đang tải dữ liệu...", true);
//        pdBuff.setCancelable(true);
//    }
//
//    @Override
//    protected void onResume() {
//        //Đăng ký broadcast receiver
//        if (!mBufferBroadcastIsRegistered) {
//            registerReceiver(broadcastBufferReceiver, new IntentFilter(PlayerService.BROADCAST_BUFFER));
//            mBufferBroadcastIsRegistered = true;
//        }
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        //Hủy đăng ký broadcast receiver
//        if (mBufferBroadcastIsRegistered) {
//            unregisterReceiver(broadcastBufferReceiver);
//            mBufferBroadcastIsRegistered = false;
//        }
//        super.onPause();
//    }
}
