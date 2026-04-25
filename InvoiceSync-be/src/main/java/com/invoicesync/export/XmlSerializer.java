package com.invoicesync.export;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class XmlSerializer {

  public byte[] serialize(final Document template) {
    removeWhitespaceNodes(template);
    try {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.transform(new DOMSource(template), new StreamResult(out));
      return out.toByteArray();
    } catch (TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  //TODO make this private when invoices will be transferred to use populator
  public void removeWhitespaceNodes(final Node node) {
    final NodeList children = node.getChildNodes();
    for (int i = children.getLength() - 1; i >= 0; i--) {
      final Node child = children.item(i);
      if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
        node.removeChild(child);
      } else if (child.hasChildNodes()) {
        removeWhitespaceNodes(child);
      }
    }
  }

}
