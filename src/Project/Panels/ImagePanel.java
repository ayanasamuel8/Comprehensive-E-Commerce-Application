package Project.Panels;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JPanel that displays a background image.
 */
public class ImagePanel extends JPanel {
    private Image img;

    /**
     * Constructs an ImagePanel with the specified image path and layout manager.
     *
     * @param imgPath the path to the image file
     * @param layout  the layout manager for the panel
     */
    public ImagePanel(String imgPath, LayoutManager layout) {
        img = new ImageIcon(imgPath).getImage();
        setLayout(layout);
    }

    /**
     * Paints the component with the background image, scaled to fit the panel.
     *
     * @param g the Graphics object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
    }
}
