package com.lq186.fabric.sdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.Enrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lq186.fabric.util.PathUtils;

public class FabricStore {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private String filePath;

	private final Map<String, FabricUser> fabricUsers = new HashMap<>();

	public FabricStore(File file) {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.error(String.format("Could not create file: %s", file.getAbsolutePath()), e);
			}
		}
		this.filePath = file.getAbsolutePath();
	}

	public void setValue(String name, String value) {
		Properties properties = loadProperties();
		try (OutputStream outStream = new FileOutputStream(filePath)) {
			properties.setProperty(name, value);
			properties.store(outStream, "");
		} catch (Exception e) {
			logger.error(String.format("Could not save the keyvalue store [%s -> %s].", name, value), e);
		}
	}

	public String getValue(String name) {
		return loadProperties().getProperty(name);
	}

	private Properties loadProperties() {
		Properties properties = new Properties();
		try (InputStream inStream = new FileInputStream(filePath)) {
			properties.load(inStream);
		} catch (Exception e) {
			logger.error(String.format("Could not load keyvalue store from file[%s]", filePath), e);
		}
		return properties;
	}

	public FabricUser getUser(String name, String mspId, String skPath, String certificatePath) {
		String keyForFabricStoreName = FabricUser.getKeyForFabricStoreName(name, skPath, certificatePath);
		FabricUser fabricUser = fabricUsers.get(keyForFabricStoreName);
		if (null != fabricUser) {
			return fabricUser;
		}
		fabricUser = (FabricUser) getStoreFromFile(keyForFabricStoreName);
		if (null != fabricUser) {
			fabricUsers.put(keyForFabricStoreName, fabricUser);
			return fabricUser;
		} else {
			fabricUser = new FabricUser(name, mspId, skPath, certificatePath);
			fabricUser.setFabricStore(this);
			fabricUser.setMspId(mspId);
			try (InputStream certInStream = PathUtils.path2InputStream(certificatePath);
					InputStream skInStream = PathUtils.path2InputStream(skPath)) {
				String certificate = new String(IOUtils.toByteArray(certInStream), "UTF-8");
				PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(skInStream));
				fabricUser.setEnrollment(new StoreEnrollement(privateKey, certificate));
				fabricUser.saveState();
				fabricUsers.put(keyForFabricStoreName, fabricUser);
			} catch (Exception e) {
				String message = "Could not load certificate and sk files.";
				logger.error(message, e);
				throw new RuntimeException(message);
			}
			return fabricUser;
		}
	}

	public Object getStoreFromFile(String keyForFabricStoreName) {
		String valueStr = getValue(keyForFabricStoreName);
		if (null != valueStr) {
			try (ByteArrayInputStream byteInStream = new ByteArrayInputStream(Hex.decode(valueStr))) {
				try (ObjectInputStream objectInStream = new ObjectInputStream(byteInStream)) {
					return objectInStream.readObject();
				}
			} catch (Exception e) {
				logger.error(String.format("Unable read object from file. object serialized: %s", valueStr), e);
			}
		}
		return null;
	}

	private PrivateKey getPrivateKeyFromBytes(byte[] data) throws IOException {
		try (final Reader pemReader = new StringReader(new String(data))) {
			final PrivateKeyInfo pemPair;
			try (PEMParser pemParser = new PEMParser(pemReader)) {
				pemPair = (PrivateKeyInfo) pemParser.readObject();
			}
			return new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider()).getPrivateKey(pemPair);
		}
	}

	static final class StoreEnrollement implements Enrollment, Serializable {

		private static final long serialVersionUID = 1L;

		private final PrivateKey privateKey;

		private final String certificate;

		public StoreEnrollement(PrivateKey privateKey, String certificate) {
			this.privateKey = privateKey;
			this.certificate = certificate;
		}

		@Override
		public PrivateKey getKey() {
			return privateKey;
		}

		@Override
		public String getCert() {
			return certificate;
		}

	}
}
