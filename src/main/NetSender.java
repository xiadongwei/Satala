package main;
import createFrame.CreateSenderFrame;

/**
 * @author user
 * 
 */
public class NetSender {

	/**
	 * @param args
	 */
	public NetSender(){
		new CreateSenderFrame().start();
	}
	
	public NetSender(String hostAddress){
		new CreateSenderFrame(hostAddress).start();
	}
	
	public NetSender(String hostAddress,int x,int y,String message){
		new CreateSenderFrame(hostAddress,x,y,message).start();
	}
	
	
	public static void main(String[] args) {
		if (args.length>0){
			if (args[0].equals("-h")&&args[1]!=null){
				new NetSender(args[1]);
			}
		}else 
			new NetSender();
	}
}
