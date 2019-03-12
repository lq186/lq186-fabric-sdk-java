package com.lq186.fabric.sdk.listener;

import com.lq186.fabric.bean.FabricBlockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOperationBlockListener implements FabricBlockListener {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void received(FabricBlockInfo fabricBlockInfo) {
		logger.info(String.format("revived block -> hash: %s", fabricBlockInfo.getCalculatedBlockHash()));
	}

}
