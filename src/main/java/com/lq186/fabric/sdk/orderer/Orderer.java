package com.lq186.fabric.sdk.orderer;

public class Orderer {

	private String name; // 域名

	private String location; // 访问地址

	private String serverCrtPath; // TLS证书

	public static final Orderer newInstance(String name, String location, String serverCrtPath) {
		Orderer orderer = new Orderer();
		orderer.name = name;
		orderer.location = location;
		orderer.serverCrtPath = serverCrtPath;
		return orderer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getServerCrtPath() {
		return serverCrtPath;
	}

	public void setServerCrtPath(String serverCrtPath) {
		this.serverCrtPath = serverCrtPath;
	}

}
