package com.yyx.win8common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

// ����������
public abstract class Helper {
	// ��src�����д�start2λ�ÿ�ʼ��count���ֽڸ��Ƶ�dst�����start1λ��
	public static void byteCopy(byte[] dst, int start1, byte[] src, int start2, int count) {
		for (int i = 0; i < count; i++)
			dst[start1 + i] = src[start2 + i];
	}
	
	// ��src�����д�startλ�ÿ�ʼ��count���ֽڡ���ȡ������
	public static byte[] byteCut(byte[] src, int start, int count) {
		byte[] result = new byte[count];
		for (int i = 0; i < count; i++)
			result[i] = src[i + start];
		return result;
	}
	
	// ��һ��intת����4�ֽڲ����Ƶ�dst������startλ��
	public static void int2Bytes(int n, byte[] dst, int start) {
		dst[start] = (byte)(n & 0xff);
		dst[start + 1] = (byte)((n & 0xff00) >> 8);
		dst[start + 2] = (byte)((n & 0xff0000) >> 16);
		dst[start + 3] = (byte)((n & 0xff000000) >> 24);
	}
	
	// ��src������startλ�ÿ�ʼ��4�ֽ�ת����һ��int
	public static int bytes2Int(byte[] src, int start) {
		int a = src[start];
		int b = src[start + 1];
		int c = src[start + 2];
		int d = src[start + 3];
		return (d << 24) | (c << 16) | (b << 8) | a;
	}
	
	// ��ȡ���Ե�IP��ַ
	public static InetAddress getPcAddress() throws SocketException {
		InetAddress addr;
		
		// ��ȡ��������ӿ�
		Enumeration<NetworkInterface> intfs = NetworkInterface.getNetworkInterfaces();
		
		// ������������ӿ�
		while (intfs.hasMoreElements()) {
			NetworkInterface intf = intfs.nextElement();
			
			// ��ȡ�ýӿ��ϵ����е�ַ
			Enumeration<InetAddress> addrs = intf.getInetAddresses();
			
			// �����ýӿ��ϵ����е�ַ
			while (addrs.hasMoreElements()) {
				addr = addrs.nextElement();
				
				// �����ַ�����Ǳ��ػػ���ַ
				// �����ַ������IPv6��ַ
				// ʣ�¾���IPv4��
				if (!addr.isLoopbackAddress() && addr.toString().indexOf(':') < 0)
					return addr;
			}
		}
		
		return null;
	}
	
	// ��ȡ�ֻ�WiFi��IP��ַ
	public static InetAddress getPhoneAddr() throws SocketException {
		InetAddress addr;
		
		Enumeration<NetworkInterface> intfs = NetworkInterface.getNetworkInterfaces();
		while (intfs.hasMoreElements()) {
			NetworkInterface intf = intfs.nextElement();
			
			// ֻ���ְ���wlan��eth������ӿڣ���ΪWiFi����
			if (!intf.getDisplayName().contains("wlan") &&
				!intf.getDisplayName().contains("eth"))
				continue;
			
			Enumeration<InetAddress> addrs = intf.getInetAddresses();
			while (addrs.hasMoreElements()) {
				addr = addrs.nextElement();
				
				if (addr.toString().indexOf(':') < 0)
					return addr;
			}
		}
		
		return null;
	}
	
	// ��ArrayList<String>ת����CharSequence[]
	public static CharSequence[] strList2Array(ArrayList<String> l) {
		CharSequence[] result = new CharSequence[l.size()];
		for (int i = 0; i < l.size(); i++)
			result[i] = l.get(i);
		return result;
	}
}
