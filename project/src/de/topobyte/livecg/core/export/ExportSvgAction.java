package de.topobyte.livecg.core.export;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.xml.transform.TransformerException;

import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.ui.action.BasicAction;
import de.topobyte.livecg.core.ui.filefilters.FileFilterSvg;

public class ExportSvgAction extends BasicAction
{

	private static final long serialVersionUID = 1L;

	private Component component;
	private AlgorithmPainter algorithmPainter;
	private SizeProvider sizeProvider;

	public ExportSvgAction(Component component,
			AlgorithmPainter algorithmPainter, SizeProvider sizeProvider)
	{
		super("Export SVG", "Export the current view to a SVG image", null);
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
		fc.setFileFilter(new FileFilterSvg());
		int returnVal = fc.showSaveDialog(component);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = fc.getSelectedFile();
		lastDirectoryService.setLastActiveDirectory(file.getParentFile());

		try {
			SvgExporter.exportSVG(file, algorithmPainter,
					sizeProvider.getWidth(), sizeProvider.getHeight());
		} catch (IOException ex) {
			System.out.println("unable to export image (IOException): "
					+ ex.getMessage());
		} catch (TransformerException ex) {
			System.out.println("unable to export image (TransfomerException): "
					+ ex.getMessage());
		}
	}

}
