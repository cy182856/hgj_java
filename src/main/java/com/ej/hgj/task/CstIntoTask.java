package com.ej.hgj.task;

import com.ej.hgj.dao.cron.CronDaoMapper;
import com.ej.hgj.entity.cron.Cron;
import com.ej.hgj.service.task.BasicDataTaskService;
import com.ej.hgj.task.service.CstIntoTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@EnableScheduling
public class CstIntoTask implements SchedulingConfigurer {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CstIntoTaskService cstIntoTaskService;

    @Autowired
    private CronDaoMapper cronDaoMapper;

    /**
     * 客户入住定时任务-超过7天未入住了删除数据
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(() -> cstIntoTaskService.cstIntoTask(), triggerContext -> {
            Cron cronByType = cronDaoMapper.getByType("9");
            String cron = cronByType.getCron();
            return new CronTrigger(cron).nextExecutionTime(triggerContext);
        });
    }
}
