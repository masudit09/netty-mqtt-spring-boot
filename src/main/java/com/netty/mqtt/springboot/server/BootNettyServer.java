package com.netty.mqtt.springboot.server;

import com.netty.mqtt.springboot.handler.BootChannelInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BootNettyServer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private int port = 1883;

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workGroup;

    public void startup() {
//        System.out.println("StartUp success.");
//        logger.info("Logger: StartUp success.");
        try {
            bossGroup = new NioEventLoopGroup(1);
            workGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup);
            bootstrap.channel(NioServerSocketChannel.class);

            bootstrap.option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.SO_RCVBUF, 10485760);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel socketChannel) {
                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                    channelPipeline.addLast("encoder", MqttEncoder.INSTANCE);
                    channelPipeline.addLast("decoder", new MqttDecoder());
                    channelPipeline.addLast(new BootChannelInboundHandler());
                }
            });
            ChannelFuture f = bootstrap.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception ex) {
            System.out.println("Exception on starting:"+ ex.toString());
        }
        System.out.println("StartUp success.");
        logger.info("Logger: StartUp success.");

    }

    public void shutdown() throws InterruptedException {
        if(workGroup != null && bossGroup != null) {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            System.out.println("Shutdown success.");
        }
    }

}
