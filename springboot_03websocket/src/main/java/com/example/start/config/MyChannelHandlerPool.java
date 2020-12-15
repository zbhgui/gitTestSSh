package com.example.start.config;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @description:
 * @createDate: 2020/12/15
 * @author:
 */
public class MyChannelHandlerPool {
  public MyChannelHandlerPool(){}

  public static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

}
