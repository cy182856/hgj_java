package com.ej.hgj.task;

import com.ej.hgj.dao.cron.CronDaoMapper;
import com.ej.hgj.entity.cron.Cron;
import com.ej.hgj.task.service.CstIntoTaskService;
import com.ej.hgj.task.service.MonCarRenOrderTaskService;
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
public class MonCarRenOrderTask implements SchedulingConfigurer {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MonCarRenOrderTaskService monCarRenOrderTaskService;

    @Autowired
    private CronDaoMapper cronDaoMapper;

    /**
     * OFW月租车续费订单，超过7天未支付订单删除
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(() -> monCarRenOrderTaskService.monCarRenOrderTask(), triggerContext -> {
            Cron cronByType = cronDaoMapper.getByType("15");
            String cron = cronByType.getCron();
            return new CronTrigger(cron).nextExecutionTime(triggerContext);
        });
    }
}
