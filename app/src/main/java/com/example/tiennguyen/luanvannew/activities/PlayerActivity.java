package com.example.tiennguyen.luanvannew.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tiennguyen.luanvannew.MainActivity;
import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.adapters.PlayerAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.PlayerService;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import co.mobiwise.library.InteractivePlayerView;
import co.mobiwise.library.OnActionClickedListener;

public class PlayerActivity extends AppCompatActivity implements OnActionClickedListener, View.OnClickListener,
        Toolbar.OnMenuItemClickListener {

    Constants CONSTANTS = new Constants();
    public static TextView musicTitle;
    public static TextView artistName;
    private LinearLayout llBack;
    public static LinearLayout llBackward, llNextward, llShuffle, llReplay;
    public static ImageView icNext;
    public static ImageView icPrevious;
    public static ImageView controlPlayPause;
    public static ImageView icShuffle, icReplay;
    public static ImageView icNextward, icBackward;
    public static InteractivePlayerView interactivePlayerView;

    private RecyclerView rcPlayerList;
    private LinearLayoutManager llm;
    private SongItem songItem;
    private int index;
    private String type = "";
    private ArrayList<SongItem> arrayListSong = new ArrayList<>();
    ArrayList<PersonItem> arrArtist = new ArrayList<>();

    private LinearLayout llTimerChecked;
    private ImageView imgTimer;
    private Calendar calendar;
    private SessionManagement session;
    private AlarmManager alarmManager;
    private Intent intent;
    private PendingIntent pendingIntent;

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
            type = bundle.getString("type");
            if (type.equals(Constants.SONG_CATEGORIES)) {
                songItem = (SongItem) bundle.getSerializable("songItem");
                ArrayList<PersonItem> arrArtist = bundle.getParcelableArrayList("arrArtist");
                ArrayList<PersonItem> arrComposer = bundle.getParcelableArrayList("arrComposer");
                songItem.setArtist(arrArtist);
                songItem.setComposer(arrComposer);
            } else if (type.equals(Constants.PLAYER_COLLAPSE)) {
                index = bundle.getInt("index", 0);
            } else {
                index = bundle.getInt("index", 0);
                arrayListSong = bundle.getParcelableArrayList("arrSong");
                for (int i = 0; i <arrayListSong.size(); i++) {
                    ArrayList<PersonItem> arrArtist = bundle.getParcelableArrayList("arrArtist" + i);
                    ArrayList<PersonItem> arrComposer = bundle.getParcelableArrayList("arrComposer" + i);
                    arrayListSong.get(i).setArtist(arrArtist);
                    arrayListSong.get(i).setComposer(arrComposer);
                }
            }
        }
        initViews();
        displayList();
        playerService = new Intent(this, PlayerService.class);
        playerService.putExtra("songIndex", index);
        if (!type.equals(Constants.PLAYER_COLLAPSE)) {
            playerService.putExtra("playNew", true);
        }
        startService(playerService);
    }

    private void displayList() {
        songArr = new ArrayList<>();
        if (type.equals(CONSTANTS.SONG_CATEGORIES)) {
            if (((MyApplication) getApplication()).getAlbumOrCategory()) {
                songArr.add(songItem);
            } else {
                songArr = ((MyApplication) getApplication()).getArrayPlayer();
                songArr.add(songItem);
                index = songArr.size() - 1;
            }
            ((MyApplication) getApplication()).setAlbumOrCategory(false);
        } else if (type.equals(CONSTANTS.PLAYER_COLLAPSE)) {
            songArr = ((MyApplication) getApplication()).getArrayPlayer();
        } else {
            songArr = arrayListSong;
            ((MyApplication) getApplication()).setAlbumOrCategory(true);
        }
        ((MyApplication) getApplication()).setArrayPlayer(songArr);
        PlayerAdapter adapter = new PlayerAdapter(songArr, this, this);
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
        llBackward = (LinearLayout) findViewById(R.id.llBackward);
        llNextward = (LinearLayout) findViewById(R.id.llNextward);
        llShuffle = (LinearLayout) findViewById(R.id.llShuffle);
        llReplay = (LinearLayout) findViewById(R.id.llReplay);

        session = new SessionManagement(getBaseContext());
        intent = new Intent(PlayerActivity.this, AlarmTimerActivity.class);
        llTimerChecked = (LinearLayout) findViewById(R.id.llTimerChecked);
        imgTimer = (ImageView) findViewById(R.id.imgTimerChecked);
        if (session.isCheckAlarm()) {
            imgTimer.setImageResource(R.drawable.timer_checked);
        } else {
            imgTimer.setImageResource(R.drawable.timer_unchecked);
        }

        llTimerChecked.setOnClickListener(this);

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
            case R.id.llTimerChecked:
                if (session.isCheckAlarm()) {
                    imgTimer.setImageResource(R.drawable.timer_checked);
                    session.setCheckAlarm(false);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            session.setCheckAlarm(false);
                            Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                            startActivity(intent);

                            // Tao su kien ket thuc app
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startActivity(startMain);
                            finish();
                        }
                    }, session.getAutoStopPlayMusicTime() * 60 * 1000);
                } else {
                    imgTimer.setImageResource(R.drawable.timer_unchecked);
                    session.setCheckAlarm(true);
                }
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
