package com.arch.monitor_data.socket;

import com.arch.monitor_data.entity.ArchAssetPo;
import com.arch.monitor_data.service.ArchAssetService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Api(tags = "socket发送信息")
@ServerEndpoint(value = "/currentMonitorState/{usId}/{bridgeName}")
@Component
@Slf4j
public class CurrentMonitorSocket {

    /** 静态变量，用来记录当前在线连接数，应该把它设计成线程安全的 */
    private static  int onlineCount = 0 ;

    /** concurrent包的线程安全Set,用来存放每个客户端对应的MyWebSocket对象 */
    private static CopyOnWriteArraySet<CurrentMonitorSocket> wsClientMap = new CopyOnWriteArraySet<>() ;

    /** 与某个客户端的连接会话，需要通过它来给客户端发送数据 */
    private Session session ;

    /** 接收sid */
    private String usId = "" ;

    /** 接收bridgeName */
    private String bridgeName = "" ;

    private List<ArchAssetPo> archAssetPoList ;

    private static ArchAssetService archAssetService ;

    @Autowired
    public void setArchServiceImpl (ArchAssetService archAssetService) {
        CurrentMonitorSocket.archAssetService = archAssetService ;
    }

    /** 连接建立成功调用的方法 */
    @OnOpen
    public void onOpen (Session session, @PathParam("usId") String usId, @PathParam("bridgeName") String bridgeName) {
        this.session = session ;
        wsClientMap.add(this) ;
        addOnlineCount() ;
        /** 在线数+1 */
        log.info("有新窗口开始监听：" + usId + "，当前在线人数为" + getOnlineCount());
        this.usId = usId ;
        this.bridgeName = bridgeName ;
        this.archAssetPoList = archAssetService.findArchAssetList(bridgeName)  ;
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
     * 收到客户端消息
     * @param message 客户端发送过来的消息
     * @param session 当前会话session
     * @throws IOException
     * */
    @OnMessage
    public void onMessage (String message, Session session) throws IOException {
        sendMsgToAll (message) ;
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
    public  static  void  sendMsgToAll(String message) throws IOException  {
        for (com.arch.monitor_data.socket.CurrentMonitorSocket item : wsClientMap) {
            item.session.getBasicRemote().sendText(message);
        }
    }

    /**
     * 群发自定义消息
     * */
    public static void sendInfo (String message) throws  IOException {

        for (CurrentMonitorSocket item : wsClientMap) {
            try {
                /** 这里可以设定只推送给这个sid的，为null则全部推送  */
                log.info("CurrentMonitorSocket用户：" + item.usId + "推送消息到窗口，推送内容：" + message);
                Map<String, String> map = JSONObject.fromObject(message) ;
                Map<String, String> sendMap = new HashMap<String,String>() ;
                for (ArchAssetPo archAssetPo: item.archAssetPoList) {
                    sendMap.put(archAssetPo.getAssetName(), map.get(archAssetPo.getAssetName())) ;
                }
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
        return com.arch.monitor_data.socket.CurrentMonitorSocket.onlineCount ;
    }

    public static synchronized void addOnlineCount() {
        com.arch.monitor_data.socket.CurrentMonitorSocket.onlineCount++ ;
    }

    public static synchronized void subOnlineCount() {
        com.arch.monitor_data.socket.CurrentMonitorSocket.onlineCount-- ;
    }

}
