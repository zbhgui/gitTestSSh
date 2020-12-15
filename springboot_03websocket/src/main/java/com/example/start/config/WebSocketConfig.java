package com.example.start.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @description:
 * @createDate: 2020/12/15
 * @author: zbh
 */
@Configuration
public class WebSocketConfig {
  @Bean
  public ServerEndpointExporter getServerEndpointExporter(){
    return new ServerEndpointExporter();
  }
}
