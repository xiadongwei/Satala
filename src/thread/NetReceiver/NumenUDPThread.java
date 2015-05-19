package thread.NetReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import main.NetTransfer;
import thread.NetSender.SendUDPThread;
import extend.Configuration;


public class NumenUDPThread extends Thread {

	public byte[] buf = new byte[100];

	public byte[] sendBuf = null;

	private DatagramSocket UDP;
	
	private DatagramSocket UDPSender ;

	private DatagramPacket packet;

	private File broadCastFile = new File("./","CastFile");

	private PrintWriter pw = null;

	private List<String> memoryMirror = new ArrayList<String>();
	
	private List<String> cmdList = null;

	public NumenUDPThread() { // starting NetReceiver process BroadCast Package
		try {
			UDP = new DatagramSocket(main.NetReceiver.UDPport);
			UDPSender = new DatagramSocket();
			this.start();
			new SendUDPThread("recreate");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> splitToken(String fs,String charActer) {
		StringTokenizer pt = new StringTokenizer(fs, charActer);
		List<String> l = new ArrayList<String>();
		while (pt.hasMoreTokens()) {
			l.add(pt.nextToken());
		}
		return l;
	}
	
	public void run() {
		try {
			while (true) {
				packet = new DatagramPacket(buf, buf.length);
				UDP.receive(packet);
				buf = packet.getData();
				String receiveIPPacket = new String(buf, 0, packet.getLength());
				cmdList = splitToken(receiveIPPacket,"|");
				if (((String)cmdList.get(0)).equals("broadcast")){
					sendRebound((String)splitToken((String) cmdList.get(1)," ").get(0));
					insertItem(((String) cmdList.get(1)));
				}else if (((String)cmdList.get(0)).equals("rebound")){
					insertItem(((String) cmdList.get(1)));
				}else if (((String)cmdList.get(0)).equals("removeIP")){
					removeItem(((String) cmdList.get(1)));
				}else if (((String)cmdList.get(0)).equals("recreate")){
					memoryMirror.clear();
					writeFile();
					new SendUDPThread("broadcast"); 
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			UDPSender.close();
			UDP.close();
		}
	}

	public void sendRebound(String IPAddress) throws IOException  {
		try {
			Configuration rc = new Configuration(NetTransfer.SetupFile);//相对路径
			    
			String hostName = rc.getValue("HostName");//以下读取properties文件的值
				
			if (hostName.equals("")) hostName = new String(InetAddress.getLocalHost().getHostName().getBytes());
					
			sendBuf = ("rebound|"+InetAddress.getLocalHost().getHostAddress()+" "+hostName+"|").getBytes();
			packet = new DatagramPacket(sendBuf, sendBuf.length,InetAddress.getByName(IPAddress), main.NetReceiver.UDPport);
		} catch (UnknownHostException e) {
			removeItem(IPAddress);
			e.printStackTrace();
		}
		UDPSender.send(packet);
	}
	
	public void insertItem(String receiveIPPacket) {

		boolean status = true;
		
		for(String tempStr : memoryMirror){
			if (tempStr.equals(receiveIPPacket)){
				status = false;
			}
		}
		if (status){
			memoryMirror.add(receiveIPPacket);
			writeFile();
		}
	}
	
	public void removeItem(String IPAddress){
		memoryMirror.remove(IPAddress);
		writeFile();
	}
	
	public void writeFile(){
		
		try {
			pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(broadCastFile)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (String tempStr : memoryMirror){
			pw.println(tempStr);
		}
		pw.close();
	}

}