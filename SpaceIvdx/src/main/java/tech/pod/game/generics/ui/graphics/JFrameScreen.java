package tech.pod.game.generics.ui.graphics;

import java.awt.Graphics;
import java.awt.image.DataBufferInt;
import java.util.AbstractMap;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

/**
 * Implement a {@link GameScreen} based on the {@link JFrame} Swing component.
 * @param <C> The targeted color type
 */
public class JFrameScreen<C extends Color> extends JFrame implements GameScreen<C>
{
    private GameImage<C> backGround;
    private int[] pixelScreen;
    private JPanel panel;
    private ColorConverter<C, Integer> colorConverter;
    private UIConfiguration configuration;

    private static class ScreenPanel extends JPanel
    {
        private transient BufferedImage image;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(this.image, 0, 0, null);
        }

        private static ScreenPanel of(BufferedImage image) {
            var panel = new ScreenPanel();
            panel.image = image;
            return panel;
        }
    }

    /**
     * Instantiate a JFrameScreen by defining its {@link ColorConverter}.
     * @param colorConverter should not be null
     */
    public JFrameScreen(UIConfiguration configuration,
                        ColorConverter<C, Integer> colorConverter,
                        GameImage<C> backGround) {
        this.configuration = configuration;
        this.colorConverter = Objects.requireNonNull(colorConverter, "JFrameScreen: null color converter");
        this.backGround = Objects.requireNonNull(backGround);
    }

    /**
     * Copy an image to the game screen.
     * @param image should not be null
     */
    private void copyPixels(GameImage<C> image) {
        int[] pixels = new int[this.configuration.getSize()];
        int[] idx = new int[]{0};
        image.stream()
             .map(color -> {
                 int i = idx[0];
                 var idxPixel = new AbstractMap.SimpleEntry<>(i, this.colorConverter.convert(color));
                 idx[0] += 1;
                 return idxPixel;
             })
             .filter(idxPixel -> idxPixel.getKey() < this.configuration.getSize())
             .forEach(idxPixel -> pixels[idxPixel.getKey()] = idxPixel.getValue());
        System.arraycopy(pixels, 0, this.pixelScreen, 0, pixels.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameScreen<C> configure(UIConfiguration configuration, GameImage<C> backGround) {
        if (this.panel != null) {
            this.remove(this.panel);
        }
        this.backGround = backGround;
        BufferedImage bim = new BufferedImage(configuration.getWidth(),
                                              configuration.getHeight(),
                                              BufferedImage.TYPE_INT_ARGB);
        this.pixelScreen = ((DataBufferInt) bim.getRaster().getDataBuffer()).getData();
        this.copyPixels(backGround);
        this.panel = ScreenPanel.of(bim);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(configuration.getWidth(), configuration.getHeight());
        this.add(this.panel);
        this.setVisible(true);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameScreen<C> spool() {
        this.copyPixels(this.backGround);
        this.panel.revalidate();
        this.panel.repaint();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameScreen<C> draw() {
        if (this.panel == null) {
            this.configure(this.configuration, this.backGround);
        }
        return this.draw(this.backGround);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameScreen<C> draw(GameImage<C> image) {
        if (this.panel == null) {
            this.configure(this.configuration, this.backGround);
        }
        this.copyPixels(image);
        this.panel.revalidate();
        this.panel.repaint();
        return this;
    }

    public static <C extends Color> JFrameScreen<C> of(
            UIConfiguration uiConfiguration,
            ColorConverter<C, Integer> colorConverter,
            GameImage<C> background
    ) {
        return new JFrameScreen<>(uiConfiguration, colorConverter, background);
    }
}
