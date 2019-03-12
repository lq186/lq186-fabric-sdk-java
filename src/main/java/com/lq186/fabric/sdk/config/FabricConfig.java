package com.lq186.fabric.sdk.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.lq186.fabric.sdk.FabricOrg;
import com.lq186.fabric.sdk.chaincode.Chaincode;
import com.lq186.fabric.sdk.listener.FabricBlockListener;
import com.lq186.fabric.util.PathUtils;

@Configuration
@ConfigurationProperties(prefix = "fabric")
public class FabricConfig {

	private static final Map<String, FabricOrg> ORG_MAP = new HashMap<>();

	private static final String FABRIC_PATH = "classpath:fabric";

	private String storePath;

	private String channelName;

	private String sdkOrg;

	private Chaincode chaincode;

	private List<FabricOrg> orgs;

	private String chanelArtifactsPath; // 默认路径：/xxx/WEB-INF/classes/fabric/channel-artifacts/

	private String cryptoConfigPath; // 默认路径： /xxx/WEB-INF/classes/fabric/crypto-config/

	private boolean registerEvent = false;

	private Class<? extends FabricBlockListener> blockListener;

	public FabricConfig() {
		chanelArtifactsPath = PathUtils.toPath(FABRIC_PATH, "channel-artifacts");
		cryptoConfigPath = PathUtils.toPath(FABRIC_PATH, "crypto-config");
	}

	public Chaincode getChaincode() {
		return chaincode;
	}

	public void setChaincode(Chaincode chaincode) {
		this.chaincode = chaincode;
	}

	public String getChanelArtifactsPath() {
		return chanelArtifactsPath;
	}

	public void setChanelArtifactsPath(String chanelArtifactsPath) {
		this.chanelArtifactsPath = chanelArtifactsPath;
	}

	public String getCryptoConfigPath() {
		return cryptoConfigPath;
	}

	public void setCryptoConfigPath(String cryptoConfigPath) {
		this.cryptoConfigPath = cryptoConfigPath;
	}

	public boolean isRegisterEvent() {
		return registerEvent;
	}

	public void setRegisterEvent(boolean registerEvent) {
		this.registerEvent = registerEvent;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	public String getChannelName() {
		return channelName;
	}

	public List<FabricOrg> getOrgs() {
		return orgs;
	}

	public void setOrgs(List<FabricOrg> orgs) {
		this.orgs = orgs;
		if (null != orgs) {
			orgs.forEach(org -> {
				ORG_MAP.put(org.getName(), org);
			});
		}
	}

	public FabricOrg getSdkFabricOrg() {
		return ORG_MAP.get(sdkOrg);
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getSdkOrg() {
		return sdkOrg;
	}

	public void setSdkOrg(String sdkOrg) {
		this.sdkOrg = sdkOrg;
	}

	public Class<? extends FabricBlockListener> getBlockListener() {
		return blockListener;
	}

	public void setBlockListener(Class<? extends FabricBlockListener> blockListener) {
		this.blockListener = blockListener;
	}

}
