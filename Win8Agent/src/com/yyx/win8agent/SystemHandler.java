package com.yyx.win8agent;

import java.io.IOException;
import java.net.DatagramPacket;

// 显示“系统”界面
public class SystemHandler implements CommandHandlerInterface {
	
	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// 执行Shell命令：control /name Microsoft.System
			Runtime.getRuntime().exec("control /name Microsoft.System");
		} catch (IOException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
