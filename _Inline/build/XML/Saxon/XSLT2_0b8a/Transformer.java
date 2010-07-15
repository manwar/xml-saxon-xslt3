
import net.sf.saxon.s9api.*;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

public class Transformer
{
	private XsltExecutable xslt;
	private Processor proc;
	private HashMap<String, String> params;

	public Transformer (String stylesheet)
		throws SaxonApiException
	{
		proc = new Processor(false);
		XsltCompiler comp = proc.newXsltCompiler();
		xslt = comp.compile(new StreamSource(new StringReader(stylesheet)));
		params = new HashMap<String, String>();
	}
	
	public void paramClear ()
	{
		params.clear();
	}

	public void paramAdd (String key, String value)
	{
		params.put(key, value);
	}

	public void paramRemove (String key)
	{
		params.remove(key);
	}

	public String paramGet (String key)
	{
		return params.get(key);
	}

	public String transform (String doc, String method)
		throws SaxonApiException
	{
		XdmNode source = proc.newDocumentBuilder().build(
			new StreamSource(new StringReader(doc))
			);
		
		Serializer out = new Serializer();
		out.setOutputProperty(Serializer.Property.METHOD, method);
		StringWriter sw = new StringWriter();
		out.setOutputWriter(sw);
		
		Iterator i = params.keySet().iterator();
		while (i.hasNext())
		{
			Object k = i.next();
			Object v = params.get(k);
			
			System.out.println(">>> " + k + " => " + v "\n");
		}

		XsltTransformer trans = xslt.load();
		trans.setInitialContextNode(source);
		trans.setDestination(out);
		trans.transform();
		
		return sw.toString();
	}
}

