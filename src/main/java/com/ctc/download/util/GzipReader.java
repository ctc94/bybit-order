package com.ctc.download.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;

public class GzipReader {
	private static final Logger log = LoggerFactory.getLogger(GzipReader.class);

	public static List<Map<String, String>> readGzip_BufferedReader(String filename) {
		// 반환용 리스트
		List<Map<String, String>> ret = new ArrayList<Map<String, String>>();
		String columnList[] = null;
		try {
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filename))));
			String line = "";
			int i = 0;
			while ((line = in.readLine()) != null) {
				line = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, line);
				String array[] = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, line).split(",");
				if (i++ == 0) {// 첫째행은 컬럼명을 가져온다.
					columnList = array;
					continue;
				} else {// 나머니 데이터는 키값을 매핑시킨 맵데이터로 넣는다.
					Map<String, String> m = new HashMap<String, String>();
					int j = 0;
					for (String v : array) {
						m.put(columnList[j++], v);
					}
					ret.add(m);
				} 
			}
			in.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return ret;
	}

	public static void readGzip_scanner(String filename) {
		try {

			Scanner scanner = new Scanner(new GZIPInputStream(new FileInputStream(filename)));
			int line = 0;
			while (scanner.hasNextLine()) {
				log.info("[" + filename + "] " + (line++) + " : " + scanner.next());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
