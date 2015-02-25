package com.yyx.win8agent;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

import com.yyx.win8common.Protocol;

// ���������������ִ�е�Ԥ������
public class Preprocessor implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			Robot robot = new Robot();
			
			// ����Ϣ˵Ҫ�ͷ�Win�����Ǿ��ͷŵ�
			// ����Ӧ���б���ѡ���Ҫ�л���Ӧ���Ժ�
			// �ŵ�Win�����൱��ѡ����
			if (Protocol.needReleaseWindows(p))
				robot.keyRelease(KeyEvent.VK_WINDOWS);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
