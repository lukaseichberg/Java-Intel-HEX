package com.lukaseichberg.intelhex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IntelHexLoader {

	private static final int DATA_RECORD = 0;
	private static final int EOF_RECORD = 1;
	private static final int ESA_RECORD = 2;
	private static final int SSA_RECORD = 3;
	private static final int ELA_RECORD = 4;
	private static final int SLA_RECORD = 5;
	
	private static Map<Integer, Byte> bytes;
	private static int maxAddress;
	
	public static IntelHexFile loadFile(String filePath) throws IOException {
		File file = new File(filePath);
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		bytes = new HashMap<>();
		maxAddress = 0;
		
		String line;
		while ((line = br.readLine()) != null) {
			loadLine(line);
		}
		
		byte[] data = new byte[maxAddress + 1];
		for (int i = 0; i < maxAddress; i++) {
			if (bytes.containsKey(i)) {
				data[i] = bytes.get(i);
			} else {
				data[i] = 0;
			}
		}
		
		return new IntelHexFile(file.getAbsolutePath(), data);
	}
	
	private static void loadLine(String line) throws IOException {
		if (line.startsWith(":")) {
			byte[] recordBytes = getAllBytes(line);
			int length = recordBytes[0] & 0xFF;
			int offset = ((recordBytes[1] & 0xFF) << 8) | (recordBytes[2] & 0xFF);
			int rectyp = recordBytes[3] & 0xFF;
			int chksum = (int) recordBytes[4 + length] & 0xFF;
			
			byte[] data = new byte[length];
			for (int i = 0; i < length; i++) {
				data[i] = recordBytes[4 + i];
			}
			
			// test checksum
			int sum = 0;
			for (int i = 0; i < recordBytes.length - 1; i++) {
				sum += (int) recordBytes[i] & 0xFF;
			}
			sum = -sum & 0xFF;
			if (sum != chksum) {
				throw(new IOException("Invalid checksum."));
			}
			
			switch (rectyp) {
				case DATA_RECORD:
					loadDataRecord(offset, data);
					break;
				case EOF_RECORD:
					break;
				case ESA_RECORD:
					loadESARecord();
					break;
				case SSA_RECORD:
					break;
				case ELA_RECORD:
					break;
				case SLA_RECORD:
					break;
				default:
					throw(new IOException("Invalid record type."));
			}
			
			
			
		} else {
			throw(new IOException("Invalid file format."));
		}
	}
	
	private static byte[] getAllBytes(String data) {
		int byteCount = (data.length() - 1) / 2;
		byte[] bytes = new byte[byteCount];
		for (int i = 0; i < byteCount; i++) {
			String byteHEX = data.substring(i * 2 + 1, (i + 1) * 2 + 1);
			byte value = (byte) Integer.parseInt(byteHEX, 16);
			bytes[i] = value;
		}
		return bytes;
	}
	
	private static void loadDataRecord(int offset, byte[] data) {
		for (int i = 0; i < data.length; i++) {
			int address = offset + i;
			if (address > maxAddress) {
				maxAddress = address;
			}
			bytes.put(address, data[i]);
		}
	}
	
	private static void loadESARecord() {
		
	}
	
}
