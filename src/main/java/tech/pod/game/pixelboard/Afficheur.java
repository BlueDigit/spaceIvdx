package tech.pod.game.pixelboard;

import javax.swing.*;

import java.awt.*;
import java.awt.image.*;

public class Afficheur extends JFrame
{
    int[] tab;
    JPanel jp; //= new JPanel();

    class ImagePanel extends JPanel
    {
        private BufferedImage image;

        public ImagePanel(BufferedImage i)
        {
            image = i;
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
    }

    Afficheur(int width, int height, int[] pixels)
    {
        BufferedImage bim = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_ARGB
        );
        tab = ((DataBufferInt) bim.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, tab, 0, pixels.length);
        this.jp = new ImagePanel(bim);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(bim.getWidth(), bim.getHeight());
        this.setSize(width, height + 20);
        this.add(jp);
        this.setVisible(true);
    }

    void update(int[] pixels)
    {
        System.arraycopy(pixels, 0, tab, 0, pixels.length);
        jp.revalidate();
        jp.repaint();
    }

}