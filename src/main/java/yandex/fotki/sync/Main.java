package yandex.fotki.sync;

import yandex.fotki.sync.service.ManageService;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args==null || args.length<4)
		{
			System.out.println("Usage:");
			System.out.println("username(as in URL) album, token dir");
			return;
		}
		final ManageService service = new ManageService();
		
		String username=args[0];
		String album=args[1];
		String token=args[2];
		String dir=args[3];
		
		System.out.println("username: " + username);
		System.out.println("album: " + album);
		System.out.println("token: " + token);
		System.out.println("dir: " + dir);
		
		System.out.println("Starting ... ");
		service.sync(username, album, token, dir);
		System.out.println("Finished ... ");
	}

}
