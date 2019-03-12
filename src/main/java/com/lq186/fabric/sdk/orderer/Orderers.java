package com.lq186.fabric.sdk.orderer;

import java.util.ArrayList;
import java.util.List;

public class Orderers {

	private String domainName; // 根域名

	private List<Orderer> orderers = new ArrayList<>(); // 排序节点集合

	public void addOrderer(Orderer orderer) {
		orderers.add(orderer);
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public List<Orderer> getOrderers() {
		return orderers;
	}

	public void setOrderers(List<Orderer> orderers) {
		this.orderers = orderers;
	}

}
