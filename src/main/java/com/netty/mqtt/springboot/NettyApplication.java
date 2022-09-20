package com.netty.mqtt.springboot;

import com.netty.mqtt.springboot.server.BootNettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyApplication {
 public static void main(String args[]) {
     SpringApplication application = new SpringApplication(NettyApplication.class);
     application.run(args);
     new BootNettyServer().startup();
 }
}
