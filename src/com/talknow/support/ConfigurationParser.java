package com.talknow.support;

import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class ConfigurationParser{
	
	private final static ConfigurationParser cp = new ConfigurationParser();
	
	public static String getConfIP() {
		return cp.server_ip;
	}
	
	public static int getConfPort() {
		return cp.server_port;
	}
	
	private final String URL = "configuration.xml";
	private String server_ip = null;
	private int server_port = 0;
	
	private ConfigurationParser() {
		try {
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(URL);
			NodeList list = document.getChildNodes();
			Node configuration = list.item(1);
			NodeList childlist = configuration.getChildNodes();
			server_ip = childlist.item(1).getTextContent();
			server_port = Integer.parseInt(childlist.item(3).getTextContent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
