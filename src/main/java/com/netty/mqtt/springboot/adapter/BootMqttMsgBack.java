package com.netty.mqtt.springboot.adapter;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BootMqttMsgBack {

    private static Logger logger = LoggerFactory.getLogger(BootMqttMsgBack.class);

    public static void connack (Channel channel, MqttMessage mqttMessage) {
        MqttConnectMessage mqttConnectMessage = (MqttConnectMessage) mqttMessage;
        MqttFixedHeader mqttFixedHeaderInfo = mqttConnectMessage.fixedHeader();
        MqttConnectVariableHeader mqttConnectVariableHeader = mqttConnectMessage.variableHeader();

        MqttConnAckVariableHeader mqttConnAckVariableHeaderBack = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, mqttConnectVariableHeader.isCleanSession());
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.CONNACK, mqttFixedHeaderInfo.isDup(), MqttQoS.AT_MOST_ONCE, mqttFixedHeaderInfo.isRetain(), 0x02);

        MqttConnAckMessage connAck = new MqttConnAckMessage(mqttFixedHeaderBack, mqttConnAckVariableHeaderBack);

        logger.info("Back--"+ connAck.toString());
        channel.writeAndFlush(connAck);
    }

    public static void puback(Channel channel, MqttMessage mqttMessage) {
        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) mqttMessage ;
        MqttFixedHeader mqttFixedHeader = mqttPublishMessage.fixedHeader();
        MqttQoS qos = (MqttQoS) mqttFixedHeader.qosLevel();
        byte[] headBytes = new byte[mqttPublishMessage.payload().readableBytes()];
        mqttPublishMessage.payload().readBytes(headBytes);
        String data = new String(headBytes);
        System.out.println("publish data--"+ data);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader;
        MqttFixedHeader mqttFixedHeaderBack;
        MqttPubAckMessage pubAck;

        switch (qos) {
            case AT_MOST_ONCE:
                break;
            case AT_LEAST_ONCE:
                mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(mqttPublishMessage.variableHeader().packetId());
                mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.PUBACK, mqttFixedHeader.isDup(), MqttQoS.AT_MOST_ONCE, mqttFixedHeader.isRetain(), 0x02);
                pubAck = new MqttPubAckMessage(mqttFixedHeaderBack,mqttMessageIdVariableHeader);
                logger.info("back--"+pubAck.toString());
                channel.writeAndFlush(pubAck);
                break;
            case EXACTLY_ONCE:
                mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(mqttPublishMessage.variableHeader().packetId());
                mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.PUBACK, mqttFixedHeader.isDup(), MqttQoS.AT_MOST_ONCE, mqttFixedHeader.isRetain(), 0x02);
                pubAck = new MqttPubAckMessage(mqttFixedHeaderBack,mqttMessageIdVariableHeader);
                logger.info("back--"+pubAck.toString());
                channel.writeAndFlush(pubAck);
                break;
            default:
                break;
        }

    }

    public static void pubcomp(Channel channel, MqttMessage mqttMessage) {
        MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) mqttMessage ;
        MqttFixedHeader mqttFixedHeader = mqttPublishMessage.fixedHeader();
        MqttQoS qos = (MqttQoS) mqttFixedHeader.qosLevel();
        byte[] headBytes = new byte[mqttPublishMessage.payload().readableBytes()];
        mqttPublishMessage.payload().readBytes(headBytes);

    }

    public static void suback(Channel channel, MqttMessage mqttMessage) {
        MqttSubscribeMessage mqttSubscribeMessage = (MqttSubscribeMessage) mqttMessage;
        MqttMessageIdVariableHeader messageIdVariableHeader = mqttSubscribeMessage.variableHeader();

        MqttMessageIdVariableHeader variableHeaderBack = MqttMessageIdVariableHeader.from(messageIdVariableHeader.messageId());
        Set<String> topics = mqttSubscribeMessage.payload().topicSubscriptions().stream().map(mqttTopicSubscription ->
                mqttTopicSubscription.topicName()).collect(Collectors.toSet());

        List<Integer> grantedQoSLevels = new ArrayList<>(topics.size());
        for(int i=0; i< topics.size(); i++) {
            grantedQoSLevels.add(mqttSubscribeMessage.payload().topicSubscriptions().get(i).qualityOfService().value());
        }
        MqttSubAckPayload payloadBack = new MqttSubAckPayload(grantedQoSLevels);
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 2+topics.size());
        MqttSubAckMessage subAck = new MqttSubAckMessage(mqttFixedHeaderBack, variableHeaderBack, payloadBack);
        logger.info("back--"+ subAck.toString());
        channel.writeAndFlush(subAck);
    }

    public static void unsuback(Channel channel, MqttMessage mqttMessage) {
        MqttMessageIdVariableHeader messageIdVariableHeader = (MqttMessageIdVariableHeader) mqttMessage.variableHeader();
        MqttMessageIdVariableHeader variableHeaderBack = MqttMessageIdVariableHeader.from(messageIdVariableHeader.messageId());
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 2);
        MqttUnsubAckMessage unSubAck = new MqttUnsubAckMessage(mqttFixedHeaderBack, variableHeaderBack);
        logger.info("back--"+ unSubAck.toString());
        channel.writeAndFlush(unSubAck);

    }

    public static void pingresp(Channel channel, MqttMessage mqttMessage) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PINGRESP, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttMessage mqttMessageBack = new MqttMessage(fixedHeader);
        logger.info("info--"+ mqttMessageBack.toString());
        channel.writeAndFlush(mqttMessageBack);

    }
}
