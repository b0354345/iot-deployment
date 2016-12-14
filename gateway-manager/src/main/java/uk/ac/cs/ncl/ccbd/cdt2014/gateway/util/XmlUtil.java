package uk.ac.cs.ncl.ccbd.cdt2014.gateway.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;
/**
 * @author Saleh Mohamed
 * @version (April 20 16)
 */

public class XmlUtil {
	
	/**
	 * Parse xml string returned from received from a gateway application
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document parseXml(String xmlString) throws ParserConfigurationException, SAXException, IOException  {
		// Remove three lines before prolog
		xmlString = xmlString.substring(xmlString.indexOf(System.getProperty("line.separator"))+1);
		xmlString = xmlString.substring(xmlString.indexOf(System.getProperty("line.separator"))+1);
		xmlString = xmlString.substring(xmlString.indexOf(System.getProperty("line.separator"))+1);
		
		// Create document DocumentBuilder and Document from the xmlString
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
        Document doc = builder.parse(input);
      
        return doc;	
	}

	
	/**
	 * Update a property of a given configuration file and return a string representation of the
	 * xml document
	 * @param doc
	 * @param property
	 * @param newValue
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static String updateXmlDoc(Document doc, List<String> properties) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		for (String property: properties) {
			String key = property.split(":")[0];
			String value = property.split(":")[1];
	        NodeList propertiesNode = doc.getElementsByTagName("esf:property");
	        for (int i = 0; i < propertiesNode.getLength(); i++) {
	      	  Node node = propertiesNode.item(i);
	      	  if (node.getNodeType() == Node.ELEMENT_NODE) {
	      		  Element el = (Element)node;
	      		  if (key.equals(el.getAttribute("name"))) {
	      			  el.getElementsByTagName("esf:value").item(0).setTextContent(value);
	      		  }   		  
	      	  }
	        }  
		}
        return parseXmlDocToString(doc);
	}
	
	private static String parseXmlDocToString(Document xmlDoc)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		// Add formatting to the xml document in case we want to save to a file
        System.setProperty(DOMImplementationRegistry.PROPERTY,"com.sun.org.apache.xerces.internal.dom.DOMImplementationSourceImpl");
        final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
        final LSSerializer writer = impl.createLSSerializer();
        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE); // Set this to true if the output needs to be beautified.
        writer.getDomConfig().setParameter("xml-declaration", true); // Set this to true if the declaration is needed to be outputted
        
        return writer.writeToString(xmlDoc);
	}	
}
