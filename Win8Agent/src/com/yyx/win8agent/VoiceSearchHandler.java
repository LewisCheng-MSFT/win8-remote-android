package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

import com.yyx.win8common.Config;
import com.yyx.win8common.Protocol;

// �����������沢������Ϣ�еġ������Ĭ�Ϸ���ΪӦ�ã�
public class VoiceSearchHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// ����Ϣ�н���������
			String str = Protocol.getString(p);
			
			// ��ȡϵͳ������
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			
			// ��ȡ�����嵱ǰ������
			Transferable oldClip = clipboard.getContents(null);
			
			// �Ѷ���Ž�������
			Transferable clip = new StringSelection(str);
			clipboard.setContents(clip, null);
			
			// ����Win+Q������Ӧ������������
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_WINDOWS);
			robot.keyPress(KeyEvent.VK_Q);
	        robot.keyRelease(KeyEvent.VK_Q);
	        robot.keyRelease(KeyEvent.VK_WINDOWS);
	        
	        // ��ʱһ�£��ȴ������������
	        robot.delay(Config.DELAY_BETWEEN_KEY_EVENTS_IN_MS);
	        
	        // ����Ctrl+Vճ��������Ķ��ﵽ������
	        robot.keyPress(KeyEvent.VK_CONTROL);
	        robot.keyPress(KeyEvent.VK_V);
	        robot.keyRelease(KeyEvent.VK_V);
	        robot.keyRelease(KeyEvent.VK_CONTROL);
	        
	        // ��ʱһ�£���ճ����
	        robot.delay(Config.DELAY_BETWEEN_KEY_EVENTS_IN_MS);
	        
	        // ����Enter��ʼ����
	        robot.keyPress(KeyEvent.VK_ENTER);
	        robot.keyRelease(KeyEvent.VK_ENTER);
	        
	        // �Ѽ�����ԭ�������ݻ�ԭ��ȥ
	        clipboard.setContents(oldClip, null);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
