package com.ctc.order.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class FileUtil {
	public static List<String> listFiles(String dir) throws IOException {
		// Comparator<Path> pathNameComparator =
		// Comparator.comparing(Path::getFileName);

		try (Stream<Path> stream = Files.list(Paths.get(dir))) {
			return stream.filter(file -> !Files.isDirectory(file))
					// .sorted(pathNameComparator)
					.map(Path::getFileName).map(Path::toString).collect(Collectors.toList());
		}
	}
	
	public static List<String> readFile(String filename) {
		List<String> ret = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			String line = "";
			while ((line = in.readLine()) != null) {
				ret.add(line);
			}
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return ret;
	}
}
