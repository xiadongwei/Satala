package view.NetTransferSetup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import extend.Configuration;
import extend.JIpAddressField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import thread.NetSender.SendUDPThread;

import main.NetTransfer;

public class SetupFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField runCmd_Text;
	private JTextField niceName_Text;
	private JList list;
	private JTabbedPane tabbedPane;
	private JPanel panel_1;
	private JLabel niceNameLabel;
	private JCheckBox confirmCheckBox;
	private JCheckBox envelopCheckBox;
	private JCheckBox replyCheckBox;
	private JCheckBox beepCheckBox;
	private JCheckBox onTopCheckBox;
	private JLabel runCommandLabel;
	private JButton okButton;
	private JButton applyButton;
	private JPanel panel;
	private JIpAddressField ipAddressField;
	private JButton addButton;
	private JLabel ipAddressLabel;
	private File setupfile;
	private BufferedReader setupbr = null;  
	private PrintWriter setuppw = null;
	private JPanel hostSetup_Pane;
	private JPanel configuration_Pane;
	private JPanel receiverSetup_Pane;
	private JPopupMenu popupMenu;
	private JMenuItem deleteMenuItem;
	/**
	 * Launch the application
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			SetupFrame frame = new SetupFrame();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame
	 */
	public SetupFrame() {
		super();
		setTitle("NetTransfer Setup");
		setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/view/Picture/LOGO.png")));
		setResizable(false);
		setBounds(100, 100, 361, 342);
		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				
				dispose();
			}
		});
		tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		panel_1 = new JPanel();
		panel_1.setLayout(null);
		tabbedPane.addTab("Setup", null, panel_1, null);

		okButton = new JButton();
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSetup();
				dispose();
			}
		});
		okButton.setText("OK");
		okButton.setBounds(176, 256, 69, 25);
		panel_1.add(okButton);

		applyButton = new JButton();
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveSetup();
			}
		});
		applyButton.setText("Apply");
		applyButton.setBounds(251, 256, 69, 25);
		panel_1.add(applyButton);

		hostSetup_Pane = new JPanel();
		hostSetup_Pane.setLayout(null);
		hostSetup_Pane.setBorder(new TitledBorder(null, "Host Setup", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		hostSetup_Pane.setBounds(10, 10, 330, 66);
		panel_1.add(hostSetup_Pane);

		niceNameLabel = new JLabel();
		niceNameLabel.setBounds(25, 30, 96, 15);
		hostSetup_Pane.add(niceNameLabel);
		niceNameLabel.setText("Nice Name:");

		niceName_Text = new JTextField();
		niceName_Text.setBounds(127, 25, 180, 21);
		hostSetup_Pane.add(niceName_Text);

		configuration_Pane = new JPanel();
		configuration_Pane.setLayout(null);
		configuration_Pane.setBorder(new TitledBorder(null, "Configuration", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		configuration_Pane.setBounds(10, 75, 330, 99);
		panel_1.add(configuration_Pane);

		confirmCheckBox = new JCheckBox();
		confirmCheckBox.setBounds(125, 25, 88, 30);
		configuration_Pane.add(confirmCheckBox);
		confirmCheckBox.setText("Confirm");

		envelopCheckBox = new JCheckBox();
		envelopCheckBox.setBounds(23, 25, 82, 30);
		configuration_Pane.add(envelopCheckBox);
		envelopCheckBox.setText("Envelop");

		replyCheckBox = new JCheckBox();
		replyCheckBox.setBounds(220, 25, 82, 30);
		configuration_Pane.add(replyCheckBox);
		replyCheckBox.setText("Reply");

		beepCheckBox = new JCheckBox();
		beepCheckBox.setBounds(23, 61, 88, 30);
		configuration_Pane.add(beepCheckBox);
		beepCheckBox.setText("Beep");

		onTopCheckBox = new JCheckBox();
		onTopCheckBox.setBounds(125, 61, 120, 30);
		configuration_Pane.add(onTopCheckBox);
		onTopCheckBox.setText("On Top");

		receiverSetup_Pane = new JPanel();
		receiverSetup_Pane.setLayout(null);
		receiverSetup_Pane.setBorder(new TitledBorder(null, "Receiver Setup", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
		receiverSetup_Pane.setBounds(10, 175, 330, 73);
		panel_1.add(receiverSetup_Pane);

		runCommandLabel = new JLabel();
		runCommandLabel.setBounds(25, 31, 99, 15);
		receiverSetup_Pane.add(runCommandLabel);
		runCommandLabel.setText("Run Command:");

		runCmd_Text = new JTextField();
		runCmd_Text.setBounds(130, 30, 174, 21);
		receiverSetup_Pane.add(runCmd_Text);

		panel = new JPanel();
		panel.setLayout(null);
		tabbedPane.addTab("BroadCast Setup", null, panel, null);

		ipAddressField = new JIpAddressField();
		ipAddressField.setBounds(87, 10, 184, 25);
		panel.add(ipAddressField);

		list = new JList();
		list.setModel(new DefaultListModel());
		list.setBorder(new LineBorder(Color.black, 1, false));
		list.setBounds(10, 41, 332, 240);
		panel.add(list);

		popupMenu = new JPopupMenu();
		addPopup(list, popupMenu);

		deleteMenuItem = new JMenuItem();
		deleteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				int [] remove = list.getSelectedIndices();
				for (int i =0 ; i<remove.length; i++){
					((DefaultListModel)list.getModel()).remove(i);
				}
				Enumeration<?> em  = ((DefaultListModel) list.getModel()).elements();
				List<String> l = new ArrayList<String>();
				while (em.hasMoreElements()){
					l.add((String) em.nextElement());
				}
				writeFile(l);
			}
		});
		deleteMenuItem.setText("Remove");
		popupMenu.add(deleteMenuItem);

		addButton = new JButton();
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent arg0) {
				add(ipAddressField.getIpAddress());
			}
		});
		addButton.setText("Add");
		addButton.setBounds(277, 10, 65, 25);
		panel.add(addButton);

		ipAddressLabel = new JLabel();
		ipAddressLabel.setText("IP Address:");
		ipAddressLabel.setBounds(10, 15, 71, 15);
		panel.add(ipAddressLabel);
		setLocation();
		
		Configuration rc = new Configuration(NetTransfer.SetupFile);
	    niceName_Text.setText(rc.getValue("HostName"));
	    runCmd_Text.setText(rc.getValue("Command"));
	    if (rc.getValue("Envelop").toUpperCase().equals("FALSE")) envelopCheckBox.setSelected(false);
	    else envelopCheckBox.setSelected(true);
	    if (rc.getValue("Confirm").toUpperCase().equals("TRUE")) confirmCheckBox.setSelected(true);
	    else confirmCheckBox.setSelected(false);
	    if (rc.getValue("Reply").toUpperCase().equals("FALSE")) replyCheckBox.setSelected(false);
	    else replyCheckBox.setSelected(true);
	    if (rc.getValue("Beep").toUpperCase().equals("FALSE")) beepCheckBox.setSelected(false);
	    else beepCheckBox.setSelected(true);
	    if (rc.getValue("OnTop").toUpperCase().equals("TRUE")) onTopCheckBox.setSelected(true);
	    else onTopCheckBox.setSelected(false);
	    readFile();
		//
	}
	public void setLocation(){

		int sw = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		int sh = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		int x = sw/2-this.getBounds().width/2;
		
		int y = sh/2-this.getBounds().height/2;
		
		this.setLocation(x, y);
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger())
					showMenu(e);
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	private void saveSetup(){
		Configuration saveCf = new Configuration();
	    if (niceName_Text.getText().equals("")||niceName_Text==null)
			try {
				niceName_Text.setText(new String(InetAddress.getLocalHost().getHostName().getBytes()));
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		if (runCmd_Text.getText().equals("")||runCmd_Text==null) runCmd_Text.setText("cmd /c start \"\"");
		if (envelopCheckBox.isSelected()) saveCf.setValue("Envelop", "true");
		else saveCf.setValue("Envelop", "false");
		if (confirmCheckBox.isSelected()) saveCf.setValue("Confirm", "true");
		else saveCf.setValue("Confirm", "false");
		if (replyCheckBox.isSelected()) saveCf.setValue("Reply", "true");
		else saveCf.setValue("Reply", "false");
		if (beepCheckBox.isSelected()) saveCf.setValue("Beep", "true");
		else saveCf.setValue("Beep", "false");
		if (onTopCheckBox.isSelected()) saveCf.setValue("OnTop", "true");
		else saveCf.setValue("OnTop", "false");
		saveCf.setValue("HostName", niceName_Text.getText());
	    saveCf.setValue("Command", runCmd_Text.getText());
	    saveCf.saveFile(NetTransfer.SetupFile,"HostName");
	    new SendUDPThread("recreate");
	}
	private void add(String str){
		Enumeration<?> em  = ((DefaultListModel) list.getModel()).elements();
		List<String> l = new ArrayList<String>();
		while (em.hasMoreElements()){
			l.add((String) em.nextElement());
		}
		int i = 0;
		for (String str1 : l){
			if (str1.equals(str)) break;
			i++;
		}
		if (i==l.size()) {
			((DefaultListModel)list.getModel()).addElement(str);
			l.add(str);
			writeFile(l);
		}
	}
	private void writeFile(List<String> l){
		setupfile = new File("./","CastSetup");
		try {
			setuppw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(setupfile)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String str1 : l){
			setuppw.println(str1);
			System.out.println(str1);
		}

		setuppw.close();
	}
	private void readFile(){
		setupfile = new File("./","CastSetup");
		try {
			setupbr = new BufferedReader(new InputStreamReader(new FileInputStream(setupfile)));
			while (true){
				String str = setupbr.readLine();
				if (str==null) break;
				else{
					((DefaultListModel)list.getModel()).addElement(str);
				}
			}
			setupbr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
