package com.lq186.fabric.sdk.listener;

import com.lq186.fabric.bean.FabricBlockInfo;

@FunctionalInterface
public interface FabricBlockListener {

	void received(FabricBlockInfo fabricBlockInfo);

}
