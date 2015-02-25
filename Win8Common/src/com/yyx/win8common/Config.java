package com.yyx.win8common;

// 配置参数类
public abstract class Config {
	// 手势匹配的概率超过多少才算识别成功
	public static final double GESTURE_RECOGNITION_THRESHOLD = 5.0;
	
	// 从多靠近屏幕边缘的距离滑入算“从边缘滑入”？
	public static final int SIDE_IN_MARGIN = 80;
	
	// 滑到多靠近边缘的地方算“从边缘滑出”？
	public static final int SIDE_OUT_MARGIN = 80;
	
	// 长按屏幕中间时，震动多少毫秒
	public static final long LONG_PRESS_VIBRATE_IN_MS = 50;
	
	// 两次按键事件之间延迟多少毫秒（给系统反应时间）
	public static final int DELAY_BETWEEN_KEY_EVENTS_IN_MS = 200;
}
