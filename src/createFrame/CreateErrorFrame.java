package createFrame;

import view.ErrorFrame.ErrorFrame;

public class CreateErrorFrame extends Thread {
	
	private String message;
	
	private String picture;
	
	public void run(){
		
		new ErrorFrame(message,picture).setVisible(true);
	}
	
	public void setMessage(String message){
		
		this.message = message;
	}
	
	public void setErrorPicture(String picture){
		this.picture = picture;
	}
	
}
