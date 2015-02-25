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

// 启动搜索界面并搜索消息中的“短语”（默认分类为应用）
public class VoiceSearchHandler implements CommandHandlerInterface {

	@Override
	public void handleCommand(DatagramPacket p) {
		try {
			// 从消息中解析出短语
			String str = Protocol.getString(p);
			
			// 获取系统剪贴板
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			
			// 获取剪贴板当前的内容
			Transferable oldClip = clipboard.getContents(null);
			
			// 把短语放进剪贴板
			Transferable clip = new StringSelection(str);
			clipboard.setContents(clip, null);
			
			// 按下Win+Q启动“应用搜索”界面
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_WINDOWS);
			robot.keyPress(KeyEvent.VK_Q);
	        robot.keyRelease(KeyEvent.VK_Q);
	        robot.keyRelease(KeyEvent.VK_WINDOWS);
	        
	        // 延时一下，等待搜索界面出来
	        robot.delay(Config.DELAY_BETWEEN_KEY_EVENTS_IN_MS);
	        
	        // 按下Ctrl+V粘贴剪贴板的短语到搜索框
	        robot.keyPress(KeyEvent.VK_CONTROL);
	        robot.keyPress(KeyEvent.VK_V);
	        robot.keyRelease(KeyEvent.VK_V);
	        robot.keyRelease(KeyEvent.VK_CONTROL);
	        
	        // 延时一下，等粘贴好
	        robot.delay(Config.DELAY_BETWEEN_KEY_EVENTS_IN_MS);
	        
	        // 按下Enter开始搜索
	        robot.keyPress(KeyEvent.VK_ENTER);
	        robot.keyRelease(KeyEvent.VK_ENTER);
	        
	        // 把剪贴板原来的内容还原回去
	        clipboard.setContents(oldClip, null);
		} catch (AWTException e) {
			System.err.println("Command Handler Error");
			e.printStackTrace();
		}
	}

}
