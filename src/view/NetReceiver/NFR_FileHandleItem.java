package view.NetReceiver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import main.NetTransfer;
import extend.Configuration;

public class NFR_FileHandleItem extends JPanel {

    private JLabel fileLogo_Label;
    private ControlButton link;
    private JPanel panel;
    private JProgressBar progressBar;
    private JPanel panel_1;
    private JLabel fileName_Label;
    private JLabel fileSize_Label;
    private JPanel panel_2;
    private JLabel transferSpeed_Label;
    private String type;
    private int port;
    private String fileLogo = "/view/Picture/file.png";
    private String folderLogo = "/view/Picture/folder.png";
    private int hashFile;
    private long fileSize;
    private String fileName;
    private DecimalFormat df = new DecimalFormat("#.00");
    private DataOutputStream outByte = null;
    private String filePath = "";
    private JFileChooser jfc = new JFileChooser();
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the panel
     */
    public NFR_FileHandleItem() {
        super();
        initialize();
    }

    public NFR_FileHandleItem(int hashFile, String filename, long filesize, String type, Socket socket) {
        try {
            outByte = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initialize();
        setFileName(filename);
        setFileSize(filesize);
        setHashFile(hashFile);
        setType(type);
    }

    private void initialize() {
        setPreferredSize(new Dimension(298, 30));
        setMinimumSize(new Dimension(298, 30));
        setMaximumSize(new Dimension(65535, 30));
        setLayout(new BorderLayout());

        fileLogo_Label = new JLabel();
        fileLogo_Label.setHorizontalTextPosition(SwingConstants.CENTER);
        fileLogo_Label.setHorizontalAlignment(SwingConstants.CENTER);
        fileLogo_Label.setPreferredSize(new Dimension(30, 30));
        add(fileLogo_Label, BorderLayout.WEST);

        link = new ControlButton();
        link.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent arg0) {
                if (link.getControlMode().equals("save")) {
                    int yes = 0;
                    if (type.equals("FILE")) {
                        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        jfc.setMultiSelectionEnabled(false);
                        jfc.setSelectedFile(new File(fileName));

                        if (jfc.showSaveDialog(NFR_FileHandleItem.this) == JFileChooser.APPROVE_OPTION) {
                            if (jfc.getSelectedFile().exists()) {
                                yes = JOptionPane.showConfirmDialog(NFR_FileHandleItem.this, "This file is existing, overwrite? (Y/N)",
                                        "Alerm Dialog", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(getClass().getResource("/view/Picture/Alerm.png")));

                            }
                            if (yes == 0) {
                                filePath = jfc.getSelectedFile().getPath();
                                try {
                                    outByte.writeUTF("ReqFile|" + hashFile + "|" + filePath + "|" + NFR_FileHandleItem.this.hashCode() + "|" + port + "|");
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                link.setControlMode("cancel");
                            }
                        }

                    } else if (type.equals("FOLDER")) {
                        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        jfc.setMultiSelectionEnabled(false);
                        jfc.setSelectedFile(new File(fileName));
                        if (jfc.showSaveDialog(NFR_FileHandleItem.this) == JFileChooser.APPROVE_OPTION) {
                            filePath = jfc.getSelectedFile().getPath();
                            try {
					    outByte.writeUTF("ReqDir|" + hashFile + "|" + filePath + "|" + NFR_FileHandleItem.this.hashCode() + "|" + port + "|");
				    } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            link.setControlMode("cancel");
                        }
                    }
                } else if (link.getControlMode().equals("cancel")) {
                    try {
                        outByte.writeUTF("Cancel|" + NFR_FileHandleItem.this.hashCode() + "|");
                        link.setControlMode("save");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else if (link.getControlMode().equals("open")) {
                    Configuration rc = new Configuration(NetTransfer.SetupFile);
                    String cmd = rc.getValue("Command");
                    if (cmd.equals("")) {
                        cmd = "cmd /c start \"\"";
                    }
                    if (!NFR_FileHandleItem.this.getType().equals("FOLDER")) {
                        try {

                            Runtime.getRuntime().exec(cmd + " \"" + NFR_FileHandleItem.this.filePath + "\"");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        try {
                            StringBuffer filePath = new StringBuffer(
                                    NFR_FileHandleItem.this.filePath);
                            if (filePath.charAt(filePath.length() - 1) == '\\') {
                                Runtime.getRuntime().exec(
                                        cmd + " \"" + NFR_FileHandleItem.this.filePath + NFR_FileHandleItem.this.fileName + "\"");
                            } else {
                                Runtime.getRuntime().exec(
                                        cmd + " \"" + NFR_FileHandleItem.this.filePath + "\\" + NFR_FileHandleItem.this.fileName + "\"");
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
        link.setHorizontalTextPosition(SwingConstants.CENTER);
        link.setHorizontalAlignment(SwingConstants.CENTER);
        link.setPreferredSize(new Dimension(30, 30));
        link.setControlMode("save");

        add(link, BorderLayout.EAST);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(0, 12));
        panel.add(progressBar, BorderLayout.SOUTH);

        panel_1 = new JPanel();
        panel_1.setLayout(new BorderLayout());
        panel.add(panel_1, BorderLayout.CENTER);

        fileSize_Label = new JLabel();
        panel_1.add(fileSize_Label, BorderLayout.EAST);

        panel_2 = new JPanel();
        panel_2.setLayout(new BorderLayout());
        panel_1.add(panel_2, BorderLayout.CENTER);

        fileName_Label = new JLabel();
        panel_2.add(fileName_Label);

        transferSpeed_Label = new JLabel();
        panel_2.add(transferSpeed_Label, BorderLayout.EAST);
    }

    public void setConnectPort(int port) {
        this.port = port;
    }

    public void setType(String type) {
        this.type = type;
        if (type.equals("FILE")) {
            fileLogo_Label.setIcon(new ImageIcon(getClass().getResource(fileLogo)));
        } else if (type.equals("FOLDER")) {
            fileLogo_Label.setIcon(new ImageIcon(getClass().getResource(folderLogo)));
        }
    }

    public String getType() {
        return this.type;
    }

    public void setProcess(int selection) {
        progressBar.setToolTipText(String.valueOf(selection) + "%");
        progressBar.setValue(selection);

    }

    public void setMaxProcess(int max) {
        progressBar.setMaximum(max);
    }

    public int getProcess() {
        return progressBar.getValue();
    }

    public void setHashFile(int hashFile) {
        this.hashFile = hashFile;
    }

    public int getHashFile() {
        return this.hashFile;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        fileName_Label.setText(NFR_FileHandleItem.this.fileName);
        fileName_Label.setToolTipText(NFR_FileHandleItem.this.fileName);
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
        float tfSize = (float) fileSize;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int h = 0;
        for (h = 0; tfSize > 1024f; h++) {
            tfSize = (tfSize / 1024f);
        }
        fileSize_Label.setText(this.df.format(tfSize) + units[h]);
        fileSize_Label.setToolTipText(this.df.format(tfSize) + units[h]);
    }

    public void setSpeed(String speed) {
        transferSpeed_Label.setText(speed);
    }

    public void setTransfered(long fileSize) {
        float tfSize = (float) fileSize;
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int h = 0;
        for (h = 0; tfSize > 1024f; h++) {
            tfSize = (tfSize / 1024f);
        }
        fileSize_Label.setText(this.df.format(tfSize) + units[h]);
    }

    public long getFileSize() {
        return (long) this.fileSize;
    }

    public void setLinkText(String text) {
        this.link.setControlMode(text);
    }

    public String toString() {
        return "HashFile=" + getHashFile() + " FileName=" + getFileName() + " FileSize=" + getFileSize();
    }
}
