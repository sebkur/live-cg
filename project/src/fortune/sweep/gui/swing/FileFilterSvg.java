package fortune.sweep.gui.swing;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilterSvg extends FileFilter
{

	@Override
	public boolean accept(File path)
	{
		return path.isDirectory() || path.getName().endsWith(".svg");
	}

	@Override
	public String getDescription()
	{
		return "SVG images (*.svg)";
	}

}
