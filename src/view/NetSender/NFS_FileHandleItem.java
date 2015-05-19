package view.NetSender;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import extend.ListDir;

public class NFS_FileHandleItem extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel fileLogo_Label;
	private JLabel removeItem_Label;
	private JPanel panel;
	private JLabel fileName_Label;
	private JLabel fileSize_Label;
	
	private Hashtable<Integer,File> selectedFiles;
	private JScrollPane NFS_scrollPane;
	private JPanel NFS_backPanel;
	private File file;
	private DecimalFormat df = new DecimalFormat("#.00");
	/**
	 * Create the panel
	 */
	public NFS_FileHandleItem(JScrollPane NFS_scrollPane, JPanel NFS_backPanel,Hashtable<Integer,File> selectedFiles) {
		super();
		this.NFS_scrollPane = NFS_scrollPane;
		this.NFS_backPanel = NFS_backPanel;
		this.selectedFiles = selectedFiles;
		initialize();
		//
	}
	private void initialize(){
		setPreferredSize(new Dimension(300, 30));
		setMinimumSize(new Dimension(300, 30));
		setMaximumSize(new Dimension(65535, 30));
                
//		setPreferredSize(new Dimension(300, 32));
		setLayout(new BorderLayout());

		fileLogo_Label = new JLabel();
		fileLogo_Label.setHorizontalAlignment(SwingConstants.CENTER);
		fileLogo_Label.setPreferredSize(new Dimension(30, 30));
		fileLogo_Label.setHorizontalTextPosition(SwingConstants.CENTER);
		fileLogo_Label.setIcon(new ImageIcon(getClass().getResource("/view/Picture/file.png")));
		add(fileLogo_Label, BorderLayout.WEST);

		removeItem_Label = new JLabel();
		removeItem_Label.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				selectedFiles.remove(file.hashCode());
				NFS_backPanel.remove(NFS_FileHandleItem.this);
				NFS_scrollPane.paintAll(NFS_scrollPane.getGraphics());
			}
		});
		removeItem_Label.setHorizontalAlignment(SwingConstants.CENTER);
		removeItem_Label.setPreferredSize(new Dimension(30, 30));
		removeItem_Label.setHorizontalTextPosition(SwingConstants.CENTER);
		removeItem_Label.setIcon(new ImageIcon(getClass().getResource("/view/Picture/trash.png")));
		add(removeItem_Label, BorderLayout.EAST);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		fileName_Label = new JLabel();
		panel.add(fileName_Label, BorderLayout.CENTER);
		fileName_Label.setText("");

		fileSize_Label = new JLabel();
		panel.add(fileSize_Label, BorderLayout.EAST);
		fileSize_Label.setText(" ");
	}

	public void setType(String type){
		if (type.equals("FILE")){
			fileLogo_Label.setIcon(new ImageIcon(getClass().getResource("/view/Picture/file.png")));
		}else if (type.equals("FOLDER")){
			fileLogo_Label.setIcon(new ImageIcon(getClass().getResource("/view/Picture/folder.png")));
		}
	}
	
	public void setFile(File file){
		this.file = file;
		if (file.isDirectory()) {
			setType("FOLDER");
			setFileSize(getFolderSize(file));
			System.out.println("NFS_FileHandleItem = "+getFolderSize(file));
		}
		else{
			setType("FILE");
			setFileSize(file.length());
		}
		setFileName(file.getName());
	}
	
	public void setFileName(String fileName){
		fileName_Label.setText(fileName);
	}
	
	public void setFileSize(long fileSize){
		float tfSize = (float)fileSize;
		String[] units = { "B", "KB", "MB", "GB", "TB" };
		int h = 0;
		for (h = 0; tfSize > 1024f; h++) {
			tfSize = (tfSize / 1024f);
		}
		fileSize_Label.setText(this.df.format(tfSize) + units[h]);
	}
	public static long getFolderSize(File dir){
		long dirSize = 0;
		ListDir dirTree = new ListDir(dir.getPath());
		for(int i=0;i<dirTree.fileList.length;i++){
			if (!dirTree.fileList[i].isHidden()) {
				if(dirTree.fileList[i].isFile()){
					dirSize = dirSize+dirTree.fileList[i].length();
				} else if (dirTree.fileList[i].isDirectory()) {
					dirSize = dirSize+getFolderSize(dirTree.fileList[i]);
				}
			}
		}
		return dirSize;
	}
}
