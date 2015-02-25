package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

// ESC¼ü
public class KeyEscapeHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ESCAPE);
	        robot.keyRelease(KeyEvent.VK_ESCAPE);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
