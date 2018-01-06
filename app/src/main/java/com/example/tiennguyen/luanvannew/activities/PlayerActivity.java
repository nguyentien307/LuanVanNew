package com.example.tiennguyen.luanvannew.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiennguyen.luanvannew.MainActivity;
import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.models.PersonItem;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.services.PlayerService;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import java.util.ArrayList;
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

    public static RecyclerView rcPlayerList;
    private LinearLayoutManager llm;
    private SongItem songItem;
    public static int index;
    private String type = "";
    private ArrayList<SongItem> arrayListSong = new ArrayList<>();
    ArrayList<PersonItem> arrArtist = new ArrayList<>();

    private LinearLayout llTimerChecked;
    private ImageView imgTimer;
    private SessionManagement session;

    ImageView img_bg;

    ListView lvSongs;
    public static ArrayList<SongItem> songArr;

    // set volumn
    private LinearLayout llVolumn;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private LinearLayout llControlVolumn;
    private boolean isOpenControlVolumn = false;
    private Timer timer;

    public static Context context;
    public static Activity activity;

    Intent playerService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        context = this;
        activity = this;
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
        playerService.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(playerService);
    }

    private void displayList() {
        songArr = new ArrayList<>();
        if (type.equals(CONSTANTS.SONG_CATEGORIES)) {
            if (((MyApplication) getApplication()).getAlbumOrCategory()) {
                songArr.add(songItem);
            } else {
                songArr = ((MyApplication) getApplication()).getArrayPlayer();
                int i = 0;
                for (i = 0; i < songArr.size(); i++) {
                    if (StringUtils.convertedToUnsigned(songArr.get(i).getTitle()).equals(StringUtils.convertedToUnsigned(songItem.getTitle()))) {
                        break;
                    }
                }
                if (i < songArr.size()) {
                    index = i;
                } else {
                    songArr.add(songItem);
                    index = songArr.size() - 1;
                }
            }
            ((MyApplication) getApplication()).setAlbumOrCategory(false);
        } else if (type.equals(CONSTANTS.PLAYER_COLLAPSE)) {
            songArr = ((MyApplication) getApplication()).getArrayPlayer();
        } else {
            songArr = arrayListSong;
            ((MyApplication) getApplication()).setAlbumOrCategory(true);
        }
        ((MyApplication) getApplication()).setArrayPlayer(songArr);
//        PlayerAdapter adapter = new PlayerAdapter(songArr, this, this);
//        rcPlayerList.setAdapter(adapter);
//        rcPlayerList.scrollToPosition(index);
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

        llVolumn = (LinearLayout) findViewById(R.id.llVolumn);
        volumeSeekbar = (SeekBar)findViewById(R.id.sbVolumn);
        llControlVolumn = (LinearLayout) findViewById(R.id.llControlVolumn);
        llControlVolumn.setVisibility(View.GONE);
        llVolumn.setOnClickListener(this);

        session = new SessionManagement(getBaseContext());
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
                timer = new Timer();
                if (!session.isCheckAlarm()) {
                    imgTimer.setImageResource(R.drawable.timer_checked);
                    session.setCheckAlarm(true);
                    if (session.getAutoStopPlayMusicTime() > 0) {
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                session.setCheckAlarm(false);
                                Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
                                startActivity(intent);

                                // Tao su kien ket thuc app
                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                //stopService(playerService);
                                startActivity(startMain);
                                stopService(playerService);
                            }
                        }, session.getAutoStopPlayMusicTime() * 60 * 1000);
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.not_set_timer), Toast.LENGTH_SHORT).show();
                        imgTimer.setImageResource(R.drawable.timer_unchecked);
                        session.setCheckAlarm(false);
                        timer.cancel();
                    }
                } else {
                    imgTimer.setImageResource(R.drawable.timer_unchecked);
                    session.setCheckAlarm(false);
                    timer.cancel();
                }
                break;
            case R.id.llVolumn:
                if (!isOpenControlVolumn) {
                    llControlVolumn.setVisibility(View.VISIBLE);
                    setVolumeControlStream(AudioManager.STREAM_MUSIC);
                    initControls();
                    isOpenControlVolumn = true;
                } else {
                    llControlVolumn.setVisibility(View.GONE);
                    isOpenControlVolumn = false;
                }
                break;
            default:
                break;
        }
    }

    private void initControls() {
        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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


    @Override
    protected void onPause() {
        super.onPause();
        if (!session.getPlayInBackground()) {
            playerService = new Intent(this, PlayerService.class);
            startService(playerService);
        }
    }
}
