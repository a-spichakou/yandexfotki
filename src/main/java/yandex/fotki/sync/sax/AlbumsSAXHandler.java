package yandex.fotki.sync.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import yandex.fotki.sync.domain.Album;

public class AlbumsSAXHandler extends DefaultHandler {

	private List<Album> albums = new ArrayList<Album>();
	private Album tmpAlbum = new Album();
	private String tmpValue;
	private String nextUrl;

	@Override
	public void startElement(String s, String s1, String elementName,
			Attributes attributes) throws SAXException {
		if ("entry".equalsIgnoreCase(elementName)) {
			tmpAlbum = new Album();
		}
		if ("link".equalsIgnoreCase(elementName)) {
			final String rel = attributes.getValue("rel");
			if ("next".equalsIgnoreCase(rel)) {
				nextUrl = attributes.getValue("href");
			}
			if ("photos".equalsIgnoreCase(rel)) {
				tmpAlbum.setPhotosLink(attributes.getValue("href"));
			}
		}
	}

	@Override
	public void endElement(String s, String s1, String element)
			throws SAXException {
		if ("id".equalsIgnoreCase(element)) {
			tmpAlbum.setId(tmpValue);
		}
		if ("title".equalsIgnoreCase(element)) {
			tmpAlbum.setName(tmpValue);
		}
		if ("entry".equalsIgnoreCase(element)) {
			albums.add(tmpAlbum);
		}
	}

	@Override
	public void characters(char[] ac, int i, int j) throws SAXException {
		tmpValue = new String(ac, i, j);
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public String getNextUrl() {
		return nextUrl;
	}
}
