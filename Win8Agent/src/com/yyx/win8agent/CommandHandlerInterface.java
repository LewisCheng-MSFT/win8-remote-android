package com.yyx.win8agent;

import java.net.DatagramPacket;

// ������������Ĺ����ӿ�
public interface CommandHandlerInterface {
	public void handleCommand(DatagramPacket p);
}
