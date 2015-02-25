package com.yyx.win8remote;

import java.net.DatagramPacket;
import java.util.ArrayList;

import com.yyx.win8common.Helper;
import com.yyx.win8common.Protocol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;

// 语音搜索类
public class VoiceSearch {
	private Activity activity;
	private int requestCode;
	private SideEffectInterface sideEffect;
	private Remote remote = Remote.getSingleton();
	private ArrayList<String> results;
	
	public VoiceSearch(Activity activity, int requestCode, SideEffectInterface sideEffect) {
		this.activity = activity;
		this.requestCode = requestCode;
		this.sideEffect = sideEffect;
	}
	
	// 开始语音搜索
	public void startVoiceSearch() {
		// 创建Intent并启动Google Voice Activity
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    	intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请开始说话");
    	activity.startActivityForResult(intent, requestCode);
	}
	
	// 当语音搜索完成时，Activity会调用此回调方法处理语音识别的结果
	public void finishVoiceRecognition(int requestCode, int resultCode, Intent data) {
		// resultCode为OK时说明语音识别成功了
		if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
			// 得到语音识别的短语列表（结果），是一个ArrayList<String>
			results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			// 把上述results转换为CharSequence数组
			CharSequence[] strArray = Helper.strList2Array(results);

			// 显示一个列表对话框，让用户选择最匹配的那个短语
			new AlertDialog.Builder(activity)
			.setItems(strArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// 把选定的短语发送给Agent
					sendVoiceResult(results.get(which));
					
					// 副作用：搜索界面是Metro界面，所以要切换到Metro模式
					sideEffect.switch2MetroMode();
				}
			})
			.setTitle("请选择最接近的一项")
			.show();
		}
	}
	
	// 将语音识别结果发送给Agent
	private void sendVoiceResult(String result) {
		// 创建语音消息
		DatagramPacket packet = Protocol.generateCommand(remote.agentAddress(), Protocol.VOICE_SEARCH);
		
		// 把语音识别出来的短语存入这个消息
		Protocol.putString(packet, result);
		
		// 发送语音消息
		remote.send(packet);
	}
}
