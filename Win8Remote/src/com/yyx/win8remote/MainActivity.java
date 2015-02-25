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
	// ����ʶ��Activity��Request Code
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
	
	// True��ͨ��ģʽ
	// False��Metroģʽ
	private static boolean isGeneralMode = true;
	
	// True�����ƿ���
	// False��ָ�����
	private static boolean isGestureControl = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// ��ȡ��Ļ��С����ȡ���ƿ⣬�����ݸ�����ʶ�����
		Display display = getWindowManager().getDefaultDisplay();
		recognizer = new GestureRecognizer(display.getWidth(), display.getHeight(),
				GestureLibraries.fromRawResource(this, R.raw.gestures), this);
		
		// ���Ƽ����
		detector = new GestureDetector(recognizer);
		detector.setIsLongpressEnabled(true); // �����ⳤ���¼�
		
		// ��ʾ����IP
		txtAgent = (TextView)findViewById(R.id.txt_agent);
		
		// ��ʾ��ǰģʽ
		txtMode = (TextView)findViewById(R.id.txt_mode);
		
		// ����ʶ��View
		// Metroģʽ��ֱ����Activity��ʵ�ֵģ���ʱ���View�ǲ��ɼ���invisible����
		// ͨ��ģʽ�������View�Ͻ��еģ������ǿɼ���visible����
		gestureGeneral = (GestureOverlayView)findViewById(R.id.gesture_general);
		gestureGeneral.addOnGesturePerformedListener(recognizer); // ע������ʶ�����
		
		// ģʽ�л���ť
		btnModeSwitch = (Button)findViewById(R.id.btn_mode_switch);
		btnModeSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// �л�ģʽ
				switchMode();
			}
		});

		// ����������ť
		btnVoiceSearch = (Button)findViewById(R.id.btn_voice_search);
		btnVoiceSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// ������������
				voiceSearch.startVoiceSearch();
			}
		});
		
		// �����л���ť
		btnControlSwitch = (Button)findViewById(R.id.btn_ctl_switch);
		btnControlSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// �л����Ʒ�ʽ
				switchControl();
			}
		});

		// MainActivity���ؽ�ʱҪ�ָ�������ʱ��ģʽ
		set2GeneralMode(isGeneralMode);
		
		// ����һ�µ���IP����ʾ
		updateTxtAgent();
		
		// ���Agent��û���ҵ���������WiFi����ʱ������ȥ��Agent
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
		// ��ѡOptions�˵��Ĵ���
		if (item.getItemId() == R.id.menu_exit)
			System.exit(0);
		else
			showHelp();
		
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Metroģʽ�£��ѱ�Activity�Ĵ����¼����ݸ����Ƽ����
		return detector.onTouchEvent(event);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ����ʶ��Activity����ʱ��Ҫ���ݸ�������������
		voiceSearch.finishVoiceRecognition(requestCode, resultCode, data);
	}
	
	// ��ʾ�������
	private void showHelp() {
		new AlertDialog.Builder(this)    
		.setTitle("����")
		.setMessage(R.string.help_contents)
		.setPositiveButton("֪����", null)
		.show();
	}
	
	// ����Agent
	private void findAgent() {
		// ��ʾ���ҵĽ��ȿ�
		dlgFindAgent = new FindAgentDialog(this);
		dlgFindAgent.show();
		
		// ��Remoteȥ����Agent
		// �����FindAgentHandler��Ϊ���ڲ������ʱ
		// �첽֪ͨMainActivity�޸ĵ���IP����ʾ
		remote.findAgent(new FindAgentHandler());
	}
	
	// ����Ϊͨ��ģʽ
	private void set2GeneralMode(boolean b) {
		if (b) {
			// ͨ��ģʽ��û�п����л����ܵ�
			btnControlSwitch.setVisibility(Button.INVISIBLE);
			
			// ��ģʽ�л���ť�����ֻ��ɡ�Metroģʽ��
			btnModeSwitch.setText(R.string.metro_gesture);
			
			// ͨ��ģʽҪ��gestureGeneral���View�����ص�
			gestureGeneral.setVisibility(GestureOverlayView.VISIBLE);
			
			isGeneralMode = true;
		} else {
			btnControlSwitch.setVisibility(Button.VISIBLE);
			
			btnModeSwitch.setText(R.string.general_gesture);
			
			gestureGeneral.setVisibility(GestureOverlayView.INVISIBLE);
			
			isGeneralMode = false;
		}
		
		// ����ģʽ��ʾ
		updateTxtMode();
	}
	
	// ��ͨ��ģʽ/Metroģʽ���л�
	private void switchMode() {
		if (isGeneralMode)
			set2GeneralMode(false);
		else
			set2GeneralMode(true);
	}
	
	// ����Ϊ���ƿ��Ʒ�ʽ
	private void set2GestureControl(boolean b) {
		// ֪ͨһ������ʶ�������ҲҪ�л�
		recognizer.set2GestureControl(b);
		isGestureControl = b;
	
		// �����л���ť������ҲҪ��
		if (b)
			btnControlSwitch.setText(R.string.pointer_ctl);
		else
			btnControlSwitch.setText(R.string.gesture_ctl);
		
		// ����ģʽ��ʾ
		updateTxtMode();
	}
	
	// �����ƿ���/ָ����Ƽ��л�
	private void switchControl() {
		if (isGestureControl)
			set2GestureControl(false);
		else
			set2GestureControl(true);
	}
	
	// ���µ�ǰģʽ��ʾ
	private void updateTxtMode() {
		if (isGeneralMode) {
			txtMode.setText(R.string.general_mode);
			return;
		}
		
		// Metroģʽ��Ҫ�����ֿ��Ʒ�ʽ��������ָ��
		if (isGestureControl)
			txtMode.setText(R.string.metro_gesture_mode);
		else
			txtMode.setText(R.string.metro_pointer_mode);
	}
	
	// ���µ���IP��ʾ
	private void updateTxtAgent() {
		if (remote.isAgentFound())
			txtAgent.setText("����IP��" + remote.agentIP());
		else
			txtAgent.setText("����IP��û�ҵ�");
	}
	
	// Agentû���ҵ�ʱ��Ҫ������������û���Ȼ���˳�
	private void agentNotFound() {
		new AlertDialog.Builder(MainActivity.this)
		.setTitle("�Ҳ�������")
		.setMessage("���ȷ���˳���")
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		})
		.show();
	}
	
	// ���WiFi���ӣ�û�������������û�ֱ���˳�
	private boolean detectWifiConnectivity() {
		WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
		int state = wifiMgr.getWifiState();
		if (state != WifiManager.WIFI_STATE_ENABLED &&
				state != WifiManager.WIFI_STATE_ENABLING) {
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("WiFiδ����")
			.setMessage("���ȷ���˳���")
			.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
	
	// ����Agentʱ��Ҫ��MainActivity����ʾ�Ľ��ȿ�
	private class FindAgentDialog extends ProgressDialog {

		public FindAgentDialog(Context context) {
			super(context);
			
			setTitle("���ڲ��ҵ���");
			setMessage("���Ժ󡭡�");
			setCancelable(false);
		}
		
		@Override
		public void onBackPressed() {
			// ���û���Back��ʱ������ȡ�����ң����˳�
			agentNotFound();
		}
	}
	
	// ��Agent�������ʱ���������Handler֪ͨMainActivity
	@SuppressLint("HandlerLeak")
	private class FindAgentHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// �ص����ȿ�
			dlgFindAgent.dismiss();
			
			if (msg.what == Remote.AGENT_FOUND)
				updateTxtAgent(); // �ҵ��˸��µ���IP��ʾ
			else
				agentNotFound(); // �Ҳ���
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
