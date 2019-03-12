package com.lq186.fabric.sdk.kafka;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;

import com.google.protobuf.GeneratedMessageV3;

public class ProtobufSerializer implements Serializer<GeneratedMessageV3> {

	@Override
	public void configure(Map<String, ?> configs, boolean isKey) {

	}

	@Override
	public byte[] serialize(String topic, GeneratedMessageV3 data) {
		if (null != data) {
			return data.toByteArray();
		}
		return null;
	}

	@Override
	public void close() {

	}

}
