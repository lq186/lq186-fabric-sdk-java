package com.lq186.fabric.bean;

import java.io.Serializable;

public class FabricChaincode implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String version;

	private String path;

	public String getName() {
		return name;
	}

	public FabricChaincode setName(String name) {
		this.name = name;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public FabricChaincode setVersion(String version) {
		this.version = version;
		return this;
	}

	public String getPath() {
		return path;
	}

	public FabricChaincode setPath(String path) {
		this.path = path;
		return this;
	}

}
