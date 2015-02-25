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

// ����������
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
	
	// ��ʼ��������
	public void startVoiceSearch() {
		// ����Intent������Google Voice Activity
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    	intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "�뿪ʼ˵��");
    	activity.startActivityForResult(intent, requestCode);
	}
	
	// �������������ʱ��Activity����ô˻ص�������������ʶ��Ľ��
	public void finishVoiceRecognition(int requestCode, int resultCode, Intent data) {
		// resultCodeΪOKʱ˵������ʶ��ɹ���
		if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
			// �õ�����ʶ��Ķ����б����������һ��ArrayList<String>
			results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			
			// ������resultsת��ΪCharSequence����
			CharSequence[] strArray = Helper.strList2Array(results);

			// ��ʾһ���б�Ի������û�ѡ����ƥ����Ǹ�����
			new AlertDialog.Builder(activity)
			.setItems(strArray, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// ��ѡ���Ķ��﷢�͸�Agent
					sendVoiceResult(results.get(which));
					
					// �����ã�����������Metro���棬����Ҫ�л���Metroģʽ
					sideEffect.switch2MetroMode();
				}
			})
			.setTitle("��ѡ����ӽ���һ��")
			.show();
		}
	}
	
	// ������ʶ�������͸�Agent
	private void sendVoiceResult(String result) {
		// ����������Ϣ
		DatagramPacket packet = Protocol.generateCommand(remote.agentAddress(), Protocol.VOICE_SEARCH);
		
		// ������ʶ������Ķ�����������Ϣ
		Protocol.putString(packet, result);
		
		// ����������Ϣ
		remote.send(packet);
	}
}
