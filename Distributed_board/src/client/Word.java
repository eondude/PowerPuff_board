package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.Serializable;

class Word extends drawings implements Serializable
{
	private static final long serialVersionUID = 1L;

	void draw(Graphics2D g2d) {
        g2d.setPaint(new Color(R, G, B));
        g2d.setFont(new Font(s2, x2 + y2, ((int) stroke) * 16));
        if (s1 != null) {
            g2d.drawString(s1, x1, y1);
        }
    }
}

