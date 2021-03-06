package com.github.screenlive;

import android.app.Activity;
import android.view.SurfaceHolder;

import com.github.screenlive.jni.PusherNative;
import com.github.screenlive.param.AudioParam;
import com.github.screenlive.param.VideoParam;
import com.github.screenlive.pusher.AudioPusher;
import com.github.screenlive.pusher.VideoPusher;


public class LivePusher {

	private final static String TAG = "LivePusher";
	private VideoParam videoParam;
	private AudioParam audioParam;
	private VideoPusher videoPusher;
	private PusherNative mNative;
	private AudioPusher audioPusher;
	private LiveStateChangeListener mListener;
	private Activity mActivity;

	static {
		System.loadLibrary("Pusher");
	}

	public LivePusher(Activity activity, int width, int height, int bitrate,
			int fps, int cameraId) {
		mActivity = activity;
		videoParam = new VideoParam(width, height, bitrate, fps, cameraId);
		audioParam = new AudioParam();
		mNative = new PusherNative();
	}

	public void prepare(SurfaceHolder surfaceHolder) {
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		videoPusher = new VideoPusher(mActivity, surfaceHolder, videoParam,
				mNative);
		audioPusher = new AudioPusher(audioParam, mNative);
		videoPusher.setLiveStateChangeListener(mListener);
		audioPusher.setLiveStateChangeListener(mListener);
	}

	public void startPusher(String url) {
		videoPusher.startPusher();
		audioPusher.startPusher();
		mNative.startPusher(url);
	}

	public void stopPusher() {
		videoPusher.stopPusher();
		audioPusher.stopPusher();
		mNative.stopPusher();
	}

	public void relase() {
		mActivity = null;
		stopPusher();
		videoPusher.setLiveStateChangeListener(null);
		audioPusher.setLiveStateChangeListener(null);
		mNative.setLiveStateChangeListener(null);
		videoPusher.release();
		audioPusher.release();
		mNative.release();
	}

	public void setLiveStateChangeListener(LiveStateChangeListener listener) {
		mListener = listener;
		mNative.setLiveStateChangeListener(listener);
		if (null != videoPusher) {
			videoPusher.setLiveStateChangeListener(listener);
		}
		if (null != audioPusher) {
			audioPusher.setLiveStateChangeListener(listener);
		}

	}

}
