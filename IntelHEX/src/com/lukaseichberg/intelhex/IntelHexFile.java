package com.lukaseichberg.intelhex;

public class IntelHexFile {

	private String filePath;
	private byte[] data;
	
	IntelHexFile(String filePath, byte[] data) {
		this.filePath = filePath;
		this.data = data;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public byte[] getData() {
		return data;
	}
}
