package createFrame;

import view.NetTransferSetup.SetupFrame;

public class CreateSetupFrame extends Thread {
	public CreateSetupFrame(){
		super();
	}
	public void run(){
		new SetupFrame().setVisible(true);
	}
	
}
