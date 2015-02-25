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

// 手势识别类
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
		
		// 载入手势识别库
		if (!gestureLibrary.load())
			throw new RuntimeException("No Gesture Library");
		
		this.sideEffect = sideEffect;
	}
	
	// 将控制方式设置为手势控制
	public void set2GestureControl(boolean b) {
		isGestureControl = b;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
		System.out.println("Long Press");
		
		// 长按“副作用”：震动
		sideEffect.longPressVibrate();
		
		// 若是手势控制：长按是Context Menu键
		// 若是指针控制：长按是鼠标右键单击
		if (isGestureControl)
			sendCommand(Protocol.KEY_CONTEXT_MENU);
		else
			sendCommand(Protocol.MOUSE_RIGHT_CLICK);
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// 如果正显示任务列表中……
		// 那么轻叩就是选定某个应用
		// 此时只要简单放掉Win键就可以了
		if (isTaskListHolding) {
			// 放掉Win键在预处理器中完成
			// 不需要别的动作
			sendCommand(Protocol.NO_ACTION);
			return true;
		}
		
		// 若是手势控制：轻叩是回车
		// 若是指针控制：轻叩是鼠标左键单击
		if (isGestureControl)
			sendCommand(Protocol.KEY_ENTER);
		else
			sendCommand(Protocol.MOUSE_LEFT_CLICK);
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// 若是指针控制，是不响应快速滑动事件的！
		if (!isGestureControl)
			return false;
		
		// 获取速度的绝对值
		float absoluteVelocityX = Math.abs(velocityX);
		float absoluteVelocityY = Math.abs(velocityY);
		
		// 当横向速度大于纵向速度两倍时，才认为是横滑
		if (absoluteVelocityX > absoluteVelocityY * 2) {
			// 横向滑动
			if (e1.getX() < e2.getX()) {
				// 左向右
				if (e1.getX() < Config.SIDE_IN_MARGIN) {
					// 左边缘滑入
					System.out.println("Left Side-In");
					
					if (!isTaskListHolding) {
						// 显示“任务列表”
						sendCommand(Protocol.TASK_LIST_HOLDING);
						
						// “任务列表”停住状态，即按着Win键不放
						isTaskListHolding = true;
					}
				} else if (e2.getX() > screenWidth - Config.SIDE_OUT_MARGIN) { 
					// 右边缘滑出
					System.out.println("Right Side-Out");
				} else {
					// 一般右滑
					System.out.println("Right Fling");
					
					// 按向右键
					sendCommand(Protocol.KEY_RIGHT);
				}
			} else {
				// 右向左
				if (e1.getX() > screenWidth - Config.SIDE_IN_MARGIN) {
					// 右边缘滑入
					System.out.println("Right Side-In");
					
					// 显示“Charms菜单”
					sendCommand(Protocol.CHARMS_MENU);
				} else if (e2.getX() < Config.SIDE_OUT_MARGIN) {
					// 左边缘滑出
					System.out.println("Left Side-Out");
				} else {
					// 一般左滑
					System.out.println("Left Fling");
					
					// 按向左键
					sendCommand(Protocol.KEY_LEFT);
				}
			}
		} else if (absoluteVelocityY > absoluteVelocityX * 2) {
			// 纵向滑动
			if (e1.getY() < e2.getY()) {
				// 上向下
				if (e1.getY() < Config.SIDE_IN_MARGIN) {
					// 上边缘滑入
					System.out.println("Top Side-In");
					
					// 关闭当前Metro应用
					sendCommand(Protocol.CLOSE_APP);
				} else if (e2.getY() > screenHeight - Config.SIDE_IN_MARGIN) {
					// 下边缘滑出
					System.out.println("Bottom Side-Out");
					
					// 按ESC键
					sendCommand(Protocol.KEY_ESCAPE);
				} else {
					// 一般下滑
					System.out.println("Down Fling");
					
					// 若正在显示“任务列表”：按TAB向下移动选框
					// 否则：按向下键
					if (isTaskListHolding)
						sendCommand(Protocol.KEY_TAB);
					else
						sendCommand(Protocol.KEY_DOWN);
				}
			} else {
				// 下向上
				if (e1.getY() > screenHeight - Config.SIDE_IN_MARGIN) {
					// 下边缘滑入
					System.out.println("Bottom Side-In");
					
					// 显示“所有应用”底栏
					sendCommand(Protocol.ALL_APP_MENU);
				} else if (e2.getY() < Config.SIDE_OUT_MARGIN) {
					// 上边缘滑出
					System.out.println("Top Side-Out");
				} else {
					// 一般上滑
					System.out.println("Up Fling");
					
					// 若正在显示“任务列表”：按SHIFT+TAB向上移动选框
					// 否则：按向上键
					if (isTaskListHolding)
						sendCommand(Protocol.KEY_SHIFT_TAB);
					else
						sendCommand(Protocol.KEY_UP);
				}
			}
		} else {
			// 斜向滑动暂不支持
			System.out.println("Unsupported Fling");
		}
		
		return true;
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// 若是手势控制，则不响应手指滚动事件
		if (isGestureControl)
			return false;
		
		// 按1：1像素比发送鼠标移动的相对距离
		sendMouseMove((int)distanceX, (int)distanceY);
		return true;
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// 用手势库识别手势
		ArrayList<Prediction> gestList = gestureLibrary.recognize(gesture);
		
		// 有识别出一个手势吗？
		if (gestList.size() > 0) {
			// 获取匹配度最高的那个手势
			Prediction prediction = gestList.get(0);
			
			// 匹配度必须超过一定数值才算识别成功
			if (prediction.score > Config.GESTURE_RECOGNITION_THRESHOLD) {
				// 因为通用模式手势比较少
				// 就直接用一连串if简化判断了
				if (prediction.name.equals("start")) {
					// 开始屏幕
					sendCommand(Protocol.KEY_WINDOWS);
					
					// 副作用：切换到Metro模式
					sideEffect.switch2MetroMode();
				} else if (prediction.name.equals("control panel")) {
					// 控制面板
					sendCommand(Protocol.CONTROL_PANEL);
				} else if (prediction.name.equals("system")) {
					// 系统
					sendCommand(Protocol.SYSTEM);
				} else if (prediction.name.equals("setting")) {
					// 设置界面
					sendCommand(Protocol.SETTING);
					
					// 副作用：切换到Metro模式
					sideEffect.switch2MetroMode();
				} else if (prediction.name.equals("desktop")) {
					// 显示桌面
					sendCommand(Protocol.DESKTOP);
				}
			}
		}
	}
	
	// 发送命令消息
	private void sendCommand(int commandId) {
		// 创建命令消息
		DatagramPacket packet = Protocol.generateCommand(remote.agentAddress(), commandId);
		
		// 在显示“任务列表”时，按下Tab或Shift+Tab时，任务列表要继续显示，并且不能释放Win键
		// 否则，任意别的手势事件都会关掉任务列表（即释放Win键）
		if (isTaskListHolding && commandId != Protocol.KEY_TAB && commandId != Protocol.KEY_SHIFT_TAB) {
			isTaskListHolding = false;
			Protocol.releaseWindows(packet);
		}
		
		// 发送命令消息
		remote.send(packet);
	}
	
	// 发送鼠标移动消息
	private void sendMouseMove(int dx, int dy) {
		// 创建鼠标移动消息
		DatagramPacket packet = Protocol.generateCommand(remote.agentAddress(), Protocol.MOUSE_MOVE);
		
		// 把两个方向的相对位移作为两个参数存入消息中
		Protocol.putInts(packet, dx, dy);
		
		// 发送鼠标移动消息
		remote.send(packet);
	}
}
