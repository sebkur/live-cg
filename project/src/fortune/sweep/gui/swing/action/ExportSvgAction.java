package fortune.sweep.gui.swing.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.transform.TransformerException;

import fortune.sweep.Algorithm;
import fortune.sweep.export.SvgExporter;
import fortune.sweep.gui.core.Config;
import fortune.sweep.gui.swing.FileFilterSvg;
import fortune.sweep.gui.swing.SwingFortune;

public class ExportSvgAction extends SwingFortuneAction
{

	private static final long serialVersionUID = 1L;

	public ExportSvgAction(SwingFortune swingFortune)
	{
		super("Export SVG", "Export the current view to a SVG image", null,
				swingFortune);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(swingFortune.getLastActiveDirectory());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterSvg());
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
			SvgExporter.exportSVG(file, algorithm, config, dimension.width,
					dimension.height);
		} catch (IOException ex) {
			System.out.println("unable to export image (IOException): "
					+ ex.getMessage());
		} catch (TransformerException ex) {
			System.out.println("unable to export image (TransfomerException): "
					+ ex.getMessage());
		}
	}

}
