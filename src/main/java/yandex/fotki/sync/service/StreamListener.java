package yandex.fotki.sync.service;

import java.io.File;

public class StreamListener {
	private long size = 0;
	private long uploadedSize = 0;
	
	private int dotsCount = 100;
	private long sizeForDot = 0;
	
	public StreamListener(File f){
		size = f.length();
		sizeForDot = size/dotsCount;
	}

	void counterChanged(int delta){
		 uploadedSize+=delta;
		 
		 if(uploadedSize>=sizeForDot){
			 System.out.print("."); 
			 uploadedSize=0;
		 }
	 }
}
