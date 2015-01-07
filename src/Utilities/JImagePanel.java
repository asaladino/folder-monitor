package Utilities;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * @author Adam
 */
public class JImagePanel extends JPanel {

    private Image img = null;
    private int type = 1;
    public static final int DEFAULT = 0;
    public static final int HORIZONTAL_TILE = 1;

    public JImagePanel() {
        initComponents();
    }

    public JImagePanel(String img) {
        if (img != null) {
            this.img = new ImageIcon(img).getImage();
        }
    }

    public JImagePanel(Image img) {
        this.img = img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
            if (type == HORIZONTAL_TILE) {
                // Add 1 for rounding error.
                double numberOfTiles = Math.abs(getWidth() / size.getWidth()) + 1;
                for (int i = 0; i < numberOfTiles; i++) {
                    if (i == 0) {
                        g.drawImage(img, i, 0, null);
                    } else {
                        g.drawImage(img, (int) (i * size.getWidth()), 0, null);
                    }
                }
            } else {
                g.drawImage(img, 0, 0, null);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
