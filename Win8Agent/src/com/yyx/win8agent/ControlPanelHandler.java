package com.yyx.win8agent;

import java.io.IOException;
import java.net.DatagramPacket;

// �򿪡�������塱
public class ControlPanelHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// ִ��Shell���control
			Runtime.getRuntime().exec("control");
		} catch (IOException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
