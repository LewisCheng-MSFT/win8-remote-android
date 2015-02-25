package com.yyx.win8agent;

import java.net.DatagramPacket;

// 所有命令处理程序的公共接口
public interface CommandHandlerInterface {
	public void handleCommand(DatagramPacket p);
}
