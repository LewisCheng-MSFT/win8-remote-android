package com.yyx.win8remote;

import java.io.IOException;
import java.net.*;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yyx.win8common.*;

// 遥控器（Remote）类
public class Remote {
	// 传递给Handler的消息中what参数的取值
	public static final int AGENT_NOT_FOUND = 0;
	public static final int AGENT_FOUND = 1;
	
	private DatagramSocket protocolSocket;
	private InetAddress agentAddress;
	private NetThread netThread = new NetThread();
	private static Remote singleton = new Remote();
	
	// 找Agent
	public void findAgent(Handler handler) {
		// 要创建一条新线程再找
		// 因为android新版是不能在主线程里访问网络的
		new FindAgentThread(handler).start();
	}
	
	// Agent找到没？
	public boolean isAgentFound() {
		return agentAddress != null;
	}
	
	// 返回Agent的IP字符串
	public String agentIP() {
		return agentAddress.toString().substring(1);
	}
	
	// 返回Agent的地址对象
	public InetAddress agentAddress() {
		return agentAddress;
	}
	
	// 异步发送消息给消息队列
	public void send(DatagramPacket packet) {
		Message msg = Message.obtain(netThread.handler);
		msg.obj = packet;
		msg.sendToTarget();
	}
	
	// 获取Remote的单例
	// 确保整个应用中只有一个Remote实例
	public static Remote getSingleton() {
		return singleton;
	}
	
	// 当消息队列收到异步消息时的处理程序
	// 动作很简单：把这个包发出去
	@SuppressLint("HandlerLeak")
	private class NetHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			DatagramPacket packet = (DatagramPacket)msg.obj;

			try {
				protocolSocket.send(packet);
			} catch (IOException e) {
				System.err.println("Send Error");
				e.printStackTrace();
			}
		}
	}
	
	// 处理消息队列的线程
	// 为何要用消息队列？
	// 还是这个道理：主线程不允许访问网络
	// 必须另开一个线程负责发送消息
	// 这个线程在没消息（消息队列为空）时，处于休眠状态，不浪费CPU
	// 在收到一个消息（消息队列不为空）时，则调用上面的NetHandler发送消息
	private class NetThread extends Thread {
		public Handler handler;
		
		@Override
		public void run() {
			// 为当前线程创建消息队列
			Looper.prepare();
			
			// 默认使用上面创建的消息队列
			handler = new NetHandler();
			
			// 开始消息循环
			Looper.loop();
		}
	}
	
	// 用于查找Agent的线程
	// 原因和上面一样：主线程不支持访问网络
	// 为何不和NetThread合并在一起？答：太麻烦
	// 何况不是一件事，还是分两个线程做吧！
	private class FindAgentThread extends Thread {
		private Handler handler;
		
		public FindAgentThread(Handler handler) {
			this.handler = handler;
		}
		
		@Override
		public void run() {
			Message msg = Message.obtain(handler);
			try {
				// 创建协议Socket
				protocolSocket = new DatagramSocket(Protocol.PROTOCOL_PORT);
				
				// 创建多播Socket
				MulticastSocket multicastSocket = new MulticastSocket();
	
				// 产生多播Hello消息，通告自己的IP地址
				DatagramPacket helloPacket = Protocol.generateHello();
	
				// 创建即将接收的Ack消息
				DatagramPacket ackPacket = PacketFactory.createReceivePacket();
				
				// 设置Ack消息的接收超时，超时了重新广播
				protocolSocket.setSoTimeout(Protocol.ACK_TIMEOUT);
				
				while (true) {
					// 广播发送Hello消息
					multicastSocket.send(helloPacket);
					
					try {
						// 等待接收Agent的Ack消息
						protocolSocket.receive(ackPacket);
						
						// 收到了Ack就退出循环了
						break;
					} catch (SocketTimeoutException e) {
						// 接收超时，重来
						System.out.println("Receive Timeout");
					}
				}
				
				// 关闭昂贵的Socket资源
				multicastSocket.close();
				
				// 从Ack消息解析出Agent的IP地址
				agentAddress = Protocol.resolveAck(ackPacket);
				System.out.println("Agent IP is " + agentAddress.toString());
				
				// 知道了Agent的IP地址以后，就可以开启Net线程了
				// 它负责把收到的消息发送给Agent
				netThread.start();
				
				msg.what = AGENT_FOUND;
			} catch (IOException e) {
				System.err.println("Find Agent Error");
				e.printStackTrace();
				
				msg.what = AGENT_NOT_FOUND;
			}
			
			// 通知MainActivity查找结果
			msg.sendToTarget();
		}
	}
}
