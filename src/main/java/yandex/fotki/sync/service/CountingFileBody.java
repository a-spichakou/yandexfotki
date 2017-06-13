package yandex.fotki.sync.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.http.entity.mime.content.FileBody;

final class CountingFileBody extends FileBody {

	private StreamListener streamListener;

	CountingFileBody(File file) {
		super(file);
		streamListener = new StreamListener(file);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		CountingOutputStream output = new CountingOutputStream(out) {
			@Override
			protected void beforeWrite(int n) {
				streamListener.counterChanged(n);
				super.beforeWrite(n);
			}
		};
		super.writeTo(output);
	}
}