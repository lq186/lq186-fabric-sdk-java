package com.lq186.fabric.sdk.peer;

public class Peer {

	private String name; // 域名 

	private String eventHubName; // 事件域名

	private String location; // 节点地址 grpc://127.0.0.1:7051

	private String eventHubLocation; // 事件节点地址 grpc://127.0.0.1:7053

	private String caLocation; // 证书服务地址 http://127.0.0.1:7054

	private boolean addEventHub = false; // 增加事件处理

	private String serverCrtPath; // TLS证书

	public static final Peer newInstance(String name, String eventHubName, String location, String eventHubLocation,
			String caLocation, boolean addEventHub, String serverCrtPath) {
		Peer peer = new Peer();
		peer.name = name;
		peer.location = location;
		peer.eventHubName = eventHubName;
		peer.eventHubLocation = eventHubLocation;
		peer.caLocation = caLocation;
		peer.addEventHub = addEventHub;
		peer.serverCrtPath = serverCrtPath;
		return peer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEventHubName() {
		return eventHubName;
	}

	public void setEventHubName(String eventHubName) {
		this.eventHubName = eventHubName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEventHubLocation() {
		return eventHubLocation;
	}

	public void setEventHubLocation(String eventHubLocation) {
		this.eventHubLocation = eventHubLocation;
	}

	public String getCaLocation() {
		return caLocation;
	}

	public void setCaLocation(String caLocation) {
		this.caLocation = caLocation;
	}

	public boolean isAddEventHub() {
		return addEventHub;
	}

	public void setAddEventHub(boolean addEventHub) {
		this.addEventHub = addEventHub;
	}

	public String getServerCrtPath() {
		return serverCrtPath;
	}

	public void setServerCrtPath(String serverCrtPath) {
		this.serverCrtPath = serverCrtPath;
	}

}
