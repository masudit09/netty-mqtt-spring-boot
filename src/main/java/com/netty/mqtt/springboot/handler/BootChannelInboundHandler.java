package com.netty.mqtt.springboot.handler;

import com.netty.mqtt.springboot.adapter.BootMqttMsgBack;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class BootChannelInboundHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(null == msg) {
            MqttMessage mqttMessage = (MqttMessage) msg;
            logger.info("info--"+ mqttMessage.toString());
            MqttFixedHeader mqttFixedHeader = mqttMessage.fixedHeader();
            Channel channel = ctx.channel();

            if(mqttFixedHeader.messageType().equals(MqttMessageType.CONNECT)) {
                BootMqttMsgBack.connack(channel, mqttMessage);
            }
            switch (mqttFixedHeader.messageType()) {
                case PUBLISH:
                    System.out.println("publish");
                    BootMqttMsgBack.puback(channel, mqttMessage);
                    break;
                case PUBREL:
                    System.out.println("pubrel");
                    BootMqttMsgBack.puback(channel, mqttMessage);
                    break;
                case SUBSCRIBE:
                    System.out.println("subscribe");
                    BootMqttMsgBack.suback(channel, mqttMessage);
                    break;
                case UNSUBSCRIBE:
                    System.out.println("un-subscribe");
                    BootMqttMsgBack.unsuback(channel, mqttMessage);
                    break;
                case PINGREQ:
                    System.out.println("pingreq");
                    BootMqttMsgBack.pingresp(channel, mqttMessage);
                    break;
                case DISCONNECT:
                    System.out.println("disconnect");
                    break;
                default:
                    System.out.println(mqttFixedHeader.messageType().toString());
                    break;
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
