package view.NetReceiver;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ControlButton extends JLabel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private ImageIcon saveImage = new ImageIcon(getClass().getResource("/view/Picture/save.png"));
    private ImageIcon cancelImage = new ImageIcon(getClass().getResource("/view/Picture/cancel.png"));
    private ImageIcon openImage = new ImageIcon(getClass().getResource("/view/Picture/open.png"));
    private String controlMode = "save";

    public String getControlMode() {

        return controlMode;
    }

    public void setControlMode(String controlMode) {

        if (controlMode.equals("save")) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setIcon(saveImage);
                    setToolTipText("Save");
                }
            });

            this.controlMode = controlMode;

        } else if (controlMode.equals("cancel")) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setIcon(cancelImage);
                    setToolTipText("Cancel");
                }
            });
            this.controlMode = controlMode;

        } else if (controlMode.equals("open")) {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    setIcon(openImage);
                    setToolTipText("Open");
                }
            });
            this.controlMode = controlMode;
        }
    }
}
