package com.zx.fileupload.utils;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;

public class XmlPaser {
	private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 

	public static Document xmlDomParse(File file) {
		Document document=null;
		  DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
			document = builder.parse(file); 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return document;
	}
}
