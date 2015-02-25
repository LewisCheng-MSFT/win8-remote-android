package com.yyx.win8remote;

import java.net.DatagramPacket;
import java.util.ArrayList;

import com.yyx.win8common.*;

import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

// ����ʶ����
public class GestureRecognizer extends SimpleOnGestureListener implements OnGesturePerformedListener {
	private int screenWidth;
	private int screenHeight;
	private GestureLibrary gestureLibrary;
	private SideEffectInterface sideEffect;
	private Remote remote = Remote.getSingleton();
	private boolean isTaskListHolding = false;
	private boolean isGestureControl = true;
	
	public GestureRecognizer(int weight, int height, GestureLibrary library, SideEffectInterface sideEffect) {
		screenWidth = weight;
		screenHeight = height;
		
		gestureLibrary = library;
		
		// ��������ʶ���
		if (!gestureLibrary.load())
			throw new RuntimeException("No Gesture Library");
		
		this.sideEffect = sideEffect;
	}
	
	// �����Ʒ�ʽ����Ϊ���ƿ���
	public void set2GestureControl(boolean b) {
		isGestureControl = b;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
		System.out.println("Long Press");
		
		// �����������á�����
		sideEffect.longPressVibrate();
		
		// �������ƿ��ƣ�������Context Menu��
		// ����ָ����ƣ�����������Ҽ�����
		if (isGestureControl)
			sendCommand(Protocol.KEY_CONTEXT_MENU);
		else
			sendCommand(Protocol.MOUSE_RIGHT_CLICK);
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// �������ʾ�����б��С���
		// ��ô��ߵ����ѡ��ĳ��Ӧ��
		// ��ʱֻҪ�򵥷ŵ�Win���Ϳ�����
		if (isTaskListHolding) {
			// �ŵ�Win����Ԥ�����������
			// ����Ҫ��Ķ���
			sendCommand(Protocol.NO_ACTION);
			return true;
		}
		
		// �������ƿ��ƣ���ߵ�ǻس�
		// ����ָ����ƣ���ߵ������������
		if (isGestureControl)
			sendCommand(Protocol.KEY_ENTER);
		else
			sendCommand(Protocol.MOUSE_LEFT_CLICK);
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// ����ָ����ƣ��ǲ���Ӧ���ٻ����¼��ģ�
		if (!isGestureControl)
			return false;
		
		// ��ȡ�ٶȵľ���ֵ
		float absoluteVelocityX = Math.abs(velocityX);
		float absoluteVelocityY = Math.abs(velocityY);
		
		// �������ٶȴ��������ٶ�����ʱ������Ϊ�ǺỬ
		if (absoluteVelocityX > absoluteVelocityY * 2) {
			// ���򻬶�
			if (e1.getX() < e2.getX()) {
				// ������
				if (e1.getX() < Config.SIDE_IN_MARGIN) {
					// ���Ե����
					System.out.println("Left Side-In");
					
					if (!isTaskListHolding) {
						// ��ʾ�������б�
						sendCommand(Protocol.TASK_LIST_HOLDING);
						
						// �������б�ͣס״̬��������Win������
						isTaskListHolding = true;
					}
				} else if (e2.getX() > screenWidth - Config.SIDE_OUT_MARGIN) { 
					// �ұ�Ե����
					System.out.println("Right Side-Out");
				} else {
					// һ���һ�
					System.out.println("Right Fling");
					
					// �����Ҽ�
					sendCommand(Protocol.KEY_RIGHT);
				}
			} else {
				// ������
				if (e1.getX() > screenWidth - Config.SIDE_IN_MARGIN) {
					// �ұ�Ե����
					System.out.println("Right Side-In");
					
					// ��ʾ��Charms�˵���
					sendCommand(Protocol.CHARMS_MENU);
				} else if (e2.getX() < Config.SIDE_OUT_MARGIN) {
					// ���Ե����
					System.out.println("Left Side-Out");
				} else {
					// һ����
					System.out.println("Left Fling");
					
					// �������
					sendCommand(Protocol.KEY_LEFT);
				}
			}
		} else if (absoluteVelocityY > absoluteVelocityX * 2) {
			// ���򻬶�
			if (e1.getY() < e2.getY()) {
				// ������
				if (e1.getY() < Config.SIDE_IN_MARGIN) {
					// �ϱ�Ե����
					System.out.println("Top Side-In");
					
					// �رյ�ǰMetroӦ��
					sendCommand(Protocol.CLOSE_APP);
				} else if (e2.getY() > screenHeight - Config.SIDE_IN_MARGIN) {
					// �±�Ե����
					System.out.println("Bottom Side-Out");
					
					// ��ESC��
					sendCommand(Protocol.KEY_ESCAPE);
				} else {
					// һ���»�
					System.out.println("Down Fling");
					
					// ��������ʾ�������б�����TAB�����ƶ�ѡ��
					// ���򣺰����¼�
					if (isTaskListHolding)
						sendCommand(Protocol.KEY_TAB);
					else
						sendCommand(Protocol.KEY_DOWN);
				}
			} else {
				// ������
				if (e1.getY() > screenHeight - Config.SIDE_IN_MARGIN) {
					// �±�Ե����
					System.out.println("Bottom Side-In");
					
					// ��ʾ������Ӧ�á�����
					sendCommand(Protocol.ALL_APP_MENU);
				} else if (e2.getY() < Config.SIDE_OUT_MARGIN) {
					// �ϱ�Ե����
					System.out.println("Top Side-Out");
				} else {
					// һ���ϻ�
					System.out.println("Up Fling");
					
					// ��������ʾ�������б�����SHIFT+TAB�����ƶ�ѡ��
					// ���򣺰����ϼ�
					if (isTaskListHolding)
						sendCommand(Protocol.KEY_SHIFT_TAB);
					else
						sendCommand(Protocol.KEY_UP);
				}
			}
		} else {
			// б�򻬶��ݲ�֧��
			System.out.println("Unsupported Fling");
		}
		
		return true;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// �������ƿ��ƣ�����Ӧ��ָ�����¼�
		if (isGestureControl)
			return false;
		
		// ��1��1���رȷ�������ƶ�����Ծ���
		sendMouseMove((int)distanceX, (int)distanceY);
		return true;
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// �����ƿ�ʶ������
		ArrayList<Prediction> gestList = gestureLibrary.recognize(gesture);
		
		// ��ʶ���һ��������
		if (gestList.size() > 0) {
			// ��ȡƥ�����ߵ��Ǹ�����
			Prediction prediction = gestList.get(0);
			
			// ƥ��ȱ��볬��һ����ֵ����ʶ��ɹ�
			if (prediction.score > Config.GESTURE_RECOGNITION_THRESHOLD) {
				// ��Ϊͨ��ģʽ���ƱȽ���
				// ��ֱ����һ����if���ж���
				if (prediction.name.equals("start")) {
					// ��ʼ��Ļ
					sendCommand(Protocol.KEY_WINDOWS);
					
					// �����ã��л���Metroģʽ
					sideEffect.switch2MetroMode();
				} else if (prediction.name.equals("control panel")) {
					// �������
					sendCommand(Protocol.CONTROL_PANEL);
				} else if (prediction.name.equals("system")) {
					// ϵͳ
					sendCommand(Protocol.SYSTEM);
				} else if (prediction.name.equals("setting")) {
					// ���ý���
					sendCommand(Protocol.SETTING);
					
					// �����ã��л���Metroģʽ
					sideEffect.switch2MetroMode();
				} else if (prediction.name.equals("desktop")) {
					// ��ʾ����
					sendCommand(Protocol.DESKTOP);
				}
			}
		}
	}
	
	// ����������Ϣ
	private void sendCommand(int commandId) {
		// ����������Ϣ
		DatagramPacket packet = Protocol.generateCommand(remote.agentAddress(), commandId);
		
		// ����ʾ�������б�ʱ������Tab��Shift+Tabʱ�������б�Ҫ������ʾ�����Ҳ����ͷ�Win��
		// ���������������¼�����ص������б����ͷ�Win����
		if (isTaskListHolding && commandId != Protocol.KEY_TAB && commandId != Protocol.KEY_SHIFT_TAB) {
			isTaskListHolding = false;
			Protocol.releaseWindows(packet);
		}
		
		// ����������Ϣ
		remote.send(packet);
	}
	
	// ��������ƶ���Ϣ
	private void sendMouseMove(int dx, int dy) {
		// ��������ƶ���Ϣ
		DatagramPacket packet = Protocol.generateCommand(remote.agentAddress(), Protocol.MOUSE_MOVE);
		
		// ��������������λ����Ϊ��������������Ϣ��
		Protocol.putInts(packet, dx, dy);
		
		// ��������ƶ���Ϣ
		remote.send(packet);
	}
}
