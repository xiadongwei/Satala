package thread.NetSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import extend.IPAndHostPackage;
import extend.SortIPAddress;
import view.NetSender.NetSenderFrame;

public class GetOnlineListThread extends SendUDPThread {

	private NetSenderFrame nsf = null;

	private BufferedReader br = null;
	
	private File file;
	
	private List<String> items = new ArrayList<String>();;
	
	public GetOnlineListThread(NetSenderFrame nsf) {
		super();
		this.nsf = nsf;
		this.nsf.removeComboAllItems();
		try {
			
			file = new File("./","CastFile");
			
//			file = File.createTempFile("./","CastFile");
//			file.deleteOnExit();
			
			if (!file.exists()) file.createNewFile();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		readFileToCombo();
		
		try {			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GetOnlineListThread() {
		super();
		items.clear();
		try {
			
			file = new File("./","CastFile");
			
//			file = File.createTempFile("./","CastFile");
//			file.deleteOnExit();
			
			if (!file.exists()) file.createNewFile();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		readFileToItems();
		
		try {			
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GetOnlineListThread(String param) {

		super(param);
	}
	
	public String[] getItems(){
		return sortItemsList();
	}
	
	public void readFileToCombo() {
		try {
			nsf.removeComboAllItems();
			while(true){
				String t = br.readLine();
				if (t == null) break;
				else nsf.addComboAddress(t);
			}
			nsf.sortComboList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readFileToItems() {
		try {
			items.clear();
			while(true){
				String t = br.readLine();
				if (t == null) break;
				else items.add(t);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String[] sortItemsList(){
		IPAndHostPackage IPHostPackage[];
		String[] titem = new String[512];
		int h = 0;
		for(String item : items){
			titem[h++]= item;
		}
		String[] item = new String[h];
		for (int i = 0; i<h; i++){
			item[i]=titem[i];
		}
		IPHostPackage = new IPAndHostPackage[item.length];
		for (int i = 0; i<item.length; i++){
			String ipAddress = (String) NetSenderFrame.splitToken(item[i]).get(0);
			String hostName = (String) NetSenderFrame.splitToken(item[i]).get(1);
			IPHostPackage[i] = new IPAndHostPackage(ipAddress,hostName);
		}
		item = new SortIPAddress(IPHostPackage).getSortedIPAddress();
		return(item);
	}

}