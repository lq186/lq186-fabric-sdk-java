package com.lq186.fabric.sdk.channel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.lq186.fabric.sdk.FabricResult;
import com.lq186.fabric.sdk.ResultCode;
import com.lq186.fabric.sdk.chaincode.Chaincode;
import com.lq186.fabric.sdk.config.FabricConfig;
import com.lq186.fabric.sdk.listener.FabricBlockListener;
import com.lq186.fabric.sdk.orderer.Orderers;
import com.lq186.fabric.sdk.peer.Peer;
import com.lq186.fabric.util.PathUtils;
import com.lq186.fabric.util.Utils;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.protos.common.Common.Envelope;
import org.hyperledger.fabric.protos.common.Common.Payload;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.protos.orderer.Kafka.KafkaMessage;
import org.hyperledger.fabric.protos.orderer.Kafka.KafkaMessageRegular;
import org.hyperledger.fabric.protos.peer.FabricProposal;
import org.hyperledger.fabric.protos.peer.FabricProposalResponse;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeResponse;
import org.hyperledger.fabric.sdk.BlockInfo.EnvelopeInfo;
import org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType;
import org.hyperledger.fabric.sdk.BlockInfo.TransactionEnvelopeInfo;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.UpgradeProposalRequest;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.transaction.TransactionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.lq186.fabric.bean.FabricBlockInfo;
import com.lq186.fabric.bean.FabricBlockchainInfo;
import com.lq186.fabric.bean.FabricChaincode;
import com.lq186.fabric.bean.FabricEndorserInfo;
import com.lq186.fabric.bean.FabricEnvelopeInfo;
import com.lq186.fabric.bean.FabricIdentitiesInfo;
import com.lq186.fabric.bean.FabricNsRwsetInfo;
import com.lq186.fabric.bean.FabricTransactionActionInfo;
import com.lq186.fabric.bean.FabricTransactionEnvelopeInfo;
import com.lq186.fabric.bean.FabricTxReadInfo;
import com.lq186.fabric.bean.FabricTxReadWriteSetInfo;
import com.lq186.fabric.bean.FabricTxWriteInfo;

public class FabricChannel {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private String channelName = "mychannel"; // 通道名称

	private Channel channel;

	private HFClient client;

	private FabricConfig fabricConfig;

	private User user;

	private KafkaTemplate<String, Message> kafkaTemplate;

	private static final String CHARSET_UTF8 = "UTF-8";

	public FabricChannel(FabricConfig fabricConfig, HFClient client, User user,
			KafkaTemplate<String, Message> kafkaTemplate) throws InvalidArgumentException, TransactionException {
		this.client = client;
		this.fabricConfig = fabricConfig;
		this.user = user;
		this.channelName = fabricConfig.getChannelName();
		this.kafkaTemplate = kafkaTemplate;
		init();
	}

	private void init() throws InvalidArgumentException, TransactionException {

		client.setUserContext(user);
		channel = client.newChannel(channelName);
		logger.debug(String.format("join channel: %s", channelName));

		addOrderers();

		addPeers();

		initializeChannel(channel);
	}

	private void addOrderers() {
		fabricConfig.getOrgs().forEach(fabricOrg -> {
			Orderers orderers = fabricOrg.getOrderers();
			if (null != orderers) {
				orderers.getOrderers().forEach(orderer -> {
					String ordererPemFilePath = PathUtils.toPath(fabricConfig.getCryptoConfigPath(),
							"ordererOrganizations", orderers.getDomainName(), "orderers", orderer.getName(), "tls",
							"server.crt");
					if (null != orderer.getServerCrtPath()) {
						ordererPemFilePath = orderer.getServerCrtPath();
					}
					try {
						Properties properties = createProperties(ordererPemFilePath, orderer.getName());
						channel.addOrderer(client.newOrderer(orderer.getName(),
								Utils.grpcTLSify(fabricOrg.isUseTLS(), orderer.getLocation()), properties));
					} catch (Exception e) {
						logger.error(String.format("Could not add orderer (%s@%s) to channel %s.", orderer.getName(),
								orderer.getLocation(), channelName), e);
					}
				});
			}
		});

	}

	private void addPeers() {
		fabricConfig.getOrgs().forEach(fabricOrg -> {
			fabricOrg.getPeers().forEach(peer -> {
				String peerCrt = PathUtils.toPath(fabricConfig.getCryptoConfigPath(), "peerOrganizations",
						fabricOrg.getDomainName(), "peers", peer.getName(), "tls", "server.crt");
				if (null != peer.getServerCrtPath()) {
					peerCrt = peer.getServerCrtPath();
				}
				try {
					Properties properties = createProperties(peerCrt, peer.getName());

					channel.addPeer(client.newPeer(peer.getName(),
							Utils.grpcTLSify(fabricOrg.isUseTLS(), peer.getLocation()), properties));

					if (peer.isAddEventHub()) {
						try {
							channel.addEventHub(client.newEventHub(peer.getEventHubName(),
									Utils.grpcTLSify(fabricOrg.isUseTLS(), peer.getEventHubLocation()), properties));
						} catch (InvalidArgumentException e) {
							logger.error(String.format("Add peer %s event hub to channel fail.", peer.getName()), e);
						}
					}

				} catch (Exception e) {
					logger.error(String.format("Add peer %s to channel fail.", peer.getName()), e);
				}
			});
		});
	}

	private void initializeChannel(Channel channel) throws InvalidArgumentException, TransactionException {
		boolean isInitialized = channel.isInitialized();
		if (!isInitialized) {
			channel.initialize();
		}
		logger.debug("channel initialized -> ", channel.isInitialized());

		if (fabricConfig.isRegisterEvent()) {
			FabricBlockListener blockListener = getConfigedBlockListener();
			if (null != blockListener) {
				channel.registerBlockListener(blockEvent -> {
					try {
						FabricResult result = execBlockInfo(blockEvent);
						getConfigedBlockListener().received((FabricBlockInfo) result.getData());
					} catch (Exception e) {
						logger.error("block listener process error.", e);
					}
				});
			}
		}
	}

	private FabricBlockListener getConfigedBlockListener() {
		if (null != fabricConfig.getBlockListener()) {
			try {
				return fabricConfig.getBlockListener().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public FabricResult joinPeer(Peer peer) throws InvalidArgumentException, ProposalException {
		String peerCrt = PathUtils.toPath(fabricConfig.getCryptoConfigPath(), "peerOrganizations",
				fabricConfig.getSdkFabricOrg().getDomainName(), "peers", peer.getName(), "tls", "server.crt");
		if (null != peer.getServerCrtPath()) {
			peerCrt = peer.getServerCrtPath();
		}
		try {
			Properties properties = createProperties(peerCrt, peer.getName());
			org.hyperledger.fabric.sdk.Peer fabricPeer = client.newPeer(peer.getName(), peer.getLocation(), properties);
			if (channel.getPeers().stream().anyMatch(p -> p.getUrl().equals(fabricPeer.getUrl()))) {
				return FabricResult.newInstance(ResultCode.ERROR, "Peer has already in channel.");
			}
			channel.joinPeer(fabricPeer);

			if (peer.isAddEventHub()) {
				try {
					channel.addEventHub(
							client.newEventHub(peer.getEventHubName(), peer.getEventHubLocation(), properties));
				} catch (InvalidArgumentException e) {
					logger.error(String.format("Add peer %s event hub to channel fail.", peer.getName()), e);
				}
			}
		} catch (Exception e) {
			String message = String.format("Add peer %s to channel fail. More info: %s", peer.getName(),
					e.getMessage());
			logger.error(message, e);
			return FabricResult.newInstance(ResultCode.ERROR, message);
		}
		return FabricResult.newInstance(ResultCode.SUCCESS, "Peer join channel success.");
	}

	public FabricResult queryBlockByNumber(long blockNumber)
			throws InvalidArgumentException, ProposalException, IOException {
		return execBlockInfo(channel.queryBlockByNumber(blockNumber));
	}

	public FabricResult queryBlockByHash(byte[] blockHash)
			throws InvalidArgumentException, ProposalException, IOException {
		return execBlockInfo(channel.queryBlockByHash(blockHash));
	}

	public FabricResult queryBlockByTransactionID(String txID)
			throws InvalidArgumentException, ProposalException, IOException {
		return execBlockInfo(channel.queryBlockByTransactionID(txID));
	}

	public FabricResult getBlockchainInfo() throws InvalidArgumentException, ProposalException {
		return FabricResult.newInstance(ResultCode.SUCCESS, FabricBlockchainInfo.from(channel.queryBlockchainInfo()));
	}

	private Properties createProperties(String pemFilePath, String hostnameOverride) throws IOException {
		Properties properties = new Properties();
		try (InputStream inStream = PathUtils.path2InputStream(pemFilePath)) {
			properties.put("pemBytes", IOUtils.toByteArray(inStream));
		}
		properties.setProperty("hostnameOverride", hostnameOverride);
		// properties.setProperty("allowAllHostNames", "true");
		properties.setProperty("sslProvider", "openSSL");
		properties.setProperty("negotiationType", "TLS");
		properties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
		// 设置keepAlive以避免在不活跃的http2连接上超时的例子。在5分钟内，需要对服务器端进行更改，以接受更快的ping速率。
		properties.put("grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] { 5L, TimeUnit.MINUTES });
		properties.put("grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] { 8L, TimeUnit.SECONDS });
		properties.setProperty("ordererWaitTimeMilliSecs", "300000");
		return properties;
	}

	private FabricResult execBlockInfo(BlockInfo blockInfo) throws IOException, InvalidArgumentException {
		final long blockNumber = blockInfo.getBlockNumber();

		FabricBlockInfo fabricBlockInfo = new FabricBlockInfo();
		fabricBlockInfo.setBlockNumber(blockNumber).setDataHash(Hex.encodeHexString(blockInfo.getDataHash()))
				.setPreviousHashID(Hex.encodeHexString(blockInfo.getPreviousHash()))
				.setCalculatedBlockHash(Hex.encodeHexString(SDKUtils.calculateBlockHash(client, blockNumber,
						blockInfo.getPreviousHash(), blockInfo.getDataHash())))
				.setEnvelopeCount(blockInfo.getEnvelopeCount());

		for (EnvelopeInfo info : blockInfo.getEnvelopeInfos()) {

			FabricEnvelopeInfo fabricEnvelopeInfo = new FabricEnvelopeInfo();
			fabricEnvelopeInfo.setChannelId(info.getChannelId()).setTransactionID(info.getTransactionID())
					.setValidationCode(info.getValidationCode()).setTimestamp(info.getTimestamp().getTime())
					.setType(String.valueOf(info.getType()))
					.setCreator(new FabricIdentitiesInfo().setId(info.getCreator().getId())
							.setMspid(info.getCreator().getMspid()))
					.setValid(info.isValid()).setNonce(Hex.encodeHexString(info.getNonce()));

			if (info.getType() == EnvelopeType.TRANSACTION_ENVELOPE) {
				TransactionEnvelopeInfo txeInfo = (TransactionEnvelopeInfo) info;

				int txCount = txeInfo.getTransactionActionInfoCount();

				FabricTransactionEnvelopeInfo fabricTransactionEnvelopeInfo = new FabricTransactionEnvelopeInfo();
				fabricTransactionEnvelopeInfo.setTxCount(txCount).setValid(txeInfo.isValid())
						.setValidationCode(txeInfo.getValidationCode());

				for (int i = 0; i < txCount; i++) {
					TransactionEnvelopeInfo.TransactionActionInfo txInfo = txeInfo.getTransactionActionInfo(i);
					int endorsementsCount = txInfo.getEndorsementsCount();
					int chaincodeInputArgsCount = txInfo.getChaincodeInputArgsCount();
					FabricTransactionActionInfo fabricTransactionActionInfo = new FabricTransactionActionInfo();

					fabricTransactionActionInfo.setResponseStatus(txInfo.getResponseStatus())
							.setResponseMessage(new String(txInfo.getResponseMessageBytes(), CHARSET_UTF8))
							.setEndorsementsCount(endorsementsCount).setChaincodeInputArgsCount(chaincodeInputArgsCount)
							.setStatus(txInfo.getProposalResponseStatus())
							.setPayload(new String(txInfo.getProposalResponsePayload(), CHARSET_UTF8));

					fabricTransactionActionInfo.setChaincode(new FabricChaincode().setName(txInfo.getChaincodeIDName())
							.setVersion(txInfo.getChaincodeIDVersion()).setPath(txInfo.getChaincodeIDPath()));

					for (int n = 0; n < endorsementsCount; ++n) {
						BlockInfo.EndorserInfo endorserInfo = txInfo.getEndorsementInfo(n);
						fabricTransactionActionInfo.addEndorser(
								new FabricEndorserInfo().setId(endorserInfo.getId()).setMspId(endorserInfo.getMspid())
										.setSignature(Hex.encodeHexString(endorserInfo.getSignature())));
					}

					for (int z = 0; z < chaincodeInputArgsCount; ++z) {
						fabricTransactionActionInfo.addArg(new String(txInfo.getChaincodeInputArgs(z), CHARSET_UTF8));
					}

					TxReadWriteSetInfo rwsetInfo = txInfo.getTxReadWriteSet();
					if (null != rwsetInfo) {
						int nsRWsetCount = rwsetInfo.getNsRwsetCount();

						FabricTxReadWriteSetInfo fabricTxReadWriteSetInfo = new FabricTxReadWriteSetInfo();
						fabricTxReadWriteSetInfo.setNsRWsetCount(nsRWsetCount);

						for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {
							final String namespace = nsRwsetInfo.getNamespace();
							KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();

							FabricNsRwsetInfo fabricNsRwsetInfo = new FabricNsRwsetInfo();

							int rs = -1;
							for (KvRwset.KVRead readList : rws.getReadsList()) {

								long readVersionBlockNum = readList.getVersion().getBlockNum();
								long readVersionTxNum = readList.getVersion().getTxNum();

								fabricNsRwsetInfo.addReadInfo(
										new FabricTxReadInfo().setNamespace(namespace).setReadSetIndex(++rs)
												.setKey(readList.getKey()).setReadVersionBlockNum(readVersionBlockNum)
												.setReadVersionTxNum(readVersionTxNum));

							}

							rs = -1;
							for (KvRwset.KVWrite writeList : rws.getWritesList()) {
								fabricNsRwsetInfo.addWriteInfo(new FabricTxWriteInfo().setNamespace(namespace)
										.setWriteSetIndex(++rs).setKey(writeList.getKey())
										.setValue(new String(writeList.getValue().toByteArray(), CHARSET_UTF8)));
							}

							fabricTxReadWriteSetInfo.addNsRwsetInfo(fabricNsRwsetInfo);
						}
						fabricTransactionActionInfo.setRwsetInfo(fabricTxReadWriteSetInfo);
					}
					fabricTransactionEnvelopeInfo.addTransactionActionInfos(fabricTransactionActionInfo);
				}
				fabricEnvelopeInfo.setTransactionEnvelope(fabricTransactionEnvelopeInfo);
			}
			fabricBlockInfo.addEnvelope(fabricEnvelopeInfo);
		}
		return FabricResult.newInstance(ResultCode.SUCCESS, fabricBlockInfo);
	}

	public FabricResult install(Chaincode chaincode)
			throws ChaincodeEndorsementPolicyParseException, IOException, InvalidArgumentException, ProposalException {

		InstallProposalRequest request = client.newInstallProposalRequest();

		request.setChaincodeName(chaincode.getName());
		request.setChaincodeVersion(chaincode.getVersion());
		request.setChaincodeSourceLocation(new File(chaincode.getSource()));
		request.setChaincodePath(chaincode.getPath());
		request.setChaincodeLanguage(TransactionRequest.Type.GO_LANG);
		request.setProposalWaitTime(chaincode.getProposalWaitTime());

		long currentStart = System.currentTimeMillis();
		Collection<ProposalResponse> installProposalResponses = client.sendInstallProposal(request, channel.getPeers());
		logger.info("chaincode install transaction proposal time = " + (System.currentTimeMillis() - currentStart));
		return toPeerResponse(installProposalResponses, false, chaincode.getVersion());
	}

	public FabricResult instantiate(Chaincode chaincode, String... args)
			throws ChaincodeEndorsementPolicyParseException, IOException, InvalidArgumentException, ProposalException {
		InstantiateProposalRequest request = client.newInstantiationProposalRequest();

		setRequestInfo(request, chaincode, null, args);

		setChaincodeEndorsementPolicy(request, chaincode);

		request.setTransientMap(INSTANTIATE_MAP);

		long currentStart = System.currentTimeMillis();
		Collection<ProposalResponse> instantiateProposalResponses = channel.sendInstantiationProposal(request,
				channel.getPeers());
		logger.info("chaincode instantiate transaction proposal time = " + (System.currentTimeMillis() - currentStart));
		return toOrdererResponse(instantiateProposalResponses);
	}

	public FabricResult upgrade(Chaincode chaincode, String... args)
			throws ChaincodeEndorsementPolicyParseException, IOException, InvalidArgumentException, ProposalException {
		UpgradeProposalRequest request = client.newUpgradeProposalRequest();

		setRequestInfo(request, chaincode, null, args);

		setChaincodeEndorsementPolicy(request, chaincode);

		request.setTransientMap(UPGRADE_MAP);

		long currentStart = System.currentTimeMillis();
		Collection<ProposalResponse> upgradeProposalResponses = channel.sendUpgradeProposal(request,
				channel.getPeers());
		logger.info("chaincode instantiate transaction proposal time = " + (System.currentTimeMillis() - currentStart));
		return toOrdererResponse(upgradeProposalResponses);
	}

	public FabricResult invoke(Chaincode chaincode, String fcn, String... args)
			throws UnsupportedEncodingException, InvalidArgumentException, ProposalException {
		TransactionProposalRequest request = client.newTransactionProposalRequest();

		setRequestInfo(request, chaincode, fcn, args);

		request.setTransientMap(TRANSACTION_MAP);

		long currentStart = System.currentTimeMillis();
		Collection<ProposalResponse> transactionProposalResponses = channel.sendTransactionProposal(request,
				channel.getPeers());
		logger.info(String.format("chaincode invoke transaction proposal time = %d",
				(System.currentTimeMillis() - currentStart)));
		// return FabricResult.newInstance(ResultCode.SUCCESS, "");
		return toOrdererResponse(transactionProposalResponses);
	}

	public FabricResult query(Chaincode chaincode, String fcn, String... args)
			throws UnsupportedEncodingException, InvalidArgumentException, ProposalException {
		QueryByChaincodeRequest request = client.newQueryProposalRequest();

		setRequestInfo(request, chaincode, fcn, args);

		request.setTransientMap(QUERY_MAP);

		long currentStart = System.currentTimeMillis();
		Collection<ProposalResponse> queryProposalResponses = channel.queryByChaincode(request, channel.getPeers());
		logger.info("chaincode query transaction proposal time = " + (System.currentTimeMillis() - currentStart));

		return toPeerResponse(queryProposalResponses, true, chaincode.getVersion());
	}

	private void setRequestInfo(TransactionRequest request, Chaincode chaincode, String fcn, String... args) {
		request.setChaincodeID(chaincode.getId());
		if (null != fcn && fcn.length() > 0) {
			request.setFcn(fcn);
		}
		if (null != args) {
			request.setArgs(args);
		}
		request.setProposalWaitTime(chaincode.getProposalWaitTime());
	}

	private void setChaincodeEndorsementPolicy(TransactionRequest request, Chaincode chaincode)
			throws ChaincodeEndorsementPolicyParseException, IOException {
		ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
		chaincodeEndorsementPolicy.fromYamlFile(new File(chaincode.getPolicy()));
		request.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
	}

	public FabricResult toOrdererResponse(Collection<ProposalResponse> proposalResponses)
			throws UnsupportedEncodingException, InvalidArgumentException, ProposalException {

		Collection<ProposalResponse> successful = new LinkedList<>();
		Collection<ProposalResponse> failed = new LinkedList<>();

		proposalResponses.forEach(response -> {
			if (response.getStatus() == Status.SUCCESS) {
				successful.add(response);
			} else {
				failed.add(response);
			}
		});

		Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
				.getProposalConsistencySets(proposalResponses);
		if (proposalConsistencySets.size() != 1) {
			logger.error(String.format("Excepted only one set of consistemt proposal responses but got %d",
					proposalConsistencySets.size()));
		}

		if (failed.size() > 0) {
			ProposalResponse firstResponse = failed.iterator().next();
			// logger.error(String.format("Not enough endorsers for inspect: %d endorser
			// error: %s. Was verified: %s",
			// failed.size(), firstResponse.getMessage(),
			// String.valueOf(firstResponse.isVerified())));
			return FabricResult.newInstance(ResultCode.ERROR, firstResponse.getMessage());
		} else {

			// logger.debug("Successfully received transaction proposal responses.");
			ProposalResponse response = proposalResponses.iterator().next();
			byte[] payload = response.getChaincodeActionResponsePayload();
			String payloadStr = null == payload ? "" : new String(payload, "UTF-8");
			// logger.debug(String.format("playload as string -> %s", payloadStr));

			CompletableFuture<TransactionEvent> future = channel.sendTransaction(successful);
			try {
				TransactionEvent event = future.get(10, TimeUnit.SECONDS);
				logger.info(String.format("txid: %s, valid: %s", event.getTransactionID(), event.isValid()));
			} catch (Exception e) {
				logger.error("Send transaction to orderer error. ", e);
				return FabricResult.newInstance(ResultCode.ERROR,
						String.format("Send transaction to orderer error. More info: %s", e.getMessage()));
			}

			return FabricResult.newInstance(ResultCode.SUCCESS, payloadStr).setTxid(response.getTransactionID());
		}

	}

	public FabricResult toKafkaResponse(Collection<ProposalResponse> proposalResponses)
			throws UnsupportedEncodingException, InvalidArgumentException, ProposalException {

		Collection<ProposalResponse> successful = new LinkedList<>();
		Collection<ProposalResponse> failed = new LinkedList<>();

		proposalResponses.forEach(response -> {
			if (response.getStatus() == Status.SUCCESS) {
				successful.add(response);
			} else {
				failed.add(response);
			}
		});

		Collection<Set<ProposalResponse>> proposalConsistencySets = SDKUtils
				.getProposalConsistencySets(proposalResponses);
		if (proposalConsistencySets.size() != 1) {
			logger.error(String.format("Excepted only one set of consistemt proposal responses but got %d",
					proposalConsistencySets.size()));
		}

		if (failed.size() > 0) {
			ProposalResponse firstResponse = failed.iterator().next();
			// logger.error(String.format("Not enough endorsers for inspect: %d endorser
			// error: %s. Was verified: %s",
			// failed.size(), firstResponse.getMessage(),
			// String.valueOf(firstResponse.isVerified())));
			return FabricResult.newInstance(ResultCode.ERROR, firstResponse.getMessage());
		} else {

			// logger.debug("Successfully received transaction proposal responses.");
			ProposalResponse response = proposalResponses.iterator().next();
			byte[] payload = response.getChaincodeActionResponsePayload();
			String payloadStr = null == payload ? "" : new String(payload, "UTF-8");
			// logger.debug(String.format("playload as string -> %s", payloadStr));

			// 直接组装消息 KafkaMessage

			List<FabricProposalResponse.Endorsement> ed = new LinkedList<>();
			FabricProposal.Proposal proposal = null;
			ByteString proposalResponsePayload = null;
			String proposalTransactionID = null;

			for (ProposalResponse sdkProposalResponse : proposalResponses) {
				ed.add(sdkProposalResponse.getProposalResponse().getEndorsement());
				if (proposal == null) {
					proposal = sdkProposalResponse.getProposal();
					proposalTransactionID = sdkProposalResponse.getTransactionID();
					proposalResponsePayload = sdkProposalResponse.getProposalResponse().getPayload();

				}
			}
			logger.info(String.format("proposalTransactionID --> %s", proposalTransactionID));
			try {
				TransactionBuilder transactionBuilder = TransactionBuilder.newBuilder();

				Payload transactionPayload = transactionBuilder.chaincodeProposal(proposal).endorsements(ed)
						.proposalResponsePayload(proposalResponsePayload).build();

				Envelope transactionEnvelope = createTransactionEnvelope(transactionPayload, client.getUserContext());

				KafkaMessage.Builder builder = KafkaMessage.newBuilder();
				KafkaMessageRegular.Builder regularBuilder = KafkaMessageRegular.newBuilder();
				regularBuilder.setClass_Value(KafkaMessageRegular.Class.NORMAL_VALUE);
				regularBuilder.setConfigSeq(2);
				regularBuilder.setOriginalOffset(0);
				regularBuilder.setPayload(transactionEnvelope.toByteString());
				builder.setRegular(regularBuilder);

				ListenableFuture<SendResult<String, Message>> future = kafkaTemplate.send(channelName, "0",
						builder.build());

				future.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info(String.format("txid -> %s", response.getTransactionID()));

			/*
			 * CompletableFuture<TransactionEvent> future =
			 * channel.sendTransaction(successful); try { TransactionEvent event =
			 * future.get(10, TimeUnit.SECONDS);
			 * logger.info(String.format("txid: %s, valid: %s", event.getTransactionID(),
			 * event.isValid())); } catch (Exception e) { e.printStackTrace(); }
			 */
			return FabricResult.newInstance(ResultCode.SUCCESS, payloadStr).setTxid(response.getTransactionID());
		}

	}

	public FabricResult toPeerResponse(Collection<ProposalResponse> proposalResponses, boolean checkVerified,
			String version) {

		for (ProposalResponse response : proposalResponses) {
			if (isFailed(response, checkVerified, version)) {
				String message = String.format(
						"Failed install/query proposal form peer: %s status: %s. Message: %s. Verified: %s",
						response.getPeer().getName(), String.valueOf(response.getStatus()), response.getMessage(),
						String.valueOf(response.isVerified()));
				logger.debug(message);
				return FabricResult.newInstance(ResultCode.ERROR, message);
			} else {
				String payload = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
				logger.debug(String.format("Install/query payload from peer: %s, txid: %s, payload: %s",
						response.getPeer().getName(), response.getTransactionID(), payload));
				return FabricResult.newInstance(ResultCode.SUCCESS, payload).setTxid(response.getTransactionID());
			}
		}
		return FabricResult.newInstance(ResultCode.ERROR, null);
	}

	private Envelope createTransactionEnvelope(Payload transactionPayload, User user) throws CryptoException {

		return Envelope.newBuilder().setPayload(transactionPayload.toByteString())
				.setSignature(ByteString.copyFrom(
						client.getCryptoSuite().sign(user.getEnrollment().getKey(), transactionPayload.toByteArray())))
				.build();

	}

	private boolean isFailed(ProposalResponse proposalResponse, boolean checkVerified, String version) {
		return (checkVerified && (!proposalResponse.isVerified() && !StringUtils.equals(version, "1.2")))
				|| proposalResponse.getStatus() != ChaincodeResponse.Status.SUCCESS;
	}

	private static final Map<String, byte[]> QUERY_MAP = new HashMap<>();
	static {
		try {
			QUERY_MAP.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(CHARSET_UTF8));
			QUERY_MAP.put("method", "QueryByChaincodeRequest".getBytes(CHARSET_UTF8));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final Map<String, byte[]> TRANSACTION_MAP = new HashMap<>();
	static {
		try {
			TRANSACTION_MAP.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(CHARSET_UTF8));
			TRANSACTION_MAP.put("method", "TransactionProposalRequest".getBytes(CHARSET_UTF8));
			TRANSACTION_MAP.put("result", ":)".getBytes(CHARSET_UTF8));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final Map<String, byte[]> UPGRADE_MAP = new HashMap<>();
	static {
		try {
			UPGRADE_MAP.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(CHARSET_UTF8));
			UPGRADE_MAP.put("method", "TransactionProposalRequest".getBytes(CHARSET_UTF8));
			UPGRADE_MAP.put("result", ":)".getBytes(CHARSET_UTF8));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final Map<String, byte[]> INSTANTIATE_MAP = new HashMap<>();
	static {
		try {
			INSTANTIATE_MAP.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(CHARSET_UTF8));
			INSTANTIATE_MAP.put("method", "InstantiateProposalRequest".getBytes(CHARSET_UTF8));
			INSTANTIATE_MAP.put("result", ":)".getBytes(CHARSET_UTF8));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public HFClient getClient() {
		return client;
	}

	public void setClient(HFClient client) {
		this.client = client;
	}

}
