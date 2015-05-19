package thread.NetSender;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Hashtable;
import main.NetTransfer;
import view.NetSender.NFS_FileHandleItem;
import createFrame.CreateErrorFrame;
import extend.Configuration;
import java.util.Enumeration;
import thread.NetSender.NetSenderThread;

public class NetSenderDaemon extends SenderThread {

    private String message;
    private String hostName;
    private String sendPackage = null;
    private Hashtable<Integer, File> selectedFiles;
    private Hashtable<Integer, NetSenderThread> threadGroup = new Hashtable<Integer, NetSenderThread>();

    public NetSenderDaemon(String destAddress, String hostName, int port, String message,
            Hashtable<Integer, File> selectedFiles) {

        super(destAddress, port);

        this.message = message;

        this.hostName = hostName;

        this.selectedFiles = selectedFiles;
        
        this.setName("NetSenderDaemon-"+hostName);
    }

    public void run() {
        try {
            Configuration rc = new Configuration(NetTransfer.SetupFile);
            
            String envelop = rc.getValue("Envelop");

            String confirm = rc.getValue("Confirm");

            if (envelop.toUpperCase().equals("FALSE")) {
                envelop = "F";
            } else {
                envelop = "T";
            }

            if (confirm.toUpperCase().equals("TRUE")) {
                confirm = "T";
            } else {
                confirm = "F";
            }

            sendPackage = "OpenFrame|v1.3|" + envelop + "|" + confirm + "|" + this.hostName + "|" + this.message + "|";

            for (File f : selectedFiles.values()) {
                if (f.isDirectory()) {

                    sendPackage = sendPackage + "AddFolder|" + f.hashCode() + "|"
                            + f.getName() + "|"
                            + NFS_FileHandleItem.getFolderSize(f) + "|";
                } else {

                    sendPackage = sendPackage + "AddFile|" + f.hashCode() + "|"
                            + f.getName() + "|" + f.length() + "|";
                }
            }

            writeUTF(sendPackage);

            boolean connect = true;

            while (connect) {

                cmdList = splitToken(readUTF());

                if (equParam(0, "ConfirmEnd")) {

                    CreateErrorFrame thread = new CreateErrorFrame();
                    String ip = this.socket.getInetAddress().getHostAddress();
                    thread.setMessage(ip + " \nAlready received.");
                    thread.setErrorPicture(view.ErrorFrame.ErrorFrame.ALERM);
                    thread.start();

                } else if (equParam(0, "ReqFile")) {

                    int port = Integer.valueOf(getParam(4)).intValue();

                    int hashFile = Integer.valueOf(getParam(1)).intValue();

                    String filePath = getParam(2);

                    int NTRHandle = Integer.valueOf(getParam(3)).intValue();

                    NetSenderThread senderSession = new NetSenderThread(destAddress, port,
                            hashFile, filePath, NTRHandle, selectedFiles,
                            "PutFile");
                    senderSession.setActive(true);

                    senderSession.start();

                    threadGroup.put(NTRHandle, senderSession);

                } else if (equParam(0, "ReqDir")) {

                    int port = Integer.valueOf(getParam(4)).intValue();

                    int hashFile = Integer.valueOf(getParam(1)).intValue();

                    String filePath = getParam(2);

                    int NTRHandle = Integer.valueOf(getParam(3)).intValue();

                    NetSenderThread senderSession = new NetSenderThread(destAddress, port,
                            hashFile, filePath, NTRHandle, selectedFiles,
                            "PutFolder");
                    senderSession.setActive(true);

                    senderSession.start();

                    threadGroup.put(NTRHandle, senderSession);

                } else if (equParam(0, "Cancel")) {

                    int NTRHandle = Integer.valueOf(getParam(1)).intValue();

                    NetSenderThread senderSession = threadGroup.get(NTRHandle);

                    senderSession.setActive(false);
                    
                } else if (equParam(0, "Disconnect")) {
                    Enumeration ert=threadGroup.elements();
                    while(ert.hasMoreElements()){
                         NetSenderThread senderSession =(NetSenderThread)ert.nextElement();
                         senderSession.setActive(false);
                    }
                    
                    
                    System.out.println("NetSenderDaemon is close");
                    
                    connect = false;

                    closeSocket();
                    //System.exit(0);
                }
            }

        } catch (UnknownHostException e) {

            CreateErrorFrame thread = new CreateErrorFrame();
            thread.setMessage("The host & address incorrect");
            thread.start();

            e.printStackTrace();

        } catch (IOException e) {

            try {

                closeSocket();

            } catch (IOException e1) {

                CreateErrorFrame thread = new CreateErrorFrame();
                thread.setMessage("Close socket failed");
                thread.start();

                e1.printStackTrace();

            }

            CreateErrorFrame thread = new CreateErrorFrame();
            thread.setMessage("Lose host connection");
            thread.start();

            e.printStackTrace();
        }
    }
}
