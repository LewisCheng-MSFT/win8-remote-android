package com.yyx.win8common;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

// 用来定义Agent与Remote通讯方式的协议类
public class Protocol {
	// 消息格式
	// 1. Hello消息：
	//    | 1 byte (HELLO) | 1 byte (no use) | 4 bytes (remote ip) | ... |
	// 2. Ack消息：
	//    | 1 byte (ACK) | 1 byte (no use) | 4 bytes (agent ip) | ... |
	// 3. 命令消息（int1、int2是参数）：
	//    | 1 byte (command id) | 1 byte (release Win?) | 4 bytes (int1) | 4 bytes (int2) | ... |
	// 4. 语音消息（int1是短语字符串的字节长度）：
	//    | 1 byte (VOICE_SEARCH) | 1 byte (no use) | 4 bytes (int1) | int1 bytes (string) | ... |
	
	// 命令序号（Command Id），每一项都是一种命令
	public static final int NO_ACTION = -1;
	public static final int HELLO = 1;
	public static final int ACK = 2;
	public static final int CHARMS_MENU = 3;
	public static final int KEY_ESCAPE = 4;
	public static final int ALL_APP_MENU = 5;
	public static final int KEY_CONTEXT_MENU = 6;
	public static final int KEY_WINDOWS = 7;
	public static final int KEY_UP = 8;
	public static final int KEY_DOWN = 9;
	public static final int KEY_LEFT = 10;
	public static final int KEY_RIGHT = 11;
	public static final int TASK_LIST_HOLDING = 12;
	public static final int KEY_TAB = 13;
	public static final int KEY_ENTER = 14;
	public static final int KEY_SHIFT_TAB = 15;
	public static final int MOUSE_LEFT_CLICK = 16;
	public static final int MOUSE_RIGHT_CLICK = 17;
	public static final int MOUSE_MOVE = 18;
	public static final int CLOSE_APP = 19;
	public static final int CONTROL_PANEL = 20;
	public static final int SYSTEM = 21;
	public static final int SETTING = 22;
	public static final int DESKTOP = 23;
	public static final int VOICE_SEARCH = 24;
	
	// 消息长度
	public static final int PACKET_LENGTH = 32;
	
	// 多播端口（UDP）
	public static final int MULTICAST_PORT = 4566;
	
	// 协议端口（UDP）
	public static final int PROTOCOL_PORT = 4567;
	
	// 面向所有主机的多播地址
	public static final String MULTICAST_ADDRESS = "224.0.0.8";
	
	// Remote等待Agent回复广播Hello消息的超时，超时了就重新广播
	public static final int ACK_TIMEOUT = 2000;
	
	// 协议Socket接收超时，超时了就向Remote发Ack
	public static final int IDLE_TIMEOUT = 5000;
	
	// 为Remote创建多播Hello消息
	public static DatagramPacket generateHello() throws UnknownHostException, SocketException {
		// 这个消息发向多播地址/多播端口
		DatagramPacket packet = PacketFactory.createSendPack(
				InetAddress.getByName(Protocol.MULTICAST_ADDRESS),
				Protocol.MULTICAST_PORT);
		byte[] buf = packet.getData();
		
		// 设置Command Id=HELLO
		buf[0] = HELLO;
		
		// 获取手机WiFi的地址
		byte[] addrBytes = Helper.getPhoneAddr().getAddress();
		
		// 把这个IP地址复制到消息中
		Helper.byteCopy(buf, 2, addrBytes, 0, 4);
		
		return packet;
	}
	
	// 为Remote创建命令消息
	public static DatagramPacket generateCommand(InetAddress agentAddress, int commandId) {
		// 这个消息发向Agent地址/协议端口
		DatagramPacket packet = PacketFactory.createSendPack(agentAddress, Protocol.PROTOCOL_PORT);
		byte[] buf = packet.getData();
		
		// 设置Command Id
		buf[0] = (byte)commandId;
		
		return packet;
	}
	
	// 在消息中指定Agent要释放掉Win键
	public static void releaseWindows(DatagramPacket packet) {
		packet.getData()[1] = 1;
	}
	
	// 需要Agent释放掉Win键吗？
	public static boolean needReleaseWindows(DatagramPacket packet) {
		return packet.getData()[1] == 1;
	}
	
	// 为Agent创建Ack消息
	public static DatagramPacket generateAck(InetAddress remoteAddress) throws UnknownHostException, SocketException {
		// 这个消息发向Remote地址/协议端口
		DatagramPacket packet = PacketFactory.createSendPack(remoteAddress, Protocol.PROTOCOL_PORT);
		byte[] buf = packet.getData();
		
		// 设置Command Id为ACK
		buf[0] = ACK;
		
		// 获取电脑地址
		byte[] addressBytes = Helper.getPcAddress().getAddress();
		
		// 把这个IP地址复制到消息中
		Helper.byteCopy(buf, 2, addressBytes, 0, 4);
		
		return packet;
	}
	
	// 从Hello消息中解析出Remote地址
	public static InetAddress resolveHello(DatagramPacket packet) throws UnknownHostException {
		byte[] buf = packet.getData();
		
		// 不是HELLO消息抛异常
		if (buf[0] != HELLO)
			throw new RuntimeException("Invalid Hello From Remote");
		
		// 从消息中截出地址
		return InetAddress.getByAddress(Helper.byteCut(buf, 2, 4));
	}
	
	// 从Ack消息中解析出Agent地址
	public static InetAddress resolveAck(DatagramPacket packet) throws UnknownHostException {
		byte[] buf = packet.getData();
		
		// 不是ACK消息抛异常
		if (buf[0] != ACK)
			throw new RuntimeException("Invalid Ack From Agent");
		
		// 从消息中截出地址
		return InetAddress.getByAddress(Helper.byteCut(buf, 2, 4));
	}
	
	// 从消息中解析出Command Id
	public static int resolveCommandId(DatagramPacket packet) {
		return packet.getData()[0];
	}
	
	// 在消息中存储两个int参数
	public static void putInts(DatagramPacket packet, int n1, int n2) {
		putInt1(packet, n1);
		putInt2(packet, n2);
	}
	
	// 在消息中存储第一个int参数
	public static void putInt1(DatagramPacket packet, int n1) {
		Helper.int2Bytes(n1, packet.getData(), 2);
	}
	
	// 在消息中存储第二个int参数
	public static void putInt2(DatagramPacket packet, int n2) {
		Helper.int2Bytes(n2, packet.getData(), 6);
	}
	
	// 从消息中获取第一个int参数
	public static int getInt1(DatagramPacket packet) {
		return Helper.bytes2Int(packet.getData(), 2);
	}
	
	// 从消息中获取第二个int参数
	public static int getInt2(DatagramPacket packet) {
		return Helper.bytes2Int(packet.getData(), 6);
	}
	
	// 在消息中存储字符串
	public static void putString(DatagramPacket packet, String str) {
		try {
			// 把字符串按utf8格式转换成字节数组
			byte[] bytes = str.getBytes("utf-8");
			
			// 防止长度超标（去掉前6个字节已经用掉，剩下的可以用来存字符串）
			if (bytes.length > PACKET_LENGTH - 6)
				throw new RuntimeException("String Too Long For Packet");
			
			// 在int1位置存储字符串的字节长度
			putInt1(packet, bytes.length);
			
			// 把字符串复制到消息中
			Helper.byteCopy(packet.getData(), 6, bytes, 0, bytes.length);
		} catch (UnsupportedEncodingException e) {
			// 不可能发生的
			e.printStackTrace();
		}
	}
	
	// 从消息中取得字符串
	public static String getString(DatagramPacket packet) {
		try {
			// 从int1位置取得字符串的字节长度
			int length = getInt1(packet);
			
			// 从消息中截出指定长度的字节数组
			byte[] bytes = Helper.byteCut(packet.getData(), 6, length);
			
			// 把字节数组转换为utf8字符串
			return new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// 不可能发生的
			e.printStackTrace();
			return null;
		}
	}
}
