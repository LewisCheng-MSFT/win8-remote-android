package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

// 显示“所有应用”界面
public class AllAppMenuHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// 按下Win+Z即可
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_WINDOWS);
			robot.keyPress(KeyEvent.VK_Z);
	        robot.keyRelease(KeyEvent.VK_Z);
	        robot.keyRelease(KeyEvent.VK_WINDOWS);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
