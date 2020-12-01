package tech.pod.game.pixelboard;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
public class ImageUtil{
  static int[] readImage(String fnam) throws IOException{
    int[] tab;
    BufferedImage bufi = ImageIO.read(new File(fnam));
    int width = bufi.getWidth();
    tab = bufi.getRGB(
                      0,
                      0,width,bufi.getHeight(),null,
                      0,
                      width);
    return tab;
  }
  static int getImageWidth(String fnam) throws IOException{
    BufferedImage bufi = ImageIO.read(new File(fnam));
    return bufi.getWidth();
  }
  static int getImageHeight(String fnam) throws IOException{
    BufferedImage bufi = ImageIO.read(new File(fnam));
    return bufi.getHeight();
  }
  static int[] explodePixel(int pix){
    int[] pt = new int[4];
    pt[0]= pix >> 24 & 0x000000FF;
    pt[1]= pix >> 16 & 0x000000FF;
    pt[2]= pix >> 8 & 0x000000FF;
    pt[3]= pix & 0x000000FF;
    return pt;
  }
  static int computePixel(int a, int r, int g, int b){
    return (((((a <<8)+ r) << 8) + g) <<8) + b;
  }
  static int computePixel(int[] pt){
    return (((((pt[0] <<8)+ pt[1]) << 8) + pt[2]) <<8) + pt[3];
  }
  static int alphaFromPix(int pix){
    return pix >> 24 & 0x000000FF;
  }
  static int redFromPix(int pix){
    return pix >> 16 & 0x000000FF;
  }
  static int greenFromPix(int pix){
    return pix >> 8 & 0x000000FF;
  }
  static int blueFromPix(int pix){
    return pix & 0x000000FF;
  }
}