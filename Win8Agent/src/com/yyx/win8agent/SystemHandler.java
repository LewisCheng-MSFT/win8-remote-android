package com.yyx.win8agent;

import java.io.IOException;
import java.net.DatagramPacket;

// ��ʾ��ϵͳ������
public class SystemHandler implements CommandHandlerInterface {
	
	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// ִ��Shell���control /name Microsoft.System
			Runtime.getRuntime().exec("control /name Microsoft.System");
		} catch (IOException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
