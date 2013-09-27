package fortune.sweep.gui.swing;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilterBitmap extends FileFilter
{

	@Override
	public boolean accept(File path)
	{
		return path.isDirectory() || path.getName().endsWith(".png");
	}

	@Override
	public String getDescription()
	{
		return "PNG images (*.png)";
	}

}
