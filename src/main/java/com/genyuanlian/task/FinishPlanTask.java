package com.genyuanlian.task;

import com.genyuanlian.constant.CODE;
import com.genyuanlian.dao.PlanDao;
import com.genyuanlian.pojo.Plan;
import com.genyuanlian.pojo.PlanData;
import com.genyuanlian.service.PlanService;
import com.genyuanlian.utils.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by xinruichao on 2018/6/8.
 */
@Component
public class FinishPlanTask {

    private Logger logger = Logger.getLogger(FinishPlanTask.class);

    @Autowired
    private PlanDao planDao;

    @Autowired
    private PlanService planService;

    @Scheduled(cron="59 59 23 * * ?")
    public void finishPlan() {
        try {
            logger.info("begin finishPlan");
            List<Plan> plans = planDao.findAll();
            for (Plan plan : plans) {
                if (plan.getSubmit() == 0) {
                    String wriqi = plan.getWriqi();
                    Date date = DateUtil.parse(wriqi + " 23:59:59");
                    if (System.currentTimeMillis() >= date.getTime()) {
                        PlanData planData = planService.submitPlan(plan.getId());
                        if (planData.getStatus() != CODE.SUCCESS) {
                            planDao.finishPlan(plan.getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("finishPlan: " + e.toString());
        }
    }

}
