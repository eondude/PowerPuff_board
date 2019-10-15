package client;

import java.awt.Graphics2D;
import java.io.Serializable;

public class drawings implements Serializable
{
  	private static final long serialVersionUID = 1L;
	int x1, y1, x2, y2;
    int R, G, B;
    float stroke;
    int type;
    String s1;
    String s2;
    void draw(Graphics2D g2d) {};
}