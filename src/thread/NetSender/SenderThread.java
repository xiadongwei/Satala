package thread.NetSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import createFrame.CreateErrorFrame;

public class SenderThread extends Thread {

	/**
	 * @param args
	 */
	
	protected Socket socket = null;

	protected DataInputStream inByte = null;

	protected DataOutputStream outByte = null;
	
	protected String destAddress = null;
	
	protected int port;
	
	protected List<String> cmdList = null;
	
	private static String SPLIT = "|";
	
	protected boolean isConnected = false;
	
	/*
	 * 构造方法:
	 * destAddress:目标地址
	 * port：使用的网络端口
	 */
	public SenderThread(String destAddress,int port){
		
		super();
		
		this.destAddress = destAddress;
		
		this.port = port;
		
		try {
			
			startSocket();
			
			this.isConnected = true;
		
		} catch (UnknownHostException e) {
			
			CreateErrorFrame thread = new CreateErrorFrame();
			thread.setMessage("The host & address incorrect");
			thread.start();
			
			e.printStackTrace();
		
		} catch (IOException e) {

			CreateErrorFrame thread = new CreateErrorFrame();
			thread.setMessage("Lose network connection");
			thread.start();
			
			e.printStackTrace();
			
			try {
			
				closeSocket();
			
			} catch (IOException e1) {
				
				e.printStackTrace();
			}
		} 
		
	}
	/*
	 * 打开网络接口
	 */
	
	protected void startSocket() throws UnknownHostException, IOException{
		
		socket = new Socket(destAddress, port);

		inByte = new DataInputStream(socket.getInputStream());

		outByte = new DataOutputStream(socket.getOutputStream());
	}
	/*
	 * 关闭网络接口
	 */
	
	protected void closeSocket() throws IOException{
		
		if (socket!=null && !socket.isClosed()) socket.close();
		
		if (inByte!=null) inByte.close();
		
		if (outByte!=null) outByte.close();
	}
	/*
	 *从字符串中以"|"作为分割符获取cmdList列表。
	 */
	public List<String> splitToken(String fs) {
		
		StringTokenizer pt = new StringTokenizer(fs, SPLIT);
		
		List<String>l = new ArrayList<String>();
		
		while (pt.hasMoreTokens()) {
		
			l.add(pt.nextToken());
		}
		
		return l;
	}
	/*
	 * 从cmdList中根据Index的值判断是否与param相等。相等返回True，不相等返回False。
	 */
	public boolean equParam(int index, String param){
		
		if (cmdList.get(index).equals(param)) return true;
		
		else return false; 
	}
	/*
	 * 返回cmdList中Index项的值。
	 */
	public String getParam(int index){
		
		return cmdList.get(index);
	}
	/*
	 * 向网络流中写入UTF字符串。
	 */
	public void writeUTF(String Stream) throws IOException{
		
		outByte.writeUTF(Stream);
		
		System.out.println(Stream+"                    - "+this.getClass().getName());
	}
	
	/*
	 * 从网络流中读入UTF字符串。
	 */
	public String readUTF() throws IOException{
		
		String recString = inByte.readUTF();
		
		System.out.println(recString+"                    - "+this.getClass().getName());
		
		return (recString);
	}
	/*
	 * 返回是否已经连接成功。
	 */
	public boolean isConnected(){
		
		return isConnected;
	}
	
}
