package com.yyx.win8common;

import java.net.DatagramPacket;
import java.net.InetAddress;


// 消息工厂
// 封装了DatagramPacket的创建过程
public class PacketFactory {

	// 创建用于接收的消息
	public static DatagramPacket createReceivePacket() {
		byte[] buf = new byte[Protocol.PACKET_LENGTH];
		return new DatagramPacket(buf, buf.length);
	}
	
	// 创建用于发送的消息
	public static DatagramPacket createSendPack(InetAddress destAddr, int port) {
		byte[] buf = new byte[Protocol.PACKET_LENGTH];
		return new DatagramPacket(buf, buf.length, destAddr, port);
	}
}
