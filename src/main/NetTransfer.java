package main;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import thread.NetSender.GetOnlineListThread;

public class NetTransfer {

    public static String version = "1.7";
    public static String SetupFile = "setup.properties";

    public boolean CreateTrayIcon(String trayImage, String trayName, PopupMenu popup) {
        boolean isCreated = false;
        final TrayIcon trayIcon;
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/view/Picture/LOGO.png"));
            trayIcon = new TrayIcon(image, trayName, popup);
            trayIcon.setImageAutoSize(true);
            //创建一个Action监听器:左键双击事件
            ActionListener al = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
//                    trayIcon.displayMessage("托盘事件", "这个双击事件己收到", TrayIcon.MessageType.WARNING);
                    new GetOnlineListThread("recreate");
                    new NetSender();
                }
            };
/*            MouseAdapter al = new MouseAdapter(){
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                             new GetOnlineListThread("recreate");
                             new NetSender();      
                        }
                }
            };*/
//            trayIcon.addMouseListener(al);
            trayIcon.addActionListener(al);
            try {
                tray.add(trayIcon);
                isCreated = true;
            } catch (AWTException e) {
                System.err.println("无法创建托盘:" + e);
                isCreated = false;
            }
        }
        return isCreated;
    }

    public PopupMenu createPopup() {
        PopupMenu popup = new PopupMenu();
        MenuItem menuExit = new MenuItem("Exit");
        MenuItem menuOpen = new MenuItem("Open NetSender");
        MenuItem menuSeparator = new MenuItem("-");
        MenuItem menuSetup = new MenuItem("Setup");

        //创建退出菜单监听器
        ActionListener exitListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        //创建打开监听器
        ActionListener openListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new GetOnlineListThread("recreate");
                new NetSender();
            }
        };
        ActionListener setupListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                new NetTransferSetup();
            }
        };
        menuExit.addActionListener(exitListener);
        menuOpen.addActionListener(openListener);
        menuSetup.addActionListener(setupListener);
        popup.add(menuOpen);
        popup.add(menuSetup);
        popup.add(menuSeparator);
        popup.add(menuExit);
        return popup;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
//        try {
////                      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
////			UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
//            UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel");
//        } catch (ClassNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (UnsupportedLookAndFeelException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        if (new NetReceiver().isStarted) {
            NetTransfer nt = new NetTransfer();
            String iconFileName = "netjava.gif";
            PopupMenu pop = nt.createPopup();
            nt.CreateTrayIcon(iconFileName, "NetTransfer " + version, pop);
        }
    }
}
