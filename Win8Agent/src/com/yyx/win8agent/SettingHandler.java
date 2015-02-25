package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

import com.yyx.win8common.Config;

// ��Win8�ġ����á�����
public class SettingHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			Robot robot = new Robot();
			
			// ����Win+I�����Ҳ���ʾ���ñ���
			robot.keyPress(KeyEvent.VK_WINDOWS);
			robot.keyPress(KeyEvent.VK_I);
	        robot.keyRelease(KeyEvent.VK_I);
	        robot.keyRelease(KeyEvent.VK_WINDOWS);
	        
	        // ��ʱһ�£��ȱ�������
	        robot.delay(Config.DELAY_BETWEEN_KEY_EVENTS_IN_MS);
	        
	        // ����Shift+Tab��ʹѡ���ܵ������ĵ������á�����
	        robot.keyPress(KeyEvent.VK_SHIFT);
	        robot.keyPress(KeyEvent.VK_TAB);
	        robot.keyRelease(KeyEvent.VK_SHIFT);
	        robot.keyRelease(KeyEvent.VK_TAB);
	        
	        // ��ʱһ�£���ѡ��λ
	        robot.delay(Config.DELAY_BETWEEN_KEY_EVENTS_IN_MS);
	        
	        // ����Enter������ȫ�������á�����
	        robot.keyPress(KeyEvent.VK_ENTER);
	        robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
