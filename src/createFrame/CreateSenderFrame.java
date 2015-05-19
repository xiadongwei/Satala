package createFrame;

import view.NetSender.NetSenderFrame;
import main.NetTransfer;
import extend.Configuration;

public class CreateSenderFrame extends Thread {

    private String address = "";
    private String message = "";
    private int x = 65535;
    private int y = 65535;

    public CreateSenderFrame() {
        super();
    }

    public CreateSenderFrame(String address) {
        this();
        this.address = address;
    }

    public CreateSenderFrame(String address, int x, int y, String message) {
        this();
        this.address = address;
        this.x = x;
        this.y = y;
        this.message = message;
    }

    public void run() {

        Configuration rc = new Configuration(NetTransfer.SetupFile);

        String onTop = rc.getValue("OnTop");

        String reply = rc.getValue("Reply");

        NetSenderFrame c = new NetSenderFrame();

        if (!onTop.toUpperCase().equals("FALSE")) {
            c.setAlwaysOnTop(true);
        }

        if (!reply.toUpperCase().equals("FALSE") && !message.equals(" ") && !message.equals("")) {
            c.setMessage(message);
        }

        c.setTitle("NetSender");

        c.setComboAddress(address);

        if ((x != 65535) && (y != 65535)) {
            c.setLocation(x, y);
        }

        c.setVisible(true);
    }
}
