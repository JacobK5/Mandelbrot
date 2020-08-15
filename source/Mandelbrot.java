import java.awt.*;
import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

public class Mandelbrot extends JPanel {
	
	int iterations;
	JFrame frame;
	Timer timer;
	BufferedImage image;
	double zoomXMin;
	double zoomXMax;
	double zoomYMin;
	double zoomYMax;
	boolean zooming;
	int mx;
	int my;
	boolean in;

	public Mandelbrot() {
		iterations = 100;

		this.addMouseListener(new MyMouseListener());
		this.addKeyListener(new TAdapter());
		this.setFocusable(true);

		frame = new JFrame("Mandelbrot!");
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(this);
		frame.setVisible(true);

		image = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);

		zoomXMin = -2;
		zoomXMax = 2;
		zoomYMin = -2;
		zoomYMax = 2;
		makeImage();

		timer = new Timer(100, new TimerListener());
		timer.start();
	}

	public static void main(String[] args) {
		Mandlebrot m = new Mandlebrot();
	}

	public void makeImage() {
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++) {
				double a = map(x, 0, frame.getWidth(), zoomXMin, zoomXMax);
				double b = map(y, 0, frame.getHeight(), zoomYMin, zoomYMax);
				double ca = a;
				double cb = b;
				double k;
				for(k = 0; k < iterations; k++) {
					double oldA = a;
					double oldB = b;
					a = (oldA * oldA - oldB * oldB) + ca;
					b = (2 * oldA * oldB) + cb;
					if(a > 16) {
						break;
					}
				}
				float hu;
				if(k == iterations) {
					hu = 0;
				} else {
					hu = (float) map(k, 0, 100, 0, 1);
				}
				image.setRGB(x , y, Color.getHSBColor(hu, 1, 1).getRGB());
				frame.repaint();
			}
		}
	}

	

	public double map(double num, double min, double max, double newMin, double newMax) {
		return ((num - min) / (max - min) * (newMax - newMin) + newMin);
	}

	public void mainLoop() {
		if(zooming) {
			int multiplier = 1;
			if(in) {
				multiplier = -1;
			}
			int extra = 1;
			double rangeUp = (zoomYMax - zoomYMin);
			double rangeLeft = (zoomXMax - zoomXMin);
			double leftPercent = map(mx, 0, frame.getWidth(), 0, 1);
			double upPercent = map(my, frame.getHeight(), 0, 0, 1);

			zoomXMin += -multiplier * extra * leftPercent * rangeLeft / 10;
			zoomXMax += multiplier * (1 - leftPercent) * rangeLeft / 10;
			zoomYMin += -multiplier * (1 - upPercent) * rangeUp / 10;
			zoomYMax += multiplier * upPercent * rangeUp / 10;

			makeImage();
		}

	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}

	public class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			mainLoop();
		}
	}

	public class MyMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {}

    	public void mouseExited(MouseEvent e) {}

    	public void mousePressed(MouseEvent e) {
    		if(e.getButton() == MouseEvent.BUTTON1) {
    			in = true;
    		} else {
    			in = false;
    		}
    		mx = e.getX();
			my = e.getY();
       		zooming = true;
    	}

	    public void mouseReleased(MouseEvent e) {
	    	zooming = false;
	    }

	    public void mouseEntered(MouseEvent e) {}
	}

	private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        	if(e.getKeyCode() == KeyEvent.VK_SPACE) {
        		zoomXMin = -2;
				zoomXMax = 2;
				zoomYMin = -2;
				zoomYMax = 2;
				makeImage();
        	}
        }
    }
}