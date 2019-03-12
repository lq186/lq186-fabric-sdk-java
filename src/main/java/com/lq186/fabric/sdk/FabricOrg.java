package com.lq186.fabric.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lq186.fabric.sdk.peer.Peer;
import org.hyperledger.fabric.sdk.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lq186.fabric.sdk.orderer.Orderers;

public class FabricOrg {

	private static final Map<String, User> USER_MAP = new HashMap<>();

	private Logger logger = LoggerFactory.getLogger(getClass());

	private String name; // 名称

	private String mspId; // 会员ID

	private String domainName; // 域名

	private String username; // 执行SDK的用户名

	private Orderers orderers; // 排序节点

	private List<Peer> peers = new ArrayList<>(); // 组织下的所有节点

	private boolean useTLS; // 使用TLS

	public void addUser(FabricUser fabricUser, FabricStore fabricStore) {
		USER_MAP.put(fabricUser.getName(), fabricStore.getUser(fabricUser.getName(), fabricUser.getMspId(),
				fabricUser.getSkPath(), fabricUser.getCertificatePath()));
	}

	public User getUser() {
		return getUser(username);
	}

	public User getUser(String username) {
		return USER_MAP.get(username);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Orderers getOrderers() {
		return orderers;
	}

	public void setOrderers(Orderers orderers) {
		this.orderers = orderers;
	}

	public boolean isUseTLS() {
		return useTLS;
	}

	public void setUseTLS(boolean useTLS) {
		this.useTLS = useTLS;
	}

	public void setPeers(List<Peer> peers) {
		this.peers = peers;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMspId() {
		return mspId;
	}

	public void setMspId(String mspId) {
		this.mspId = mspId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public List<Peer> getPeers() {
		return peers;
	}

}
