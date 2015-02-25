package com.yyx.win8remote;

import java.io.IOException;
import java.net.*;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yyx.win8common.*;

// ң������Remote����
public class Remote {
	// ���ݸ�Handler����Ϣ��what������ȡֵ
	public static final int AGENT_NOT_FOUND = 0;
	public static final int AGENT_FOUND = 1;
	
	private DatagramSocket protocolSocket;
	private InetAddress agentAddress;
	private NetThread netThread = new NetThread();
	private static Remote singleton = new Remote();
	
	// ��Agent
	public void findAgent(Handler handler) {
		// Ҫ����һ�����߳�����
		// ��Ϊandroid�°��ǲ��������߳�����������
		new FindAgentThread(handler).start();
	}
	
	// Agent�ҵ�û��
	public boolean isAgentFound() {
		return agentAddress != null;
	}
	
	// ����Agent��IP�ַ���
	public String agentIP() {
		return agentAddress.toString().substring(1);
	}
	
	// ����Agent�ĵ�ַ����
	public InetAddress agentAddress() {
		return agentAddress;
	}
	
	// �첽������Ϣ����Ϣ����
	public void send(DatagramPacket packet) {
		Message msg = Message.obtain(netThread.handler);
		msg.obj = packet;
		msg.sendToTarget();
	}
	
	// ��ȡRemote�ĵ���
	// ȷ������Ӧ����ֻ��һ��Remoteʵ��
	public static Remote getSingleton() {
		return singleton;
	}
	
	// ����Ϣ�����յ��첽��Ϣʱ�Ĵ������
	// �����ܼ򵥣������������ȥ
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
	
	// ������Ϣ���е��߳�
	// Ϊ��Ҫ����Ϣ���У�
	// ��������������̲߳������������
	// ������һ���̸߳�������Ϣ
	// ����߳���û��Ϣ����Ϣ����Ϊ�գ�ʱ����������״̬�����˷�CPU
	// ���յ�һ����Ϣ����Ϣ���в�Ϊ�գ�ʱ������������NetHandler������Ϣ
	private class NetThread extends Thread {
		public Handler handler;
		
		@Override
		public void run() {
			// Ϊ��ǰ�̴߳�����Ϣ����
			Looper.prepare();
			
			// Ĭ��ʹ�����洴������Ϣ����
			handler = new NetHandler();
			
			// ��ʼ��Ϣѭ��
			Looper.loop();
		}
	}
	
	// ���ڲ���Agent���߳�
	// ԭ�������һ�������̲߳�֧�ַ�������
	// Ϊ�β���NetThread�ϲ���һ�𣿴�̫�鷳
	// �ο�����һ���£����Ƿ������߳����ɣ�
	private class FindAgentThread extends Thread {
		private Handler handler;
		
		public FindAgentThread(Handler handler) {
			this.handler = handler;
		}
		
		@Override
		public void run() {
			Message msg = Message.obtain(handler);
			try {
				// ����Э��Socket
				protocolSocket = new DatagramSocket(Protocol.PROTOCOL_PORT);
				
				// �����ಥSocket
				MulticastSocket multicastSocket = new MulticastSocket();
	
				// �����ಥHello��Ϣ��ͨ���Լ���IP��ַ
				DatagramPacket helloPacket = Protocol.generateHello();
	
				// �����������յ�Ack��Ϣ
				DatagramPacket ackPacket = PacketFactory.createReceivePacket();
				
				// ����Ack��Ϣ�Ľ��ճ�ʱ����ʱ�����¹㲥
				protocolSocket.setSoTimeout(Protocol.ACK_TIMEOUT);
				
				while (true) {
					// �㲥����Hello��Ϣ
					multicastSocket.send(helloPacket);
					
					try {
						// �ȴ�����Agent��Ack��Ϣ
						protocolSocket.receive(ackPacket);
						
						// �յ���Ack���˳�ѭ����
						break;
					} catch (SocketTimeoutException e) {
						// ���ճ�ʱ������
						System.out.println("Receive Timeout");
					}
				}
				
				// �رհ����Socket��Դ
				multicastSocket.close();
				
				// ��Ack��Ϣ������Agent��IP��ַ
				agentAddress = Protocol.resolveAck(ackPacket);
				System.out.println("Agent IP is " + agentAddress.toString());
				
				// ֪����Agent��IP��ַ�Ժ󣬾Ϳ��Կ���Net�߳���
				// ��������յ�����Ϣ���͸�Agent
				netThread.start();
				
				msg.what = AGENT_FOUND;
			} catch (IOException e) {
				System.err.println("Find Agent Error");
				e.printStackTrace();
				
				msg.what = AGENT_NOT_FOUND;
			}
			
			// ֪ͨMainActivity���ҽ��
			msg.sendToTarget();
		}
	}
}
