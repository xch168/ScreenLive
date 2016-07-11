package com.github.screenlive;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.CameraInfo;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener,
		Callback, LiveStateChangeListener {

	private Button mPublishBtn;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private boolean isStart;
	private LivePusher livePusher;

	private MediaProjectionManager mMediaProjectionManager;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {

			case -100:
				Toast.makeText(MainActivity.this, "视频预览开始失败", 0).show();
				livePusher.stopPusher();
				break;
			case -101:
				Toast.makeText(MainActivity.this, "音频录制失败", 0).show();
				livePusher.stopPusher();
				break;
			case -102:
				Toast.makeText(MainActivity.this, "音频编码器配置失败", 0).show();
				livePusher.stopPusher();
				break;
			case -103:
				Toast.makeText(MainActivity.this, "视频频编码器配置失败", 0).show();
				livePusher.stopPusher();
				break;
			case -104:
				Toast.makeText(MainActivity.this, "流媒体服务器/网络等问题", 0).show();
				livePusher.stopPusher();
				break;
			}
			mPublishBtn.setText("推流");
			isStart = false;
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mPublishBtn = (Button) findViewById(R.id.btn_publish);
		mPublishBtn.setOnClickListener(this);

		mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
		Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
		startActivityForResult(captureIntent, 0);



	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);

		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		livePusher = new LivePusher(this, 960, 720, 1024000, 15,
				CameraInfo.CAMERA_FACING_FRONT);
		livePusher.setLiveStateChangeListener(this);
		livePusher.prepare(mSurfaceHolder);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		livePusher.relase();
	}

	@Override
	public void onClick(View v) {
		if (isStart) {
			mPublishBtn.setText("推流");
			isStart = false;
			livePusher.stopPusher();
		} else {
			mPublishBtn.setText("停止");
			isStart = true;
			livePusher.startPusher("rtmp://117.25.152.85/test2/xch");// TODO: 设置流媒体服务器地址

		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		System.out.println("MAIN: CREATE");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		System.out.println("MAIN: CHANGE");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		System.out.println("MAIN: DESTORY");
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onErrorPusher(int code) {
		System.out.println("code:" + code);
		mHandler.sendEmptyMessage(code);
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onStartPusher() {
		Log.d("MainActivity", "开始推流");
	}

	/**
	 * 可能运行在子线程
	 */
	@Override
	public void onStopPusher() {
		Log.d("MainActivity", "结束推流");
	}

}
