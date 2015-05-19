package thread.NetSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

import extend.ListDir;


public class NetSenderThread extends SenderThread {

	private Hashtable<Integer,File> selectedFiles;

	private int hashFile;

	private int NTRHandle;

	private String filePath;

	private String type;
	
	private boolean active = true;

	public NetSenderThread(String destAddress, int port, int hashFile,
			String filePath, int NTRHandle, Hashtable<Integer,File> selectedFiles, String type) {

		super(destAddress, port);

		this.filePath = filePath;

		this.hashFile = hashFile;

		this.NTRHandle = NTRHandle;

		this.selectedFiles = selectedFiles;

		this.type = type;
		
		this.setPriority(Thread.MAX_PRIORITY);
                
                this.setName("NetSenderThread");
	}
	/*
	 * 设置中断如果active＝false则传送终止，并且关闭socket
	 */
	public void setActive(boolean active){
		
		this.active = active;
	}
	
	public void run() {
		try {
			if (this.type.equals("PutFile")) {
				// PutFile|filePath|NTRhandle|fileSize

				File sendFile = selectedFiles.get(hashFile);
				
				writeUTF("PutFile|" + this.filePath + "|" + this.NTRHandle+"|"+sendFile.length()+"|");

				SendFile(sendFile);
				
			}else if (this.type.equals("PutFolder")){
				// PutFolder|filePath|NTRHandle
				
				File dir = selectedFiles.get(hashFile);
				
				writeUTF("FolderInfo|"+this.NTRHandle);
				
				writeUTF("PutFolder|" + this.filePath + "|" + this.NTRHandle);
				
				writeUTF("CreateFolder|"
						+ dir.getName() + "|");
				
				SendDirectory(dir);
				
				writeUTF("End|");
			}
			
		
		} catch (IOException e) {
//			CreateErrorFrame thread = new CreateErrorFrame();
//			thread.setMessage("失去与目标主机的连接");
//			thread.start();
			e.printStackTrace();
		}finally{
		
			try {
				closeSocket();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void SendFile(File openFile) throws IOException  {

		FileInputStream fis = new FileInputStream(openFile);

		long size = openFile.length();

		byte[] buffer = new byte[0xffff];

		long count = size / 0xffff;

		while ((count--) > 0 && active) {

			fis.read(buffer);
			
			try {
				outByte.write(buffer);
			} catch (IOException e) {
				try {
					fis.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			
			yield();
		}

		fis.read(buffer, 0, (int) (size % 0xffff));

		try {
			outByte.write(buffer, 0, (int) (size % 0xffff));
		} catch (IOException e) {
			try {
				fis.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		fis.close();
		
		if (!active&&(!type.equals("FOLDER"))) closeSocket();
		
		yield();

	}

	public void SendDirectory(File dir) throws IOException {
		
		ListDir dirTree = new ListDir(dir.getPath());
		
		for (int i = 0; i < dirTree.fileList.length && active; i++) {
			
			if (!dirTree.fileList[i].isHidden()) {
				
				if (dirTree.fileList[i].isFile()) {
				
					if (active) writeUTF("PutFolderFile|" + dirTree.fileList[i].getName()
							+ "|" + dirTree.fileList[i].length() + "|");
					
					if (active) this.SendFile(dirTree.fileList[i]);
				
				} else if (dirTree.fileList[i].isDirectory()) {
				
					if (active) writeUTF("CreateFolder|"
							+ dirTree.fileList[i].getName() + "|");
					
					if (active) SendDirectory(dirTree.fileList[i]);
				}
			}
		}
		
		if (active) outByte.writeUTF("GoUpFolder|");
	}
}
