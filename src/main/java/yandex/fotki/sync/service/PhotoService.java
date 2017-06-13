package yandex.fotki.sync.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.SAXException;

import yandex.fotki.sync.domain.Album;
import yandex.fotki.sync.domain.Photo;
import yandex.fotki.sync.sax.PhotosSAXHandler;

public class PhotoService {

	private DefaultHttpClient httpclient;
	
	public PhotoService() {
		httpclient = new DefaultHttpClient();
	}
	
	public List<Photo> getPhotos(Album album) throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		final String photosLink = album.getPhotosLink();
		final List<Photo> parse2photos = parse2photos(photosLink);
		return parse2photos;
	}
	
	public void uploadPhoto(File file, Album album) throws ClientProtocolException, IOException
	{
		final HttpPost method = new HttpPost(album.getPhotosLink());
		final HttpParams params = new BasicHttpParams();		
		method.setParams(params);
				
		MultipartEntity entity = new MultipartEntity();
		FileBody fileBody = new CountingFileBody(file);
		
		entity.addPart("image", fileBody);
		entity.addPart("access", new StringBody("private"));		
		
		method.setEntity(entity);
		method.addHeader(RequestHeaderFactory.getHeader());
		HttpResponse response = httpclient.execute(method);
		method.releaseConnection();
		System.out.println(response.getStatusLine());
	}
	
	public void deletePhoto(Photo photo) throws ClientProtocolException, IOException{
		final HttpDelete method = new HttpDelete(photo.getUrl());
		final HttpParams params = new BasicHttpParams();		
		method.setParams(params);
				
		method.addHeader(RequestHeaderFactory.getHeader());
		HttpResponse response = httpclient.execute(method);
		method.releaseConnection();
		System.out.println(response.getStatusLine());
	}

	private List<Photo> parse2photos(String url)
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

		PhotosSAXHandler photosSAXHandler = new PhotosSAXHandler();

		final InputStream content = entity.getContent();

		parser.parse(content, photosSAXHandler);

		final List<Photo> photos = photosSAXHandler.getAlbums();
		final String nextUrl = photosSAXHandler.getNextUrl();
		if (nextUrl != null) {
			final List<Photo> parse2aphotos = parse2photos(nextUrl);
			photos.addAll(parse2aphotos);
		}
		
		httpget.releaseConnection();
		return photos;
	}
}
