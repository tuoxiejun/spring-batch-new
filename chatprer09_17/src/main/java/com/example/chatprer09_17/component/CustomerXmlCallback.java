package com.example.chatprer09_17.component;

import org.springframework.batch.item.xml.StaxWriterCallback;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

@Component
public class CustomerXmlCallback implements StaxWriterCallback {
    @Override
    public void write(XMLEventWriter writer) throws IOException {
        XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
        try {
            writer.add(xmlEventFactory.createStartElement("ns", "https://www.benson.com", "identification"));
            writer.add(xmlEventFactory.createStartElement("", "", "author"));
            writer.add(xmlEventFactory.createAttribute("name",  "Benson"));
            writer.add(xmlEventFactory.createEndElement("", "", "author"));
            writer.add(xmlEventFactory.createEndElement("ns", "https://www.benson.com", "identification"));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
}
