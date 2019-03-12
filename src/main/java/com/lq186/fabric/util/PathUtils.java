package com.lq186.fabric.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class PathUtils {

	public static final String getClassPath() {
		String classPath = PathUtils.class.getResource("/").getFile();
		int index = classPath.indexOf(":");
		if (index > 0) {
			classPath = classPath.substring(index + 1);
		}
		return classPath;
	}

	public static final String toPath(String... paths) {
		return String.join(File.separator, paths);
	}

	public static final String userDefaultSKFilePath(String cryptoConfigPath, String username, String orgDomainName) {
		String path = toPath(cryptoConfigPath, "peerOrganizations", orgDomainName, "users",
				String.format("%s@%s", username, orgDomainName), "msp", "keystore");
		return FileUtils.findSkFilePath(path);
	}

	public static final String userDefaultCertificatePath(String cryptoConfigPath, String username,
			String orgDomainName) {
		return toPath(cryptoConfigPath, "peerOrganizations", orgDomainName, "users",
				String.format("%s@%s", username, orgDomainName), "msp", "signcerts",
				String.format("%s@%s-cert.pem", username, orgDomainName));
	}

	public static final InputStream path2InputStream(String path) throws IOException {
		PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = patternResolver.getResources(path.replace("classpath:", ""));
		return resources[0].getInputStream();
	}
}
