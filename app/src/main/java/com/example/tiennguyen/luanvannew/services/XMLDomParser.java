package com.example.tiennguyen.luanvannew.services;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Quyen Hua on 11/22/2017.
 */

public class XMLDomParser {
    public Document getDocument(String xml)
    {
        Document document = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder db = factory.newDocumentBuilder();
            //InputSource is = new InputSource();
            //is.setCharacterStream(new StringReader(xml));
            //is.setEncoding("UTF-8");
            byte[] bytes = Charset.forName("UTF-8").encode(xml).array();
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            document = db.parse(is);

        }catch(ParserConfigurationException e)
        {
            Log.e("Error: ", e.getMessage(), e);
            return null;
        }
        catch (SAXException e) {
            Log.e("Error: ", e.getMessage(), e);
            return null;
        }
        catch(IOException e){
            Log.e("Error: ", e.getMessage(), e);
            return null;
        }
        return document;
    }
    public String getValue(Element item, String name)
    {
        NodeList nodes = item.getElementsByTagName(name);
        return this.getTextNodeValue(nodes.item(0));
    }

    public String getValue(org.w3c.dom.Document doc, String nameNode){
        NodeList nodeList = doc.getElementsByTagName(nameNode);
        String name = nodeList.item(0).getTextContent();
        return  name;
    }

    public String getValueData(Element item, String nameNode){
        NodeList nodeList = item.getElementsByTagName(nameNode);
        String name = nodeList.item(0).getTextContent();
        return  name;
    }

    private final String getTextNodeValue(Node elem) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
}
