package com.yyx.win8remote;

import com.yyx.win8common.Config;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.gesture.GestureLibraries;
import android.gesture.GestureOverlayView;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements SideEffectInterface {
	// 语音识别Activity的Request Code
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	
	private TextView txtAgent;
	private TextView txtMode;
	private GestureOverlayView gestureGeneral;
	private Button btnModeSwitch;
	private Button btnVoiceSearch;
	private Button btnControlSwitch;
	private FindAgentDialog dlgFindAgent;
	
	private Remote remote = Remote.getSingleton();
	private VoiceSearch voiceSearch = new VoiceSearch(
			this, VOICE_RECOGNITION_REQUEST_CODE, this);
	private GestureRecognizer recognizer;
	private GestureDetector detector;
	
	// True：通用模式
	// False：Metro模式
	private static boolean isGeneralMode = true;
	
	// True：手势控制
	// False：指针控制
	private static boolean isGestureControl = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 获取屏幕大小、读取手势库，并传递给手势识别对象
		Display display = getWindowManager().getDefaultDisplay();
		recognizer = new GestureRecognizer(display.getWidth(), display.getHeight(),
				GestureLibraries.fromRawResource(this, R.raw.gestures), this);
		
		// 手势检测器
		detector = new GestureDetector(recognizer);
		detector.setIsLongpressEnabled(true); // 允许检测长按事件
		
		// 显示电脑IP
		txtAgent = (TextView)findViewById(R.id.txt_agent);
		
		// 显示当前模式
		txtMode = (TextView)findViewById(R.id.txt_mode);
		
		// 手势识别View
		// Metro模式是直接在Activity上实现的，此时这个View是不可见（invisible）的
		// 通用模式是在这个View上进行的，所以是可见（visible）的
		gestureGeneral = (GestureOverlayView)findViewById(R.id.gesture_general);
		gestureGeneral.addOnGesturePerformedListener(recognizer); // 注册手势识别对象
		
		// 模式切换按钮
		btnModeSwitch = (Button)findViewById(R.id.btn_mode_switch);
		btnModeSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 切换模式
				switchMode();
			}
		});

		// 语音搜索按钮
		btnVoiceSearch = (Button)findViewById(R.id.btn_voice_search);
		btnVoiceSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 启动语音搜索
				voiceSearch.startVoiceSearch();
			}
		});
		
		// 控制切换按钮
		btnControlSwitch = (Button)findViewById(R.id.btn_ctl_switch);
		btnControlSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 切换控制方式
				switchControl();
			}
		});

		// MainActivity被重建时要恢复被销毁时的模式
		set2GeneralMode(isGeneralMode);
		
		// 设置一下电脑IP的显示
		updateTxtAgent();
		
		// 如果Agent还没有找到（并且有WiFi连接时），则去找Agent
		if (!remote.isAgentFound() && detectWifiConnectivity())
			findAgent();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// 点选Options菜单的处理
		if (item.getItemId() == R.id.menu_exit)
			System.exit(0);
		else
			showHelp();
		
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Metro模式下，把本Activity的触摸事件传递给手势检测器
		return detector.onTouchEvent(event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 语音识别Activity返回时，要传递给语音搜索对象
		voiceSearch.finishVoiceRecognition(requestCode, resultCode, data);
	}
	
	// 显示软件帮助
	private void showHelp() {
		new AlertDialog.Builder(this)    
		.setTitle("帮助")
		.setMessage(R.string.help_contents)
		.setPositiveButton("知道了", null)
		.show();
	}
	
	// 查找Agent
	private void findAgent() {
		// 显示查找的进度框
		dlgFindAgent = new FindAgentDialog(this);
		dlgFindAgent.show();
		
		// 叫Remote去查找Agent
		// 这里的FindAgentHandler是为了在查找完成时
		// 异步通知MainActivity修改电脑IP的显示
		remote.findAgent(new FindAgentHandler());
	}
	
	// 设置为通用模式
	private void set2GeneralMode(boolean b) {
		if (b) {
			// 通用模式是没有控制切换功能的
			btnControlSwitch.setVisibility(Button.INVISIBLE);
			
			// 把模式切换按钮的文字换成“Metro模式”
			btnModeSwitch.setText(R.string.metro_gesture);
			
			// 通用模式要把gestureGeneral这个View给隐藏掉
			gestureGeneral.setVisibility(GestureOverlayView.VISIBLE);
			
			isGeneralMode = true;
		} else {
			btnControlSwitch.setVisibility(Button.VISIBLE);
			
			btnModeSwitch.setText(R.string.general_gesture);
			
			gestureGeneral.setVisibility(GestureOverlayView.INVISIBLE);
			
			isGeneralMode = false;
		}
		
		// 更新模式显示
		updateTxtMode();
	}
	
	// 在通用模式/Metro模式间切换
	private void switchMode() {
		if (isGeneralMode)
			set2GeneralMode(false);
		else
			set2GeneralMode(true);
	}
	
	// 设置为手势控制方式
	private void set2GestureControl(boolean b) {
		// 通知一下手势识别对象，它也要切换
		recognizer.set2GestureControl(b);
		isGestureControl = b;
	
		// 控制切换按钮的文字也要换
		if (b)
			btnControlSwitch.setText(R.string.pointer_ctl);
		else
			btnControlSwitch.setText(R.string.gesture_ctl);
		
		// 更新模式显示
		updateTxtMode();
	}
	
	// 在手势控制/指针控制间切换
	private void switchControl() {
		if (isGestureControl)
			set2GestureControl(false);
		else
			set2GestureControl(true);
	}
	
	// 更新当前模式显示
	private void updateTxtMode() {
		if (isGeneralMode) {
			txtMode.setText(R.string.general_mode);
			return;
		}
		
		// Metro模式还要分两种控制方式：手势与指针
		if (isGestureControl)
			txtMode.setText(R.string.metro_gesture_mode);
		else
			txtMode.setText(R.string.metro_pointer_mode);
	}
	
	// 更新电脑IP显示
	private void updateTxtAgent() {
		if (remote.isAgentFound())
			txtAgent.setText("电脑IP：" + remote.agentIP());
		else
			txtAgent.setText("电脑IP：没找到");
	}
	
	// Agent没有找到时，要弹出个框告诉用户，然后退出
	private void agentNotFound() {
		new AlertDialog.Builder(MainActivity.this)
		.setTitle("找不到电脑")
		.setMessage("点击确定退出！")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		})
		.show();
	}
	
	// 检测WiFi连接，没有连接提醒下用户直接退出
	private boolean detectWifiConnectivity() {
		WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
		int state = wifiMgr.getWifiState();
		if (state != WifiManager.WIFI_STATE_ENABLED &&
				state != WifiManager.WIFI_STATE_ENABLING) {
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("WiFi未开启")
			.setMessage("点击确定退出！")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
				}
			})
			.show();
			return false;
		}
		
		return true;
	}
	
	// 查找Agent时，要在MainActivity上显示的进度框
	private class FindAgentDialog extends ProgressDialog {

		public FindAgentDialog(Context context) {
			super(context);
			
			setTitle("正在查找电脑");
			setMessage("请稍后……");
			setCancelable(false);
		}
		
		@Override
		public void onBackPressed() {
			// 当用户按Back键时，允许取消查找，并退出
			agentNotFound();
		}
	}
	
	// 当Agent查找完成时，会用这个Handler通知MainActivity
	@SuppressLint("HandlerLeak")
	private class FindAgentHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// 关掉进度框
			dlgFindAgent.dismiss();
			
			if (msg.what == Remote.AGENT_FOUND)
				updateTxtAgent(); // 找到了更新电脑IP显示
			else
				agentNotFound(); // 找不到
		}
		
	}

	@Override
	public void switch2MetroMode() {
		set2GeneralMode(false);
	}

	@Override
	public void longPressVibrate() {
		Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
		vibrator.vibrate(Config.LONG_PRESS_VIBRATE_IN_MS);
	}
}
