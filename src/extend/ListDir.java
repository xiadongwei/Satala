package extend;
import java.io.*;

public class ListDir {

	protected String path;
	protected String fileName;
	public File[] fileList;
	protected File dir;
	protected boolean searchSubDir;
	
	public ListDir() {
	
		this("./");
	}
	
	public ListDir(String _path) {
		
		this.path = _path;
		this.setDir();
		this.setDirFileList();
	}
	
	protected void setDir() {
	
		this.dir = new File(this.path);
	}
	
	protected void setDirFileList() {
	
		this.fileList = this.dir.listFiles();
	}
	
	/*public static void main(String[]args) {
	
		listDir NetSenderFrame = new listDir("f:/");
		
		for(int i=0;i<NetSenderFrame.fileList.length;i++){
			
			if (!NetSenderFrame.fileList[i].isHidden()) {
				if(NetSenderFrame.fileList[i].isFile()){
				
					System.out.println("File is: " + NetSenderFrame.fileList[i]);
				} else if (NetSenderFrame.fileList[i].isDirectory()) {
				
					System.out.println("Directory is: " + NetSenderFrame.fileList[i]);
				}
			}
		}
	}*/
}
