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
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tiennguyen.luanvannew.R;
import com.example.tiennguyen.luanvannew.activities.PlayerActivity;
import com.example.tiennguyen.luanvannew.commons.StringUtils;
import com.example.tiennguyen.luanvannew.fragments.PlayerCollapseFm;
import com.example.tiennguyen.luanvannew.models.SongItem;

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
public class PlayerService extends Service implements MediaPlayer.OnCompletionListener, OnActionClickedListener,
        View.OnClickListener, View.OnTouchListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener {

    String URL_TEMPLATE = "http://mp3.zing.vn/html5/song/";

    // Player Music
    private WeakReference<TextView> musicTitle, artistName;
    private WeakReference<InteractivePlayerView> interactivePlayerViewWeakReference;
    private WeakReference<LinearLayout> llNextward, llBackward, llShuffle, llReplay;
    private WeakReference<ImageView> icNext, icPrevious, icControlPlayPause, icShuffle, icReplay, icNextward, icBackward;
    private ArrayList<SongItem> songArr = new ArrayList<>();

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
                    headsetDisconnected();
                    break;
                case (1):
                    break;
            }
        }
    };

    private void headsetDisconnected() {
        pauseMedia();
    }

    @Override
    public void onCreate() {
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
        int songIndex = intent.getIntExtra("songIndex", 0);
        boolean playNew = intent.getBooleanExtra("playNew", false);
        interactivePlayerViewWeakReference.get().setMax(100);
        interactivePlayerViewWeakReference.get().setProgress(0);
        Log.v(TAG, String.valueOf(songIndex));
        if (songIndex != currentSongIndex || playNew) {
            playSong(songIndex);
//            initNotification(songIndex);
            currentSongIndex = songIndex;
        } else if (currentSongIndex != -1) {
            musicTitle.get().setText(songArr.get(currentSongIndex).getTitle());
            artistName.get().setText(StringUtils.getArtists(songArr.get(songIndex).getArtist()));

            tvTitleCol.get().setText(songArr.get(currentSongIndex).getTitle());
            artistName.get().setText(StringUtils.getArtists(songArr.get(songIndex).getArtist()));
            if (mp.isPlaying())
                icControlPlayPause.get().setBackgroundResource(R.drawable.ic_action_pause);
            else
                icControlPlayPause.get().setBackgroundResource(R.drawable.ic_action_play);
        }

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
        return START_STICKY;
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
        interactivePlayerViewWeakReference.get().setOnActionClickedListener(this);

        // Player Collapse
        tvTitleCol = new WeakReference<>(PlayerCollapseFm.tvTitleCol);
        tvArtistCol = new WeakReference<>(PlayerCollapseFm.tvArtistCol);

        llNextCol = new WeakReference<>(PlayerCollapseFm.llNextCol);
        llPreviousCol = new WeakReference<>(PlayerCollapseFm.llPreCol);
        llPlayCol = new WeakReference<>(PlayerCollapseFm.llPlayCol);
        btnNextCol = new WeakReference<>(PlayerCollapseFm.btnNextCol);
        btnPreviousCol = new WeakReference<>(PlayerCollapseFm.btnPreviousCol);
        btnPlayCol = new WeakReference<>(PlayerCollapseFm.btnPlayCol);

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
        mHandler.removeCallbacks(mUpdateTimeTask);
        try {
            mp.reset();
            mp.setDataSource(URL_TEMPLATE + songArr.get(songIndex).getLink());
            //gửi tin nhắn đến MainActivity để hiển thị đồng bộ
//                sendBufferingBroadcast();
            musicTitle.get().setText(songArr.get(songIndex).getTitle());
            artistName.get().setText(StringUtils.getArtists(songArr.get(songIndex).getArtist()));

            tvTitleCol.get().setText(songArr.get(songIndex).getTitle());
            tvArtistCol.get().setText(StringUtils.getArtists(songArr.get(songIndex).getArtist()));

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

//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        //xóa tin nhắn xử lý cập nhật progress bar
//        mHandler.removeCallbacks(mUpdateTimeTask);
//    }
//
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        mHandler.removeCallbacks(mUpdateTimeTask);
//        int totalDuration = mp.getDuration();
//        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
//        //Thực hiện về trước sau theo số giây
//        mp.seekTo(currentPosition);
//        //Cập nhật lại thời gian ProgressBar
//        updateProgressBar();
//    }

    // --------------------Push Notification
    // Set up the notification ID
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private void initNotification(int songIndex) {
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.ic_action_play;
        CharSequence tickerText = "Audio Book";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        Context context = getApplicationContext();
        CharSequence songName = songArr.get(songIndex).getTitle();

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
//        notification.setLatestEventInfo(context, songName, null, contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

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

    // Thua
    @Override
    public void onActionClicked(int i) {
        switch (i) {
            case 1:
                isShuffle = true;
                break;
            case 2:
                break;
            case 3:
                isRepeat = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.control:
            case R.id.imgPlayCol:
            case R.id.llPlayCol:
                if (currentSongIndex != -1) {
                    if (mp.isPlaying()) {
                        pauseMedia();
                    } else {
                        // Resume song
                        if (mp != null) {
                            playMedia();
                        }
                    }
                }
                break;
            case R.id.imgNext:
            case R.id.imgNextCol:
            case R.id.llNextCol:
                if (currentSongIndex != -1) {
                    if (currentSongIndex < (songArr.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        // play first song
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }
                break;
            case R.id.imgPrevious:
            case R.id.imgPreCol:
            case R.id.llPreCol:
                if (currentSongIndex != -1) {
                    if (currentSongIndex > 0) {
                        playSong(currentSongIndex - 1);
                        currentSongIndex = currentSongIndex - 1;
                    } else {
                        // play last song
                        playSong(songArr.size() - 1);
                        currentSongIndex = songArr.size() - 1;
                    }
                }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        currentSongIndex = -1;
        mHandler.removeCallbacks(mUpdateTimeTask);
        Log.d("Player Service", "Player Service Stopped");
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
        }

        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }
        //Hủy đăng ký headsetReceiver
        unregisterReceiver(headsetReceiver);
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
}
