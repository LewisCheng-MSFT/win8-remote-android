package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

// ÏòÏÂ¼ü
public class KeyDownHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_DOWN);
	        robot.keyRelease(KeyEvent.VK_DOWN);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
