package com.yyx.win8agent;

import java.io.IOException;
import java.net.DatagramPacket;

// 打开“控制面板”
public class ControlPanelHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// 执行Shell命令：control
			Runtime.getRuntime().exec("control");
		} catch (IOException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
