import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;

import wiiremotej.IRLight;

public class DebugViewer {
	private JFrame frame;
	private JPanel panel;
	private Point[] points;
	private Point[] rawPoints;

	public DebugViewer() {
		frame = new JFrame();
		frame.setTitle("Lights");
		frame.setSize(1024, 768);
		frame.setResizable(false);
		points = new Point[] { new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) };
		rawPoints = new Point[] { new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0) };
		panel = new JPanel() {
			private static final long serialVersionUID = 1136277909253495557L;

			public void paintComponent(Graphics graphics) {
				graphics.setColor(Color.BLACK);
				graphics.fillRect(0, 0, 1024, 768);
				if (points[0] != null && points[0].x != 0) {
					graphics.setColor(Color.WHITE);
					for (int i = 0; i < 4; i++)
						graphics.fillOval(points[i].x, points[i].y, 100, 100);
				}
				if (rawPoints[0] != null && rawPoints[0].x != 0) {
					graphics.setColor(Color.GRAY);
					for (int i = 0; i < 4; i++)
						graphics.fillOval(rawPoints[i].x, rawPoints[i].y, 50, 50);
				}

			}
		};
		frame.add(panel);
		frame.setVisible(true);
	}

	public void repaint() {
		panel.repaint();
	}

	public void setLights(IRLight[] p) {
		for (int i = 0; i < 4; i++) {
			int x = 0, y = 0;
			if (p[i] != null) {
				x = (int) (p[i].getX() * 1024);
				y = (int) (p[i].getY() * 768);
			}
			points[i] = new Point(x, y);
		}
		repaint();
	}

	public void setRawLights(IRLight[] r) {
		for (int i = 0; i < 4; i++) {
			int x = 0, y = 0;
			if (r[i] != null) {
				x = (int) (r[i].getX() * 1024);
				y = (int) (r[i].getY() * 768);
			}
			rawPoints[i] = new Point(x, y);
		}
		repaint();
	}
}
