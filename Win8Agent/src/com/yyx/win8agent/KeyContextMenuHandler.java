package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

// ��ʾ�������Ĳ˵���
public class KeyContextMenuHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// ����Context Menu������
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTEXT_MENU);
	        robot.keyRelease(KeyEvent.VK_CONTEXT_MENU);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
