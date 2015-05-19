package view.NetSender;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import thread.NetSender.GetOnlineListThread;
import thread.NetSender.NetSenderDaemon;
import main.NetTransfer;
import extend.Configuration;
import extend.IPAndHostPackage;
import extend.SortIPAddress;

public class NetSenderFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
	private JScrollPane textScrollPane;
	private JTextPane textPane;
	private JPanel panel;
	private JPanel functionPane;
	private JButton refButton;
	private JButton sendButton;
	private JLabel netsenderLabel;
	private JComboBox comboBox;
	private JScrollPane NFS_scrollPane;
	private JPanel NFS_backPanel;
	private JFileChooser jfc;
	private JPopupMenu popupMenu;
	private JMenuItem addFiles;
	private JMenuItem addFolder;
	private JMenuItem removeAll;
	@SuppressWarnings("unused")
	private DropTarget target;
	final private Hashtable<Integer,File> selectedFiles = new Hashtable<Integer,File>();//����File ����б�

	/**
	 * Create the frame
	 */
	public NetSenderFrame() {
		super();
		setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/view/Picture/LOGO.png")));
		setResizable(true);
		
		setBounds(100, 100, 327, 287);
                setLocation();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		splitPane = new JSplitPane();
		splitPane.setDividerSize(3);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		getContentPane().add(splitPane, BorderLayout.CENTER);

		textScrollPane = new JScrollPane();
		textScrollPane.setPreferredSize(new Dimension(0, 122));
		splitPane.setLeftComponent(textScrollPane);

		textPane = new JTextPane();
		textScrollPane.setViewportView(textPane);

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		splitPane.setRightComponent(panel);

		functionPane = new JPanel();
		functionPane.setLayout(null);
		functionPane.setPreferredSize(new Dimension(0, 24));
		panel.add(functionPane, BorderLayout.NORTH);

		comboBox = new JComboBox();
		comboBox.setBounds(0, 0, 200, 24);
		comboBox.setMaximumSize(new Dimension(230, 24));
		comboBox.addItem("");
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(final ItemEvent arg0) {
				comboBox.getEditor().getEditorComponent().setBackground(Color.WHITE);
			}
		});
		comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyPressed(final KeyEvent arg0) {
				comboBox.getEditor().getEditorComponent().setBackground(Color.WHITE);
			}
		});
		comboBox.setEditable(true);
		functionPane.add(comboBox);
		
		refButton = new JButton();
		refButton.setBounds(200, 0, 30, 24);
		refButton.setMnemonic('r');
		refButton.setMaximumSize(new Dimension(30, 24));
		refButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
//				new GetOnlineListThread("recreate");
				new GetOnlineListThread(NetSenderFrame.this);
			}
		});
		refButton.setPreferredSize(new Dimension(30, 24));
		refButton.setIcon(new ImageIcon(getClass().getResource("/view/Picture/refresh.png")));
		functionPane.add(refButton);

		sendButton = new JButton();
		sendButton.setBounds(230, 0, 89, 24);
		sendButton.setMaximumSize(new Dimension(70, 24));
		sendButton.setMnemonic('s');
		sendButton.setPreferredSize(new Dimension(95, 24));
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (((String) comboBox.getSelectedItem())==null||((String) comboBox.getSelectedItem()).equals("")){
					comboBox.requestFocus();
					comboBox.getEditor().getEditorComponent().setBackground(Color.PINK);
//					comboBox.paintAll(comboBox.getGraphics());
				}else{
					NetSenderDaemon nsd = null;
					String IPAddress = "";
					String hostName = "";
					IPAddress = NetSenderFrame.splitToken((String)comboBox.getSelectedItem()).get(0);
					try {
						Configuration rc = new Configuration(NetTransfer.SetupFile);
					    
					    hostName = rc.getValue("HostName");
						
					    if (hostName.equals("")) hostName = new String(InetAddress.getLocalHost().getHostName().getBytes());
					    
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} 
					setVisible(false);
					if (textPane.getText()==null || textPane.getText().equals("")) nsd = new NetSenderDaemon(IPAddress,hostName,main.NetReceiver.TCPport," ",selectedFiles);
					
					else nsd = new NetSenderDaemon(IPAddress,hostName,main.NetReceiver.TCPport,textPane.getText(),selectedFiles);
				
					if (nsd.isConnected()){
						
						nsd.start();
						
						dispose();
						
					}else{
				
						setVisible(true);
					}
					
				}
			}
		});
		sendButton.setIcon(new ImageIcon(getClass().getResource("/view/Picture/send.png")));
		sendButton.setText("Send");
		functionPane.add(sendButton);

		netsenderLabel = new JLabel();
		netsenderLabel.setBorder(new TitledBorder(null, "",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		panel.add(netsenderLabel, BorderLayout.SOUTH);

		NFS_scrollPane = new JScrollPane();
		NFS_scrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		NFS_scrollPane.setAutoscrolls(true);
		panel.add(NFS_scrollPane, BorderLayout.CENTER);

		NFS_backPanel = new JPanel();
		NFS_backPanel.setLayout(new BoxLayout(NFS_backPanel,
				BoxLayout.PAGE_AXIS));
		NFS_backPanel.setAutoscrolls(true);
		NFS_scrollPane.setViewportView(NFS_backPanel);
		popupMenu = new JPopupMenu();
		NFS_backPanel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}

			private void showMenu(MouseEvent e) {
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		});

		addFiles = new JMenuItem();
		NetSenderFrame.this.jfc = new JFileChooser();
		addFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setMultiSelectionEnabled(true);
				if (jfc.showOpenDialog(NetSenderFrame.this) == JFileChooser.APPROVE_OPTION) {
					File[] sFiles = jfc.getSelectedFiles();
					for (int i = 0; i < sFiles.length; i++) {
						insertSelectedFiles(sFiles[i]);
					}
				}
				NFS_scrollPane.paintAll(NFS_scrollPane.getGraphics());
				jfc.updateUI();
			}
		});
		addFiles.setIcon(new ImageIcon(getClass().getResource("/view/Picture/file.png")));
		addFiles.setText("Add Files");
		popupMenu.add(addFiles);

		addFolder = new JMenuItem();
		addFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setMultiSelectionEnabled(true);
				if (jfc.showOpenDialog(NetSenderFrame.this) == JFileChooser.APPROVE_OPTION) {
					File[] sFiles = jfc.getSelectedFiles();
					for (int i = 0; i < sFiles.length; i++) {
						insertSelectedFiles(sFiles[i]);
					}
				}
				NFS_scrollPane.paintAll(NFS_scrollPane.getGraphics());
				jfc.updateUI();
			}
		});
		addFolder.setIcon(new ImageIcon(getClass().getResource("/view/Picture/folder.png")));
		addFolder.setText("Add Folder");
		popupMenu.add(addFolder);

		removeAll = new JMenuItem();
		removeAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				NFS_backPanel.removeAll();
				
				NFS_scrollPane.paintAll(NFS_scrollPane.getGraphics());
			}
		});
		removeAll.setIcon(new ImageIcon(getClass().getResource("/view/Picture/trash.png")));
		removeAll.setText("Remove All");
		popupMenu.add(removeAll);

		target = new DropTarget(NFS_scrollPane,
				DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
					@SuppressWarnings("unchecked")
					public void drop(DropTargetDropEvent event) {
						event.acceptDrop(DnDConstants.ACTION_COPY);
						Transferable transferable = event.getTransferable();
						DataFlavor[] flavors = transferable
								.getTransferDataFlavors();
						for (int i = 0; i < flavors.length; i++) {
							DataFlavor d = flavors[i];
							if (d.equals(DataFlavor.javaFileListFlavor)) {
								List<File> fileList;
								try {
									fileList = (List<File>) transferable
										.getTransferData(d);
									for (File sFiles : fileList) {
                                                                            insertSelectedFiles(sFiles);
									}
								} catch (UnsupportedFlavorException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								NFS_scrollPane.paintAll(NFS_scrollPane.getGraphics());
							}
						}	
					}

					public void dropActionChanged(DropTargetDragEvent arg0) {
						// TODO Auto-generated method stub
						
					}
			
		});
		new GetOnlineListThread(NetSenderFrame.this);
	}
        private void insertSelectedFiles(File sFiles){
             if (selectedFiles.get(sFiles.hashCode())==null){
                selectedFiles.put(sFiles.hashCode(), sFiles);
		NFS_FileHandleItem nfh = new NFS_FileHandleItem(NFS_scrollPane,NFS_backPanel,selectedFiles);
		nfh.setFile(sFiles);
                NFS_backPanel.add(nfh);
             }
             
        }
        
	public void setLocation(){
		java.util.Random rx = new java.util.Random();
		
		java.util.Random ry = new java.util.Random();
		
		int sw = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		int sh = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		int x1 = sw/2-this.getBounds().width/2;
		
//		int x2 = sw/2-x1;
		
		int y1 = sh/2-this.getBounds().height/2;
		
//		int y2 = sh/2-y1;
		
		int x = rx.nextInt(this.getBounds().width/2)+x1;
		
		int y = ry.nextInt(this.getBounds().height/2)+y1;
		
		this.setLocation(x, y);
	}
	
	public static List<String> splitToken(String fs) {
		
		StringTokenizer pt = new StringTokenizer(fs, " ");
		
		List<String> l = new ArrayList<String>();
		
		while (pt.hasMoreTokens()) {
		
			l.add(pt.nextToken());
		}
		
		return l;
	}

	public void setComboAddress(String address){
		comboBox.setSelectedItem(address);
	}
	public void setMessage(String message){
		StringBuffer sb= new StringBuffer(">"+message);
		for (int i=0; i<sb.length();i++){
			if (sb.charAt(i)=='\n') {
				sb.replace(i, i+1, "\n>");
				i++;
			}
		}
		textPane.setText(String.valueOf(sb));
		textPane.setSelectionStart(textPane.getText().length());
		textPane.setSelectionEnd(textPane.getText().length());
	}
	public void addComboAddress(String address){
		comboBox.addItem(address);
	}
	public void removeComboAllItems(){
		comboBox.removeAllItems();
	}
	public void sortComboList(){
		IPAndHostPackage IPHostPackage[];
		String[] items = new String[comboBox.getItemCount()];
		for (int i = 0; i< comboBox.getItemCount(); i++){
			items[i] = (String) comboBox.getItemAt(i);
		}
		IPHostPackage = new IPAndHostPackage[items.length];
		for (int i = 0; i<items.length; i++){
			String ipAddress = (String) NetSenderFrame.splitToken(items[i]).get(0);
			String hostName = (String) NetSenderFrame.splitToken(items[i]).get(1);
			IPHostPackage[i] = new IPAndHostPackage(ipAddress,hostName);
		}
		items = new SortIPAddress(IPHostPackage).getSortedIPAddress();
		removeComboAllItems();
		for (int i = 0; i<items.length; i++){
			comboBox.addItem(items[i]);
		}
		comboBox.setSelectedItem("");
	}
	
}
