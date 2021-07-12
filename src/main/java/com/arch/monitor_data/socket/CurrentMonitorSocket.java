package com.arch.monitor_data.socket;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Api(tags = "socket发送信息")
@ServerEndpoint(value = "/currentMonitor")
@Component
@Slf4j
public class CurrentMonitorSocket {

    private static int onlineCount = 0 ;
    /** 静态变量，用来记录当前在线连接数，应该把它设计成线程安全的 */

    private static CopyOnWriteArraySet<CurrentMonitorSocket> wsClientMap = new CopyOnWriteArraySet<>() ;
    /** concurrent包的线程安全Set,用来存放每个客户端对应的MyWebSocket对象 */

    private Session session ;
    /** 与某个客户端的连接会话，需要通过它来给客户端发送数据 */

    /**
     * 连接建立成功调用的方法
     * */
    @OnOpen
    /** 连接建立以后会自动调用@OnOpen注解的方法，把当前的会话session存起来 */
    public void onOpen (Session session) {
        this.session = session ;
        wsClientMap.add(this) ;
        addOnlineCount() ;
        /** 当前Session的数量+1 */
    }

    /** 连接关闭  */
    @OnClose
    public void onClose () {
        wsClientMap.remove(this) ;
        subOnlineCount() ;
        /** 当前Session的数量-1 */
    }

    /**
     * 收到客户端消息
     * @param message 客户端发送过来的消息
     * @param session 当前会话session
     * */
    @OnMessage
    public void onMessage (String message, Session session) throws IOException {
        sendMsgToAll(message) ;
        /** 前端给当前session发了一个消息，那么消息就会传到这里来  */
    }

    @OnError
    public  void onError (Session session, Throwable error) {
        error.printStackTrace();
        /** 连接出错了以后，就把错误的消息打日志里面去 */
    }

    /**
     * 给所有客户端群发消息
     * @param message 消息内容
     *
     * */
    public static void sendMsgToAll(String message) throws IOException {
        for (CurrentMonitorSocket item : wsClientMap) {
            item.session.getBasicRemote().sendText(message);
            /** 遍历所有连接的session，找到远程客户端，发送消息 */
        }
    }

    public void sendMessage (String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return CurrentMonitorSocket.onlineCount ;
    }

    public static synchronized void addOnlineCount() {
        CurrentMonitorSocket.onlineCount++ ;
    }

    public static synchronized void subOnlineCount() {
        CurrentMonitorSocket.onlineCount-- ;
    }

}
