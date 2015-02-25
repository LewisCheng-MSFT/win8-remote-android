package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

import com.yyx.win8common.Config;

// 打开Win8的“设置”界面
public class SettingHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			Robot robot = new Robot();
			
			// 按下Win+I，在右侧显示设置边栏
			robot.keyPress(KeyEvent.VK_WINDOWS);
			robot.keyPress(KeyEvent.VK_I);
	        robot.keyRelease(KeyEvent.VK_I);
	        robot.keyRelease(KeyEvent.VK_WINDOWS);
	        
	        // 延时一下，等边栏出现
	        robot.delay(Config.DELAY_BETWEEN_KEY_EVENTS_IN_MS);
	        
	        // 按下Shift+Tab，使选框跑到“更改电脑设置”上面
	        robot.keyPress(KeyEvent.VK_SHIFT);
	        robot.keyPress(KeyEvent.VK_TAB);
	        robot.keyRelease(KeyEvent.VK_SHIFT);
	        robot.keyRelease(KeyEvent.VK_TAB);
	        
	        // 延时一下，等选框到位
	        robot.delay(Config.DELAY_BETWEEN_KEY_EVENTS_IN_MS);
	        
	        // 按下Enter，进入全屏“设置”界面
	        robot.keyPress(KeyEvent.VK_ENTER);
	        robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
