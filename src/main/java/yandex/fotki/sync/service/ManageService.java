package yandex.fotki.sync.service;

import java.io.File;
import java.util.List;

import yandex.fotki.sync.domain.Album;
import yandex.fotki.sync.domain.Photo;

public class ManageService {
	private AlbumService albumService = new AlbumService();
	private PhotoService photoService = new PhotoService();

	public void sync(String username, String album, String token, String dir) {

		// Set token
		RequestHeaderFactory.init(token);

		if (dir == null) {
			System.out.println("Dir is not set - exiting");
			return;
		}

		final File f = new File(dir);
		if (!f.isDirectory()) {
			System.out.println("Given path is not dir - exiting");
			return;
		}

		final Credentials credentials = new Credentials(username, null);
		Album album2 = null;
		try {
			album2 = albumService.getAlbum(credentials, album);
		} catch (Exception e) {
			System.out.println("Something went wrong");
			e.printStackTrace();
			return;
		}

		if (album2 == null) {
			System.out.println("Unable to find album");
			return;
		}
		List<Photo> photos = null;
		try {
			photos = photoService.getPhotos(album2);
		} catch (Exception e) {
			System.out.println("Something went wrong");
			e.printStackTrace();
			return;
		}

		final File[] listFiles = f.listFiles();
		for (File file : listFiles) {
			boolean loaded = false;
			for (Photo photo : photos) {
				final String name = file.getName();
				if (name.equalsIgnoreCase(photo.getName())) {
					loaded = true;
					System.out.println("Skiping " + name);
					break;
				}

			}
			if (!loaded) {
				System.out.println("Uploading file: " + file.getName() + " to album "
						+ album2.getName());
				System.out.println("Started: " + System.currentTimeMillis());
				try {
					photoService.uploadPhoto(file, album2);
				} catch (Exception e) {
					System.out.println("Something went wrong");
					e.printStackTrace();
				}
				System.out.println("Finished: " + System.currentTimeMillis());
			}
		}

	}

}
