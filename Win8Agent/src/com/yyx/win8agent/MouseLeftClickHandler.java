package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.net.DatagramPacket;

// Êó±ê×ó¼üµ¥»÷
public class MouseLeftClickHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			Robot robot = new Robot();
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
