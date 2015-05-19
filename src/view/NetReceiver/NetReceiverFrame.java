package view.NetReceiver;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import main.NetSender;
import thread.NetReceiver.ConnectSession;
import thread.NetReceiver.ReceiverThread;

public class NetReceiverFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private CardLayout card;
    private JButton confirmButton;
    private JSplitPane splitPane;
    private JScrollPane textScrollPane;
    private JEditorPane textPane;
    private JPanel panel;
    private JPanel functionPane;
    private JLabel IPHostLabel;
    private JPanel funtionGroupPane;
    private JButton replyButton;
    private JButton closeButton;
    private JScrollPane NFR_scrollPane;
    private JPanel NFR_backPanel;
    private JLabel netreceiverLabel;
    private Socket socket = null;
    private List<String> packageList = null;
    private List<Integer> hashNTR = new ArrayList<Integer>();
    private ReceiverThread rThread;
    private int x,  y;

    /**
     * Create the frame
     */
    public NetReceiverFrame() {

        super();

        initialize();
    }

    public NetReceiverFrame(Socket socket, List<String> packageList) {

        super();

        this.socket = socket;

        this.packageList = packageList;

        initialize();
    //
    }

    private void initialize() {
        card = new CardLayout();
        getContentPane().setLayout(card);
        setBounds(100, 100, 233, 142);
//		setBounds(100, 100, 327, 287);
        setLocation();
        setIconImage(Toolkit.getDefaultToolkit().createImage(getClass().getResource("/view/Picture/LOGO.png")));
        this.rThread = new ReceiverThread(socket);
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                try {
                    
                    rThread.writeUTF("Disconnect");
                    System.out.println("writeUTF(Disconnect)");
                    removeHashNTR();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    dispose();
                }
            }
        });
        confirmButton = new JButton();
        confirmButton.setName("button");
        getContentPane().add(confirmButton, confirmButton.getName());
        confirmButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                card.next(getContentPane());
                setBounds(100, 100, 327, 287);
                setLocation(x, y);
                paintAll(getGraphics());
            }
        });
        confirmButton.setText(getParam(2) + " " + socket.getInetAddress().getHostAddress());
        splitPane = new JSplitPane();
        splitPane.setDividerSize(3);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setName("splitPane");
        getContentPane().add(splitPane, splitPane.getName());

        textScrollPane = new JScrollPane();
        textScrollPane.setPreferredSize(new Dimension(0, 122));
        textScrollPane.setMinimumSize(new Dimension(0, 122));
        splitPane.setLeftComponent(textScrollPane);

        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(0, 122));
        textPane.setEditable(false);
        textScrollPane.setViewportView(textPane);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        splitPane.setRightComponent(panel);

        functionPane = new JPanel();
        functionPane.setLayout(new BorderLayout());
        functionPane.setPreferredSize(new Dimension(0, 24));
        panel.add(functionPane, BorderLayout.NORTH);

        IPHostLabel = new JLabel();
        functionPane.add(IPHostLabel, BorderLayout.CENTER);

        funtionGroupPane = new JPanel();
        funtionGroupPane.setLayout(new BorderLayout());
        functionPane.add(funtionGroupPane, BorderLayout.EAST);

        replyButton = new JButton();
        replyButton.setMnemonic('r');
        replyButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                String hostAddr = socket.getInetAddress().getHostAddress();
                new NetSender(hostAddr, getLocation().x, getLocation().y, textPane.getText());
                try {
                    removeHashNTR();
                    rThread.writeUTF("Disconnect");
                    System.out.println("writeUTF(Disconnect)");

                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    dispose();
                }
            }
        });
        replyButton.setPreferredSize(new Dimension(70, 30));
        replyButton.setText("Reply");
        funtionGroupPane.add(replyButton);

        closeButton = new JButton();
        closeButton.setMnemonic('c');
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    removeHashNTR();
                    rThread.writeUTF("Disconnect");
                    System.out.println("writeUTF(Disconnect)");
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    dispose();
                }
            }
        });
        closeButton.setText("Close");
        funtionGroupPane.add(closeButton, BorderLayout.EAST);

        NFR_scrollPane = new JScrollPane();
        NFR_scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(NFR_scrollPane, BorderLayout.CENTER);

        NFR_backPanel = new JPanel();
        NFR_backPanel.setLayout(new BoxLayout(NFR_backPanel, BoxLayout.PAGE_AXIS));
        NFR_scrollPane.setViewportView(NFR_backPanel);

        netreceiverLabel = new JLabel();
        netreceiverLabel.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        panel.add(netreceiverLabel, BorderLayout.SOUTH);

        int i = 2;

        setHostInfo(getParam(i++) + " " + socket.getInetAddress().getHostAddress());

        setMessage(getParam(i++));

        while (i < packageList.size()) {

            if (equParam(i, "AddFile")) {

                i++;

                int hashFile = Integer.valueOf(getParam(i++)).intValue();

                String fileName = getParam(i++);

                long fileSize = Long.valueOf(getParam(i++)).longValue();

                NFR_FileHandleItem fhi = new NFR_FileHandleItem(hashFile, fileName, fileSize, "FILE", socket);

                fhi.setConnectPort(main.NetReceiver.TCPport);

                ConnectSession.hashNTR.put(fhi.hashCode(), fhi);

                hashNTR.add(fhi.hashCode());

                NFR_backPanel.add(fhi);

            } else if (equParam(i, "AddFolder")) {

                i++;

                int hashFile = Integer.valueOf(getParam(i++)).intValue();

                String fileName = getParam(i++);

                long fileSize = Long.valueOf(getParam(i++)).longValue();

                NFR_FileHandleItem fhi = new NFR_FileHandleItem(hashFile, fileName, fileSize, "FOLDER", socket);

                fhi.setConnectPort(main.NetReceiver.TCPport);

                ConnectSession.hashNTR.put(fhi.hashCode(), fhi);

                hashNTR.add(fhi.hashCode());

                NFR_backPanel.add(fhi);
            }
        }
        if (getParam(0).equals("F")) {
            card.next(getContentPane());
            setBounds(100, 100, 327, 287);
            setLocation(x, y);
            paintAll(getGraphics());
        }
        if (getParam(1).equals("T")) {
            try {
                this.rThread.writeUTF("ConfirmEnd");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.packageList = null;
    }

    private void removeHashNTR() {
        for (Integer i : hashNTR) {
            ConnectSession.hashNTR.remove(i);
        }
    }

    public void setLocation() {

        java.util.Random rx = new java.util.Random();

        java.util.Random ry = new java.util.Random();

        int sw = Toolkit.getDefaultToolkit().getScreenSize().width;

        int sh = Toolkit.getDefaultToolkit().getScreenSize().height;

        int x1 = sw / 2 - this.getBounds().width / 2;

        int x2 = sw / 2 - x1;

        int y1 = sh / 2 - this.getBounds().height / 2;

        int y2 = sh / 2 - y1;

        this.x = rx.nextInt(this.getBounds().width / 2) + x2;

        this.y = ry.nextInt(this.getBounds().height / 2) + y2;

        this.setLocation(x, y);
    }

    public static List<String> splitToken(String fs) {

        StringTokenizer pt = new StringTokenizer(fs, "|");

        List<String> l = new ArrayList<String>();

        while (pt.hasMoreTokens()) {

            l.add(pt.nextToken());
        }

        return l;
    }
    
    /*
     * 从cmdList中根据Index的值判断是否与param相等。相等返回True，不相等返回False。
     */
    public boolean equParam(int index, String param) {

        if (packageList.get(index).equals(param)) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 返回cmdList中Index项的值。
     */
    public String getParam(int index) {

        return packageList.get(index);
    }

    public void setMessage(String message) {
        textPane.setText(message);
    }

    public void setHostInfo(String hostMessage) {
        this.IPHostLabel.setText(hostMessage);
        this.IPHostLabel.setToolTipText(hostMessage);
    }
}

class FNameAndSize {

    public String fileName;
    public long fileSize;
    public boolean isDirectory = false;

    public FNameAndSize() {
        super();
    }

    public FNameAndSize(String fileName, long fileSize) {

        this.fileName = fileName;

        this.fileSize = fileSize;

        isDirectory = false;
    }

    public FNameAndSize(String fileName) {

        this.fileName = fileName;

        isDirectory = true;
    }
}
