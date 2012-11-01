package yandex.fotki.sync.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.xml.sax.SAXException;

import yandex.fotki.sync.domain.Album;
import yandex.fotki.sync.sax.AlbumsSAXHandler;

public class AlbumService {

	private DefaultHttpClient httpclient;
	private List<Album> albums;

	public AlbumService() {
		httpclient = new DefaultHttpClient();
	}

	public List<Album> getAlbums(Credentials credentials) throws IOException,
			IllegalStateException, SAXException, ParserConfigurationException {

		albums = parse2albums("http://api-fotki.yandex.ru/api/users/"
				+ credentials.getUsername() + "/albums/");

		return albums;
	}

	public Album getAlbum(Credentials credentials, String name)
			throws IllegalStateException, IOException, SAXException,
			ParserConfigurationException {
		if (albums == null) {
			getAlbums(credentials);
		}
		for (Album album : albums) {
			if (album.getName().equalsIgnoreCase(name)) {
				return album;
			}
		}
		return null;
	}

	private List<Album> parse2albums(String url)
			throws ClientProtocolException, IOException,
			ParserConfigurationException, SAXException {
		final HttpGet httpget = new HttpGet(url);
		final BasicHttpParams params = new BasicHttpParams();
		httpget.setParams(params);

		httpget.addHeader(RequestHeaderFactory.getHeader());

		final HttpResponse response = httpclient.execute(httpget);

		final HttpEntity entity = response.getEntity();

		final SAXParserFactory factory = SAXParserFactory.newInstance();
		final SAXParser parser = factory.newSAXParser();

		AlbumsSAXHandler albumsSAXHandler = new AlbumsSAXHandler();

		final InputStream content = entity.getContent();

		parser.parse(content, albumsSAXHandler);

		final List<Album> albums = albumsSAXHandler.getAlbums();
		final String nextUrl = albumsSAXHandler.getNextUrl();
		if (nextUrl != null) {
			final List<Album> parse2albums = parse2albums(nextUrl);
			albums.addAll(parse2albums);
		}

		httpget.releaseConnection();
		return albums;
	}

}
