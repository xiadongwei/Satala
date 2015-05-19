package thread.NetReceiver;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import view.NetReceiver.NFR_FileHandleItem;

public class ConnectSession extends Thread {

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    static public Hashtable<Integer, NFR_FileHandleItem> hashNTR = new Hashtable<Integer, NFR_FileHandleItem>();
    private boolean active = true;
    private boolean run = true;
    private int port;

    public ConnectSession(int port) {

        super();
        this.setName("ConnectSession");
        setPort(port);
        while (active) {
            try {
                serverSocket = new ServerSocket(port);
                active = false;
            } catch (BindException e) {
                active = true;
                setPort(++port);
                System.out.println("useing port " + port);
            } catch (IOException e) {
                active = false;
                e.printStackTrace();
            }
        }
        try {

            serverSocket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public void startSocket(int port) throws IOException {

        setPort(port);

        startSocket();
    }

    public void setPort(int port) {

        this.port = port;
    }

    public int getConnectPort() {

        return this.port;
    }

    public void setStop() throws IOException {

        this.run = false;

        closeSocket();
    }

    private void closeSocket() throws IOException {

        if (serverSocket != null || !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    private void startSocket() throws IOException {

        serverSocket = new ServerSocket(port);
    }

    public void run() {
        try {
            startSocket(port);
            while (run) {
                socket = serverSocket.accept();
                new NetReceiverThread(socket).start();
            }
        } catch (IOException e) {
//			e.printStackTrace();
        } finally {

            free();
        }
    }

    protected void free() {
        serverSocket = null;
        socket = null;
        hashNTR = null;
    }
}
