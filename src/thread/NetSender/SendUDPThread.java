package thread.NetSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import main.NetTransfer;
import extend.Configuration;

public class SendUDPThread extends Thread {

	public byte[] buf = new byte[100];

	public byte[] sendBuf = null;

	private DatagramSocket UDP = null;

	private DatagramPacket packet = null;

	private BufferedReader setupbr = null;
	
	private File setupfile;
	
	private String UTPcmd;
	
	public SendUDPThread() {
		
		super();
		
	}

	public SendUDPThread(String param) {
		super();
		if (param.equals("recreate")){
			reCreate();
		}else{
			UTPcmd = param;
			this.start();
		}
	}
	
	private void reCreate(){
		
		try {
			UDP = new DatagramSocket();
			buf = ("recreate"+"|").getBytes();
			packet = new DatagramPacket(buf, buf.length, InetAddress
					.getByName("127.0.0.1"), main.NetReceiver.UDPport);
			UDP.send(packet);
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public void run() {
		String localAddress = null;
		String localHost = null;
		try {
			UDP = new DatagramSocket();
			localAddress = InetAddress.getLocalHost().getHostAddress();
			Configuration rc = new Configuration(NetTransfer.SetupFile);//相对路径
			localHost = rc.getValue("HostName");//以下读取properties文件的值
			if (localHost.equals("")) 
				localHost = new String(InetAddress.getLocalHost().getHostName().getBytes());
			buf = (UTPcmd+"|" + localAddress+ " " +localHost + "|").getBytes();
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		try {
			packet = new DatagramPacket(buf, buf.length, InetAddress
						.getByName("255.255.255.255"), main.NetReceiver.UDPport);
			UDP.send(packet);
			
			setupfile = new File("./","CastSetup");
			if (!setupfile.exists()) setupfile.createNewFile();
			setupbr = new BufferedReader(new InputStreamReader(new FileInputStream(setupfile)));
			
			while (true){
				String str = setupbr.readLine();
				if (str==null) break;
				else{
					str = (String)splitToken(str," ").get(0);
					packet = new DatagramPacket(buf, buf.length, InetAddress
							.getByName(str), main.NetReceiver.UDPport);
					UDP.send(packet);
				}
			}
		} catch (UnknownHostException e) {
							
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				setupbr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UDP.close();
		}
	}

	public List<String> splitToken(String fs,String acter) {
		StringTokenizer pt = new StringTokenizer(fs, acter);
		List<String> l = new ArrayList<String>();
		while (pt.hasMoreTokens()) {
			l.add(pt.nextToken());
		}
		return l;
	}

}