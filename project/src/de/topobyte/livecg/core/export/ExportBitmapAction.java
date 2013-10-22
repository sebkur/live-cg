package de.topobyte.livecg.core.export;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometryeditor.filefilters.FileFilterBitmap;

public class ExportBitmapAction extends BasicAction
{

	private static final long serialVersionUID = 1L;

	final static Logger logger = LoggerFactory
			.getLogger(ExportBitmapAction.class);

	private Component component;
	private AlgorithmPainter algorithmPainter;

	private SizeProvider sizeProvider;

	public ExportBitmapAction(Component component,
			AlgorithmPainter algorithmPainter, SizeProvider sizeProvider)
	{
		super("Export Bitmap", "Export the current view to a bitmap", null);
		this.component = component;
		this.algorithmPainter = algorithmPainter;
		this.sizeProvider = sizeProvider;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		LastDirectoryService lastDirectoryService = LastDirectoryService
				.getInstance();
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(lastDirectoryService.getLastActiveDirectory());
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setFileFilter(new FileFilterBitmap());
		int returnVal = fc.showSaveDialog(component);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		lastDirectoryService.setLastActiveDirectory(file.getParentFile());

		try {
			GraphicsExporter.exportPNG(file, algorithmPainter,
					sizeProvider.getWidth(), sizeProvider.getHeight());
		} catch (IOException ex) {
			logger.error("unable to export image: " + ex.getMessage());
		}
	}

}
