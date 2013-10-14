package mekanism.common.voice;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class VoiceServerManager
{
	public Set<VoiceConnection> connections = new HashSet<VoiceConnection>();
	
	public ServerSocket serverSocket;
	
	public boolean running;
	
	public Thread listenThread;
	
	public void start()
	{
		System.out.println("Starting up voice server.");
		
		try {
			serverSocket = new ServerSocket(36123);
			(listenThread = new ListenThread()).start();
		} catch(Exception e) {}
		
		running = true;
	}
	
	public void stop()
	{
		try {
			listenThread.interrupt();
			
			serverSocket.close();
			serverSocket = null;
			
			System.out.println("Shutting down voice server.");
		} catch(SocketException e) {
			if(!e.getLocalizedMessage().toLowerCase().equals("socket closed"))
			{
				e.printStackTrace();
			}
		} catch(Exception e) {
			System.err.println("Error while stopping voice server.");
			e.printStackTrace();
		}
		
		running = false;
	}
	
	public void sendToPlayers(short byteCount, byte[] audioData, VoiceConnection connection)
	{
		if(connection.entityPlayer == null)
		{
			return;
		}
		
		int channel = connection.getCurrentChannel();
		
		if(channel == 0)
		{
			return;
		}
		
		for(VoiceConnection iterConn : connections)
		{
			if(iterConn.entityPlayer == null || iterConn == connection || !iterConn.canListen(channel))
			{
				continue;
			}
			
			iterConn.sendToPlayer(byteCount, audioData, connection);
		}
	}
	
	public class ListenThread extends Thread
	{
		public ListenThread()
		{
			setDaemon(true);
			setName("VoiceServer Listen Thread");
		}
		
		@Override
		public void run()
		{
			while(running)
			{
				try {
					Socket s = serverSocket.accept();
					VoiceConnection connection = new VoiceConnection(s);
					connections.add(connection);
					
					System.out.println("Accepted new connection.");
				} catch(SocketException e) {
					if(!e.getLocalizedMessage().toLowerCase().equals("socket closed"))
					{
						e.printStackTrace();
					}
				} catch(Exception e) {
					System.err.println("Error while accepting connection.");
					e.printStackTrace();
				}
			}
		}
	}
}