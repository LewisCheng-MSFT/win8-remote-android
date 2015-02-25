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


// Win8遥控器（Remote）的PC代理（Agent）
// 在电脑端接收、解析、执行Remote发来的消息
public class Agent {
	private InetAddress remoteAddress;
	private HashMap<Integer, CommandHandlerInterface> commandHandlers =
			new HashMap<Integer, CommandHandlerInterface>();
	private Preprocessor preprocessor = new Preprocessor();
	
	public Agent() {
		// 将协议命令映射到对应的命令处理程序
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

	// 在本地局域网中查找Remote
	public void findRemote() throws IOException {
		// 创建多播Socket，并加入以224.0.0.8为地址的多播组
		MulticastSocket multicastSocket = new MulticastSocket(Protocol.MULTICAST_PORT);
		multicastSocket.joinGroup(InetAddress.getByName(Protocol.MULTICAST_ADDRESS));
		
		// 等待接收Remote发来的多播Hello消息
		System.out.println("Waiting for Remote...");
		DatagramPacket packet = PacketFactory.createReceivePacket();
		multicastSocket.receive(packet);
		multicastSocket.close(); // Socket是贵重资源，用完就要释放掉
		
		// 从Hello消息中解析出Remote的IP地址
		remoteAddress = Protocol.resolveHello(packet);
		System.out.println("Remote IP is " + remoteAddress.toString());
	}
	
	// 启动Agent进入消息循环
	public void start() throws IOException {
		// 协议Socket是Agent用来和Remote通信的
		DatagramSocket protocolSocket = new DatagramSocket(Protocol.PROTOCOL_PORT);
		
		// Agent主动发第一条ACK，告知Remote自己的IP
		protocolSocket.send(Protocol.generateAck(remoteAddress));
		
		// 为协议Socket设置接收超时
		protocolSocket.setSoTimeout(Protocol.IDLE_TIMEOUT);
		
		// 进入消息循环
		while (true) {
			DatagramPacket packet = PacketFactory.createReceivePacket();
			try {
				protocolSocket.receive(packet);
				
				// 在交给相应命令处理程序处理之前，
				// 要先经过预处理器做一些统一的预处理
				preprocessor.handleCommand(packet);
				
				// 从消息中解析出Command Id（即是什么命令）
				int commandId = Protocol.resolveCommandId(packet);
				
				// 如果消息指定了有效动作（即Command Id不是NO_ACTION），
				// 则调用对应的命令处理程序。
				if (commandId != Protocol.NO_ACTION) {
					CommandHandlerInterface handler = commandHandlers.get(commandId);
					handler.handleCommand(packet);
				}
			} catch (SocketTimeoutException e) {
				// 超过IDLE_TIMEOUT时间没收到Remote的消息
				// 就给Remote发一条Ack以重新建立连接
				protocolSocket.send(Protocol.generateAck(remoteAddress));
				System.out.println("Idle Timeout, Re-Ack");
			}
		}
	}

	public static void main(String[] args) {
		try {
			// 创建Agent对象
			Agent agent = new Agent();
			
			// 本地局域网中寻找Remote
			agent.findRemote();
			
			// 启动Agent并进入消息循环
			agent.start();
		} catch (Exception e) {
			// 有异常就全打出来
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
