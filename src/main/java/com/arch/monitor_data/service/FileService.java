package com.arch.monitor_data.service;


import com.arch.monitor_data.entity.ArchMonitorDataPo;
import com.arch.monitor_data.entity.ArchMonitorRecordPo;
import com.arch.monitor_data.repo.ArchMonitorDataRepo;
import com.arch.monitor_data.repo.ArchMonitorRecordRepo;
import com.arch.monitor_data.util.DateUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class FileService {

    public static ConcurrentHashMap<String, List<String>> map = new ConcurrentHashMap<>() ;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(8) ;
    private static final ExecutorService threadPool1 = Executors.newFixedThreadPool(2) ;
    @Value("${arch.data.path}")
    private String path ;

    private final String lock = "lock" ;

    @Value("${arch.data.backup}")
    private String backupPath ;

    @Resource
    private ArchMonitorDataRepo archMonitorDataRepo ;

    @Resource
    private ArchMonitorRecordRepo archMonitorRecordRepo ;

    @Transactional
    public void readFile() throws InterruptedException {
        File file = new File(path) ;
        /** path 是 real_time_data 的文件夹路径  */
        if (!file.exists()) {
            log.error("未找到实时数据文件夹");
        } else {
            File[] dataFiles = file.listFiles();
            /** 找到VWS文件夹下的所有传感器数据文件 */
            CountDownLatch await = new CountDownLatch(dataFiles.length) ;
            for (File dataFile:dataFiles) {
                threadPool.execute(new DataThread(dataFile, await));
            }
            await.await();
        }
    }

    class DataThread implements Runnable{
        /** Runnable 是创建线程的方法，是一个接口 */
        /** Runnable 里面只提供了一个run方法，这个方法得自己重写 */
        /** 存到缓存里 */
        private final File file;
        /** 被final修饰的就不能被修改 */
        private final CountDownLatch await ;
        DataThread(File file, CountDownLatch await) {
            this.file=file ;
            this.await=await ;
        }
        @SneakyThrows
        @Override
        @Transactional
        public void  run() {
            List<ArchMonitorDataPo> archMonitorDataPoList = new ArrayList<>() ;
            if (file.exists()) {
                try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));) {
                    String s = br.readLine() ;
                    /** 读取第一行 */
                    String[] arr1 = StringUtils.split(s, ";");
                    /** 将第一行以；分割，并存入String数组arr1中 */
                    while ((s=br.readLine()) != null) {
                        String[] arr2 = StringUtils.split(s,";") ;
                        Map<String, String> map1 = new HashMap<>() ;
                        for (int i=1; i<arr2.length; i++) {
                            ArchMonitorDataPo archMonitorDataPo = new ArchMonitorDataPo() ;
                            archMonitorDataPo.setCreateTime(DateUtil.stringToDate(arr2[0]));
                            archMonitorDataPo.setAssetName(arr1[i]);
                            archMonitorDataPo.setValue(Double.parseDouble(arr2[i]));
                            archMonitorDataPoList.add(archMonitorDataPo) ;
                            map1.put(arr1[i], String.valueOf(Double.parseDouble(arr2[i]))) ;
                            /** WebSocket里的数据是从下面的代码里面取值的，所以这里要加锁，防止多线程运行的时候冲突 */
                            synchronized (lock) {
                                List<String> list = map.get(arr1[i]) ;
                                if (list != null) {
                                    if (list.size()>299) {
                                        do {
                                            list.remove(list.get(0)) ;
                                        } while (list.size() == 299) ;
                                    }
                                } else {
                                    list = new ArrayList<>() ;
                                }
                                list.add(String.valueOf(Double.parseDouble(arr2[i]))) ;
                                map.put(arr1[i],list) ;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("文件" + file.getName() + "处理失败："+ e.getMessage());
                }
            }
            ArchMonitorRecordPo archMonitorRecordPo = new ArchMonitorRecordPo() ;
            archMonitorRecordPo.setCreateTime(new Date());
            archMonitorRecordPo.setFileName(file.getName());
            threadPool1.execute(new DataThread1(archMonitorRecordPo, archMonitorDataPoList));
            backupFile(file) ;
            await.countDown();
        }
    }

    class DataThread1 implements Runnable {
        /** 存到数据库里 */
        private final ArchMonitorRecordPo archMonitorRecordPo ;
        private final List<ArchMonitorDataPo> archMonitorDataPoList ;

        DataThread1 (ArchMonitorRecordPo archMonitorRecordPo, List<ArchMonitorDataPo> archMonitorDataPoList) {
            this.archMonitorRecordPo = archMonitorRecordPo ;
            this.archMonitorDataPoList = archMonitorDataPoList ;
        }

        public void run() {
            archMonitorRecordRepo.save(archMonitorRecordPo) ;
            archMonitorDataRepo.saveAll(archMonitorDataPoList) ;
        }
    }

    private void backupFile(File file) throws IOException {
        File backupFile = new File(backupPath + "/" + file.getName()) ;
        FileCopyUtils.copy(file, backupFile) ;
        file.delete() ;
    }


}
