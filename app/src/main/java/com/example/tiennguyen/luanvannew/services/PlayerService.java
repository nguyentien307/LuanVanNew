package com.example.tiennguyen.luanvannew.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tiennguyen.luanvannew.MyApplication;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.adapters.PlayerAdapter;
import com.example.tiennguyen.luanvannew.commons.Constants;
import com.example.tiennguyen.luanvannew.commons.ImageBigmap;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.commons.ZingMP3LinkTemplate;
import com.example.tiennguyen.luanvannew.fragments.PlayerCollapseFm;
import com.example.tiennguyen.luanvannew.models.SongItem;
import com.example.tiennguyen.luanvannew.sessions.SessionManagement;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;

import co.mobiwise.library.InteractivePlayerView;
import co.mobiwise.library.OnActionClickedListener;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener,
        View.OnClickListener, View.OnTouchListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener {

    Notification status;
    private final String LOG_TAG = "NotificationService";

    // Player Music
    private WeakReference<TextView> musicTitle, artistName;
    private WeakReference<InteractivePlayerView> interactivePlayerViewWeakReference;
    private WeakReference<LinearLayout> llNextward, llBackward, llShuffle, llReplay;
    private WeakReference<ImageView> icNext, icPrevious, icControlPlayPause, icShuffle, icReplay, icNextward, icBackward;
    private ArrayList<SongItem> songArr = new ArrayList<>();
    private WeakReference<RecyclerView> rcPlayerList;

    // Player Collapse
    private WeakReference<TextView> tvTitleCol, tvArtistCol;
    private WeakReference<LinearLayout> llNextCol, llPreviousCol, llPlayCol;
    private WeakReference<ImageView> imgTitleCol, btnNextCol, btnPreviousCol, btnPlayCol;

    private Handler mHandler = new Handler();
    private int seekForwardTime = 5000; // 5 giây
    private int seekBackwardTime = 5000; // 5 giây
    public static MediaPlayer mp;
    public static int currentSongIndex = -1;
    public static int songindexForPause = 0;
    //Thành lập broadcast identifier and intent
    public static final String BROADCAST_BUFFER = "quyenhua0403";
    Intent bufferIntent;

    private boolean isPausedInCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private static final String TAG = "TELEPHONESERVICE";

    private boolean isRepeat = false;
    private boolean isShuffle = false;
    private int titleLength = 25;
    private int artistLength = 33;

    private SessionManagement session;

    //BroadCast Receiver sử dụng để lắng nge và phát sóng dữ liệu khi tai nge được cắm
    //Và nếu có cắm tai nge, thì dừng nhạc và dừng service
    private int headsetSwitch = 1;
    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headsetConnected = false;
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.v(TAG, "ACTION_HEADSET_PLUG Intent received");
            if (intent.hasExtra("state")) {
                if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
                    headsetConnected = false;
                    headsetSwitch = 0;
                } else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
                    headsetConnected = true;
                    headsetSwitch = 1;
                }
            }
            switch (headsetSwitch) {
                case (0):
                    if (!session.getContinueWhenRemovePhone())
                        headsetDisconnected();
                    break;
                case (1):
                    if (!session.getContinueWhenPlugPhone())
                        headsetDisconnected();
                    break;
            }
        }
    };

    private void headsetDisconnected() {
        pauseMedia();
    }

    @Override
    public void onCreate() {
        session = new SessionManagement(getApplicationContext());

        mp = new MediaPlayer();
        mp.setOnCompletionListener(this);
        mp.setOnErrorListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnBufferingUpdateListener(this);
        mp.setOnSeekCompleteListener(this);
        mp.setOnInfoListener(this);
        mp.reset();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        bufferIntent = new Intent(BROADCAST_BUFFER);
        //Đăng ký nhận tai nge
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
//        songArr = PlayerCollapseFm.songArr;
        super.onCreate();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        initUI();
        songArr = PlayerActivity.songArr;

        //Nếu có cuộc gọi đến, tạm dừng máy nge nhạc, và resume khi ngắt kết nối cuộc gọi.
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                // String stateString = "N/A";
                Log.v(TAG, "Starting CallStateChange");
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING://có cuộc gọi đến
                        if (mp != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE://kết thúc cuộc gọi
                        //Bắt đầu play nhac.
                        if (mp != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };
        //Đăng ký lắng nge từ việc quản lý điện thoại
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        super.onStart(intent, startId);
        controlPlayerNotification(intent);
        PlayerAdapter adapter = new PlayerAdapter(songArr, PlayerActivity.context, PlayerActivity.activity, currentSongIndex);
        rcPlayerList.get().setAdapter(adapter);
        rcPlayerList.get().scrollToPosition(currentSongIndex);
        return START_STICKY;
    }

    private void controlPlayerNotification(Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                int songIndex = intent.getIntExtra("songIndex", 0);
                boolean playNew = intent.getBooleanExtra("playNew", false);
                interactivePlayerViewWeakReference.get().setMax(100);
                interactivePlayerViewWeakReference.get().setProgress(0);
                if (songIndex != currentSongIndex || playNew) {
                    playSong(songIndex);
                    currentSongIndex = songIndex;
                    if (session.getPlayInBackground()) {
                        showNotification();
                    }
                } else if (currentSongIndex != -1) {
                    musicTitle.get().setText(StringUtils.newText(songArr.get(currentSongIndex).getTitle(), titleLength));
                    artistName.get().setText(StringUtils.newText(StringUtils.getArtists(songArr.get(currentSongIndex).getArtist()), artistLength));

                    tvTitleCol.get().setText(StringUtils.newText(songArr.get(currentSongIndex).getTitle(), titleLength));
                    tvArtistCol.get().setText(StringUtils.getArtists(songArr.get(currentSongIndex).getArtist()));
                    Glide.with(getBaseContext()).load(songArr.get(currentSongIndex).getLinkImg())
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .placeholder(R.drawable.item_up)
                            .error(R.drawable.item_up)
                            .into(imgTitleCol.get());
                    interactivePlayerViewWeakReference.get().setCoverURL(songArr.get(currentSongIndex).getLinkImg());
                    if (mp.isPlaying()) {
                        btnPlayCol.get().setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
                        icControlPlayPause.get().setBackgroundResource(R.drawable.ic_action_pause);
                    } else {
                        btnPlayCol.get().setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
                        icControlPlayPause.get().setBackgroundResource(R.drawable.ic_action_play);
                    }
                    if (session.getPlayInBackground()) {
                        showNotification();
                    }
                }
            } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
                setActionPrev();
            } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
                setActionPlay();
            } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
                setActionNext();
            } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
                stopMedia();
                stopForeground(true);
                stopSelf();
            }
        }else {
            pauseMedia();
        }
    }

    private void initUI() {
        // Player Music
        musicTitle = new WeakReference<>(PlayerActivity.musicTitle);
        artistName = new WeakReference<>(PlayerActivity.artistName);

        interactivePlayerViewWeakReference = new WeakReference<>(PlayerActivity.interactivePlayerView);
        icNext = new WeakReference<>(PlayerActivity.icNext);
        icPrevious = new WeakReference<>(PlayerActivity.icPrevious);
        icControlPlayPause = new WeakReference<>(PlayerActivity.controlPlayPause);
        icShuffle = new WeakReference<>(PlayerActivity.icShuffle);
        icReplay = new WeakReference<>(PlayerActivity.icReplay);
        icNextward = new WeakReference<>(PlayerActivity.icNextward);
        icBackward = new WeakReference<>(PlayerActivity.icBackward);
        llNextward = new WeakReference<>(PlayerActivity.llNextward);
        llBackward = new WeakReference<>(PlayerActivity.llBackward);
        llShuffle = new WeakReference<>(PlayerActivity.llShuffle);
        llReplay = new WeakReference<>(PlayerActivity.llReplay);

        rcPlayerList = new WeakReference<>(PlayerActivity.rcPlayerList);
        rcPlayerList.get().setHasFixedSize(true);
        LinearLayoutManager llm;
        llm = new LinearLayoutManager(getBaseContext());
        rcPlayerList.get().setLayoutManager(llm);

        icNext.get().setOnClickListener(this);
        icPrevious.get().setOnClickListener(this);
        icControlPlayPause.get().setOnClickListener(this);
        icShuffle.get().setOnClickListener(this);
        icReplay.get().setOnClickListener(this);
        icNextward.get().setOnClickListener(this);
        icBackward.get().setOnClickListener(this);
        llNextward.get().setOnClickListener(this);
        llBackward.get().setOnClickListener(this);
        llShuffle.get().setOnClickListener(this);
        llReplay.get().setOnClickListener(this);

        // Player Collapse
        tvTitleCol = new WeakReference<>(PlayerCollapseFm.tvTitleCol);
        tvArtistCol = new WeakReference<>(PlayerCollapseFm.tvArtistCol);

        llNextCol = new WeakReference<>(PlayerCollapseFm.llNextCol);
        llPreviousCol = new WeakReference<>(PlayerCollapseFm.llPreCol);
        llPlayCol = new WeakReference<>(PlayerCollapseFm.llPlayCol);
        btnNextCol = new WeakReference<>(PlayerCollapseFm.btnNextCol);
        btnPreviousCol = new WeakReference<>(PlayerCollapseFm.btnPreviousCol);
        btnPlayCol = new WeakReference<>(PlayerCollapseFm.btnPlayCol);
        imgTitleCol = new WeakReference<>(PlayerCollapseFm.imgTitleCol);

        llNextCol.get().setOnClickListener(this);
        llPreviousCol.get().setOnClickListener(this);
        llPlayCol.get().setOnClickListener(this);
        btnPlayCol.get().setOnClickListener(this);
        btnPreviousCol.get().setOnClickListener(this);
        btnNextCol.get().setOnClickListener(this);
    }

    //Gửi tin nhắn tới PlayingMusic rằng bài hát đã được lưu vào vùng nhớ đệm
    private void sendBufferingBroadcast() {
        // Log.v(TAG, "BufferStartedSent");
        bufferIntent.putExtra("buffering", "1");
        sendBroadcast(bufferIntent);
    }

    //Gửi tin nhắn tới MainActivity rằng bài hát đã sẵn sàn để play
    private void sendBufferCompleteBroadcast() {
        bufferIntent.putExtra("buffering", "0");
        sendBroadcast(bufferIntent);
    }

    private void playSong(int songIndex) {
        PlayerAdapter adapter = new PlayerAdapter(songArr, PlayerActivity.context, PlayerActivity.activity, songIndex);
        rcPlayerList.get().setAdapter(adapter);
        rcPlayerList.get().scrollToPosition(currentSongIndex);
        mHandler.removeCallbacks(mUpdateTimeTask);
        try {
            mp.reset();
            mp.setDataSource(ZingMP3LinkTemplate.URL_TEMPLATE + songArr.get(songIndex).getLink());
            //gửi tin nhắn đến MainActivity để hiển thị đồng bộ
//                sendBufferingBroadcast();
            musicTitle.get().setText(StringUtils.newText(songArr.get(songIndex).getTitle(), titleLength));
            artistName.get().setText(StringUtils.newText(StringUtils.getArtists(songArr.get(songIndex).getArtist()), artistLength));

            tvTitleCol.get().setText(StringUtils.newText(songArr.get(songIndex).getTitle(), titleLength));
            tvArtistCol.get().setText(StringUtils.getArtists(songArr.get(songIndex).getArtist()));
            Glide.with(getBaseContext()).load(songArr.get(songIndex).getLinkImg())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.item_up)
                    .error(R.drawable.item_up)
                    .into(imgTitleCol.get());

            btnPlayCol.get().setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            icControlPlayPause.get().setBackgroundResource(R.drawable.ic_action_pause);
            interactivePlayerViewWeakReference.get().setCoverURL(songArr.get(songIndex).getLinkImg());
            mp.prepare();
            mp.start();
            interactivePlayerViewWeakReference.get().setMax(mp.getDuration() / 1000);
            interactivePlayerViewWeakReference.get().setProgress(0);
            updateProgressBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Gọi sẵn sàn để phát lại bài hát.
    public void onPrepared(MediaPlayer mp) {
        //Gởi tin nhắn kết thúc
//        sendBufferCompleteBroadcast();
        playMedia();
    }

    //Thực hiện chơi nhạc của media
    public void playMedia() {
        if (!mp.isPlaying()) {
            mp.start();
            btnPlayCol.get().setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            icControlPlayPause.get().setBackgroundResource(R.drawable.ic_action_pause);
            updateProgressBar();
            if (session.getPlayInBackground()) {
                showNotification();
            }
        }
    }

    //Thực hiện tạm dừng media
    public void pauseMedia() {
        // Log.v(TAG, "Pause Media");
        if (mp.isPlaying()) {
            mp.pause();
            btnPlayCol.get().setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            icControlPlayPause.get().setBackgroundResource(R.drawable.ic_action_play);
        }

    }

    //Thực hiện dừng media
    public void stopMedia() {
        if (mp.isPlaying()) {
            mp.stop();
            btnPlayCol.get().setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            icControlPlayPause.get().setBackgroundResource(R.drawable.ic_action_play);
        }
    }

    //Cập nhật thời gian trên seekbar
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = 0;
            try {
                totalDuration = mp.getDuration();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            long currentDuration = 0;
            try {
                currentDuration = mp.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            interactivePlayerViewWeakReference.get().setMax((int) (totalDuration / 1000));
            interactivePlayerViewWeakReference.get().setProgress((int) (currentDuration / 1000));
            //Chạy lại sau 0,1s
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (isRepeat) {
            playSong(currentSongIndex);
        } else if (isShuffle) {
            Random rand = new Random();
            currentSongIndex = rand.nextInt(songArr.size() -1);
            playSong(currentSongIndex);
        } else {
            if (currentSongIndex < songArr.size() - 1) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.control:
            case R.id.imgPlayCol:
            case R.id.llPlayCol:
                setActionPlay();
                break;
            case R.id.imgNext:
            case R.id.imgNextCol:
            case R.id.llNextCol:
                setActionNext();
                break;
            case R.id.imgPrevious:
            case R.id.imgPreCol:
            case R.id.llPreCol:
                setActionPrev();
                break;
            case R.id.btnShuffle:
            case R.id.llShuffle:
                if (isShuffle) {
                    isShuffle = false;
                    icShuffle.get().setImageResource(R.drawable.shuffle_unselected);
                } else {
                    isShuffle = true;
                    icShuffle.get().setImageResource(R.drawable.shuffle_selected);
                }
                break;
            case R.id.btnReplay:
            case R.id.llReplay:
                if (isRepeat) {
                    isRepeat = false;
                    icReplay.get().setImageResource(R.drawable.replay_unselected);
                } else {
                    isRepeat = true;
                    icReplay.get().setImageResource(R.drawable.replay_selected);
                }
                break;
            case R.id.btnNextward:
            case R.id.llNextward:

                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if (currentPosition + seekForwardTime <= mp.getDuration()) {
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                } else {
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
                break;
            case R.id.btnBackward:
            case R.id.llBackward:
                // get current song position
                int currentPosition2 = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if (currentPosition2 - seekBackwardTime >= 0) {
                    // forward song
                    mp.seekTo(currentPosition2 - seekBackwardTime);
                } else {
                    // backward to starting position
                    mp.seekTo(0);
                }
                break;
            default:
                break;
        }
    }

    private void setActionPrev() {
        if (currentSongIndex != -1) {
            if (currentSongIndex > 0) {
                playSong(currentSongIndex - 1);
                currentSongIndex = currentSongIndex - 1;
            } else {
                // play last song
                playSong(songArr.size() - 1);
                currentSongIndex = songArr.size() - 1;
            }
            if (session.getPlayInBackground()) {
                showNotification();
            }
        }
    }

    private void setActionNext() {
        if (currentSongIndex != -1) {
            if (currentSongIndex < (songArr.size() - 1)) {
                playSong(currentSongIndex + 1);
                currentSongIndex = currentSongIndex + 1;
            } else {
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
            if (session.getPlayInBackground()) {
                showNotification();
            }
        }
    }

    private void setActionPlay() {
        if (currentSongIndex != -1) {
            if (mp.isPlaying()) {
                pauseMedia();
            } else {
                // Resume song
                if (mp != null) {
                    playMedia();
                }
            }
            if (session.getPlayInBackground()) {
                showNotification();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        currentSongIndex = -1;
//        mHandler.removeCallbacks(mUpdateTimeTask);
//        Log.d("Player Service", "Player Service Stopped");
//        if (mp != null) {
//            if (mp.isPlaying()) {
//                mp.stop();
//            }
//            mp.release();
//        }
//
//        if (phoneStateListener != null) {
//            telephonyManager.listen(phoneStateListener,
//                    PhoneStateListener.LISTEN_NONE);
//        }
//        //Hủy đăng ký headsetReceiver
//        unregisterReceiver(headsetReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    private void showNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

        views.setImageViewBitmap(R.id.status_bar_album_art,
                ImageBigmap.getDefaultAlbumArt(songArr.get(currentSongIndex).getLinkImg()));
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                ImageBigmap.getDefaultAlbumArt(songArr.get(currentSongIndex).getLinkImg()));

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, PlayerService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, PlayerService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, PlayerService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        if (mp.isPlaying()) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.apollo_holo_dark_pause);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    R.drawable.apollo_holo_dark_pause);
        } else {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.apollo_holo_dark_play);
            bigViews.setImageViewResource(R.id.status_bar_play,
                    R.drawable.apollo_holo_dark_play);
        }

        views.setTextViewText(R.id.status_bar_track_name, StringUtils.newText((String) tvTitleCol.get().getText(), 10));
        bigViews.setTextViewText(R.id.status_bar_track_name, StringUtils.newText((String) tvTitleCol.get().getText(), 15));

        views.setTextViewText(R.id.status_bar_artist_name, StringUtils.newText((String) tvArtistCol.get().getText(), 10));
        bigViews.setTextViewText(R.id.status_bar_artist_name, StringUtils.newText((String) tvArtistCol.get().getText(), 15));

        bigViews.setTextViewText(R.id.status_bar_album_name, StringUtils.newText((String) tvTitleCol.get().getText(), 15));

        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.ic_launcher;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }
}
