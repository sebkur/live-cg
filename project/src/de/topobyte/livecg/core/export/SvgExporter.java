package de.topobyte.livecg.core.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.topobyte.livecg.core.painting.AlgorithmPainter;
import de.topobyte.livecg.core.painting.SvgPainter;

public class SvgExporter
{

	public static void exportSVG(File file, AlgorithmPainter algorithmPainter,
			int width, int height) throws TransformerException, IOException
	{
		DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
		String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		Document doc = impl.createDocument(svgNS, "svg", null);

		Element svgRoot = doc.getDocumentElement();

		svgRoot.setAttributeNS(null, "width", Integer.toString(width));
		svgRoot.setAttributeNS(null, "height", Integer.toString(height));

		SvgPainter painter = new SvgPainter(doc, svgRoot);

		algorithmPainter.setPainter(painter);
		algorithmPainter.setWidth(width);
		algorithmPainter.setHeight(height);
		algorithmPainter.paint();

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		FileOutputStream fos = new FileOutputStream(file);
		StreamResult result = new StreamResult(fos);

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(source, result);

		fos.close();
	}
}
