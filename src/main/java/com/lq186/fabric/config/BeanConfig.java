package com.lq186.fabric.config;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.lq186.fabric.sdk.FabricOrg;
import com.lq186.fabric.sdk.FabricStore;
import com.lq186.fabric.sdk.FabricUser;
import com.lq186.fabric.sdk.channel.FabricChannel;
import com.lq186.fabric.sdk.config.FabricConfig;
import com.lq186.fabric.util.PathUtils;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Resource
    private FabricConfig fabricConfig;

    @PostConstruct
    public void addFabricOrgUser() {
        FabricOrg sdkFabricOrg = fabricConfig.getSdkFabricOrg();
        FabricUser fabricUser = new FabricUser(sdkFabricOrg.getUsername(), sdkFabricOrg.getMspId(),
                PathUtils.userDefaultSKFilePath(fabricConfig.getCryptoConfigPath(), sdkFabricOrg.getUsername(),
                        sdkFabricOrg.getDomainName()),
                PathUtils.userDefaultCertificatePath(fabricConfig.getCryptoConfigPath(), sdkFabricOrg.getUsername(),
                        sdkFabricOrg.getDomainName()));
        sdkFabricOrg.addUser(fabricUser, fabricStore());
    }

    @Bean
    public HFClient getHFClient() throws Exception {
        try {
            HFClient client = HFClient.createNewInstance();
            client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
            return client;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Bean
    public FabricStore fabricStore() {
        return new FabricStore(new File(fabricConfig.getStorePath()));
    }

    @Bean
    public FabricChannel fabricChannel() throws Exception {
        FabricChannel fabricChannel = new FabricChannel(fabricConfig, getHFClient(),
                fabricConfig.getSdkFabricOrg().getUser());
        return fabricChannel;
    }

}
