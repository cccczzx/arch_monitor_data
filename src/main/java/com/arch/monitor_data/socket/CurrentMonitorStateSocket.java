package com.arch.monitor_data.socket;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Api(tags = "socket发送信息")
@ServerEndpoint(value = "/currentMonitor/{usId}/{assetName}")
@Component
@Slf4j
public class CurrentMonitorStateSocket {

    /** 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的 */
    private static  int onlineCount = 0 ;

    /** concurrent包的线程安全set，用来存放每个客户端对应的MyWebSocket对象 */
    private static CopyOnWriteArraySet<CurrentMonitorStateSocket> wsClientMap = new CopyOnWriteArraySet<>() ;

    /** 与某个客户端的连接会话，需要通过它来给客户端发送数据 */
    private Session session ;

    /** 接收sid */
    private String usId = "" ;

    /** 接收assetName */
    private String assetName = "" ;

    /**
     * 连接建立成功调用的方法
     * @param session 当前会话session
     * */
    @OnOpen
    public void onOpen (Session session, @PathParam("usId") String usId, @PathParam("assetName") String assetName) {

        this.session = session ;
        wsClientMap.add(this) ;
        /** 加入set中 */
        addOnlineCount() ;
        /** 在线数+1 */
        log.info("有新窗口开始监听：" + usId + "当前在线人数为" + getOnlineCount()) ;
        this.usId = usId ;
        this.assetName = assetName ;

    }

    /**
     * 连接关闭
     * */
    @OnClose
    public void onClose() {
        wsClientMap.remove(this) ;
        subOnlineCount() ;
    }

    /**
     *  收到客户端消息
     * @param message 客户端发送过来的消息
     * @param session 当前会话session
     * @throws IOException
     * */
    @OnMessage
    public void onMessage (String message, Session session) throws IOException {
        sendMsgToAll(message) ;
    }

    /**
     * 发生错误
     * */
    @OnError
    public void onError (Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 给所有客户端群发消息
     * @param message 消息内容
     * @throws IOException
     * */
    public static void sendMsgToAll(String message) throws IOException {
        for (CurrentMonitorStateSocket item :wsClientMap) {
            try {
                log.info("CurrentMonitorStateSocket 用户：" + item.usId + "推送消息到窗口，推送内容：" + message);
                Map<String, String> map = JSONObject.fromObject(message) ;
                Map<String, String> sendMap = new HashMap<>() ;
                sendMap.put(item.assetName, map.get(item.assetName)) ;
                item.sendMessage(JSONObject.fromObject(sendMap).toString()) ;
            } catch (IOException e) {
                continue;
            }
        }
    }

    public void sendMessage (String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return CurrentMonitorStateSocket.onlineCount ;
    }

    public static synchronized void addOnlineCount() {
        CurrentMonitorStateSocket.onlineCount++ ;
    }

    public static synchronized void subOnlineCount() {
        CurrentMonitorStateSocket.onlineCount-- ;
    }






}
