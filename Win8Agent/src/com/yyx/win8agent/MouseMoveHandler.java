package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.net.DatagramPacket;

import com.yyx.win8common.Protocol;

// 鼠标指针移动
public class MouseMoveHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// 从消息中解析出x方向、y方向的相对位移
			int dx = Protocol.getInt1(p);
			int dy = Protocol.getInt2(p);
			
			// 移动指针（与手机屏幕像素比1：1）
			Robot robot = new Robot();
			Point point = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(point.x - dx, point.y - dy);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
