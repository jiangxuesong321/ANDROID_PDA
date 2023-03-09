package com.android.pda.utils;

import com.android.pda.database.pojo.StorageLocation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 读取 Plant 和 StorageLocation 相关配置信息
 */
public class XmlUtils {
    public static List<StorageLocation> parse(InputStream inputStream) throws Exception {
        List<StorageLocation> storageLocations = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);

        Element rootElement = document.getDocumentElement();
        NodeList plantList = rootElement.getElementsByTagName("plant");

        for (int i = 0; i < plantList.getLength(); i++) {
            Element plantElement = (Element) plantList.item(i);
            String plantId = plantElement.getElementsByTagName("id").item(0).getTextContent();

            NodeList storageLocationList = plantElement.getElementsByTagName("storagelocation");

            for (int j = 0; j < storageLocationList.getLength(); j++) {
                Element storageLocationElement = (Element) storageLocationList.item(j);
                String storageLocationId = storageLocationElement.getElementsByTagName("id").item(0).getTextContent();
                String plantName = plantElement.getElementsByTagName("id").item(0).getTextContent();
                String storageLocationName = storageLocationElement.getElementsByTagName("storagelocationdesc").item(0).getTextContent();

                StorageLocation location = new StorageLocation(plantId, storageLocationId, plantName, storageLocationName);
                storageLocations.add(location);
            }
        }

        return storageLocations;
    }
}
