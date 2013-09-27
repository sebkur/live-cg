package fortune.sweep.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import fortune.sweep.Algorithm;
import fortune.sweep.gui.core.Config;
import fortune.sweep.gui.swing.action.ExportBitmapAction;
import fortune.sweep.gui.swing.action.ExportSvgAction;
import fortune.sweep.gui.swing.action.OpenAction;
import fortune.sweep.gui.swing.action.QuitAction;
import fortune.sweep.gui.swing.action.SaveAction;
import fortune.sweep.gui.swing.eventqueue.EventQueueDialog;

public class SwingFortune extends JFrame implements Runnable
{

	private static final long serialVersionUID = 3917389635770683885L;

	public static void main(String[] args)
	{
		new SwingFortune();
	}

	private Algorithm algorithm;
	private Canvas canvas;
	private Controls controls;
	private Config config;

	private EventQueueDialog eventQueueDialog;

	private JPanel main;
	private JMenuBar menu;
	private Settings settings;

	private boolean running = false;
	private boolean foreward = true;
	private Thread thread;
	private Object wait = new Object();

	public SwingFortune()
	{
		super("Fortune's sweep");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		init();
	}

	public void init()
	{
		/*
		 * Menus
		 */

		menu = new JMenuBar();

		JMenu menuFile = new JMenu("File");
		menu.add(menuFile);
		JMenuItem open = new JMenuItem(new OpenAction(this));
		menuFile.add(open);
		JMenuItem save = new JMenuItem(new SaveAction(this));
		menuFile.add(save);
		JMenuItem exportBitmap = new JMenuItem(new ExportBitmapAction(this));
		menuFile.add(exportBitmap);
		JMenuItem exportSvg = new JMenuItem(new ExportSvgAction(this));
		menuFile.add(exportSvg);
		JMenuItem quit = new JMenuItem(new QuitAction());
		menuFile.add(quit);

		JMenu menuHelp = new JMenu("Help");
		menu.add(menuHelp);
		JMenuItem about = new JMenuItem("About");
		menuHelp.add(about);

		setJMenuBar(menu);

		/*
		 * Components, layout
		 */

		main = new JPanel();
		setContentPane(main);
		main.setLayout(new BorderLayout());

		algorithm = new Algorithm();
		config = new Config();

		config.setDrawCircles(true);
		config.setDrawBeach(true);
		config.setDrawVoronoiLines(true);
		config.setDrawDelaunay(false);

		canvas = new Canvas(algorithm, config, getWidth(), getHeight() - 50);
		controls = new Controls(this, algorithm);
		settings = new Settings(canvas, config);

		SweepControl sweepControl = new SweepControl(algorithm);
		sweepControl.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		Box south = new Box(BoxLayout.Y_AXIS);
		south.add(sweepControl);
		south.add(controls);

		main.add(settings, BorderLayout.NORTH);
		main.add(canvas, BorderLayout.CENTER);
		main.add(south, BorderLayout.SOUTH);

		algorithm.addWatcher(canvas);
		algorithm.addWatcher(sweepControl);

		canvas.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e)
			{
				algorithm.setWidth(canvas.getWidth());
				algorithm.setHeight(canvas.getHeight());
			}

		});

		setSize(800, 600);
		setVisible(true);

		/*
		 * EventQueue dialog
		 */

		eventQueueDialog = new EventQueueDialog(this, algorithm);
		eventQueueDialog.setVisible(true);
		eventQueueDialog.setLocation(getX() + getWidth(), (int) getLocation().getY());

		/*
		 * Start thread
		 */

		thread = new Thread(this);
		thread.start();
	}

	public boolean toggleRunning()
	{
		if (running) {
			running = false;
		} else {
			if (!algorithm.isFinshed()) {
				running = true;
				synchronized (wait) {
					wait.notify();
				}
			}
		}
		return running;
	}

	public void stopRunning()
	{
		if (!running) {
			return;
		}
		running = false;
	}

	public void run()
	{
		while (true) {
			if (running) {
				boolean eventsLeft;
				if (foreward) {
					eventsLeft = algorithm.nextPixel();
				} else {
					eventsLeft = algorithm.previousPixel();
				}
				if (!eventsLeft) {
					setPaused();
				}
				try {
					Thread.sleep(25L);
				} catch (InterruptedException ex) {
					// ignore
				}
			} else {
				setPaused();
			}
		}
	}

	private void setPaused()
	{
		running = false;
		controls.threadRunning(false);
		while (true) {
			try {
				synchronized (wait) {
					wait.wait();
				}
				controls.threadRunning(true);
				break;
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	/*
	 * Open / Save dialogs related stuff
	 */

	private File lastActiveDirectory = null;

	public File getLastActiveDirectory()
	{
		return lastActiveDirectory;
	}

	public void setLastActiveDirectory(File lastActiveDirectory)
	{
		this.lastActiveDirectory = lastActiveDirectory;
	}

	/*
	 * Various
	 */

	public Algorithm getAlgorithm()
	{
		return algorithm;
	}

	public void setForeward(boolean foreward)
	{
		this.foreward = foreward;
	}

	public boolean isForeward()
	{
		return foreward;
	}

	public boolean isRunning()
	{
		return running;
	}

	public Dimension getCanvasSize()
	{
		return canvas.getSize();
	}

	public Config getConfig()
	{
		return config;
	}
}
