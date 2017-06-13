package yandex.fotki.sync.service;

import java.io.File;

public class StreamListener {
	private long size = 0;
	private long uploadedSize = 0;
	
	private int dotsCount = 100;
	private long sizeForDot = 0;
	private long totalUploaded = 0;
	
	public StreamListener(File f){
		size = f.length();
		sizeForDot = size/dotsCount;
	}

	void counterChanged(int delta){
		 uploadedSize+=delta;
		 totalUploaded+=delta;
		 
		 if(uploadedSize>=sizeForDot){
			 System.out.print("|"); 
			 dotsCount--;
			 uploadedSize=0;
		 }else if(size-totalUploaded<=sizeForDot){
			while(dotsCount>0){
				System.out.print("|");
				dotsCount--;
			}
		 }
	 }
}
