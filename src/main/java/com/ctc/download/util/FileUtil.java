package com.ctc.download.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
}
