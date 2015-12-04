package kr.ac.kaist.gan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;

public class SimpleSocketThread extends Thread{
	private Socket mSocket;
	private BufferedReader buffRecv;
	private BufferedWriter buffSend;
	
	private String mAddr = "localhost";
	private int mPort = 7777;
	private boolean mConnected = false;
	private Handler mHandler = null;
	
	static class MessageTypeClass{
		public static final int SIMSOCK_CONNECTED = 1;
		public static final int SIMSOCK_DATA = 2;
		public static final int SIMSOCK_DISCONNECTED = 3;
	};
	
	public enum MessageType { SIMSOCK_CONNECTED, SIMSOCK_DATA, SIMSOCK_DISCONNECTED };
	
	public SimpleSocketThread(String addr, int port, Handler handler){
		mAddr = addr;
		mPort = port;
		mHandler = handler;
	}
	
	private void makeMessage(MessageType what, Object obj){
		Message msg = Message.obtain();
		msg.what = what.ordinal();
		msg.obj = obj;
		mHandler.sendMessage(msg);
	}
	
	private boolean connect (String addr, int port){
		try{
			InetSocketAddress socketAddress = new InetSocketAddress(InetAddress.getByName(addr),port);
			mSocket = new Socket();
			mSocket.connect(socketAddress, 7777);
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public void run(){
		if(!connect(mAddr,mPort)) return; //connect failed
		if(mSocket == null) return; //connect failed
		
		try {
			buffRecv = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			buffSend = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mConnected = true;

		makeMessage(MessageType.SIMSOCK_CONNECTED, "");
		Log.d("SimpleSocket", "socket_thread loop started");

		try{
			buffSend.write("Hello");
		}catch(IOException e){
			e.printStackTrace();
		}
		
		String aLine = null;

		while( !Thread.interrupted() ){ try {
			aLine = buffRecv.readLine(); 
			if(aLine != null) makeMessage(MessageType.SIMSOCK_DATA, aLine);
			else break;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}}

		makeMessage(MessageType.SIMSOCK_DISCONNECTED, "");
		Log.d("SimpleSocket", "socket_thread loop terminated");

		try {
			buffRecv.close(); 
			buffSend.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mConnected = false;
	}
	
	synchronized public boolean isConnected(){
		return mConnected;
	}
	
	public void sendString(Editable str){
		PrintWriter out = new PrintWriter(buffSend, true);
		out.println(str);
	}
}