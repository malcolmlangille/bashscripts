// File: src/main/java/com/example/XmlToPojo.java
package com.example;

import com.example.generated.YourRootClass;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;

public class XmlToPojo {
    public static void main(String[] args) {
        // Path to your XML example file
        File xmlFile = new File("src/main/resources/your-example.xml");

        // Create an XmlMapper instance for deserialization
        XmlMapper xmlMapper = new XmlMapper();
        try {
            // Deserialize XML into a Java object
            YourRootClass root = xmlMapper.readValue(xmlFile, YourRootClass.class);
            System.out.println("Deserialized Java object:");
            System.out.println(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
