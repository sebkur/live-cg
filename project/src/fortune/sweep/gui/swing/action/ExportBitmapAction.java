package fortune.sweep.gui.swing.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import fortune.sweep.Algorithm;
import fortune.sweep.export.GraphicsExporter;
import fortune.sweep.gui.core.Config;
import fortune.sweep.gui.swing.FileFilterBitmap;
import fortune.sweep.gui.swing.SwingFortune;

public class ExportBitmapAction extends SwingFortuneAction
{

	private static final long serialVersionUID = 1L;

	public ExportBitmapAction(SwingFortune swingFortune)
	{
		super("Export Bitmap", "Export the current view to a bitmap", null,
				swingFortune);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(swingFortune.getLastActiveDirectory());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterBitmap());
		int returnVal = fc.showSaveDialog(swingFortune);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		swingFortune.setLastActiveDirectory(file.getParentFile());

		Algorithm algorithm = swingFortune.getAlgorithm();
		Config config = swingFortune.getConfig();
		Dimension dimension = swingFortune.getCanvasSize();

		try {
			GraphicsExporter.exportPNG(file, algorithm, config,
					dimension.width, dimension.height);
		} catch (IOException ex) {
			System.out.println("unable to export image: " + ex.getMessage());
		}
	}

}
