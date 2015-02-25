package com.yyx.win8common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

// 辅助函数类
public abstract class Helper {
	// 把src数组中从start2位置开始的count个字节复制到dst数组的start1位置
	public static void byteCopy(byte[] dst, int start1, byte[] src, int start2, int count) {
		for (int i = 0; i < count; i++)
			dst[start1 + i] = src[start2 + i];
	}
	
	// 把src数组中从start位置开始的count个字节“截取”出来
	public static byte[] byteCut(byte[] src, int start, int count) {
		byte[] result = new byte[count];
		for (int i = 0; i < count; i++)
			result[i] = src[i + start];
		return result;
	}
	
	// 把一个int转换成4字节并复制到dst数组中start位置
	public static void int2Bytes(int n, byte[] dst, int start) {
		dst[start] = (byte)(n & 0xff);
		dst[start + 1] = (byte)((n & 0xff00) >> 8);
		dst[start + 2] = (byte)((n & 0xff0000) >> 16);
		dst[start + 3] = (byte)((n & 0xff000000) >> 24);
	}
	
	// 把src数组中start位置开始的4字节转换成一个int
	public static int bytes2Int(byte[] src, int start) {
		int a = src[start];
		int b = src[start + 1];
		int c = src[start + 2];
		int d = src[start + 3];
		return (d << 24) | (c << 16) | (b << 8) | a;
	}
	
	// 获取电脑的IP地址
	public static InetAddress getPcAddress() throws SocketException {
		InetAddress addr;
		
		// 获取所有网络接口
		Enumeration<NetworkInterface> intfs = NetworkInterface.getNetworkInterfaces();
		
		// 遍历所有网络接口
		while (intfs.hasMoreElements()) {
			NetworkInterface intf = intfs.nextElement();
			
			// 获取该接口上的所有地址
			Enumeration<InetAddress> addrs = intf.getInetAddresses();
			
			// 遍历该接口上的所有地址
			while (addrs.hasMoreElements()) {
				addr = addrs.nextElement();
				
				// 这个地址不能是本地回环地址
				// 这个地址不能是IPv6地址
				// 剩下就是IPv4了
				if (!addr.isLoopbackAddress() && addr.toString().indexOf(':') < 0)
					return addr;
			}
		}
		
		return null;
	}
	
	// 获取手机WiFi的IP地址
	public static InetAddress getPhoneAddr() throws SocketException {
		InetAddress addr;
		
		Enumeration<NetworkInterface> intfs = NetworkInterface.getNetworkInterfaces();
		while (intfs.hasMoreElements()) {
			NetworkInterface intf = intfs.nextElement();
			
			// 只名字包含wlan或eth的网络接口，即为WiFi网卡
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
	
	// 把ArrayList<String>转换成CharSequence[]
	public static CharSequence[] strList2Array(ArrayList<String> l) {
		CharSequence[] result = new CharSequence[l.size()];
		for (int i = 0; i < l.size(); i++)
			result[i] = l.get(i);
		return result;
	}
}
