package com.yyx.win8common;

import java.net.DatagramPacket;
import java.net.InetAddress;


// ��Ϣ����
// ��װ��DatagramPacket�Ĵ�������
public class PacketFactory {

	// �������ڽ��յ���Ϣ
	public static DatagramPacket createReceivePacket() {
		byte[] buf = new byte[Protocol.PACKET_LENGTH];
		return new DatagramPacket(buf, buf.length);
	}
	
	// �������ڷ��͵���Ϣ
	public static DatagramPacket createSendPack(InetAddress destAddr, int port) {
		byte[] buf = new byte[Protocol.PACKET_LENGTH];
		return new DatagramPacket(buf, buf.length, destAddr, port);
	}
}
