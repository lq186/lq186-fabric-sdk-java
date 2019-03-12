package com.lq186.fabric.controller.request;

import java.util.List;

import com.lq186.fabric.sdk.chaincode.Chaincode;

public class ChaincodeRequest {

	private String name;

	private String version = "v0";

	private String path = "github.com/example_cc";

	private List<String> args;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Chaincode getChaincode() {
		Chaincode chaincode = new Chaincode();
		chaincode.setName(name);
		chaincode.setVersion(version);
		chaincode.setPath(path);
		return chaincode;
	}
}
