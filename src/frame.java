import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.io.*;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class frame {
	public static void main(String[] args) {
		JFrame win = new JFrame();
		BlockoContent content = new BlockoContent();
		win.setLayout(new BorderLayout());
		win.setSize(640, 480);
		win.setMinimumSize(new Dimension(640, 480));
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		win.add(content, BorderLayout.CENTER);
		if (content.online)
			win.setTitle("Blocko - Online mode " + content.client.port);
		else
			win.setTitle("Blocko");

		// Transparent 16 x 16 pixel cursor image.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		// Create a new blank cursor.
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		// Set the blank cursor to the JFrame.
		win.getContentPane().setCursor(blankCursor);

		win.setVisible(true);
	}

	/**
	 * The main panel of BLOCKO - extends JPanel implements KeyListener,
	 * MouseListener
	 */
	public static class BlockoContent extends JPanel implements KeyListener, MouseListener {
		private static final long serialVersionUID = -5308249761720156640L;
		int x = 41, y = 21, speed = 512, mspeed = 1, fps = 60, br = 20, score = 0;
		private Graphics dbg;
		private Image dbImage = null;
		Rectangle rect1 = new Rectangle(40, 20, 550, 400);
		Rectangle Redman = new Rectangle(-20, -20, 20, 20);
		IKP isKeyPressed = new IKP();
		Timer fpsTimer;
		Timer speedTimer;
		List<Shape> testArray = new ArrayList<Shape>();
		double scale;
		BlockoClient client = new BlockoClient("dodgeblock.cf", 25567);
		boolean online = true;
		boolean red = false;

		public BlockoContent() {
			setSize(640, 480);
			setMinimumSize(this.getSize());
			setFocusable(true);
			requestFocusInWindow();
			// setBackground(Color.white);
			addKeyListener(this);
			addMouseListener(this);
			if (online)
				client.execute();

			fpsTimer = new Timer(1000 / fps, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (online) {
						char separate = ';';
						if (red)
							client.thWR.Send("" + red + separate + Redman.x + separate + Redman.y);
						else {
							client.thWR.Send("" + red + separate + x + separate + y);
						}
					}
					repaint();
				}
			});
			fpsTimer.start();

			speedTimer = new Timer(1000 / speed, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// System.out.print("tick ");
					if (isKeyPressed.Up)
						if (y - mspeed > rect1.y)
							y -= mspeed;
					if (isKeyPressed.Down)
						if (y + mspeed <= rect1.y + rect1.height - br)
							y += mspeed;
					if (isKeyPressed.Left)
						if (x - mspeed > rect1.x)
							x -= mspeed;
					if (isKeyPressed.Right)
						if (x + mspeed <= rect1.x + rect1.width - br)
							x += mspeed;

					if (Redman.intersects(new Rectangle(x, y, br, br))) {
						setBackground(Color.blue);
						if (score < 2000000)
							score += 1;
					} else {
						setBackground(Color.black);
					}
				}
			});
			speedTimer.start();
		}

		public void reset() {
			score = 0;
			Redman.setLocation(new Point(-20, -20));
			x = 41;
			y = 21;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			System.out.print("click");
			Redman.setLocation((int) (getMousePosition().x / scale) - 10, (int) (getMousePosition().y / scale) - 10);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_UP:
				isKeyPressed.Up = true;
				System.out.print("Up");
				break;
			case KeyEvent.VK_DOWN:
				isKeyPressed.Down = true;
				System.out.print("Down");
				break;
			case KeyEvent.VK_LEFT:
				isKeyPressed.Left = true;
				System.out.print("Left");
				break;
			case KeyEvent.VK_RIGHT:
				isKeyPressed.Right = true;
				System.out.print("Right");
				break;

			case KeyEvent.VK_W:
				isKeyPressed.Up = true;
				System.out.print("Up");
				break;
			case KeyEvent.VK_S:
				isKeyPressed.Down = true;
				System.out.print("Down");
				break;
			case KeyEvent.VK_A:
				isKeyPressed.Left = true;
				System.out.print("Left");
				break;
			case KeyEvent.VK_D:
				isKeyPressed.Right = true;
				System.out.print("Right");
				break;

			case KeyEvent.VK_V:
				speed -= 1;
				speedTimer.setDelay((1000 / speed) + 1);
				break;
			case KeyEvent.VK_F:
				speed += 1;
				speedTimer.setDelay((1000 / speed) + 1);
				break;

			case KeyEvent.VK_G:
				fps += 1;
				fpsTimer.setDelay(1000 / fps);
				break;
			case KeyEvent.VK_B:
				if (fps > 1)
					fps -= 1;
				fpsTimer.setDelay(1000 / fps);
				break;

			case KeyEvent.VK_R:
				reset();
				break;

			case KeyEvent.VK_E:
				red = !red;
				break;
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_UP:
				isKeyPressed.Up = false;
				break;
			case KeyEvent.VK_DOWN:
				isKeyPressed.Down = false;
				break;
			case KeyEvent.VK_LEFT:
				isKeyPressed.Left = false;
				break;
			case KeyEvent.VK_RIGHT:
				isKeyPressed.Right = false;
				break;

			case KeyEvent.VK_W:
				isKeyPressed.Up = false;
				break;
			case KeyEvent.VK_S:
				isKeyPressed.Down = false;
				break;
			case KeyEvent.VK_A:
				isKeyPressed.Left = false;
				break;
			case KeyEvent.VK_D:
				isKeyPressed.Right = false;
				break;
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			if (dbImage == null) {
				dbImage = createImage(640, 480);
				// new BufferedImage(640, 480, BufferedImage.TYPE_INT_ARGB);
				if (dbImage == null) {
					System.out.println("dbImage is null");
					return;
				} else
					dbg = dbImage.getGraphics();
			}
			scale = ((double) this.getHeight() + 39) / (double) 480;
			Point mouseP = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(mouseP, this);
			mouseP = new Point((int) (mouseP.x / scale), (int) (mouseP.y / scale));
			Graphics2D graphics2d = (Graphics2D) dbg;

			dbg.setColor(this.getBackground());
			dbg.fillRect(0, 0, dbImage.getWidth(null), dbImage.getHeight(null));

			dbg.setColor(Color.white);

			rect1 = new Rectangle(40, 20, 550, 400);
			graphics2d.draw(rect1);

			graphics2d.drawString(
					"x=" + x + " y=" + y + " fps=" + fps + " speed=" + speed + " score=" + score + " red=" + red, 40,
					15);
			graphics2d.drawString("Controlls: arrows = move; g&b = fps; v&f = speed; r = reset; e = toggleplayer", 40,
					435);
			graphics2d.fillRect(x, y, br, br);
			graphics2d.setColor(Color.black);
			graphics2d.drawString("%", x + br / 2 - 5, y + br / 2 + 3);

			graphics2d.setColor(Color.red);
			graphics2d.fill(Redman);

			graphics2d.drawRect(mouseP.x - 10, mouseP.y - 10, 19, 19);

			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;

			AffineTransform at = AffineTransform.getScaleInstance(scale, scale);

			g2d.drawRenderedImage((BufferedImage) dbImage, at);
			Toolkit.getDefaultToolkit().sync(); // sync the display on some systems
			g.dispose();

		}

		public class IKP {
			public boolean Up = false;
			public boolean Down = false;
			public boolean Left = false;
			public boolean Right = false;
		}

		// ===========================================================================
		// ============================Internet=Things=================================
		/** This is the chat client program. @author www.codejava.net */
		public class BlockoClient {
			private String hostname;
			private int port;
			private String userName;
			public WriteThread thWR;
			public ReadThread thRE;

			public BlockoClient(String hostname, int port) {
				this.hostname = hostname;
				this.port = port;
				try {
					this.userName = InetAddress.getLocalHost().toString();
				} catch (UnknownHostException e) {
					System.out.println("IP self-recognition fail");
					e.printStackTrace();
				}
			}

			public void execute() {
				try {
					Socket socket = new Socket(hostname, port);

					System.out.println("Connected to the chat server");

					thRE = new ReadThread(socket);
					thWR = new WriteThread(socket);

					thRE.start();
					thWR.start();

				} catch (UnknownHostException ex) {
					System.out.println("Server not found: " + ex.getMessage());
				} catch (IOException ex) {
					System.out.println("I/O Error: " + ex.getMessage());
				}

			}

			void setUserName(String userName) {
				this.userName = userName;
			}

			String getUserName() {
				return this.userName;
			}
		}

		/**
		 * This thread is responsible for sending to the server. @author
		 * www.codejava.net
		 */
		public class WriteThread extends Thread {
			private PrintWriter writer;

			public WriteThread(Socket socket) {
				try {
					OutputStream output = socket.getOutputStream();
					writer = new PrintWriter(output, true);
				} catch (IOException ex) {
					System.out.println("Error getting output stream: " + ex.getMessage());
					ex.printStackTrace();
				}
			}

			public void Send(String text) {
				writer.println(text);
			}
			/*
			 * public void CloseSocket() { try { socket.close(); } catch (IOException ex) {
			 * 
			 * System.out.println("Error writing to server: " + ex.getMessage()); } }
			 */
		}

		/**
		 * This thread is responsible for reading server's input. It runs in an infinite
		 * loop until the client disconnects from the server.
		 * 
		 * @author www.codejava.net
		 */
		public class ReadThread extends Thread {
			private BufferedReader reader;

			public ReadThread(Socket socket) {
				try {
					InputStream input = socket.getInputStream();
					reader = new BufferedReader(new InputStreamReader(input));
				} catch (IOException ex) {
					System.out.println("Error getting input stream: " + ex.getMessage());
					ex.printStackTrace();
				}
			}

			public void run() {
				while (true) {
					try {
						String response = reader.readLine();

						if (response == null)
							break;

						try {
							String[] args = response.split(";");
							if (args.length > 1) {
								// if (red)
								if (Boolean.parseBoolean(args[0])) {
									Redman.x = Integer.parseInt(args[1]);
									Redman.y = Integer.parseInt(args[2]);
								} else {
									x = Integer.parseInt(args[1]);
									y = Integer.parseInt(args[2]);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						System.out.println("\n" + response);
					} catch (IOException ex) {
						System.out.println("Error reading from server: " + ex.getMessage());
						ex.printStackTrace();
						break;
					}
				}
			}
		}
	}
}