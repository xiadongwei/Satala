package thread.NetReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import view.NetReceiver.NFR_FileHandleItem;
import createFrame.CreateErrorFrame;
import createFrame.CreateReceiverFrame;
import javax.swing.SwingUtilities;

public class NetReceiverThread extends ReceiverThread {

    private CreateReceiverFrame crf = null;
    private NFR_FileHandleItem ntr;
    private long count;
    private long oldCount;
    private long tCount;
    private long fileSize;
    private boolean active = true;
    private File rootDir;
    private long folderSize;
    private long folderCount;
    private FileOutputStream fos;
    private File saveFile;

    public NetReceiverThread(Socket socket) {

        super(socket);
        this.setName("NetReceiverThread");
    }

    public void run() {

//		String hostName = null, ipAddress = null;

        boolean connect = true;

        try {

            Integer NTRInteger = null;

            while (connect) {

                cmdList = splitToken(readUTF());

                if (equParam(0, "OpenFrame")) {

                    System.out.println("Receive OpenFrame - NetReceiverThread");

                    if (equParam(1, "v1.3")) {

                        List<String> packageList = this.cmdList.subList(2, cmdList.size());

                        crf = new CreateReceiverFrame(socket, packageList);

                        crf.start();

                        System.out.println("CreateReceiverFrame(socket) - NetReceiverThread");

                        connect = false;
                    }
                } else if (equParam(0, "PutFile")) {
                    //PutFile|<filePath>|<NTRHandle>|<fileSize>|

                    String filePath = getParam(1);

                    NTRInteger = new Integer(getParam(2));

                    ntr = ConnectSession.hashNTR.get(NTRInteger);

                    long fileSize = Long.valueOf(getParam(3)).longValue();

                    saveFile = new File(filePath);

                    ReceiveFile(saveFile, fileSize);

                    if (active) {
                        ConnectSession.hashNTR.remove(NTRInteger);
                    }

                    connect = false;

                } else if (equParam(0, "PutFolderFile")) {
                    // PutFolderFile|<SubFolder>|<FileSize>|

                    saveFile = new File(rootDir, ((String) cmdList.get(1)));

                    long fileSize = Long.valueOf((String) cmdList.get(2)).longValue();

                    ReceiveFile1(saveFile, fileSize);

                } else if (equParam(0, "PutFolder")) {
                    // PutFolder|<filePath>|<NTRHandle>|

                    String filePath = getParam(1);

                    NTRInteger = new Integer(getParam(2));

                    ntr = ConnectSession.hashNTR.get(NTRInteger);

                    rootDir = new File(filePath);

                } else if (equParam(0, "CreateFolder")) {
                    // CreateFolder|<SubFolder>|

                    rootDir = new File(rootDir, ((String) cmdList.get(1)));

                    rootDir.mkdir();

                } else if (equParam(0, "GoUpFolder")) {
                    // GoUpFolder|

                    rootDir = rootDir.getParentFile();

                } else if (equParam(0, "FolderInfo")) {
                    // FolderInfo|NTRHandle

                    ntr = ConnectSession.hashNTR.get(new Integer(getParam(1)));

                    folderSize = ntr.getFileSize();

                    folderCount = folderSize / 0xffff;

                    tCount = oldCount = folderCount;

                } else if (equParam(0, "End")) {
                    // End|

                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {

                            if (active) {

                                NetReceiverThread.this.ntr.setProcess(100);

                                NetReceiverThread.this.ntr.setLinkText("open");
                            }

                            NetReceiverThread.this.ntr.setSpeed("");


                        }
                    });

                    if (active) {
                        ConnectSession.hashNTR.remove(NTRInteger);
                    }

                    connect = false;
                }
            }
        } catch (IOException e) {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e1) {

                e1.printStackTrace();
            }
            if (saveFile != null) {
                saveFile.delete();
            }
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    NetReceiverThread.this.ntr.setSpeed("");
                }
            });
        } finally {

            free();
        }

    }
    /*
     * ���������ж�ȡ��ݣ���ȡĿ¼�е��ļ���
     * saveFile����ȡ��ݺ�д����ļ���
     * size���ļ���С��
     */

    private void ReceiveFile1(File saveFile, long size) {

        this.fileSize = size;

        TimerTask speedTimerTask = new TimerTask() {

            public void run() {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        NetReceiverThread.this.ntr.setTransfered((oldCount - folderCount) * 0xffff);

                        NetReceiverThread.this.ntr.setSpeed((tCount - folderCount) * 0xffff / 1024 / 1024 + " MB/s  ");
                        tCount = folderCount;
                    }
                });
            }
        };
        Timer speedTimer = new Timer(true);
        speedTimer.schedule(speedTimerTask, 1000, 1000);

        try {

            fos = new FileOutputStream(saveFile);

        } catch (FileNotFoundException e) {

            CreateErrorFrame thread = new CreateErrorFrame();
            thread.setMessage("Can not open file");
            thread.start();

            e.printStackTrace();
        }

        byte[] buffer = new byte[0xffff];

        count = size / 0xffff;

        while ((count) > 0 && active) {

            try {

                inByte.readFully(buffer);

            } catch (IOException e1) {

                active = false;

                e1.printStackTrace();
            }

            try {

                fos.write(buffer);

            } catch (IOException e) {

                active = false;

                CreateErrorFrame thread = new CreateErrorFrame();
                thread.setMessage("Please check free space");
                thread.start();
            }

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    NetReceiverThread.this.ntr.setProcess((int) (100 - folderCount * 100 / oldCount));
                }
            });
            folderCount--;
            count--;
            yield();
        }
        speedTimer.cancel();
        if (active) {
            try {
                inByte.readFully(buffer, 0, (int) (size % 0xffff));

            } catch (IOException e2) {

                e2.printStackTrace();
            }

            try {

                fos.write(buffer, 0, (int) (size % 0xffff));

            } catch (IOException e1) {

                CreateErrorFrame thread = new CreateErrorFrame();
                thread.setMessage("Please check free space");
                thread.start();
            }
        }
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                NetReceiverThread.this.ntr.setTransfered((oldCount - folderCount) * 0xffff + fileSize % 0xffff);
            }
        });

        try {

            fos.close();

        } catch (IOException e) {
            CreateErrorFrame thread = new CreateErrorFrame();
            thread.setMessage("Close file failed");
            thread.start();
        }

        buffer = null;

        yield();
    }
    /*
     * ���������ж�ȡ��ݣ���ȡ�������ļ���
     * saveFile����ȡ��ݺ�д����ļ���
     * size���ļ���С��
     */

    private void ReceiveFile(File saveFile, long size) {

        this.fileSize = size;

        TimerTask speedTimerTask = new TimerTask() {

            public void run() {

                SwingUtilities.invokeLater(new Runnable() {

                    public void run() {

                        NetReceiverThread.this.ntr.setTransfered((oldCount - count) * 0xffff);

                        NetReceiverThread.this.ntr.setSpeed((tCount - count) * 0xffff / 1024 / 1024 + " MB/s  ");
                        tCount = count;
                    }
                });
            }
        };
        Timer speedTimer = new Timer(true);
        speedTimer.schedule(speedTimerTask, 1000, 1000);

        try {

            fos = new FileOutputStream(saveFile);

        } catch (FileNotFoundException e) {

            CreateErrorFrame thread = new CreateErrorFrame();
            thread.setMessage("Can not open file");
            thread.start();

            e.printStackTrace();
        }

        byte[] buffer = new byte[0xffff];

        tCount = oldCount = count = size / 0xffff;

        while ((count) > 0 && active) {
            try {

                inByte.readFully(buffer);

            } catch (IOException e1) {

                active = false;

                e1.printStackTrace();
            }

            try {

                fos.write(buffer);

            } catch (IOException e) {
                active = false;

                CreateErrorFrame thread = new CreateErrorFrame();
                thread.setMessage("Please check free space");
                thread.start();
            }

            SwingUtilities.invokeLater(new Runnable() {

                public void run() {

                    NetReceiverThread.this.ntr.setProcess((int) (100 - count * 100 / oldCount));
                }
            });

            count--;

            yield();
        }

        speedTimer.cancel();

        if (active) {
            try {

                inByte.readFully(buffer, 0, (int) (size % 0xffff));

            } catch (IOException e2) {

                e2.printStackTrace();
            }

            try {

                fos.write(buffer, 0, (int) (size % 0xffff));

            } catch (IOException e1) {

                CreateErrorFrame thread = new CreateErrorFrame();
                thread.setMessage("Please check free space");
                thread.start();
            }
        }
        try {

            fos.close();


        } catch (IOException e) {

            CreateErrorFrame thread = new CreateErrorFrame();
            thread.setMessage("Close file failed");
            thread.start();
        }

        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    //Tonyxia
                    NetReceiverThread.this.ntr.setSpeed("");
			    System.out.println(oldCount);
			    if (oldCount <= 0) { //����ļ�С��0xffff
                        NetReceiverThread.this.ntr.setProcess(100);
                        NetReceiverThread.this.ntr.setTransfered(fileSize % 0xffff);
                    } else {
                        if (active) {
                            NetReceiverThread.this.ntr.setProcess(100);
                        }
                        NetReceiverThread.this.ntr.setTransfered((oldCount - count) * 0xffff + fileSize % 0xffff);

                    }
                    if (active) {
                        NetReceiverThread.this.ntr.setLinkText("open");
                    }
                }
            });
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        buffer = null;

        yield();
    }
}
