package com.genyuanlian.service;

import com.genyuanlian.constant.CODE;
import com.genyuanlian.dao.PlanDao;
import com.genyuanlian.dao.SponsorDao;
import com.genyuanlian.dao.UserDao;
import com.genyuanlian.dao.WitnessDao;
import com.genyuanlian.pojo.Plan;
import com.genyuanlian.pojo.PlanData;
import com.genyuanlian.pojo.Sponsor;
import com.genyuanlian.pojo.Witness;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xinruichao on 2018/5/25.
 */
@Service
public class WitnessService {

    private Logger logger = Logger.getLogger(WitnessService.class);

    @Autowired
    private WitnessDao witnessDao;

    @Autowired
    private PlanDao planDao;

    @Autowired
    private SponsorDao sponsorDao;

    /**
     * 根据sessionKey查找该计划的见证人
     * @param sessionKey
     * @return
     */
    public List<Witness> findBySessionKey(String sessionKey) {
        List<Witness> witnessList = new ArrayList<>();
        try {
            List<Witness> witnesses = witnessDao.findBySessionKey(sessionKey);
            for (Witness witness : witnesses) {
                Plan plan = planDao.findById(witness.getPlanId());
                plan.setSponsors(sponsorDao.findByPlanId(plan.getId()));
                plan.setWitnesses(witnessDao.findByPlanId(plan.getId()));
                witness.setPlan(plan);
                witnessList.add(witness);
            }
        } catch (Exception e) {
            logger.error("findBySessionKey: " + e.toString());
        }
        return witnessList;
    }

    /**
     * 确认见证
     * @param map
     * @return
     */
    public int confirmWitness(Map map) {
        try {
            witnessDao.confirmWitness(map);
            return CODE.SUCCESS;
        } catch (Exception e) {
            logger.error("confirmWitness: " + e.toString());
            return CODE.SYSTEM_ERROR;
        }
    }

    /**
     * 给出见证结果
     * @param map
     * @return
     */
    public int judgeWitness(Map map) {
        try {
            witnessDao.judgeWitness(map);
            return CODE.SUCCESS;
        } catch (Exception e) {
            logger.error("judgeWitness: " + e.toString());
            return CODE.SYSTEM_ERROR;
        }
    }

}
