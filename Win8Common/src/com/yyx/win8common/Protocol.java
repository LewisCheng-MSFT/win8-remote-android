package com.yyx.win8common;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

// ��������Agent��RemoteͨѶ��ʽ��Э����
public class Protocol {
	// ��Ϣ��ʽ
	// 1. Hello��Ϣ��
	//    | 1 byte (HELLO) | 1 byte (no use) | 4 bytes (remote ip) | ... |
	// 2. Ack��Ϣ��
	//    | 1 byte (ACK) | 1 byte (no use) | 4 bytes (agent ip) | ... |
	// 3. ������Ϣ��int1��int2�ǲ�������
	//    | 1 byte (command id) | 1 byte (release Win?) | 4 bytes (int1) | 4 bytes (int2) | ... |
	// 4. ������Ϣ��int1�Ƕ����ַ������ֽڳ��ȣ���
	//    | 1 byte (VOICE_SEARCH) | 1 byte (no use) | 4 bytes (int1) | int1 bytes (string) | ... |
	
	// ������ţ�Command Id����ÿһ���һ������
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
	
	// ��Ϣ����
	public static final int PACKET_LENGTH = 32;
	
	// �ಥ�˿ڣ�UDP��
	public static final int MULTICAST_PORT = 4566;
	
	// Э��˿ڣ�UDP��
	public static final int PROTOCOL_PORT = 4567;
	
	// �������������Ķಥ��ַ
	public static final String MULTICAST_ADDRESS = "224.0.0.8";
	
	// Remote�ȴ�Agent�ظ��㲥Hello��Ϣ�ĳ�ʱ����ʱ�˾����¹㲥
	public static final int ACK_TIMEOUT = 2000;
	
	// Э��Socket���ճ�ʱ����ʱ�˾���Remote��Ack
	public static final int IDLE_TIMEOUT = 5000;
	
	// ΪRemote�����ಥHello��Ϣ
	public static DatagramPacket generateHello() throws UnknownHostException, SocketException {
		// �����Ϣ����ಥ��ַ/�ಥ�˿�
		DatagramPacket packet = PacketFactory.createSendPack(
				InetAddress.getByName(Protocol.MULTICAST_ADDRESS),
				Protocol.MULTICAST_PORT);
		byte[] buf = packet.getData();
		
		// ����Command Id=HELLO
		buf[0] = HELLO;
		
		// ��ȡ�ֻ�WiFi�ĵ�ַ
		byte[] addrBytes = Helper.getPhoneAddr().getAddress();
		
		// �����IP��ַ���Ƶ���Ϣ��
		Helper.byteCopy(buf, 2, addrBytes, 0, 4);
		
		return packet;
	}
	
	// ΪRemote����������Ϣ
	public static DatagramPacket generateCommand(InetAddress agentAddress, int commandId) {
		// �����Ϣ����Agent��ַ/Э��˿�
		DatagramPacket packet = PacketFactory.createSendPack(agentAddress, Protocol.PROTOCOL_PORT);
		byte[] buf = packet.getData();
		
		// ����Command Id
		buf[0] = (byte)commandId;
		
		return packet;
	}
	
	// ����Ϣ��ָ��AgentҪ�ͷŵ�Win��
	public static void releaseWindows(DatagramPacket packet) {
		packet.getData()[1] = 1;
	}
	
	// ��ҪAgent�ͷŵ�Win����
	public static boolean needReleaseWindows(DatagramPacket packet) {
		return packet.getData()[1] == 1;
	}
	
	// ΪAgent����Ack��Ϣ
	public static DatagramPacket generateAck(InetAddress remoteAddress) throws UnknownHostException, SocketException {
		// �����Ϣ����Remote��ַ/Э��˿�
		DatagramPacket packet = PacketFactory.createSendPack(remoteAddress, Protocol.PROTOCOL_PORT);
		byte[] buf = packet.getData();
		
		// ����Command IdΪACK
		buf[0] = ACK;
		
		// ��ȡ���Ե�ַ
		byte[] addressBytes = Helper.getPcAddress().getAddress();
		
		// �����IP��ַ���Ƶ���Ϣ��
		Helper.byteCopy(buf, 2, addressBytes, 0, 4);
		
		return packet;
	}
	
	// ��Hello��Ϣ�н�����Remote��ַ
	public static InetAddress resolveHello(DatagramPacket packet) throws UnknownHostException {
		byte[] buf = packet.getData();
		
		// ����HELLO��Ϣ���쳣
		if (buf[0] != HELLO)
			throw new RuntimeException("Invalid Hello From Remote");
		
		// ����Ϣ�нس���ַ
		return InetAddress.getByAddress(Helper.byteCut(buf, 2, 4));
	}
	
	// ��Ack��Ϣ�н�����Agent��ַ
	public static InetAddress resolveAck(DatagramPacket packet) throws UnknownHostException {
		byte[] buf = packet.getData();
		
		// ����ACK��Ϣ���쳣
		if (buf[0] != ACK)
			throw new RuntimeException("Invalid Ack From Agent");
		
		// ����Ϣ�нس���ַ
		return InetAddress.getByAddress(Helper.byteCut(buf, 2, 4));
	}
	
	// ����Ϣ�н�����Command Id
	public static int resolveCommandId(DatagramPacket packet) {
		return packet.getData()[0];
	}
	
	// ����Ϣ�д洢����int����
	public static void putInts(DatagramPacket packet, int n1, int n2) {
		putInt1(packet, n1);
		putInt2(packet, n2);
	}
	
	// ����Ϣ�д洢��һ��int����
	public static void putInt1(DatagramPacket packet, int n1) {
		Helper.int2Bytes(n1, packet.getData(), 2);
	}
	
	// ����Ϣ�д洢�ڶ���int����
	public static void putInt2(DatagramPacket packet, int n2) {
		Helper.int2Bytes(n2, packet.getData(), 6);
	}
	
	// ����Ϣ�л�ȡ��һ��int����
	public static int getInt1(DatagramPacket packet) {
		return Helper.bytes2Int(packet.getData(), 2);
	}
	
	// ����Ϣ�л�ȡ�ڶ���int����
	public static int getInt2(DatagramPacket packet) {
		return Helper.bytes2Int(packet.getData(), 6);
	}
	
	// ����Ϣ�д洢�ַ���
	public static void putString(DatagramPacket packet, String str) {
		try {
			// ���ַ�����utf8��ʽת�����ֽ�����
			byte[] bytes = str.getBytes("utf-8");
			
			// ��ֹ���ȳ��꣨ȥ��ǰ6���ֽ��Ѿ��õ���ʣ�µĿ����������ַ�����
			if (bytes.length > PACKET_LENGTH - 6)
				throw new RuntimeException("String Too Long For Packet");
			
			// ��int1λ�ô洢�ַ������ֽڳ���
			putInt1(packet, bytes.length);
			
			// ���ַ������Ƶ���Ϣ��
			Helper.byteCopy(packet.getData(), 6, bytes, 0, bytes.length);
		} catch (UnsupportedEncodingException e) {
			// �����ܷ�����
			e.printStackTrace();
		}
	}
	
	// ����Ϣ��ȡ���ַ���
	public static String getString(DatagramPacket packet) {
		try {
			// ��int1λ��ȡ���ַ������ֽڳ���
			int length = getInt1(packet);
			
			// ����Ϣ�нس�ָ�����ȵ��ֽ�����
			byte[] bytes = Helper.byteCut(packet.getData(), 6, length);
			
			// ���ֽ�����ת��Ϊutf8�ַ���
			return new String(bytes, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// �����ܷ�����
			e.printStackTrace();
			return null;
		}
	}
}
