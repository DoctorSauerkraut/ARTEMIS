package logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utils.ConfigLogger;
import utils.Errors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XmlLogger extends Logger{
	public Document source;
	private String fileName;
	
	private Element root;
	
	public XmlLogger(String fileName_) {
		fileName = ConfigLogger.GENERATED_FILES_PATH+"xml/"+fileName_;
		
		createDocument();
		saveIntoFile();
	}
	
	public int createDocument() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			source = docBuilder.newDocument();
			
		} catch (ParserConfigurationException e) {
			GlobalLogger.error(Errors.ERROR_XML_CREATION+" Exception:Create document xml error");
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public int createRoot(String rootStr_) {
		root = source.createElement(rootStr_);
		source.appendChild(root);
		
		saveIntoFile();
		return 0;
	}
	
	public Element addChild(String childStr_, Element parent, String...params) {
		Element child = source.createElement(childStr_);
		if(params.length >= 1) {
			for(int i=0;i<params.length;i++) {
				child.setAttribute(params[i].split(":")[0], params[i].split(":")[1]);
			}
		}
		parent.appendChild(child);
		
		
		saveIntoFile();
		return child;
	}
	
	public int saveIntoFile() {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(source);
			StreamResult fileResult = new StreamResult(fileName);
			
			transformer.transform(domSource, fileResult);
			//GlobalLogger.log("Save xml file done");
		} catch (TransformerConfigurationException e) {
			GlobalLogger.error(Errors.ERROR_XML_SAVE+" Exception: cannot save XML File");
			e.printStackTrace();
		} catch (TransformerException e) {
			GlobalLogger.error(Errors.ERROR_XML_SAVE_TRANSFORMER+" Exception: transforming file during save");
			e.printStackTrace();
		}

		return 0;
	}
	
	public Element getRoot() {
		return root;
	}

}
