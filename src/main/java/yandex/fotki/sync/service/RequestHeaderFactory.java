package yandex.fotki.sync.service;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class RequestHeaderFactory {

	private static String token = "token";

	public static void init(String newToken) {
		if (token != null) {
			token = newToken;
		}
	}

	public static Header getHeader() {
		Header header = new BasicHeader("Authorization", "OAuth " + token);
		return header;
	}
}
