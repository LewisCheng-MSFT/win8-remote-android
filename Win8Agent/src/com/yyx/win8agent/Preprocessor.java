package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

import com.yyx.win8common.Protocol;

// 先于所有命令处理器执行的预处理器
public class Preprocessor implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			Robot robot = new Robot();
			
			// 若消息说要释放Win键，那就释放掉
			// 当在应用列表中选择好要切换的应用以后，
			// 放掉Win键就相当于选定了
			if (Protocol.needReleaseWindows(p))
				robot.keyRelease(KeyEvent.VK_WINDOWS);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
