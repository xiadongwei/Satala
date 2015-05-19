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
	 * ���췽��:
	 * destAddress:Ŀ���ַ
	 * port��ʹ�õ�����˿�
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
	 * ������ӿ�
	 */
	
	protected void startSocket() throws UnknownHostException, IOException{
		
		socket = new Socket(destAddress, port);

		inByte = new DataInputStream(socket.getInputStream());

		outByte = new DataOutputStream(socket.getOutputStream());
	}
	/*
	 * �ر�����ӿ�
	 */
	
	protected void closeSocket() throws IOException{
		
		if (socket!=null && !socket.isClosed()) socket.close();
		
		if (inByte!=null) inByte.close();
		
		if (outByte!=null) outByte.close();
	}
	/*
	 *���ַ�������"|"��Ϊ�ָ����ȡcmdList�б�
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
	 * ��cmdList�и���Index��ֵ�ж��Ƿ���param��ȡ���ȷ���True������ȷ���False��
	 */
	public boolean equParam(int index, String param){
		
		if (cmdList.get(index).equals(param)) return true;
		
		else return false; 
	}
	/*
	 * ����cmdList��Index���ֵ��
	 */
	public String getParam(int index){
		
		return cmdList.get(index);
	}
	/*
	 * ����������д��UTF�ַ�����
	 */
	public void writeUTF(String Stream) throws IOException{
		
		outByte.writeUTF(Stream);
		
		System.out.println(Stream+"                    - "+this.getClass().getName());
	}
	
	/*
	 * ���������ж���UTF�ַ�����
	 */
	public String readUTF() throws IOException{
		
		String recString = inByte.readUTF();
		
		System.out.println(recString+"                    - "+this.getClass().getName());
		
		return (recString);
	}
	/*
	 * �����Ƿ��Ѿ����ӳɹ���
	 */
	public boolean isConnected(){
		
		return isConnected;
	}
	
}
