package com.aldaviva.instagram_rss.common.marshal;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.glassfish.jersey.internal.util.SimpleNamespaceResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils {

	private static final TransformerFactory TRANSFORMER_FACTORY;
	private static final XPathFactory XPATH_FACTORY;

	static {
		TRANSFORMER_FACTORY = TransformerFactory.newInstance();
		XPATH_FACTORY = XPathFactory.newInstance();
	}

	public static String documentToString(final Document document) {
		return documentToString(document, false);
	}

	public static String documentToString(final Document document, final boolean prettyPrint) {
		final StringWriter stringWriter = new StringWriter();
		try {
			final Transformer transformer;
			synchronized (TRANSFORMER_FACTORY) {
				transformer = TRANSFORMER_FACTORY.newTransformer();
			}
			if(prettyPrint) {
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			}
			transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
			return stringWriter.toString();
		} catch (final TransformerException e) {
			throw new RuntimeException(e);
		} catch (final TransformerFactoryConfigurationError e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Node> findNodesByXPath(final Node el, final String query, final boolean namespaceAware) {
		String namespaceUri = null;
		if(namespaceAware) {
			final Document doc = (el instanceof Document) ? (Document) el : el.getOwnerDocument();
			namespaceUri = doc.getDocumentElement().getAttribute("xmlns");
			if(namespaceUri.isEmpty()) {
				namespaceUri = doc.getDocumentElement().getNamespaceURI();
			}
		}
		final String namespacePrefix = (namespaceAware) ? "t" : null;
		return findNodesByXPath(el, query, namespaceAware, namespaceUri, namespacePrefix);
	}

	public static List<Node> findNodesByXPath(final Node el, final String query, final boolean namespaceAware,
	    final String namespaceUri, final String namespacePrefix) {
		final XPath xpath;
		synchronized (XPATH_FACTORY) {
			xpath = XPATH_FACTORY.newXPath();
		}

		if(namespaceAware && namespaceUri != null && namespacePrefix != null) {
			final SimpleNamespaceResolver simpleNamespaceResolver = new SimpleNamespaceResolver(namespacePrefix, namespaceUri);
			xpath.setNamespaceContext(simpleNamespaceResolver);
		}

		try {
			final NodeList nodeList = (NodeList) xpath.evaluate(query, el, XPathConstants.NODESET);
			final List<Node> results = new ArrayList<>(nodeList.getLength());
			for(int nodeIdx = 0; nodeIdx < nodeList.getLength(); nodeIdx++) {
				results.add(nodeList.item(nodeIdx));
			}
			return results;
		} catch (final XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
}
