package com.yyx.win8agent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import com.yyx.win8common.PacketFactory;
import com.yyx.win8common.Protocol;


// Win8ң������Remote����PC����Agent��
// �ڵ��Զ˽��ա�������ִ��Remote��������Ϣ
public class Agent {
	private InetAddress remoteAddress;
	private HashMap<Integer, CommandHandlerInterface> commandHandlers =
			new HashMap<Integer, CommandHandlerInterface>();
	private Preprocessor preprocessor = new Preprocessor();
	
	public Agent() {
		// ��Э������ӳ�䵽��Ӧ����������
		commandHandlers.put(Protocol.CHARMS_MENU, new CharmsMenuHandler());
		commandHandlers.put(Protocol.KEY_ESCAPE, new KeyEscapeHandler());
		commandHandlers.put(Protocol.ALL_APP_MENU, new AllAppMenuHandler());
		commandHandlers.put(Protocol.KEY_CONTEXT_MENU, new KeyContextMenuHandler());
		commandHandlers.put(Protocol.KEY_WINDOWS, new KeyWindowsHandler());
		commandHandlers.put(Protocol.KEY_UP, new KeyUpHandler());
		commandHandlers.put(Protocol.KEY_DOWN, new KeyDownHandler());
		commandHandlers.put(Protocol.KEY_LEFT, new KeyLeftHandler());
		commandHandlers.put(Protocol.KEY_RIGHT, new KeyRightHandler());
		commandHandlers.put(Protocol.TASK_LIST_HOLDING, new TaskListHoldingHandler());
		commandHandlers.put(Protocol.KEY_TAB, new KeyTabHandler());
		commandHandlers.put(Protocol.KEY_ENTER, new KeyEnterHandler());
		commandHandlers.put(Protocol.KEY_SHIFT_TAB, new KeyShiftTabHandler());
		commandHandlers.put(Protocol.MOUSE_LEFT_CLICK, new MouseLeftClickHandler());
		commandHandlers.put(Protocol.MOUSE_RIGHT_CLICK, new MouseRightClickHandler());
		commandHandlers.put(Protocol.MOUSE_MOVE, new MouseMoveHandler());
		commandHandlers.put(Protocol.CLOSE_APP, new CloseAppHandler());
		commandHandlers.put(Protocol.CONTROL_PANEL, new ControlPanelHandler());
		commandHandlers.put(Protocol.SYSTEM, new SystemHandler());
		commandHandlers.put(Protocol.SETTING, new SettingHandler());
		commandHandlers.put(Protocol.DESKTOP, new DesktopHandler());
		commandHandlers.put(Protocol.VOICE_SEARCH, new VoiceSearchHandler());
	}

	// �ڱ��ؾ������в���Remote
	public void findRemote() throws IOException {
		// �����ಥSocket����������224.0.0.8Ϊ��ַ�Ķಥ��
		MulticastSocket multicastSocket = new MulticastSocket(Protocol.MULTICAST_PORT);
		multicastSocket.joinGroup(InetAddress.getByName(Protocol.MULTICAST_ADDRESS));
		
		// �ȴ�����Remote�����ĶಥHello��Ϣ
		System.out.println("Waiting for Remote...");
		DatagramPacket packet = PacketFactory.createReceivePacket();
		multicastSocket.receive(packet);
		multicastSocket.close(); // Socket�ǹ�����Դ�������Ҫ�ͷŵ�
		
		// ��Hello��Ϣ�н�����Remote��IP��ַ
		remoteAddress = Protocol.resolveHello(packet);
		System.out.println("Remote IP is " + remoteAddress.toString());
	}
	
	// ����Agent������Ϣѭ��
	public void start() throws IOException {
		// Э��Socket��Agent������Remoteͨ�ŵ�
		DatagramSocket protocolSocket = new DatagramSocket(Protocol.PROTOCOL_PORT);
		
		// Agent��������һ��ACK����֪Remote�Լ���IP
		protocolSocket.send(Protocol.generateAck(remoteAddress));
		
		// ΪЭ��Socket���ý��ճ�ʱ
		protocolSocket.setSoTimeout(Protocol.IDLE_TIMEOUT);
		
		// ������Ϣѭ��
		while (true) {
			DatagramPacket packet = PacketFactory.createReceivePacket();
			try {
				protocolSocket.receive(packet);
				
				// �ڽ�����Ӧ����������֮ǰ��
				// Ҫ�Ⱦ���Ԥ��������һЩͳһ��Ԥ����
				preprocessor.handleCommand(packet);
				
				// ����Ϣ�н�����Command Id������ʲô���
				int commandId = Protocol.resolveCommandId(packet);
				
				// �����Ϣָ������Ч��������Command Id����NO_ACTION����
				// ����ö�Ӧ����������
				if (commandId != Protocol.NO_ACTION) {
					CommandHandlerInterface handler = commandHandlers.get(commandId);
					handler.handleCommand(packet);
				}
			} catch (SocketTimeoutException e) {
				// ����IDLE_TIMEOUTʱ��û�յ�Remote����Ϣ
				// �͸�Remote��һ��Ack�����½�������
				protocolSocket.send(Protocol.generateAck(remoteAddress));
				System.out.println("Idle Timeout, Re-Ack");
			}
		}
	}

	public static void main(String[] args) {
		try {
			// ����Agent����
			Agent agent = new Agent();
			
			// ���ؾ�������Ѱ��Remote
			agent.findRemote();
			
			// ����Agent��������Ϣѭ��
			agent.start();
		} catch (Exception e) {
			// ���쳣��ȫ�����
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
