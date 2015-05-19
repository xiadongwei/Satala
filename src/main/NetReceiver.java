package main;

import createFrame.CreateErrorFrame;
import thread.NetReceiver.ConnectSession;
import thread.NetReceiver.NumenUDPThread;

public class NetReceiver {

    public static int TCPport = 13000;
    public static int UDPport = 13000;
    public boolean isStarted = true;

    public NetReceiver() {

        ConnectSession cs = new ConnectSession(TCPport);

        if (cs.getConnectPort() != TCPport) {

            isStarted = false;

            CreateErrorFrame thread = new CreateErrorFrame();
            thread.setMessage("TCP port " + TCPport + " in used");
            thread.start();

        } else {

            new NumenUDPThread();

            new ConnectSession(TCPport).start();
        }
    }

    public static void main(String[] args) {

        new NetReceiver();
    }
}
