package view.ErrorFrame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class ErrorFrame extends JDialog {

	final static public String ERROR = "ERROR";  
	
	final static public String ALERM = "ALERM"; 
	
	private static final long serialVersionUID = 1L;

	private JLabel msgLabel;
	
	private JButton closeButton;
	
	private JLabel alermLabel;
	
	public ErrorFrame() {
		
		super();
		
		initialize();
		
	}
	
	public ErrorFrame(String message, String picture) {
		
		super();
		
		initialize();
		
		setLocation();
		
		setMessage(message);
		
		setErrorPicture(picture);
	}
	
	private void initialize(){
		
		getContentPane().setLayout(null);
		
		setResizable(false);
		
		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				
				dispose();
			}
		});

		setBounds(100, 100, 337, 105);
		
		getContentPane().add(alermLabel());
		
		getContentPane().add(msgLabel());
		
		getContentPane().add(getCloseButton());
		
	}
//initialize
	protected JButton getCloseButton() {
	
		if (closeButton == null) {
		
			closeButton = new JButton();
			
			closeButton.addActionListener(new ActionListener() {
			
				public void actionPerformed(final ActionEvent arg0) {

					setVisible(false);
					
					dispose();
				}
			});
			closeButton.setText("Close");

			closeButton.setBounds(241, 47, 80, 26);
		}
		return closeButton;
	}
	
	protected JLabel msgLabel() {
		
		if (msgLabel == null) {
		
			msgLabel = new JLabel();
			
			msgLabel.setBounds(62, 23, 260, 18);
		}
		return msgLabel;
	}
	
	protected JLabel alermLabel() {
		
		if (alermLabel == null) {
		
			alermLabel = new JLabel();
			
			alermLabel.setBounds(10, 10, 46, 45);
			
		}
		return alermLabel;
	}
// method
	public void setMessage(String msg){
		
		msgLabel.setText(msg);
	}
	
	public void setLocation(){

		int sw = Toolkit.getDefaultToolkit().getScreenSize().width;
		
		int sh = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		int x = sw-this.getBounds().width;
		
		int y = sh-this.getBounds().height-30;
		
		this.setLocation(x, y);
	}
	
	public void setErrorPicture(String picture){
		if (picture==null||picture.equals("")){
			if (alermLabel!=null){
				alermLabel.setIcon(new ImageIcon(getClass().getResource("/view/Picture/Error.png")));
			}
		}else if (picture.equals(ALERM)){
			if (alermLabel!=null){
				alermLabel.setIcon(new ImageIcon(getClass().getResource("/view/Picture/Alerm.png")));
			}
		}else if (picture.equals(ERROR)){
			if (alermLabel!=null){
				alermLabel.setIcon(new ImageIcon(getClass().getResource("/view/Picture/Error.png")));
			}
		}
	}
}
