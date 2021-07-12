package com.arch.monitor_data.schedule;

import com.arch.monitor_data.service.FileService;
import com.arch.monitor_data.socket.CurrentMonitorSocket;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleTask {

    @Resource
    private FileService fileService ;

    @Resource
    private RedisTemplate<String, Object> redisTemplate ;

    @Scheduled(cron = "0/1 * * * * ?")
    private void syncData() throws InterruptedException, IOException {
        fileService.readFile();
        log.error("end Task-------------");
    }

    @Scheduled(cron = "0/1 * * * * ?")
    private void syncData1() throws InterruptedException, IOException {
        log.error("syncData1 start!------------------");
        Map<String, String> map = new HashMap<>() ;
        for (Map.Entry<String, List<String>> entry: FileService.map.entrySet()) {
            List<String> list = entry.getValue() ;
            String aa = list.get(list.size()-1).toString() ;
            map.put(entry.getKey(), aa) ;
        }
        String result = JSONObject.fromObject(map).toString() ;
        System.out.println(result);
        CurrentMonitorSocket.sendMsgToAll(result);
    }

}
