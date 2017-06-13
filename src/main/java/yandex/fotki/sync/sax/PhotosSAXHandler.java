package yandex.fotki.sync.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import yandex.fotki.sync.domain.Photo;

public class PhotosSAXHandler extends DefaultHandler {

	private List<Photo> photo = new ArrayList<Photo>();
	private Photo tmpPhoto = new Photo();
	private String tmpValue;
	private String nextUrl;
	private String tmpUrl;

	@Override
	public void startElement(String s, String s1, String elementName,
			Attributes attributes) throws SAXException {
		if ("entry".equalsIgnoreCase(elementName)) {
			tmpPhoto = new Photo();
		}
		if ("link".equalsIgnoreCase(elementName)) {
			final String rel = attributes.getValue("rel"); 
			if ("next".equalsIgnoreCase(rel)) {
				nextUrl = attributes.getValue("href");
			}
			if ("edit".equalsIgnoreCase(rel)) {
				tmpPhoto.setUrl(attributes.getValue("href"));
			}
		}
	}

	@Override
	public void endElement(String s, String s1, String element)
			throws SAXException {
		if ("id".equalsIgnoreCase(element)) {
			tmpPhoto.setId(tmpValue);
		}
		if ("title".equalsIgnoreCase(element)) {
			tmpPhoto.setName(tmpValue);
		}
		if ("entry".equalsIgnoreCase(element)) {
			photo.add(tmpPhoto);
		}
	}

	@Override
	public void characters(char[] ac, int i, int j) throws SAXException {
		tmpValue = new String(ac, i, j);
	}

	public List<Photo> getAlbums() {
		return photo;
	}

	public String getNextUrl() {
		return nextUrl;
	}
}
