package com.example.start.control;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import sun.plugin2.message.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;

/**
 * @description:
 * @createDate: 2020/12/15
 * @author: zbh
 */
@ServerEndpoint("/openserver/{userId}")
@Component
@Slf4j
public class WebSocketServer {
  //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
  public static int onlineCount = 0;
  // concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象.
  public static ConcurrentHashMap<String, WebSocketServer> websocketMap = new ConcurrentHashMap<>();
  //与某个客户端的连接会话，需要通过它来给客户端发送数据
  public Session session;
  //接收userId
  private String userId = "";


  /**
   * 连接成功调用的方法
   *
   * @param session
   * @param userId
   */
  @OnOpen
  public void onOpen(Session session, @PathParam("userId") String userId) {
    this.session = session;
    this.userId = userId;
    if (websocketMap.containsKey(userId)) {
      websocketMap.remove(userId);
      websocketMap.put(userId, this);
    } else {
      websocketMap.put(userId, this);
      addOnLineCount();
    }

    log.info("用户连接: {}", userId);
    log.info("当前在线人数:{}", getOnLineCount());
  }

  /**
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClouse() {
    if (websocketMap.containsKey(userId)) {
      websocketMap.remove(userId);
      subOnLineCount();
    }
    log.info("用户退出:{}", userId);
    log.info("当前在线人数为:{}", getOnLineCount());
  }

  /**
   * 收到客户端信息调用的方法
   *
   * @param message
   * @param session
   */
  @OnMessage
  public void onMessage(String message, Session session) {
    log.info("用户 {} 发送消息 {}", userId, message);
    if (StringUtils.isNotBlank(message)) {
      try {
        JSONObject jsonObject = JSON.parseObject(message);
        jsonObject.put("fromUserId", this.userId);
        String toUserId = jsonObject.getString("toUserId");
        //传送给对应的用户的websocket
        if (StringUtils.isNotBlank(toUserId) && websocketMap.containsKey(toUserId)) {
          websocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
        } else {
          log.error("请求的用户{}不在该服务器上",userId);
          //否则不在这个服务器上，发送到mysql或者redis
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  @OnError
  public void onError(Session session,Throwable error){
    log.error("用户 {} 错误，原因：{}",userId,error.getMessage());
  }

  /**
   * 发送自定义消息
   */
  public static void sendInfo(String message,@PathParam(("userId"))String userId){
    log.info("发送消息到:"+userId+"，报文:"+message);
    if(StringUtils.isNotBlank(userId)&&websocketMap.containsKey(userId)){
      try {
        websocketMap.get(userId).sendMessage(message);
      } catch (IOException e) {
        log.error("用户 {} 错误，原因：{}",userId,e.getMessage());
        e.printStackTrace();
      }
    }else{
      log.error("用户"+userId+",不在线！");
    }
  }

  /**
   * 服务器主动推动消息
   *
   * @param message
   */
  public void sendMessage(String message) throws IOException {
    this.session.getBasicRemote().sendText(message);
  }

  public static synchronized int getOnLineCount() {
    return onlineCount;
  }

  public static synchronized void addOnLineCount() {
    WebSocketServer.onlineCount++;
  }

  public static synchronized void subOnLineCount() {
    WebSocketServer.onlineCount--;
  }

}
