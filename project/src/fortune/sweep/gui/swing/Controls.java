package fortune.sweep.gui.swing;

import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;

import fortune.sweep.Algorithm;

public class Controls extends Panel implements ActionListener
{

	private static final long serialVersionUID = -8452143409724541737L;

	private SwingFortune fortune;

	private Algorithm algorithm;

	private JButton buttons[];

	private static String KEY_PLAY = "play";
	private static String KEY_PLAY_REVERSE = "play-reverse";
	private static String KEY_PAUSE = "pause";
	private static String KEY_PREVIOUS_EVENT = "previous-event";
	private static String KEY_NEXT_EVENT = "next-event";
	private static String KEY_PREV_PIXEL = "previous-pixel";
	private static String KEY_NEXT_PIXEL = "next-pixel";
	private static String KEY_CLEAR = "clear";
	private static String KEY_RESTART = "restart";

	private static Map<String, String> texts = new HashMap<String, String>();
	static {
		texts.put(KEY_PLAY, "Play");
		texts.put(KEY_PLAY_REVERSE, "Play Reverse");
		texts.put(KEY_PAUSE, "Pause");
		texts.put(KEY_PREVIOUS_EVENT, "Previous event");
		texts.put(KEY_NEXT_EVENT, "Next event");
		texts.put(KEY_PREV_PIXEL, "Previous pixel");
		texts.put(KEY_NEXT_PIXEL, "Next pixel");
		texts.put(KEY_CLEAR, "Clear");
		texts.put(KEY_RESTART, "Restart");
	}

	private static Map<String, String> paths = new HashMap<String, String>();
	static {
		paths.put(KEY_PLAY, "res/media-playback-start.png");
		paths.put(KEY_PLAY_REVERSE, "res/media-playback-start-rtl.png");
		paths.put(KEY_PAUSE, "res/media-playback-pause.png");
		paths.put(KEY_PREVIOUS_EVENT, "res/media-skip-backward.png");
		paths.put(KEY_NEXT_EVENT, "res/media-skip-forward.png");
		paths.put(KEY_PREV_PIXEL, "res/media-seek-backward.png");
		paths.put(KEY_NEXT_PIXEL, "res/media-seek-backward-rtl.png");
		paths.put(KEY_CLEAR, "res/media-eject.png");
		paths.put(KEY_RESTART, "res/media-playback-stop.png");
	}

	private Map<String, Icon> icons = new HashMap<String, Icon>();

	public Controls(SwingFortune fortune, Algorithm algorithm)
	{
		this.fortune = fortune;
		this.algorithm = algorithm;

		for (String key : paths.keySet()) {
			Icon icon = ImageLoader.load(paths.get(key));
			icons.put(key, icon);
		}

		String keys[] = { KEY_PLAY, KEY_PLAY_REVERSE, KEY_PREVIOUS_EVENT,
				KEY_NEXT_EVENT, KEY_PREV_PIXEL, KEY_NEXT_PIXEL, KEY_RESTART,
				KEY_CLEAR };
		buttons = new JButton[keys.length];
		for (int i = 0; i < keys.length; i++) {
			buttons[i] = new JButton(icons.get(keys[i]));
			buttons[i].setToolTipText(texts.get(keys[i]));
			buttons[i].setMargin(new Insets(0, 0, 0, 0));
			buttons[i].addActionListener(this);
			add(buttons[i]);
		}

		threadRunning(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		int i = 0;
		if (e.getSource() == buttons[i++]) {
			if (fortune.isForeward()) {
				boolean running = fortune.toggleRunning();
				threadRunning(running);
			} else {
				fortune.setForeward(true);
				if (!fortune.isRunning()) {
					fortune.toggleRunning();
				}
			}
			threadRunning(fortune.isRunning());
		} else if (e.getSource() == buttons[i++]) {
			if (!fortune.isForeward()) {
				boolean running = fortune.toggleRunning();
				threadRunning(running);
			} else {
				fortune.setForeward(false);
				if (!fortune.isRunning()) {
					fortune.toggleRunning();
				}
			}
			threadRunning(fortune.isRunning());
		} else if (e.getSource() == buttons[i++]) {
			algorithm.previousEvent();
		} else if (e.getSource() == buttons[i++]) {
			algorithm.nextEvent();
		} else if (e.getSource() == buttons[i++]) {
			algorithm.previousPixel();
		} else if (e.getSource() == buttons[i++]) {
			algorithm.nextPixel();
		} else if (e.getSource() == buttons[i++]) {
			algorithm.restart();
		} else if (e.getSource() == buttons[i++]) {
			fortune.stopRunning();
			threadRunning(false);
			algorithm.clear();
		}
	}

	public void threadRunning(boolean running)
	{
		if (running) {
			if (fortune.isForeward()) {
				set(buttons[0], KEY_PAUSE);
				set(buttons[1], KEY_PLAY_REVERSE);
			} else {
				set(buttons[0], KEY_PLAY);
				set(buttons[1], KEY_PAUSE);
			}
			buttons[4].setEnabled(false);
			buttons[5].setEnabled(false);
		} else {
			set(buttons[0], KEY_PLAY);
			set(buttons[1], KEY_PLAY_REVERSE);
			buttons[4].setEnabled(true);
			buttons[5].setEnabled(true);
		}
		buttons[0].invalidate();
		invalidate();
		validate();
	}

	private void set(JButton button, String key)
	{
		button.setToolTipText(texts.get(key));
		button.setIcon(icons.get(key));
	}
}
