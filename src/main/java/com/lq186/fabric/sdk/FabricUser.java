package com.lq186.fabric.sdk;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.util.encoders.Hex;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricUser implements User, Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private String name; // 用户名

	private String skPath; // 节点签名密钥的PEM文件_sk

	private String certificatePath; // 节点的X509证书的PEM文件

	private Set<String> roles; // 角色

	private String account; // 账户

	private String affiliation; // 从属联盟

	private String organization; // 从属组织

	private String enrollmentSecret; // 操作密码

	private String mspId; // 会员ID

	private Enrollment enrollment; // 注册登记

	private transient FabricStore fabricStore; // 存储

	private String keyForFabricStoreName;
	
	public FabricUser(String name, String mspId, String skPath, String certificatePath) {
		this.name = name;
		this.mspId = mspId;
		this.skPath = skPath;
		this.certificatePath = certificatePath;
		this.keyForFabricStoreName = getKeyForFabricStoreName(name, skPath, certificatePath);
	}

	@Override
	public String getName() {
		return name;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
		saveState();
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	public void setAccount(String account) {
		this.account = account;
		saveState();
	}

	@Override
	public String getAccount() {
		return account;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
		saveState();
	}

	@Override
	public String getAffiliation() {
		return affiliation;
	}

	public void setEnrollment(Enrollment enrollment) {
		this.enrollment = enrollment;
		saveState();
	}

	@Override
	public Enrollment getEnrollment() {
		return enrollment;
	}

	public String getEnrollmentSecret() {
		return enrollmentSecret;
	}

	public void setEnrollmentSecret(String enrollmentSecret) {
		this.enrollmentSecret = enrollmentSecret;
		saveState();
	}

	public void setMspId(String mspId) {
		this.mspId = mspId;
		saveState();
	}

	@Override
	public String getMspId() {
		return mspId;
	}

	public boolean isRegistered() {
		return null != enrollmentSecret && enrollmentSecret.length() > 0;
	}

	public boolean isEnrolled() {
		return null != enrollment;
	}

	public void setFabricStore(FabricStore fabricStore) {
		this.fabricStore = fabricStore;
	}

	public String getSkPath() {
		return skPath;
	}

	public String getCertificatePath() {
		return certificatePath;
	}

	/**
	 * 存储当前的用户状态
	 */
	protected void saveState() {
		try (ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream()) {
			try (ObjectOutputStream objOutStream = new ObjectOutputStream(byteOutStream)) {
				objOutStream.writeObject(this);
				fabricStore.setValue(keyForFabricStoreName, Hex.toHexString(byteOutStream.toByteArray()));
			} catch (Exception e) {
				logger.error(String.format("Could not save user state. name[%s], org[%s]", name, organization), e);
			}
		} catch (Exception e) {
			logger.error(String.format("Could not save user state. name[%s], org[%s]", name, organization), e);
		}
	}

	public static final String getKeyForFabricStoreName(String name, String skPath, String certificatePath) {
		return String.format("sinouser.%s", DigestUtils.sha256Hex(String.join(":", name, skPath, certificatePath)));
	}
}
