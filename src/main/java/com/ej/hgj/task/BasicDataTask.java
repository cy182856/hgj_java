//package com.ej.hgj.task;
//
//import com.ej.hgj.service.task.BasicDataTaskService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//
//@Configuration
//@EnableScheduling
//public class BasicDataTask {
//
//    Logger logger = LoggerFactory.getLogger(getClass());
//
//    @Autowired
//    private BasicDataTaskService basicDataTaskService;
//
//    // 每1分钟执行一次
//    //@Scheduled(cron = "0 0/1 * * * ?")
//    // 每小时执行一次
//    @Scheduled(cron = "0 0 */1 * * ?")
//    public void run(){
//        logger.info("------------------基础数据同步开始!---------------");
//        basicDataTaskService.basicDataSync();
//        logger.info("------------------基础数据同步结束!---------------");
//    }
//}
