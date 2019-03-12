package com.lq186.fabric.util;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class FileUtils {

	public static void write2File(String fileName, String text) {

		try (FileOutputStream os = new FileOutputStream(fileName, true)) {

			try (OutputStreamWriter writer = new OutputStreamWriter(os, "UTF-8")) {
				writer.write(String.format("%s\n", text));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String findSkFilePath(String path) {
		try {
			PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
			Resource[] resources = patternResolver.getResources(path.replace("classpath:", "") + "/*");
			if (null != resources && resources.length > 0) {
				for (int i = 0; i < resources.length; ++i) {
					if (resources[i].getFilename().endsWith("_sk")) {
						return PathUtils.toPath(path, resources[i].getFilename());
					}
				}
			}
			throw new RuntimeException(String.format("No _sk file not found in path %s", path));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(String.format("Error found _sk file in path %s", path));
		}
	}

}
