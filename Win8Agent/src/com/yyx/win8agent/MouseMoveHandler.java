package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.net.DatagramPacket;

import com.yyx.win8common.Protocol;

// ���ָ���ƶ�
public class MouseMoveHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// ����Ϣ�н�����x����y��������λ��
			int dx = Protocol.getInt1(p);
			int dy = Protocol.getInt2(p);
			
			// �ƶ�ָ�루���ֻ���Ļ���ر�1��1��
			Robot robot = new Robot();
			Point point = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(point.x - dx, point.y - dy);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
