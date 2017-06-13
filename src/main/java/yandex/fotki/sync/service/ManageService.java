package yandex.fotki.sync.service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
		
		cleanDublicates(photos);

		final File[] listFiles = f.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith("jpg");
			}
		});
		upload(album2, photos, listFiles);

	}

	private void upload(Album album2, List<Photo> photos, final File[] listFiles) {
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
				Date startDate = new Date();
				System.out.println("Started: " + startDate);
				try {
					photoService.uploadPhoto(file, album2);
				} catch (Exception e) {
					System.out.println("Something went wrong");
					e.printStackTrace();
				}
				Date endDate = new Date();
				System.out.println("Finished: " + endDate + " took: " + (endDate.getTime()-startDate.getTime())/1000 + "s.");
				
			}
		}
	}

	private void cleanDublicates(List<Photo> photos) {
		System.out.println("Cleaning dublicates");
		final Map<String, Photo> dublicates = new HashMap<String, Photo>();
		photos.stream().forEach(new Consumer<Photo>() {

			@Override
			public void accept(Photo t) {
				if(dublicates.put(t.getName(), t)!=null){
					try {
						photoService.deletePhoto(t);
						System.out.println("Photo " + t.getName() + " deleted.");
					} catch (IOException e) {
						System.out.println("Failed to delete photo " + t.getName());
					}
				};
			}
			
		});
	}

}
