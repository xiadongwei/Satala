package createFrame;

import java.awt.Toolkit;
import java.net.Socket;
import java.util.List;

import main.NetTransfer;

import extend.Configuration;

import view.NetReceiver.NetReceiverFrame;

public class CreateReceiverFrame extends Thread {
	private Socket socket;
	
	public List<String> packageList;
	
	public CreateReceiverFrame(){
		super();
	}
	
	public CreateReceiverFrame(Socket socket,List<String> packageList){
		
		this.socket = socket;
		
		this.packageList = packageList;
	}
	
	public void run(){
		
		Configuration rc = new Configuration(NetTransfer.SetupFile);
	    
	    NetReceiverFrame c = null;
	    
	    if (this.socket!=null)
			
			c = new NetReceiverFrame(socket, packageList);
		
		else
			c = new NetReceiverFrame();	
	    
	    c.setTitle("NetReceiver");
	    
	    String onTop = rc.getValue("OnTop");
	    
	    if (!onTop.toUpperCase().equals("FALSE")) c.setAlwaysOnTop(true);
		
		String beep = rc.getValue("Beep");
		
	    if (!beep.toUpperCase().equals("FALSE")) Toolkit.getDefaultToolkit().beep();
	    
	    c.setVisible(true);
	}
}
