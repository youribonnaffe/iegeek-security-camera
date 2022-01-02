package com.meari.sdk;

import android.text.TextUtils;
import com.meari.sdk.bean.CameraInfo;
import com.meari.sdk.bean.DeviceParams;
import com.meari.sdk.bean.MeariMoveDirection;
import com.meari.sdk.bean.MeariMusic;
import com.meari.sdk.bean.RefreshInfo;
import com.meari.sdk.bean.RoiInfo;
import com.meari.sdk.bean.SDCardInfo;
import com.meari.sdk.callback.IControlMusicCallback;
import com.meari.sdk.callback.IDeviceUpgradeAllPercentCallback;
import com.meari.sdk.callback.IDeviceUpgradeCallback;
import com.meari.sdk.callback.IDeviceUpgradePercentCallback;
import com.meari.sdk.callback.IGetDeviceParamsCallback;
import com.meari.sdk.callback.IGetFaceDetectResultCallback;
import com.meari.sdk.callback.IGetMusicVolumeCallback;
import com.meari.sdk.callback.IGetRefreshInfoCallback;
import com.meari.sdk.callback.IPlaybackDaysCallback;
import com.meari.sdk.callback.IRefreshMusicStatusCallback;
import com.meari.sdk.callback.IRefreshTempAndHumCallback;
import com.meari.sdk.callback.ISDCardFormatCallback;
import com.meari.sdk.callback.ISDCardFormatPercentCallback;
import com.meari.sdk.callback.ISDCardInfoCallback;
import com.meari.sdk.callback.ISetDeviceParamsCallback;
import com.meari.sdk.common.CameraSleepType;
import com.meari.sdk.json.BaseJSONArray;
import com.meari.sdk.json.BaseJSONObject;
import com.meari.sdk.listener.MeariDeviceListener;
import com.meari.sdk.listener.MeariDeviceRecordMp4Listener;
import com.meari.sdk.listener.MeariDeviceTalkVolumeListener;
import com.meari.sdk.listener.MeariDeviceVideoSeekListener;
import com.meari.sdk.listener.MeariDeviceVideoStopListener;
import com.meari.sdk.utils.BaseUtils;
import com.meari.sdk.utils.GsonUtil;
import com.meari.sdk.utils.JsonUtil;
import com.meari.sdk.utils.Logger;
import com.meari.sdk.utils.SdkUtils;
import com.ppstrong.ppsplayer.CameraPlayer;
import com.ppstrong.ppsplayer.CameraPlayerListener;
import com.ppstrong.ppsplayer.CameraPlayerRecordMp4Listener;
import com.ppstrong.ppsplayer.CameraPlayerRecordVolumeListener;
import com.ppstrong.ppsplayer.CameraPlayerVideoSeekListener;
import com.ppstrong.ppsplayer.CameraPlayerVideoStopListener;
import com.ppstrong.ppsplayer.PPSGLSurfaceView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import org.json.JSONException;

/* loaded from: /var/folders/mb/smjzq82150ddzvk3gq411h300000gn/T/jadx-7000784439489740785.dex */
public class MeariDeviceController {
    public static final int PLAYBACK_MODE = 1;
    public static final int PLAYBACK_NONE = -1;
    public static final int PLAY_MODE = 0;
    public static final int PPSPLAYER_ALREADY_CALLED = -3;
    public static final int PPSPLAYER_ALREADY_CONNECTED = -9;
    public static final int PPSPLAYER_ALREADY_PLAYBACK = -11;
    public static final int PPSPLAYER_ALREADY_PREVIEW = -10;
    public static final int PPSPLAYER_CONNECT_IPC_FIRST = -6;
    public static final int PPSPLAYER_DEVICE_OFFLINE = -15;
    public static final int PPSPLAYER_DISCONNECTED = -14;
    public static final int PPSPLAYER_ERROR_PARAMS = -1;
    public static final int PPSPLAYER_ERROR_PASSWORD = -2;
    public static final int PPSPLAYER_IOException = -13;
    public static final int PPSPLAYER_NOT_SUPPORT = -12;
    public static final int PPSPLAYER_OK = 0;
    public static final int PPSPLAYER_PATH_INVALID = -8;
    public static final int PPSPLAYER_PREVIEW_OR_PLAYBACK_FIRST = -7;
    public static final int PPSPLAYER_REQUEST_FAILED = -5;
    public static final int PPSPLAYER_REQUEST_TIMEOUT = -4;
    public static final String STORAGE_PERMISSION_DENIED = "storage permission denied";
    public static final int VIDEO_STATUS_CAMERA_OFF = 8;
    public static final int VIDEO_STATUS_END_SLEEP = 9;
    public static final int VIDEO_STATUS_GEO_SLEEP = 7;
    public static final int VIDEO_STATUS_PLAYING = 10;
    public static final int VIDEO_STATUS_POOR_TRANSMISSION = 11;
    public static final int VIDEO_STATUS_STOP = 3;
    public static final int VIDEO_STATUS_TIME_SLEEP = 6;
    public static String mserverip = "https://audio.meari.com.cn";
    private String TAG;
    private long[] btss;
    private CameraInfo cameraInfo;
    private CameraPlayer cameraPlayer;
    private int index;

    /* loaded from: /var/folders/mb/smjzq82150ddzvk3gq411h300000gn/T/jadx-7000784439489740785.dex */
    public interface VoiceType {
        public static final int ChildVoice = 0;
        public static final int GirlVoice = 1;
        public static final int ManVoice = 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$pausePlaybackSDCard$4() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$resumePlaybackSDCard$5() {
    }

    public MeariDeviceController() {
        this.TAG = getClass().getSimpleName();
        this.btss = new long[]{0, 0, 0, 0, 0};
        if (this.cameraPlayer == null) {
            this.cameraPlayer = new CameraPlayer();
        }
    }

    public MeariDeviceController(CameraInfo cameraInfo) {
        this();
        this.cameraInfo = cameraInfo;
    }

    public void release() {
        this.cameraPlayer = null;
    }

    public CameraInfo getCameraInfo() {
        return this.cameraInfo;
    }

    public void setCameraInfo(CameraInfo cameraInfo) {
        this.cameraInfo = cameraInfo;
    }

    public boolean isUnConnected() {
        return (this.cameraPlayer.mstatus & 2) != 2;
    }

    public boolean isConnected() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        return cameraPlayer != null && cameraPlayer.IsLogined();
    }

    public boolean isConnecting() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        return cameraPlayer != null && cameraPlayer.isLogining();
    }

    public boolean isPreviewing() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        return cameraPlayer != null && cameraPlayer.IsPreviewing();
    }

    public boolean isPlaybacking() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        return cameraPlayer != null && cameraPlayer.IsPlaybacking();
    }

    public boolean isStopVoicing() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        return cameraPlayer != null && cameraPlayer.isStopVoicing();
    }

    public void enableHardDecode(boolean z) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.enableMediacodec(z);
        }
    }

    public void setVoiceParams(String str, String str2, String str3, String str4) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.setVoiceParams(str, str2, str3, str4);
        }
    }

    public void setStreamType(int i) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.setStreamType(i);
        }
    }

    public void setPlayMode(int i) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.setPlayMode(i);
        }
    }

    public void setCurrentPlayTime(int i) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.setCurrentPlayTime(i);
        }
    }

    public void setVolume(int i) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.setVolume(i);
        }
    }

    public void setNoiseSuppression(int i) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.setNoiseSuppression(i);
        }
    }

    public int getP2pMode() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            return 0;
        }
        return cameraPlayer.getp2pmode();
    }

    public int getStatus() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            return 0;
        }
        return cameraPlayer.mstatus;
    }

    public int getPlaybackTime() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            return 0;
        }
        return cameraPlayer.getPlaybackTime();
    }

    public int getBitRate() {
        long avg = (long) (((float) getAVG(this.cameraPlayer.getBts(0))) / 1024.0f);
        int i = (avg > 120 ? 1 : (avg == 120 ? 0 : -1));
        return (int) ((float) avg);
    }

    private long getAVG(long j) {
        long[] jArr;
        this.btss[0] = j;
        long j2 = 0;
        int i = 0;
        while (true) {
            jArr = this.btss;
            if (i >= jArr.length) {
                break;
            }
            if (jArr[i] == 0 || this.index == i) {
                jArr[i] = j;
            }
            j2 += jArr[i];
            i++;
        }
        int i2 = this.index + 1;
        this.index = i2;
        if (i2 == jArr.length) {
            this.index = 0;
        }
        return j2 / ((long) jArr.length);
    }

    public void freeP2P() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.freeP2P();
        }
    }

    public void initP2P() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.initP2P();
        }
    }

    public static void enableHardDecodingGlobal(boolean z) {
        CameraPlayer.setMediacodecGobal(z);
    }

    public void startConnect(final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startConnect--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        String str = this.TAG;
        Logger.m3729i(str, "--->startConnect--object: " + toString() + "; string: " + SdkUtils.getConnectString(this.cameraInfo));
        this.cameraPlayer.connectIPC2(SdkUtils.getConnectString(this.cameraInfo), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.1
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->startConnect--success--object: " + MeariDeviceController.this.toString() + "--" + str2);
                meariDeviceListener.onSuccess(str2);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->startConnect--failed--object: " + MeariDeviceController.this.toString() + "--" + str2);
                meariDeviceListener.onFailed(str2);
            }
        });
    }

    public void stopConnect(final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopConnect--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        String str = this.TAG;
        Logger.m3729i(str, "--->stopConnect--start--object: " + toString());
        this.cameraPlayer.disconnectIPC(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.2
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->stopConnect--success--object: " + MeariDeviceController.this.toString() + "--" + str2);
                meariDeviceListener.onSuccess(str2);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->stopConnect--failed--object: " + MeariDeviceController.this.toString() + "--" + str2);
                meariDeviceListener.onFailed(str2);
            }
        });
    }

    public void startPreview(PPSGLSurfaceView pPSGLSurfaceView, int i, final MeariDeviceListener meariDeviceListener, MeariDeviceVideoStopListener meariDeviceVideoStopListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startPreview--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        String str = this.TAG;
        Logger.m3729i(str, "--->startPreview--start--videoId: " + i);
        this.cameraPlayer.startPreview(pPSGLSurfaceView, i, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.3
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->startPreview--success: " + str2);
                meariDeviceListener.onSuccess(str2);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->startPreview--failed: " + str2);
                meariDeviceListener.onFailed(str2);
            }
        }, new CameraPlayerVideoStopListener(meariDeviceVideoStopListener) { // from class: com.meari.sdk.-$$Lambda$MeariDeviceController$Z5IvjVr9A2whNED0L3sMtMaQQxc
            public final /* synthetic */ MeariDeviceVideoStopListener f$1;

            {
                this.f$1 = r2;
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerVideoStopListener
            public final void onCameraPlayerVideoClosed(int i2) {
                MeariDeviceController.this.lambda$startPreview$0$MeariDeviceController(this.f$1, i2);
            }
        });
    }

    public /* synthetic */ void lambda$startPreview$0$MeariDeviceController(MeariDeviceVideoStopListener meariDeviceVideoStopListener, int i) {
        String str = this.TAG;
        Logger.m3729i(str, "--->startPreview--VideoStopListener: " + i);
        meariDeviceVideoStopListener.onVideoClosed(i);
    }

    public void stopPreview(final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopPreview--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->stopPreview--start");
        this.cameraPlayer.stopPreview(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.4
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->stopPreview--success: " + str);
                meariDeviceListener.onSuccess(str);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->stopPreview--failed: " + str);
                meariDeviceListener.onFailed(str);
            }
        });
    }

    public void changeVideoResolution(PPSGLSurfaceView pPSGLSurfaceView, int i, final MeariDeviceListener meariDeviceListener, MeariDeviceVideoStopListener meariDeviceVideoStopListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->changeVideoResolution--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        String str = this.TAG;
        Logger.m3729i(str, "--->changeVideoResolution--start--videoId: " + i);
        this.cameraPlayer.changePreview(pPSGLSurfaceView, i, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.5
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->changeVideoResolution--success: " + str2);
                meariDeviceListener.onSuccess(str2);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->changeVideoResolution--failed: " + str2);
                meariDeviceListener.onFailed(str2);
            }
        }, new CameraPlayerVideoStopListener(meariDeviceVideoStopListener) { // from class: com.meari.sdk.-$$Lambda$MeariDeviceController$DJ8T4VCOH7_knPfxunJobLfZfGw
            public final /* synthetic */ MeariDeviceVideoStopListener f$1;

            {
                this.f$1 = r2;
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerVideoStopListener
            public final void onCameraPlayerVideoClosed(int i2) {
                MeariDeviceController.this.lambda$changeVideoResolution$1$MeariDeviceController(this.f$1, i2);
            }
        });
    }

    public /* synthetic */ void lambda$changeVideoResolution$1$MeariDeviceController(MeariDeviceVideoStopListener meariDeviceVideoStopListener, int i) {
        String str = this.TAG;
        Logger.m3729i(str, "--->changeVideoResolution--StopListener-code: " + i);
        meariDeviceVideoStopListener.onVideoClosed(i);
    }

    public void getPlaybackVideoDaysInMonth(int i, int i2, int i3, final IPlaybackDaysCallback iPlaybackDaysCallback) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->getPlaybackVideoDaysInMonth--CameraPlayer is null");
            iPlaybackDaysCallback.onFailed("CameraPlayer is null");
            return;
        }
        String str = this.TAG;
        Logger.m3729i(str, "--->getPlaybackVideoDaysInMonth--start: " + i + ";" + i2 + ";" + i3);
        this.cameraPlayer.searchPlaybackListOnMonth(i, i2, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.6
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->getPlaybackVideoDaysInMonth--success: " + str2);
                iPlaybackDaysCallback.onSuccess(JsonUtil.getPlaybackDays(str2));
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->getPlaybackVideoDaysInMonth--failed: " + str2);
                iPlaybackDaysCallback.onFailed(str2);
            }
        }, i3);
    }

    public void getPlaybackVideoTimesInDay(int i, int i2, int i3, int i4, final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->getPlaybackVideoTimesInDay--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("rec_type", 0);
        baseJSONObject.put("videoid", i4);
        baseJSONObject.put("day", String.format(Locale.CHINA, "%04d%02d%02d", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3)));
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/record/search_by_day");
        String str = this.TAG;
        Logger.m3729i(str, "--->getPlaybackVideoTimesInDay--start: " + i + ";" + i2 + ";" + i3);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.7
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->getPlaybackVideoTimesInDay--success: " + str2);
                meariDeviceListener.onSuccess(str2);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->getPlaybackVideoTimesInDay--failed: " + str2);
                meariDeviceListener.onFailed(str2);
            }
        });
    }

    public void startPlaybackSDCard(PPSGLSurfaceView pPSGLSurfaceView, int i, String str, final MeariDeviceListener meariDeviceListener, MeariDeviceVideoStopListener meariDeviceVideoStopListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startPlaybackSDCard--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
        } else if (pPSGLSurfaceView == null) {
            Logger.m3729i(this.TAG, "--->startPlaybackSDCard--ppsGLSurfaceView is null");
            meariDeviceListener.onFailed("ppsGLSurfaceView is null");
        } else {
            String str2 = this.TAG;
            Logger.m3729i(str2, "--->startPlaybackSDCard--start--videoId: " + i + "; startTime: " + str);
            this.cameraPlayer.startPlaybackSd(pPSGLSurfaceView, str, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.8
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str3) {
                    String str4 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str4, "--->startPlaybackSDCard--success: " + str3);
                    meariDeviceListener.onSuccess(str3);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str3) {
                    String str4 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str4, "--->startPlaybackSDCard--failed: " + str3);
                    meariDeviceListener.onFailed(str3);
                }
            }, new CameraPlayerVideoStopListener(meariDeviceVideoStopListener) { // from class: com.meari.sdk.-$$Lambda$MeariDeviceController$c6TxJn55LV_kBJsEnJBhi7dyCr0
                public final /* synthetic */ MeariDeviceVideoStopListener f$1;

                {
                    this.f$1 = r2;
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerVideoStopListener
                public final void onCameraPlayerVideoClosed(int i2) {
                    MeariDeviceController.this.lambda$startPlaybackSDCard$2$MeariDeviceController(this.f$1, i2);
                }
            }, i);
        }
    }

    public /* synthetic */ void lambda$startPlaybackSDCard$2$MeariDeviceController(MeariDeviceVideoStopListener meariDeviceVideoStopListener, int i) {
        String str = this.TAG;
        Logger.m3729i(str, "--->startPlaybackSDCard--videoStopListener-code: " + i);
        meariDeviceVideoStopListener.onVideoClosed(i);
    }

    public void seekPlaybackSDCard(String str, final MeariDeviceListener meariDeviceListener, MeariDeviceVideoSeekListener meariDeviceVideoSeekListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->seekPlaybackSDCard--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        String str2 = this.TAG;
        Logger.m3729i(str2, "--->seekPlaybackSDCard--start--seekTime:" + str);
        this.cameraPlayer.sendPlaybackCmd(0, str, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.9
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str3) {
                String str4 = MeariDeviceController.this.TAG;
                Logger.m3729i(str4, "--->seekPlaybackSDCard--success: " + str3);
                meariDeviceListener.onSuccess(str3);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str3) {
                String str4 = MeariDeviceController.this.TAG;
                Logger.m3729i(str4, "--->seekPlaybackSDCard--failed: " + str3);
                meariDeviceListener.onFailed(str3);
            }
        }, new CameraPlayerVideoSeekListener(meariDeviceVideoSeekListener) { // from class: com.meari.sdk.-$$Lambda$MeariDeviceController$MqnCNTA5aPglKnvuNWJZ0R3o9Qo
            public final /* synthetic */ MeariDeviceVideoSeekListener f$1;

            {
                this.f$1 = r2;
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerVideoSeekListener
            public final void onCameraPlayerVideoSeek() {
                MeariDeviceController.this.lambda$seekPlaybackSDCard$3$MeariDeviceController(this.f$1);
            }
        }, true);
    }

    public /* synthetic */ void lambda$seekPlaybackSDCard$3$MeariDeviceController(MeariDeviceVideoSeekListener meariDeviceVideoSeekListener) {
        Logger.m3729i(this.TAG, "--->seekPlaybackSDCard--SeekListener");
        meariDeviceVideoSeekListener.onVideoSeek();
    }

    public void pausePlaybackSDCard(final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->pausePlaybackSDCard--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->pausePlaybackSDCard--start");
        this.cameraPlayer.sendPlaybackCmd(1, null, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.10
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->pausePlaybackSDCard--success: " + str);
                meariDeviceListener.onSuccess(str);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->pausePlaybackSDCard--failed: " + str);
                meariDeviceListener.onFailed(str);
            }
        }, lambda.INSTANCE, true);
    }

    public void resumePlaybackSDCard(final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->resumePlaybackSDCard--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->resumePlaybackSDCard--start: ");
        this.cameraPlayer.sendPlaybackCmd(2, null, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.11
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->resumePlaybackSDCard--success: " + str);
                meariDeviceListener.onSuccess(str);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->resumePlaybackSDCard--failed: " + str);
                meariDeviceListener.onFailed(str);
            }
        }, $$Lambda$MeariDeviceController$29iaIfBgajeCHkIxfiun1g0Bnpk.INSTANCE, true);
    }

    public void stopPlaybackSDCard(final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopPlaybackSDCard--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->stopPlaybackSDCard--start");
        this.cameraPlayer.stopPlaybackSd(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.12
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->stopPlaybackSDCard--success: " + str);
                meariDeviceListener.onSuccess(str);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->stopPlaybackSDCard--failed: " + str);
                meariDeviceListener.onFailed(str);
            }
        });
    }

    public void setMute(boolean z) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->setMute--CameraPlayer is null");
            return;
        }
        String str = this.TAG;
        Logger.m3729i(str, "--->setMute--isMute: " + z);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        cameraPlayer.enableMute(z, cameraPlayer.getPlayMode());
    }

    public void snapshot(String str, final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer.getPlayMode() == 0) {
            String str2 = this.TAG;
            Logger.m3729i(str2, "--->snapshot--preview--start: " + str);
            this.cameraPlayer.Playsnapshot(str, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.13
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str3) {
                    String str4 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str4, "--->snapshot--preview--success: " + str3);
                    meariDeviceListener.onSuccess(str3);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str3) {
                    String str4 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str4, "--->snapshot--preview--failed: " + str3);
                    meariDeviceListener.onFailed(str3);
                }
            });
            return;
        }
        Logger.m3729i(this.TAG, "--->snapshot--playback--start");
        this.cameraPlayer.Playbacksnapshot(str, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.14
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str3) {
                String str4 = MeariDeviceController.this.TAG;
                Logger.m3729i(str4, "--->snapshot--playback--success: " + str3);
                meariDeviceListener.onSuccess(str3);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str3) {
                String str4 = MeariDeviceController.this.TAG;
                Logger.m3729i(str4, "--->snapshot--playback--failed: " + str3);
                meariDeviceListener.onFailed(str3);
            }
        });
    }

    public void startRecordMP4(String str, final MeariDeviceListener meariDeviceListener, MeariDeviceRecordMp4Listener meariDeviceRecordMp4Listener) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startRecordMP4--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
        } else if (cameraPlayer.getPlayMode() == 0) {
            Logger.m3729i(this.TAG, "--->startRecordMP4--preview--start");
            this.cameraPlayer.startPlayRecordMp4(str, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.15
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str2) {
                    String str3 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str3, "--->startRecordMP4--preview--success: " + str2);
                    meariDeviceListener.onSuccess(str2);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str2) {
                    String str3 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str3, "--->startRecordMP4--preview--failed: " + str2);
                    meariDeviceListener.onFailed(str2);
                }
            }, new CameraPlayerRecordMp4Listener(meariDeviceRecordMp4Listener) { // from class: com.meari.sdk.-$$Lambda$MeariDeviceController$KtNMUDJ1M6gFZq-_XnoIoUg5W_s
                public final /* synthetic */ MeariDeviceRecordMp4Listener f$1;

                {
                    this.f$1 = r2;
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerRecordMp4Listener
                public final void RecordMp4Interrupt(int i) {
                    MeariDeviceController.this.lambda$startRecordMP4$6$MeariDeviceController(this.f$1, i);
                }
            });
        } else {
            Logger.m3729i(this.TAG, "--->startRecordMP4--playback--start");
            this.cameraPlayer.startPlaybackRecordMp4(str, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.16
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str2) {
                    String str3 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str3, "--->startRecordMP4--playback--success: " + str2);
                    meariDeviceListener.onSuccess(str2);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str2) {
                    String str3 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str3, "--->startRecordMP4--playback--failed: " + str2);
                    meariDeviceListener.onFailed(str2);
                }
            }, new CameraPlayerRecordMp4Listener(meariDeviceRecordMp4Listener) { // from class: com.meari.sdk.-$$Lambda$MeariDeviceController$O9dA4FlWgiFiN1nJ5VhqOS4QUA8
                public final /* synthetic */ MeariDeviceRecordMp4Listener f$1;

                {
                    this.f$1 = r2;
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerRecordMp4Listener
                public final void RecordMp4Interrupt(int i) {
                    MeariDeviceController.this.lambda$startRecordMP4$7$MeariDeviceController(this.f$1, i);
                }
            });
        }
    }

    public /* synthetic */ void lambda$startRecordMP4$6$MeariDeviceController(MeariDeviceRecordMp4Listener meariDeviceRecordMp4Listener, int i) {
        String str = this.TAG;
        Logger.m3729i(str, "--->startRecordMP4--preview--RecordMp4Interrupt: " + i);
        meariDeviceRecordMp4Listener.RecordMp4Interrupt(i);
    }

    public /* synthetic */ void lambda$startRecordMP4$7$MeariDeviceController(MeariDeviceRecordMp4Listener meariDeviceRecordMp4Listener, int i) {
        String str = this.TAG;
        Logger.m3729i(str, "--->startRecordMP4--playback--RecordMp4Interrupt: " + i);
        meariDeviceRecordMp4Listener.RecordMp4Interrupt(i);
    }

    public void stopRecordMP4(final MeariDeviceListener meariDeviceListener) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopRecordMP4--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
        } else if (cameraPlayer.getPlayMode() == 0) {
            Logger.m3729i(this.TAG, "--->stopRecordMP4--preview--start");
            this.cameraPlayer.stopPlayRecordMp4(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.17
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->stopRecordMP4--preview--success: " + str);
                    meariDeviceListener.onSuccess(str);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->stopRecordMP4--preview--failed: " + str);
                    meariDeviceListener.onFailed(str);
                }
            });
        } else {
            Logger.m3729i(this.TAG, "--->stopRecordMP4--playback--start");
            this.cameraPlayer.stopPlaybackRecordMp4(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.18
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->stopRecordMP4--playback--success: " + str);
                    meariDeviceListener.onSuccess(str);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->stopRecordMP4--playback--failed: " + str);
                    meariDeviceListener.onFailed(str);
                }
            });
        }
    }

    public void startVoiceTalk(int i, final MeariDeviceListener meariDeviceListener, final MeariDeviceTalkVolumeListener meariDeviceTalkVolumeListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startVoiceTalk--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
        } else if (i == 1) {
            Logger.m3729i(this.TAG, "--->startVoiceTalk--1--start");
            this.cameraPlayer.startvoicetalk(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.19
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--1--success: " + str);
                    meariDeviceListener.onSuccess(str);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--1--failed: " + str);
                    meariDeviceListener.onFailed(str);
                }
            }, new CameraPlayerRecordVolumeListener() { // from class: com.meari.sdk.MeariDeviceController.20
                @Override // com.ppstrong.ppsplayer.CameraPlayerRecordVolumeListener
                public void onCameraPlayerRecordvolume(int i2) {
                    String str = MeariDeviceController.this.TAG;
                    Logger.m3729i(str, "--->startVoiceTalk--1--listener--volume: " + i2);
                    meariDeviceTalkVolumeListener.onTalkVolume(i2);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerRecordVolumeListener
                public void error(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--1--listener--error: " + str);
                    meariDeviceTalkVolumeListener.onFailed(str);
                }
            });
        } else if (i == 2) {
            Logger.m3729i(this.TAG, "--->startVoiceTalk--2--start");
            this.cameraPlayer.startVoiceTalkForVQE(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.21
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--2--success: " + str);
                    meariDeviceListener.onSuccess(str);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--2--failed: " + str);
                    meariDeviceListener.onFailed(str);
                }
            }, new CameraPlayerRecordVolumeListener() { // from class: com.meari.sdk.MeariDeviceController.22
                @Override // com.ppstrong.ppsplayer.CameraPlayerRecordVolumeListener
                public void onCameraPlayerRecordvolume(int i2) {
                    String str = MeariDeviceController.this.TAG;
                    Logger.m3729i(str, "--->startVoiceTalk--2--listener--volume: " + i2);
                    meariDeviceTalkVolumeListener.onTalkVolume(i2);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerRecordVolumeListener
                public void error(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--2--listener--error: " + str);
                    meariDeviceTalkVolumeListener.onFailed(str);
                }
            });
        } else if (i == 3) {
            Logger.m3729i(this.TAG, "--->startVoiceTalk--3--start");
            this.cameraPlayer.startVoiceTalkForVoiceBell(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.23
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--3--success: " + str);
                    meariDeviceListener.onSuccess(str);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--3--failed: " + str);
                    meariDeviceListener.onFailed(str);
                }
            }, new CameraPlayerRecordVolumeListener() { // from class: com.meari.sdk.MeariDeviceController.24
                @Override // com.ppstrong.ppsplayer.CameraPlayerRecordVolumeListener
                public void onCameraPlayerRecordvolume(int i2) {
                    String str = MeariDeviceController.this.TAG;
                    Logger.m3729i(str, "--->startVoiceTalk--3--listener--volume: " + i2);
                    meariDeviceTalkVolumeListener.onTalkVolume(i2);
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerRecordVolumeListener
                public void error(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->startVoiceTalk--3--listener--error: " + str);
                    meariDeviceTalkVolumeListener.onFailed(str);
                }
            });
        }
    }

    public void stopVoiceTalk(final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopVoiceTalk--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->stopVoiceTalk--start");
        this.cameraPlayer.stopvoicetalk(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.25
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->stopVoiceTalk--success: " + str);
                meariDeviceListener.onSuccess(str);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->stopVoiceTalk--failed: " + str);
                meariDeviceListener.onFailed(str);
            }
        });
    }

    public void changeG711u2WAV(String str, String str2, final MeariDeviceListener meariDeviceListener) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->changeG711u2WAV--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        cameraPlayer.changeG711u2WAV(str, str2, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.26
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str3) {
                meariDeviceListener.onSuccess(str3);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str3) {
                meariDeviceListener.onFailed(str3);
            }
        });
    }

    public void changeG711u2Pcm(String str, String str2, final MeariDeviceListener meariDeviceListener) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->changeG711u2Pcm--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        cameraPlayer.changeG711u2Pcm(str, str2, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.27
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str3) {
                meariDeviceListener.onSuccess(str3);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str3) {
                meariDeviceListener.onFailed(str3);
            }
        });
    }

    public void startRecordVoiceMail(String str, String str2) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startRecordVoiceMail--CameraPlayer is null");
            return;
        }
        String str3 = this.TAG;
        Logger.m3729i(str3, "--->startRecordVoiceMail--start--pcmPath: " + str + "; g711uPath: " + str2);
        this.cameraPlayer.startRecordVoice(str, str2);
    }

    public void stopRecordVoiceMail() {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopRecordVoiceMail--CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->stopRecordVoiceMail--start");
        this.cameraPlayer.stopRecordVoice();
    }

    public void startPTZControl(String str) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startPTZControl--Failed! CameraPlayer is null");
            return;
        }
        String str2 = this.TAG;
        Logger.m3729i(str2, "--->startPTZControl--" + str);
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 3739:
                if (str.equals(MeariMoveDirection.f106UP)) {
                    c = 0;
                    break;
                }
                break;
            case 3089570:
                if (str.equals(MeariMoveDirection.DOWN)) {
                    c = 1;
                    break;
                }
                break;
            case 3317767:
                if (str.equals(MeariMoveDirection.LEFT)) {
                    c = 2;
                    break;
                }
                break;
            case 108511772:
                if (str.equals(MeariMoveDirection.RIGHT)) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.cameraPlayer.startptz(0, 20, 0, null);
                return;
            case 1:
                this.cameraPlayer.startptz(0, -20, 0, null);
                return;
            case 2:
                this.cameraPlayer.startptz(-80, 0, 0, null);
                return;
            case 3:
                this.cameraPlayer.startptz(80, 0, 0, null);
                return;
            default:
                return;
        }
    }

    public void stopPTZControl() {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopPTZControl--Failed! CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->stopPTZControl");
        this.cameraPlayer.stopptz(new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.28
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
            }
        });
    }

    public void zoomVideo(float f) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->zoomVideo--CameraPlayer is null");
        } else {
            cameraPlayer.zoom2(f, cameraPlayer.getPlayMode());
        }
    }

    public void moveVideo(float f, float f2) {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->moveVideo--CameraPlayer is null");
        } else {
            cameraPlayer.move2(0.0f - f, f2, cameraPlayer.getPlayMode());
        }
    }

    public void updateToken(String str) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->updateToken--CameraPlayer is null");
            return;
        }
        String str2 = this.TAG;
        Logger.m3729i(str2, "--->updateToken--start--token: " + str);
        this.cameraPlayer.updatetoken(str);
    }

    public void setAp(String str, String str2, final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->setAp--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        String str3 = this.TAG;
        Logger.m3729i(str3, "--->setAp--start--wifiName: " + str + "; password: " + str2);
        this.cameraPlayer.setAP(str, str2, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.29
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str4) {
                String str5 = MeariDeviceController.this.TAG;
                Logger.m3729i(str5, "--->setAp--success: " + str4);
                meariDeviceListener.onSuccess(str4);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str4) {
                String str5 = MeariDeviceController.this.TAG;
                Logger.m3729i(str5, "--->setAp--failed: " + str4);
                meariDeviceListener.onFailed(str4);
            }
        });
    }

    public void setWireDevice(String str, String str2) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->setWireDevice--CameraPlayer is null");
            return;
        }
        String str3 = this.TAG;
        Logger.m3729i(str3, "--->setWireDevice--start--wireConfigIp: " + str + "; token: " + str2);
        this.cameraPlayer.setlanwiredevice(str, str2);
    }

    public void sendVoiceMail(String str, final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->sendVoiceMail--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("url", str);
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/voicemail");
        String str2 = this.TAG;
        Logger.m3729i(str2, "--->sendVoiceMail--start--url: " + str);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.30
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str3) {
                String str4 = MeariDeviceController.this.TAG;
                Logger.m3729i(str4, "--->sendVoiceMail--success: " + str3);
                meariDeviceListener.onSuccess(str3);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str3) {
                String str4 = MeariDeviceController.this.TAG;
                Logger.m3729i(str4, "--->sendVoiceMail--failed: " + str3);
                meariDeviceListener.onFailed(str3);
            }
        });
    }

    public void reboot(final MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->reboot--CameraPlayer is null");
            return;
        }
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", " http://127.0.0.1/devices/reboot");
        baseJSONObject.put("reboot", "1");
        Logger.m3729i(this.TAG, "--->reboot--start");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.31
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->reboot--success: " + str);
                meariDeviceListener.onSuccess(str);
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->reboot--failed: " + str);
                meariDeviceListener.onFailed(str);
            }
        });
    }

    public static void setSearchUrl(String str) {
        CameraPlayer.mSearchUrl = str;
    }

    public void startRecordWav(String str, MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startRecordWav--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        String str2 = this.TAG;
        Logger.m3729i(str2, "--->startRecordWav--start--path: " + str);
        int startRecordWavFiles = this.cameraPlayer.startRecordWavFiles(str);
        if (startRecordWavFiles >= 0) {
            Logger.m3729i(this.TAG, "--->startRecordWav--success");
            meariDeviceListener.onSuccess("start record Wav success - status: " + startRecordWavFiles);
            return;
        }
        Logger.m3729i(this.TAG, "--->startRecordWav--failed");
        meariDeviceListener.onFailed("start record Wav failed - status: " + startRecordWavFiles);
    }

    public void stopRecordWav(MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopRecordWav--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->stopRecordWav--start");
        int stopRecordWavFiles = this.cameraPlayer.stopRecordWavFiles();
        if (stopRecordWavFiles >= 0) {
            Logger.m3729i(this.TAG, "--->stopRecordWav--success");
            meariDeviceListener.onSuccess("stop record Wav success - status: " + stopRecordWavFiles);
            return;
        }
        Logger.m3729i(this.TAG, "--->stopRecordWav--failed");
        meariDeviceListener.onFailed("stop record Wav failed - status: " + stopRecordWavFiles);
    }

    public void startChangeVoice(int i, MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->startChangeVoice--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        String str = this.TAG;
        Logger.m3729i(str, "--->startChangeVoice--start--voiceType :" + i);
        if (this.cameraPlayer.startSoundTouch(i) >= 0) {
            Logger.m3729i(this.TAG, "--->startChangeVoice--success");
            meariDeviceListener.onSuccess("start change voice success");
            return;
        }
        Logger.m3729i(this.TAG, "--->startChangeVoice--failed");
        meariDeviceListener.onFailed("start change voice failed");
    }

    public void stopChangeVoice(MeariDeviceListener meariDeviceListener) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->stopChangeVoice--CameraPlayer is null");
            meariDeviceListener.onFailed("CameraPlayer is null");
            return;
        }
        Logger.m3729i(this.TAG, "--->stopChangeVoice--start");
        if (this.cameraPlayer.stopSoundTouch() >= 0) {
            Logger.m3729i(this.TAG, "--->stopChangeVoice--success:");
            meariDeviceListener.onSuccess("stop change voice success");
            return;
        }
        Logger.m3729i(this.TAG, "--->stopChangeVoice--failed:");
        meariDeviceListener.onFailed("stop change voice failed");
    }

    public int getIntercomVolume() {
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->getIntercomVolume--CameraPlayer is null");
            return 0;
        }
        int volumePower = cameraPlayer.getVolumePower();
        String str = this.TAG;
        Logger.m3729i(str, "--->getIntercomVolume: " + volumePower);
        return volumePower;
    }

    public void setMicrophoneEnable(int i) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->setMicrophoneEnable--CameraPlayer is null");
            return;
        }
        String str = this.TAG;
        Logger.m3729i(str, "--->setMicrophoneEnable: " + i);
        this.cameraPlayer.setEnableVQEVoice(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getDeviceParams(final IGetDeviceParamsCallback iGetDeviceParamsCallback) {
        if (isConnected()) {
            getDeviceParamsImpl(iGetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.32
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getDeviceParamsImpl(iGetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iGetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDeviceParamsImpl(final IGetDeviceParamsCallback iGetDeviceParamsCallback) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->getDeviceParamsImpl--CameraPlayer is null");
            iGetDeviceParamsCallback.onFailed(0, "CameraPlayer is null");
            return;
        }
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "GET");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.33
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getDeviceParams--P2P: " + str);
                DeviceParams deviceParamsP2p = JsonUtil.getDeviceParamsP2p(str);
                if (deviceParamsP2p == null) {
                    iGetDeviceParamsCallback.onFailed(103, "Parse json error");
                } else {
                    iGetDeviceParamsCallback.onSuccess(deviceParamsP2p);
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getDeviceParams--P2P: " + str);
                iGetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getRefreshInfo(final IGetRefreshInfoCallback iGetRefreshInfoCallback) {
        if (isConnected()) {
            getRefreshInfoImpl(iGetRefreshInfoCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.34
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getRefreshInfoImpl(iGetRefreshInfoCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iGetRefreshInfoCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getRefreshInfoImpl(final IGetRefreshInfoCallback iGetRefreshInfoCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "GET");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/network");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.35
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getRefreshInfo--P2P: " + str);
                RefreshInfo refreshInfoP2p = JsonUtil.getRefreshInfoP2p(str);
                if (refreshInfoP2p == null) {
                    iGetRefreshInfoCallback.onFailed(103, "Parse json error");
                } else {
                    iGetRefreshInfoCallback.onSuccess(refreshInfoP2p);
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getRefreshInfo--P2P: " + str);
                iGetRefreshInfoCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getTemperatureHumidity(final IRefreshTempAndHumCallback iRefreshTempAndHumCallback) {
        if (isConnected()) {
            getTemperatureHumidityImpl(iRefreshTempAndHumCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.36
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getTemperatureHumidityImpl(iRefreshTempAndHumCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iRefreshTempAndHumCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getTemperatureHumidityImpl(final IRefreshTempAndHumCallback iRefreshTempAndHumCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "GET");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/temp_humidity/value");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.37
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getTemperatureHumidity--P2P: " + str);
                try {
                    BaseJSONObject baseJSONObject2 = new BaseJSONObject(str);
                    baseJSONObject2.optDouble("temperature_error", -10.0d);
                    baseJSONObject2.optDouble("humidity_error", -10.0d);
                    iRefreshTempAndHumCallback.onSuccess((float) baseJSONObject2.optDouble("temperature_c", -10.0d), (float) baseJSONObject2.optDouble("humidity", 0.0d));
                } catch (JSONException e) {
                    Logger.m3730e(getClass().getName(), e.getMessage());
                    iRefreshTempAndHumCallback.onFailed(103, "Parse json error");
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getTemperatureHumidity--P2P: " + str);
                iRefreshTempAndHumCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getMusicVolume(final IGetMusicVolumeCallback iGetMusicVolumeCallback) {
        if (isConnected()) {
            getMusicVolumeImpl(iGetMusicVolumeCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.38
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getMusicVolumeImpl(iGetMusicVolumeCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iGetMusicVolumeCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getMusicVolumeImpl(final IGetMusicVolumeCallback iGetMusicVolumeCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "GET");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/media/audio/output/volume");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.39
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getMusicVolume--P2P: " + str);
                try {
                    iGetMusicVolumeCallback.onSuccess(new BaseJSONObject(str).optInt("volume", 0));
                } catch (JSONException e) {
                    Logger.m3730e(getClass().getName(), e.getMessage());
                    iGetMusicVolumeCallback.onFailed(103, "Parse json error");
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getMusicVolume--P2P: " + str);
                iGetMusicVolumeCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMusicVolume(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setMusicVolumeImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.40
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setMusicVolumeImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMusicVolumeImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/media/audio/output/volume/" + i);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.41
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMusicVolume--P2P-volume: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getMusicVolume--P2P: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void switchMusic(final int i, final IControlMusicCallback iControlMusicCallback) {
        if (isConnected()) {
            switchMusicImpl(i, iControlMusicCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.42
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.switchMusicImpl(i, iControlMusicCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iControlMusicCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchMusicImpl(final int i, final IControlMusicCallback iControlMusicCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        if (i == 0) {
            baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/music/play/prev");
        } else {
            baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/music/play/next");
        }
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.43
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->switchMusic--P2P-status: " + i + "; successMsg: " + str);
                try {
                    iControlMusicCallback.onSuccess(new BaseJSONObject(str).optString("current_musicID", ""));
                } catch (JSONException e) {
                    Logger.m3730e(getClass().getName(), e.getMessage());
                    iControlMusicCallback.onFailed(103, "Parse json error");
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->switchMusic--P2P: " + str);
                iControlMusicCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void playMusic(final String str, final IControlMusicCallback iControlMusicCallback) {
        if (isConnected()) {
            playMusicImpl(str, iControlMusicCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.44
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str2) {
                    MeariDeviceController.this.playMusicImpl(str, iControlMusicCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str2) {
                    iControlMusicCallback.onFailed(102, str2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playMusicImpl(final String str, final IControlMusicCallback iControlMusicCallback) {
        String format = String.format("http://127.0.0.1/devices/music/id/%s/start", str);
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", format);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.45
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->playMusic--P2P-musicID: " + str + "; successMsg: " + str2);
                try {
                    iControlMusicCallback.onSuccess(new BaseJSONObject(str2).optString("current_musicID", ""));
                } catch (JSONException e) {
                    Logger.m3730e(getClass().getName(), e.getMessage());
                    iControlMusicCallback.onFailed(103, "Parse json error");
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->playMusic--P2P: " + str2);
                iControlMusicCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pauseMusic(final IControlMusicCallback iControlMusicCallback) {
        if (isConnected()) {
            pauseMusicImpl(iControlMusicCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.46
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.pauseMusicImpl(iControlMusicCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iControlMusicCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pauseMusicImpl(final IControlMusicCallback iControlMusicCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/music/play/pause");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.47
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->pauseMusic--P2P: ; successMsg: " + str);
                iControlMusicCallback.onSuccess("");
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->pauseMusic--P2P: " + str);
                iControlMusicCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getPlayMusicStatus(final IRefreshMusicStatusCallback iRefreshMusicStatusCallback) {
        if (isConnected()) {
            getPlayMusicStatusImpl(iRefreshMusicStatusCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.48
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getPlayMusicStatusImpl(iRefreshMusicStatusCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iRefreshMusicStatusCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getPlayMusicStatusImpl(final IRefreshMusicStatusCallback iRefreshMusicStatusCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "GET");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/music/play/state");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.49
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getPlayMusicStatus--P2P: ; successMsg: " + str);
                try {
                    BaseJSONObject baseJSONObject2 = new BaseJSONObject(str);
                    BaseJSONArray optBaseJSONArray = baseJSONObject2.optBaseJSONArray("play_list");
                    ArrayList<MeariMusic> arrayList = null;
                    String optString = baseJSONObject2.optString("current_musicID", "");
                    boolean optBoolean = baseJSONObject2.optBoolean("is_playing", false);
                    baseJSONObject2.optString("mode", "");
                    if (optBaseJSONArray != null) {
                        arrayList = JsonUtil.getSongList(optBaseJSONArray);
                    }
                    iRefreshMusicStatusCallback.onSuccess(optString, optBoolean, arrayList);
                } catch (JSONException e) {
                    Logger.m3730e(getClass().getName(), e.getMessage());
                    iRefreshMusicStatusCallback.onFailed(101, "Parse json error");
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getPlayMusicStatus--P2P: " + str);
                iRefreshMusicStatusCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMusicPlayMode(final String str, final IControlMusicCallback iControlMusicCallback) {
        if (isConnected()) {
            setMusicPlayModeImpl(str, iControlMusicCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.50
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str2) {
                    MeariDeviceController.this.setMusicPlayModeImpl(str, iControlMusicCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str2) {
                    iControlMusicCallback.onFailed(102, str2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMusicPlayModeImpl(final String str, final IControlMusicCallback iControlMusicCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/music/play/" + str);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.51
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setMusicPlayMode--P2P--mode: " + str + "; successMsg: " + str2);
                try {
                    iControlMusicCallback.onSuccess(new BaseJSONObject(str2).optString("current_musicID", ""));
                } catch (JSONException e) {
                    Logger.m3730e(getClass().getName(), e.getMessage());
                    iControlMusicCallback.onFailed(103, "Parse json error");
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setMusicPlayMode--P2P: " + str2);
                iControlMusicCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getSDCardInfo(final ISDCardInfoCallback iSDCardInfoCallback) {
        if (isConnected()) {
            getSDCardInfoImpl(iSDCardInfoCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.52
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getSDCardInfoImpl(iSDCardInfoCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSDCardInfoCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSDCardInfoImpl(final ISDCardInfoCallback iSDCardInfoCallback) {
        if (this.cameraPlayer == null) {
            Logger.m3729i(this.TAG, "--->getSDCardInfoImpl--CameraPlayer is null");
            iSDCardInfoCallback.onFailed(0, "CameraPlayer is null");
            return;
        }
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "GET");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/storage");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.53
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getSDCardInfo--P2P--success: " + str);
                SDCardInfo sDCardInfoP2p = JsonUtil.getSDCardInfoP2p(str);
                if (sDCardInfoP2p == null) {
                    iSDCardInfoCallback.onFailed(103, "Parse json error");
                } else {
                    iSDCardInfoCallback.onSuccess(sDCardInfoP2p);
                }
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getSDCardInfo--P2P--failed: " + str);
                iSDCardInfoCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startSDCardFormat(final ISDCardFormatCallback iSDCardFormatCallback) {
        if (isConnected()) {
            startSDCardFormatImpl(iSDCardFormatCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.54
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.startSDCardFormatImpl(iSDCardFormatCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSDCardFormatCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSDCardFormatImpl(final ISDCardFormatCallback iSDCardFormatCallback) {
        this.cameraPlayer.setdeviceparams(3, "", new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.55
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->SDCardFormat--P2P-Success: " + str);
                iSDCardFormatCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->SDCardFormat--P2P-Failed: " + str);
                iSDCardFormatCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getSDCardFormatPercent(final ISDCardFormatPercentCallback iSDCardFormatPercentCallback) {
        if (isConnected()) {
            getSDCardFormatPercentImpl(iSDCardFormatPercentCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.56
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getSDCardFormatPercentImpl(iSDCardFormatPercentCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSDCardFormatPercentCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getSDCardFormatPercentImpl(final ISDCardFormatPercentCallback iSDCardFormatPercentCallback) {
        Logger.m3729i(this.TAG, "--->SDCardFormatPercent-P2P-start");
        this.cameraPlayer.getdeviceparams(5, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.57
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->SDCardFormatPercent--P2P: " + str);
                iSDCardFormatPercentCallback.onSuccess(JsonUtil.getSDCardFormatPercent(str));
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->SDCardFormatPercent--P2P: " + str);
                iSDCardFormatPercentCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startDeviceUpgrade(final String str, final String str2, final IDeviceUpgradeCallback iDeviceUpgradeCallback) {
        if (isConnected()) {
            startDeviceUpgradeImpl(str, str2, iDeviceUpgradeCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.58
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str3) {
                    MeariDeviceController.this.startDeviceUpgradeImpl(str, str2, iDeviceUpgradeCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str3) {
                    iDeviceUpgradeCallback.onFailed(102, str3);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startDeviceUpgradeImpl(String str, String str2, final IDeviceUpgradeCallback iDeviceUpgradeCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("url", str);
        baseJSONObject.put("firmwareversion", str2 + "-upgrade.bin");
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/firmware_upgrade");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.59
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str3) {
                String str4 = MeariDeviceController.this.TAG;
                Logger.m3729i(str4, "--->DeviceUpgradeStart--P2P-Success: " + str3);
                iDeviceUpgradeCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str3) {
                String str4 = MeariDeviceController.this.TAG;
                Logger.m3729i(str4, "--->DeviceUpgradeStart--P2P-Failed: " + str3);
                iDeviceUpgradeCallback.onFailed(-1, str3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getDeviceUpgradePercent(final IDeviceUpgradePercentCallback iDeviceUpgradePercentCallback) {
        if (isConnected()) {
            getDeviceUpgradePercentImpl(iDeviceUpgradePercentCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.60
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getDeviceUpgradePercentImpl(iDeviceUpgradePercentCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iDeviceUpgradePercentCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDeviceUpgradePercentImpl(final IDeviceUpgradePercentCallback iDeviceUpgradePercentCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "GET");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/upgradeprecent");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.61
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->DeviceUpgradePercent--P2P: " + str);
                iDeviceUpgradePercentCallback.onSuccess(JsonUtil.getDeviceUpgradeTotalPercent(str));
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->DeviceUpgradePercent--P2P: " + str);
                iDeviceUpgradePercentCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getDeviceUpgradeAllPercent(final IDeviceUpgradeAllPercentCallback iDeviceUpgradeAllPercentCallback) {
        if (isConnected()) {
            getDeviceUpgradeAllPercentImpl(iDeviceUpgradeAllPercentCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.62
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getDeviceUpgradeAllPercentImpl(iDeviceUpgradeAllPercentCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iDeviceUpgradeAllPercentCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getDeviceUpgradeAllPercentImpl(final IDeviceUpgradeAllPercentCallback iDeviceUpgradeAllPercentCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "GET");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/upgradeprecent");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.63
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->DeviceUpgradePercent--P2P: " + str);
                iDeviceUpgradeAllPercentCallback.onSuccess(JsonUtil.getDeviceUpgradeTotalPercent(str), JsonUtil.getDeviceUpgradeDownloadPercent(str), JsonUtil.getDeviceUpgradePercent(str));
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->DeviceUpgradePercent--P2P: " + str);
                iDeviceUpgradeAllPercentCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPlaybackRecordVideo(final int i, final int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setPlaybackRecordVideoImpl(i, i2, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.64
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setPlaybackRecordVideoImpl(i, i2, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPlaybackRecordVideoImpl(int i, final int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        final int storeTypeToP2p = SdkUtils.storeTypeToP2p(i);
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("enable", storeTypeToP2p);
        baseJSONObject.put("duration", i2);
        baseJSONObject.put("continuity", 1);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/sd_event");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.65
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setPlaybackRecordVideo--P2P: storeType: " + storeTypeToP2p + "; duration: " + i2 + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setPlaybackRecordVideo--P2P: storeType: " + storeTypeToP2p + "; duration: " + i2 + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSdRecordVideoEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setSdRecordVideoEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.66
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setSdRecordVideoEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSdRecordVideoEnableImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("record_enable", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.67
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setSdRecordVideoEnable--P2P: enable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setSdRecordVideoEnable--P2P: enable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMirror(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setMirrorImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.68
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setMirrorImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMirrorImpl(int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        final int mirrorToP2p = SdkUtils.mirrorToP2p(i);
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("mirror", mirrorToP2p);
        this.cameraPlayer.setdeviceparams(0, baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.69
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMirror--P2P: mirrorEnable: " + mirrorToP2p + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMirror--P2P: mirrorEnable: " + mirrorToP2p + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLED(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setLEDImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.70
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setLEDImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLEDImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/led/all");
        baseJSONObject.put("enable", i);
        this.cameraPlayer.commondeviceparams(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.71
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setLED--P2P: ledEnable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setLED--P2P: ledEnable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMechanicalChimeEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setMechanicalChimeEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.72
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setMechanicalChimeEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMechanicalChimeEnableImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("external_charm", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("bell", baseJSONObject2);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.73
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMechanicalChimeEnable--P2P: status: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMechanicalChimeEnable--P2P: status: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDayNightMode(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setDayNightModeImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.74
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setDayNightModeImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDayNightModeImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("day_night_mode", i);
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.75
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setDayNightMode--P2P: dayNightMode: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setDayNightMode--P2P: dayNightMode: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSleepMode(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setSleepModeImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.76
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setSleepModeImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSleepModeImpl(int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        final String sleepModeToP2p = SdkUtils.sleepModeToP2p(i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("sleep", sleepModeToP2p);
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.77
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setSleepMode--P2P: sleepMode: " + sleepModeToP2p + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setSleepMode--P2P: sleepMode: " + sleepModeToP2p + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSleepModeTimes(final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setSleepModeTimesImpl(str, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.78
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str2) {
                    MeariDeviceController.this.setSleepModeTimesImpl(str, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str2) {
                    iSetDeviceParamsCallback.onFailed(102, str2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSleepModeTimesImpl(final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONArray baseJSONArray;
        try {
            if (TextUtils.isEmpty(str)) {
                baseJSONArray = new BaseJSONArray(GsonUtil.EMPTY_JSON_ARRAY);
            } else {
                baseJSONArray = new BaseJSONArray(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            baseJSONArray = null;
        }
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("sleep_time", baseJSONArray);
        baseJSONObject.put("sleep", CameraSleepType.SLEEP_TIME);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.79
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setSleepModeTimes--P2P: timeList: " + str + "; successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setSleepModeTimes--P2P: timeList: " + str + "; errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAlarmTimes(final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setAlarmTimesImpl(str, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.80
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str2) {
                    MeariDeviceController.this.setAlarmTimesImpl(str, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str2) {
                    iSetDeviceParamsCallback.onFailed(102, str2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAlarmTimesImpl(final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONArray baseJSONArray;
        try {
            if (TextUtils.isEmpty(str)) {
                baseJSONArray = new BaseJSONArray(GsonUtil.EMPTY_JSON_ARRAY);
            } else {
                baseJSONArray = new BaseJSONArray(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            baseJSONArray = null;
        }
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("alarm_plan", baseJSONArray);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.81
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setAlarmTimes--P2P: timeList: " + str + "; successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setAlarmTimes--P2P: timeList: " + str + "; errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLightingTimes(final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setLightingTimesImpl(str, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.82
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str2) {
                    MeariDeviceController.this.setLightingTimesImpl(str, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str2) {
                    iSetDeviceParamsCallback.onFailed(102, str2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLightingTimesImpl(final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONArray baseJSONArray;
        try {
            if (TextUtils.isEmpty(str)) {
                baseJSONArray = new BaseJSONArray(GsonUtil.EMPTY_JSON_ARRAY);
            } else {
                baseJSONArray = new BaseJSONArray(str);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            baseJSONArray = null;
        }
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("schedule_array", baseJSONArray);
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("action", "POST");
        baseJSONObject2.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject2.put("flight", baseJSONObject);
        this.cameraPlayer.commondeviceparams2(baseJSONObject2.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.83
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setLightingTimes--P2P: timeList: " + str + "; successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setLightingTimes--P2P: timeList: " + str + "; errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnvif(final int i, final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setOnvifImpl(i, str, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.84
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str2) {
                    MeariDeviceController.this.setOnvifImpl(i, str, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str2) {
                    iSetDeviceParamsCallback.onFailed(102, str2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOnvifImpl(final int i, final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("onvif_enable", i);
        baseJSONObject.put("device_password", BaseUtils.getEncodedString(str));
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.85
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setOnvif--P2P: enable: " + i + "; password: " + str + "; successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setOnvif--P2P: enable: " + i + "; password: " + str + "; errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVideoEncoding(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setVideoEncodingImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.86
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setVideoEncodingImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setVideoEncodingImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("video_enc", i);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.87
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setVideoEncoding--P2P: type: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setVideoEncoding--P2P: type: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMotion(final int i, final int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setMotionImpl(i, i2, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.88
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setMotionImpl(i, i2, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMotionImpl(final int i, int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        final int motionSensitivityToP2p = SdkUtils.motionSensitivityToP2p(i2);
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("enable", i);
        baseJSONObject.put("sensitivity", motionSensitivityToP2p);
        this.cameraPlayer.setdeviceparams(1, baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.89
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMotion--P2P: motionDetEnable: " + i + "; sensitivity: " + motionSensitivityToP2p + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMotion--P2P: motionDetEnable: " + i + "; sensitivity: " + motionSensitivityToP2p + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPirDetection(final int i, final int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setPirDetectionImpl(i, i2, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.90
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setPirDetectionImpl(i, i2, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPirDetectionImpl(final int i, int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        final int pirSensitivityToP2p = SdkUtils.pirSensitivityToP2p(i2);
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        BaseJSONObject baseJSONObject3 = new BaseJSONObject();
        baseJSONObject3.put("enable", i);
        baseJSONObject3.put("level", pirSensitivityToP2p);
        baseJSONObject2.put("pir", baseJSONObject3);
        baseJSONObject.put("bell", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.91
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setPirDetection--P2P: pirDetEnable: " + i + "; sensitivity: " + pirSensitivityToP2p + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setPirDetection--P2P: pirDetEnable: " + i + "; sensitivity: " + pirSensitivityToP2p + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPirDetectionEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setPirDetectionEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.92
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setPirDetectionEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPirDetectionEnableImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        BaseJSONObject baseJSONObject3 = new BaseJSONObject();
        baseJSONObject3.put("enable", i);
        baseJSONObject2.put("pir", baseJSONObject3);
        baseJSONObject.put("bell", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.93
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setPirDetectionEnable--P2P: pirEnable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setPirDetectionEnable--P2P: pirEnable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPirDetectionSensitivity(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setPirDetectionSensitivityImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.94
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setPirDetectionSensitivityImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPirDetectionSensitivityImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        BaseJSONObject baseJSONObject3 = new BaseJSONObject();
        baseJSONObject3.put("level", i);
        baseJSONObject2.put("pir", baseJSONObject3);
        baseJSONObject.put("bell", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.95
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setPirDetectionSensitivity--P2P: pirDetSensitivity: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setPirDetectionSensitivity--P2P: pirDetSensitivity: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAlarmArea(final RoiInfo roiInfo, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setAlarmAreaImpl(roiInfo, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.96
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setAlarmAreaImpl(roiInfo, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAlarmAreaImpl(final RoiInfo roiInfo, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", roiInfo.getEnable());
        baseJSONObject2.put("width", roiInfo.getWidth());
        baseJSONObject2.put("height", roiInfo.getHeight());
        baseJSONObject2.put("bitmap", roiInfo.getBitmap());
        baseJSONObject.put("roi", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.97
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setAlarmAreaImpl--P2P: roiInfo: " + roiInfo.toString() + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setAlarmAreaImpl--P2P: roiInfo: " + roiInfo.toString() + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAlarmFrequency(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setAlarmFrequencyImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.98
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setAlarmFrequencyImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAlarmFrequencyImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("alarm_feq", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.99
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setAlarmFrequency--P2P: alarmFrequency: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setAlarmFrequency--P2P: alarmFrequency: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSoundDetection(final int i, final int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setSoundDetectionImpl(i, i2, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.100
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setSoundDetectionImpl(i, i2, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSoundDetectionImpl(final int i, int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        final int soundSensitivityToP2p = SdkUtils.soundSensitivityToP2p(i2);
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", i);
        baseJSONObject2.put("threshold", soundSensitivityToP2p);
        baseJSONObject.put("decibel_alarm", baseJSONObject2);
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.101
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setSoundDetection--P2P: soundDetEnable: " + i + "; sensitivity: " + soundSensitivityToP2p + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setSoundDetection--P2P: soundDetEnable: " + i + "; sensitivity: " + soundSensitivityToP2p + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCryDetection(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setCryDetectionImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.102
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setCryDetectionImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCryDetectionImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", i);
        baseJSONObject.put("cry_detect", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.103
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setCryDetection--P2P: cryDetEnable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setCryDetection--P2P: cryDetEnable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAbnormalNoiseInspection(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setAbnormalNoiseInspectionImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.104
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setAbnormalNoiseInspectionImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAbnormalNoiseInspectionImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", i);
        baseJSONObject.put("cry_detect", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.105
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setAbnormalNoiseInspection--P2P: enable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setAbnormalNoiseInspection--P2P: enable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWorkMode(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setWorkModeImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.106
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setWorkModeImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWorkModeImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("workmode", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("bell", baseJSONObject2);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.107
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setWorkModeImpl--P2P: volume: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setWorkModeImpl--P2P: volume: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAntiflicker(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setAntiflickerImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.108
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setAntiflickerImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAntiflickerImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("antiflicker", i);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.109
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setAntiflickerImpl--P2P: volume: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setAntiflickerImpl--P2P: volume: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setMicroPhone(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setMicroPhoneImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.110
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setMicroPhoneImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMicroPhoneImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("microphone", i);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.111
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMicroPhoneImpl--P2P: volume: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMicroPhoneImpl--P2P: volume: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSpeaker(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setSpeakerImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.112
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setSpeakerImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSpeakerImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("speaker", i);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.113
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMicroPhoneImpl--P2P: volume: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setMicroPhoneImpl--P2P: volume: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRae(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setRaeImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.114
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setRaeImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRaeImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("rec_audio_en", i);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.115
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setRaeImpl--P2P: volume: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setRaeImpl--P2P: volume: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHumanDetection(final int i, final int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setHumanDetectionImpl(i, i2, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.116
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setHumanDetectionImpl(i, i2, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHumanDetectionImpl(final int i, final int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", i);
        baseJSONObject2.put("bnddraw", i2);
        baseJSONObject.put("people_detect", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.117
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setHumanDetection--P2P: humanDetEnable: " + i + "; humanFrameEnable: " + i2 + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setHumanDetection--P2P: humanDetEnable: " + i + "; humanFrameEnable: " + i2 + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHumanDetectionDay(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setHumanDetectionDayImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.118
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setHumanDetectionDayImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHumanDetectionDayImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable_day_filter", i);
        baseJSONObject.put("people_detect", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.119
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setHumanDetectionDay--P2P: humanDetDayEnable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setHumanDetectionDay--P2P: humanDetDayEnable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHumanDetectionNight(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setHumanDetectionNightImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.120
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setHumanDetectionNightImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHumanDetectionNightImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable_night_filter", i);
        baseJSONObject.put("people_detect", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.121
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setHumanDetectionNight--P2P: humanDetNightEnable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setHumanDetectionNight--P2P: humanDetNightEnable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHumanTrack(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setHumanTrackImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.122
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setHumanTrackImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHumanTrackImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", i);
        baseJSONObject.put("people_track", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.123
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setHumanTrack--P2P: humanTrackEnable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setHumanTrack--P2P: humanTrackEnable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSpeakVolume(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setSpeakVolumeImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.124
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setSpeakVolumeImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSpeakVolumeImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("volume", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("bell", baseJSONObject2);
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.125
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setSpeakVolume--P2P: volume: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setSpeakVolume--P2P: volume: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unlockBattery(final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            unlockBatteryImpl(iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.126
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.unlockBatteryImpl(iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unlockBatteryImpl(final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("batterylock", 1);
        baseJSONObject.put("bell", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.127
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->unlockBattery--P2P: successMsg" + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->unlockBattery--P2P: errorMsg" + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void bindWirelessChime(final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            bindWirelessChimeImpl(iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.128
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.bindWirelessChimeImpl(iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindWirelessChimeImpl(final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/charm/pair");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.129
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->bindWirelessChime--P2P: successMsg" + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->bindWirelessChime--P2P: errorMsg" + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unbindWirelessChime(final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            unbindWirelessChimeImpl(iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.130
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.unbindWirelessChimeImpl(iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unbindWirelessChimeImpl(final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/charm/unpair");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.131
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->unbindWirelessChime--P2P: successMsg" + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->unbindWirelessChime--P2P: errorMsg" + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWirelessChimeVolume(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setWirelessChimeVolumeImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.132
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setWirelessChimeVolumeImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWirelessChimeVolumeImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        BaseJSONObject baseJSONObject3 = new BaseJSONObject();
        baseJSONObject3.put("volume", i);
        baseJSONObject2.put("charm", baseJSONObject3);
        baseJSONObject.put("bell", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.133
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setWirelessChimeVolume--P2P: volume: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setWirelessChimeVolume--P2P: volume: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWirelessChimeSong(final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setWirelessChimeSongImpl(str, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.134
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str2) {
                    MeariDeviceController.this.setWirelessChimeSongImpl(str, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str2) {
                    iSetDeviceParamsCallback.onFailed(102, str2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWirelessChimeSongImpl(final String str, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        BaseJSONObject baseJSONObject3 = new BaseJSONObject();
        baseJSONObject3.put("selected", str);
        baseJSONObject2.put("charm", baseJSONObject3);
        baseJSONObject.put("bell", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.135
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setWirelessChimeSong--P2P: song: " + str + "; successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setWirelessChimeSong--P2P: song: " + str + "; errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWirelessChimeEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setWirelessChimeEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.136
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setWirelessChimeEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWirelessChimeEnableImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        BaseJSONObject baseJSONObject3 = new BaseJSONObject();
        baseJSONObject3.put("enable", i);
        baseJSONObject2.put("charm", baseJSONObject3);
        baseJSONObject.put("bell", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.137
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setWirelessChimeEnable--P2P: enable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setWirelessChimeEnable--P2P: enable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCloudUploadEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setCloudUploadEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.138
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setCloudUploadEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCloudUploadEnableImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("cloud_storage", baseJSONObject2);
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.139
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setCloudUploadEnable--P2P: enable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setCloudUploadEnable--P2P: enable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.140
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightEnableImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("always_on", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("flight", baseJSONObject2);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.141
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightEnable--P2P: enable: " + i + "; successMsg: " + str);
                    iSetDeviceParamsCallback.onSuccess();
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightEnable--P2P: enable: " + i + "; errorMsg: " + str);
                    iSetDeviceParamsCallback.onFailed(-1, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightBrightness(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightBrightnessImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.142
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightBrightnessImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightBrightnessImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("light_level", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("flight", baseJSONObject2);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.143
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightBrightness--P2P: percent: " + i + "; successMsg: " + str);
                    iSetDeviceParamsCallback.onSuccess();
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightBrightness--P2P: percent: " + i + "; errorMsg: " + str);
                    iSetDeviceParamsCallback.onFailed(-1, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightSiren(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightSirenImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.144
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightSirenImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightSirenImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("siren", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("flight", baseJSONObject2);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.145
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightSiren--P2P--enable: " + i + "; successMsg: " + str);
                    iSetDeviceParamsCallback.onSuccess();
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightSiren--P2P: ; errorMsg: " + str);
                    iSetDeviceParamsCallback.onFailed(-1, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightPirEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightPirEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.146
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightPirEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightPirEnableImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("pir_enable", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("flight", baseJSONObject2);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.147
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightPirEnable--P2P--enable: " + i + "; successMsg: " + str);
                    iSetDeviceParamsCallback.onSuccess();
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightPirEnable--P2P: ; errorMsg: " + str);
                    iSetDeviceParamsCallback.onFailed(-1, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightManualLightingDuration(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightManualLightingDurationImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.148
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightManualLightingDurationImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightManualLightingDurationImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("always_on_duration", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("flight", baseJSONObject2);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.149
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightManualLightingDuration--P2P--enable: " + i + "; successMsg: " + str);
                    iSetDeviceParamsCallback.onSuccess();
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightManualLightingDuration--P2P: ; errorMsg: " + str);
                    iSetDeviceParamsCallback.onFailed(-1, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightLinkSirenEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightLinkSirenEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.150
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightLinkSirenEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightLinkSirenEnableImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("siren_enable", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("flight", baseJSONObject2);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.151
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightLinkSirenEnable--P2P--enable: " + i + "; successMsg: " + str);
                    iSetDeviceParamsCallback.onSuccess();
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightLinkSirenEnable--P2P: ; errorMsg: " + str);
                    iSetDeviceParamsCallback.onFailed(-1, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightPirDuration(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightPirDurationImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.152
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightPirDurationImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightPirDurationImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("pir_duration", i);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("flight", baseJSONObject2);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        if (cameraPlayer != null) {
            cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.153
                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPSuccessHandler(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightPirDuration--P2P--duration: " + i + "; successMsg: " + str);
                    iSetDeviceParamsCallback.onSuccess();
                }

                @Override // com.ppstrong.ppsplayer.CameraPlayerListener
                public void PPFailureError(String str) {
                    String str2 = MeariDeviceController.this.TAG;
                    Logger.m3729i(str2, "--->setFlightPirDuration--P2P: ; errorMsg: " + str);
                    iSetDeviceParamsCallback.onFailed(-1, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightSchedule(final int i, final String str, final String str2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightScheduleImpl(i, str, str2, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.154
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str3) {
                    MeariDeviceController.this.setFlightScheduleImpl(i, str, str2, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str3) {
                    iSetDeviceParamsCallback.onFailed(102, str3);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightScheduleImpl(int i, String str, String str2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        BaseJSONObject baseJSONObject3 = new BaseJSONObject();
        baseJSONObject3.put("enable", i);
        baseJSONObject3.put("from", str);
        baseJSONObject3.put("to", str2);
        baseJSONObject2.put("schedule", baseJSONObject3);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        baseJSONObject.put("flight", baseJSONObject2);
        String str3 = this.TAG;
        Logger.m3729i(str3, "--->setFlightSchedule--P2P--scheduleJson: " + baseJSONObject3.toString());
        CameraPlayer cameraPlayer = this.cameraPlayer;
        Objects.requireNonNull(cameraPlayer);
        cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.155
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str4) {
                String str5 = MeariDeviceController.this.TAG;
                Logger.m3729i(str5, "--->setFlightSchedule--P2P--successMsg: " + str4);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str4) {
                String str5 = MeariDeviceController.this.TAG;
                Logger.m3729i(str5, "--->setFlightSchedule--P2P--errorMsg: " + str4);
                iSetDeviceParamsCallback.onFailed(-1, str4);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightSoundLightAlarmEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightSoundLightAlarmEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.156
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightSoundLightAlarmEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightSoundLightAlarmEnableImpl(int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("sla_enable", i);
        baseJSONObject.put("sla", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        String str = this.TAG;
        Logger.m3729i(str, "--->setFlightSoundLightAlarmEnable--P2P--start-enable: " + i);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        Objects.requireNonNull(cameraPlayer);
        cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.157
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setFlightSoundLightAlarmEnable--P2P--successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setFlightSoundLightAlarmEnable--P2P--errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFlightSoundLightAlarmType(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setFlightSoundLightAlarmTypeImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.158
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setFlightSoundLightAlarmTypeImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFlightSoundLightAlarmTypeImpl(int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("sla_effect", i);
        baseJSONObject.put("sla", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        String str = this.TAG;
        Logger.m3729i(str, "--->setFlightSoundLightAlarmType--P2P--start-alarmType: " + i);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        Objects.requireNonNull(cameraPlayer);
        cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.159
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setFlightSoundLightAlarmType--P2P--successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setFlightSoundLightAlarmType--P2P--errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDoublePirStatus(final int i, final int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setDoublePirStatusImpl(i, i2, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.160
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setDoublePirStatusImpl(i, i2, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDoublePirStatusImpl(int i, int i2, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        int pirSensitivityToP2p = SdkUtils.pirSensitivityToP2p(i2);
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        BaseJSONObject baseJSONObject3 = new BaseJSONObject();
        baseJSONObject3.put("enable", i);
        baseJSONObject3.put("level", pirSensitivityToP2p);
        baseJSONObject2.put("pir", baseJSONObject3);
        baseJSONObject.put("bell", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        String str = this.TAG;
        Logger.m3729i(str, "--->setDoublePirStatus--P2P--start-alarmType: " + i);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        Objects.requireNonNull(cameraPlayer);
        cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.161
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setDoublePirStatus--P2P--successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setDoublePirStatus--P2P--errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    public void getFaceDetectResult(final byte[] bArr, final IGetFaceDetectResultCallback iGetFaceDetectResultCallback) {
        if (isConnected()) {
            getFaceDetectResultImpl(bArr, iGetFaceDetectResultCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.162
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.getFaceDetectResultImpl(bArr, iGetFaceDetectResultCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iGetFaceDetectResultCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getFaceDetectResultImpl(byte[] bArr, final IGetFaceDetectResultCallback iGetFaceDetectResultCallback) {
        this.cameraPlayer.requestFaceDetect(bArr, new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.163
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getFaceDetectResult--P2P: " + str);
                iGetFaceDetectResultCallback.onSuccess(Integer.parseInt(str));
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->getFaceDetectResult--P2P: " + str);
                iGetFaceDetectResultCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTimeFormatEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setTimeFormatEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.164
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setTimeFormatEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTimeFormatEnableImpl(int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("time_show_format", i);
        baseJSONObject.put("time_show", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        String str = this.TAG;
        Logger.m3729i(str, "--->setTimeFormatEnable--P2P--start-enable: " + i);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        Objects.requireNonNull(cameraPlayer);
        cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.165
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setTimeFormatEnable--P2P--successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setTimeFormatEnable--P2P--errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRelayEnable(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setRelayEnableImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.166
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setRelayEnableImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRelayEnableImpl(int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("relay_enable", i);
        baseJSONObject.put("bell", baseJSONObject2);
        String str = this.TAG;
        Logger.m3729i(str, "--->setRelayEnable--P2P--start-enable: " + i);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        Objects.requireNonNull(cameraPlayer);
        cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.167
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setRelayEnable--P2P--successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setRelayEnable--P2P--errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRelayStatus(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setRelayStatusImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.168
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setRelayStatusImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRelayStatusImpl(int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("relay", i);
        baseJSONObject.put("bell", baseJSONObject2);
        String str = this.TAG;
        Logger.m3729i(str, "--->setRelayStatus--P2P--start-status: " + i);
        CameraPlayer cameraPlayer = this.cameraPlayer;
        Objects.requireNonNull(cameraPlayer);
        cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.169
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setRelayStatus--P2P--successMsg: " + str2);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str2) {
                String str3 = MeariDeviceController.this.TAG;
                Logger.m3729i(str3, "--->setRelayStatus--P2P--errorMsg: " + str2);
                iSetDeviceParamsCallback.onFailed(-1, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRemoveProtectAlert(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setRemoveProtectAlertImpl(i, iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.170
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setRemoveProtectAlertImpl(i, iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDevicesReboot(final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        if (isConnected()) {
            setDevicesRebootImpl(iSetDeviceParamsCallback);
        } else {
            startConnect(new MeariDeviceListener() { // from class: com.meari.sdk.MeariDeviceController.171
                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onSuccess(String str) {
                    MeariDeviceController.this.setDevicesRebootImpl(iSetDeviceParamsCallback);
                }

                @Override // com.meari.sdk.listener.MeariDeviceListener
                public void onFailed(String str) {
                    iSetDeviceParamsCallback.onFailed(102, str);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRemoveProtectAlertImpl(final int i, final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", i);
        baseJSONObject.put("tamper_alarm", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/settings");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.172
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setRemoveProtectAlert--P2P: removeProtectAlertEnable: " + i + "; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setRemoveProtectAlert--P2P: removeProtectAlertEnable: " + i + "; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDevicesRebootImpl(final ISetDeviceParamsCallback iSetDeviceParamsCallback) {
        BaseJSONObject baseJSONObject = new BaseJSONObject();
        BaseJSONObject baseJSONObject2 = new BaseJSONObject();
        baseJSONObject2.put("enable", "1");
        baseJSONObject.put("reboot", baseJSONObject2);
        baseJSONObject.put("action", "POST");
        baseJSONObject.put("deviceurl", "http://127.0.0.1/devices/reboot");
        this.cameraPlayer.commondeviceparams2(baseJSONObject.toString(), new CameraPlayerListener() { // from class: com.meari.sdk.MeariDeviceController.173
            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPSuccessHandler(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setRemoveProtectAlert--P2P: removeProtectAlertEnable: ; successMsg: " + str);
                iSetDeviceParamsCallback.onSuccess();
            }

            @Override // com.ppstrong.ppsplayer.CameraPlayerListener
            public void PPFailureError(String str) {
                String str2 = MeariDeviceController.this.TAG;
                Logger.m3729i(str2, "--->setRemoveProtectAlert--P2P: removeProtectAlertEnable: ; errorMsg: " + str);
                iSetDeviceParamsCallback.onFailed(-1, str);
            }
        });
    }
}
