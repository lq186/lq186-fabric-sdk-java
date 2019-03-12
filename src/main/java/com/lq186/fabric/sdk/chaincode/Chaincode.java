package com.lq186.fabric.sdk.chaincode;

import org.hyperledger.fabric.sdk.ChaincodeID;

public class Chaincode {

	private String name; // 链码名称

	private String path; // 链码路径

	private String version; // 链码版本

	private String policy; // 背书策略文件存放路径

	private String source; // 链码环境路径 /opt/gopath

	private ChaincodeID id; // 指定ID的链码

	private long proposalWaitTime = 200000L; // 单个提案请求的等待时间

	private Chaincode afterPropertySet() {
		if (null != name && null != path && null != version) {
			id = ChaincodeID.newBuilder().setName(name).setVersion(version).setPath(path).build();
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public Chaincode setName(String name) {
		this.name = name;
		afterPropertySet();
		return this;
	}

	public String getPath() {
		return path;
	}

	public Chaincode setPath(String path) {
		this.path = path;
		afterPropertySet();
		return this;
	}

	public String getVersion() {
		return version;
	}

	public Chaincode setVersion(String version) {
		this.version = version;
		afterPropertySet();
		return this;
	}

	public String getPolicy() {
		return policy;
	}

	public Chaincode setPolicy(String policy) {
		this.policy = policy;
		return this;
	}

	public ChaincodeID getId() {
		return id;
	}

	public Chaincode setId(ChaincodeID id) {
		this.id = id;
		return this;
	}

	public long getProposalWaitTime() {
		return proposalWaitTime;
	}

	public Chaincode setProposalWaitTime(long proposalWaitTime) {
		this.proposalWaitTime = proposalWaitTime;
		return this;
	}

	public String getSource() {
		return source;
	}

	public Chaincode setSource(String source) {
		this.source = source;
		return this;
	}

}
